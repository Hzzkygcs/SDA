using System;
using System.Diagnostics;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using HzzGrader.JavaRelated;

namespace HzzGrader
{
    public partial class MainWindow
    {
        public async Task<Tuple<string, string>> execute_cmd(string command_or_arg, int time_limit = -1,
            string stdin = "",
            string target = "cmd.exe", Process use_process = null){
            // wanna to execute shell/command prompt
            Process process = use_process;
            if (use_process == null)
                process = new Process();

            initialize_cmd_process(process);
            if (target.ToLower().Equals("cmd.exe"))
                process.StartInfo.Arguments = "/c " + command_or_arg;
            else
                process.StartInfo.Arguments = command_or_arg;

            process.Start();
            if (stdin != null)
                process.StandardInput.Write(stdin);
            process.StandardInput.Close();

            if (time_limit < 0)
                await WaitForExitAsync(process);
            // process.WaitForExit();
            else{
                // process.WaitForExit(time_limit);

                var task = WaitForExitAsync(process);
                if (await Task.WhenAny(task, Task.Delay(time_limit)) != task){
                    // task timeout (time_limit ms)
                    throw new TimeoutException();
                }
            }


            string error = process.StandardError.ReadToEnd();
            string output = process.StandardOutput.ReadToEnd();


            return new Tuple<String, string>(output, error);
        }


        // https://stackoverflow.com/a/19104345/7069108
        public static Task WaitForExitAsync(Process process,
            CancellationToken cancellationToken = default(CancellationToken)){
            if (process.HasExited) return Task.CompletedTask;

            var tcs = new TaskCompletionSource<object>();
            process.EnableRaisingEvents = true;
            process.Exited += (sender, args) => tcs.TrySetResult(null);
            if (cancellationToken != default(CancellationToken))
                cancellationToken.Register(() => tcs.SetCanceled());

            return process.HasExited ? Task.CompletedTask : tcs.Task;
        }

        public static Process initialize_cmd_process(Process process){
            process.StartInfo.FileName = "cmd.exe";
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.CreateNoWindow = true;
            process.StartInfo.RedirectStandardOutput = true;
            process.StartInfo.RedirectStandardError = true;
            process.StartInfo.RedirectStandardInput = true;
            return process;
        }

        public async Task<Tuple<string, string>> execute_cmd_optimized(string command_or_arg, Process started_process,
            int time_limit = -1, string extra_stdin = null){

            started_process.StandardInput.Write(command_or_arg);

            if (extra_stdin != null)
                started_process.StandardInput.Write(extra_stdin);


            string error = started_process.StandardError.ReadToEnd();
            string output = started_process.StandardOutput.ReadToEnd();

            return new Tuple<String, string>(output, error);
        }

        public async Task<bool> test_if_javac_and_java_is_available(){
            {
                var res = await execute_cmd("javac --version");

                if (!res.Item1.Trim().StartsWith("javac")){
                    res = await execute_cmd("java -version");
                    if (!res.Item1.Trim().StartsWith("javac")){
                        var is_cancel = MessageBox.Show(
                            "There's an error when running javac --version\nDo you wish to continue?", "",
                            MessageBoxButtons.YesNo, MessageBoxIcon.Error);
                        if (is_cancel == System.Windows.Forms.DialogResult.No){
                            information_label_set_str_content("ERROR javac not found");
                            input.Text = res.Item2;
                            program_output.Text = "";
                            expected_output.Text = "";
                            return false;
                        }
                    }
                }
            }

            {
                var res = await execute_cmd("java --version");
                string temp = res.Item1.Trim().ToLower();
                if (!(temp.StartsWith("java") || temp.StartsWith("openjdk"))){
                    temp = (await execute_cmd("java --version")).Item1.Trim().ToLower();

                    if (!(temp.StartsWith("java") || temp.StartsWith("openjdk"))){
                        var is_cancel = MessageBox.Show(
                            "There's an error when running java --version\nDo you wish to continue?", "",
                            MessageBoxButtons.YesNo, MessageBoxIcon.Error);
                        if (is_cancel == System.Windows.Forms.DialogResult.No){
                            information_label_set_str_content("ERROR java not found");
                            input.Text = res.Item2;
                            program_output.Text = "";
                            expected_output.Text = "";
                            return false;
                        }
                    }

                }
            }
            return true;
        }

        public Task<bool> compile_java_source_code(string compile_dir_path, params string[] source_file_path){
            return compile_java_source_code(new string[] { "-Xlint:unchecked" }, compile_dir_path, source_file_path);
        }

        public async Task<bool> compile_java_source_code(string[] flag, string compile_dir_path,
            params string[] source_file_path){
            Tuple<string, string> result;
            {

                string command_compile = String.Format("javac {0} -d \"{1}\" \"{2}\"",
                    String.Join(" ", flag), compile_dir_path, source_file_path[0]);
                ;

                for (int i = 1; i < source_file_path.Length; i++){
                    command_compile = String.Format("{0} \"{1}\"", command_compile, source_file_path[i]);
                }


                input.Text = command_compile;
                result = await run_cmd_asynchronously(command_compile);
                // result = await execute_cmd(command_compile);

                if (result.Item2.Length > 0 && !result.Item2.Contains("warning") || result.Item2.Contains("error")){
                    MessageBox.Show("Unexpected compile-time error");
                    string err_msg = result.Item2.Replace("       ^", "\n");
                    err_msg = String.Join("\n\n", Utility.TakeLastLines(err_msg, 2));
                    err_msg = Regex.Replace(err_msg, "   +", "  ");
                    MessageBox.Show(err_msg);
                    // MessageBox.Show(command_compile);

                    write_log("Unexpected error (compiling java source code)");
                    write_log(result.Item2);
                    write_log(command_compile + "\n\n");

                    information_label_set_str_content("unexpected error");
                    input.Text = "";
                    program_output.Text = "";
                    expected_output.Text = "";
                    return false;
                }
            }
            return true;
        }

        public void information_label_set_str_content(string content){
            information_label.Text = content;
        }

        public async Task<Tuple<string,string>> run_cmd_asynchronously(string cmd_command){
            // result = await execute_cmd_optimized(command_run, command_prompt_run_process, 4000, null);
            JavaExecute java_execute = new JavaExecute(initialize_cmd_process(new Process()), compile_dir_path);
            bool wait = true;
            java_execute.on_unblocked = execute => { wait = false; };
            java_execute.start();
            java_execute.execute_arbitrary_cmd(cmd_command);

            while (wait){
                /*if (JavaExecute.debug != null && !JavaExecute.debug.Equals("")){
                    program_output_content.Text += JavaExecute.debug;
                    JavaExecute.debug = "";
                }*/
                await Task.Delay(50);
            }
            return java_execute.flush();
        }
        
    }

    
}