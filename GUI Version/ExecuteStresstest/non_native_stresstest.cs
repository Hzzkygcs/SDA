using System;
using System.Diagnostics;
using System.IO;
using System.Threading.Tasks;
using System.Windows.Forms;
using HzzGrader.JavaRelated;

namespace HzzGrader
{
    public partial class MainWindow
    {
        public async Task<bool> stress_test_non_native(){
            if (!await test_if_javac_and_java_is_available())
                return false;

            string source_file_path = file_path.Text;
            if (!await compile_java_source_code(compile_dir_path, source_file_path))
                return false;

            // --unidentified error~ is just an arbitrary placeholder
            Tuple<string, string> result = new Tuple<string, string>("--unidentified error~", "--unidentified error~");

            {
                JavaMiniParser java_mini_parser = new JavaMiniParser(File.ReadAllText(source_file_path));
                java_mini_parser.parse();
                if (java_mini_parser.has_package_statement()){
                    MessageBox.Show("Please remove any package statement");
                    information_label.Content = "Please remove any package statement";
                    return false;
                }
            }

            string compiled_file_name = Path.GetFileNameWithoutExtension(source_file_path);
            string command_run = "";


            try{
                string input_file_name_prefix = "in_";
                string output_file_name_prefix = "out_";
                string[] files = Directory.GetFiles(testcase_folder.Text, input_file_name_prefix + "*.txt",
                    SearchOption.TopDirectoryOnly);


                for (int file_num = 0; file_num < files.Length; file_num++){
                    string program_input = File.ReadAllText(files[file_num]);
                    if (program_input[program_input.Length - 1] != '\n'){
                        program_input += '\n';
                        File.WriteAllText(files[file_num], program_input);
                    }

                    information_label.Content = Path.GetFileName(files[file_num]);
                    string input_file_name_without_input_prefix = Path.GetFileName(files[file_num]).Substring(
                        input_file_name_prefix.Length
                    );

                    string exp_output_file_name = output_file_name_prefix + input_file_name_without_input_prefix;
                    string exp_output_file_dir = String.Format("{0}\\{1}", Path.GetDirectoryName(files[file_num]),
                        exp_output_file_name);

                    if (!File.Exists(exp_output_file_dir)){
                        MessageBox.Show("File output testcase not found: " + exp_output_file_dir);
                        information_label.Content = "File output testcase not found: " + exp_output_file_dir;
                        input_content.Text = "";
                        program_output_content.Text = "";
                        expected_output_content.Text = "";
                    }


                    string java_arg = String.Format("\"{0}\" < \"{1}\"", compiled_file_name, files[file_num]);
                    // command_run = String.Format("cd \"{0}\"  &  java " + java_arg, compile_dir_path);
                    command_run = "JavaExecute() non native";
                    input_content.Text = command_run;


                    // result = await execute_cmd_optimized(command_run, command_prompt_run_process, 4000, null);
                    JavaExecute java_execute = new JavaExecute(initialize_cmd_process(new Process()), compile_dir_path);
                    bool wait = true;
                    java_execute.on_unblocked = execute => { wait = false; };
                    java_execute.start();
                    java_execute.execute_arbitrary_cmd("java -cp \".;.\" " + java_arg);

                    while (wait){
                        /*if (JavaExecute.debug != null && !JavaExecute.debug.Equals("")){
                            program_output_content.Text += JavaExecute.debug;
                            JavaExecute.debug = "";
                        }*/
                        await Task.Delay(50);
                    }
                    result = java_execute.flush();


                    if (result.Item2.Length > 0){
                        MessageBox.Show("Unexpected error");
                        MessageBox.Show(result.Item2);
                        MessageBox.Show(command_run);
                        information_label.Content = "unexpected error";
                        program_output_content.Text = "";
                        expected_output_content.Text = "";
                        return false;
                    }


                    string prog_output = result.Item1.Replace("\r", "");
                    string[] prog_output_trimmed = prog_output.Trim().Split('\n');


                    string exp_output = File.ReadAllText(exp_output_file_dir).Replace("\r", "");
                    string[] exp_output_trimmed = exp_output.Trim().Split('\n');


                    string comparison_res = compare_two_list_of_string(prog_output_trimmed, exp_output_trimmed);

                    if (comparison_res.Length != 0){
                        information_label.Content = Path.GetFileName(files[file_num]) + " -- " + (comparison_res);
                        input_content.Text = program_input;
                        program_output_content.Text = String.Join("\n", prog_output_trimmed);
                        expected_output_content.Text = String.Join("\n", exp_output_trimmed);
                        return false;
                    }
                }
            }
            catch (TimeoutException e){
                MessageBox.Show("java command time out");
                MessageBox.Show(result.Item1);
                MessageBox.Show(result.Item2);
                information_label.Content = "java command time out";
                input_content.Text = command_run;
                program_output_content.Text = result.Item1;
                expected_output_content.Text = "";
                return false;
            }
            catch (Exception e){
                MessageBox.Show(e.Message);
                throw e;
            }
            finally{
                start_stress_test_btn.IsEnabled = true;
            }

            return true;
        }
        
        
        private string compare_two_list_of_string(string[] list1, string[] list2, bool one_based = true){
            if (list1.Length != list2.Length)
                return "Line length is not the same";
            for (int i = 0; i < list1.Length; i++){
                if (!list1[i].Trim().Equals(list2[i].Trim())){
                    return (i + (one_based ? 1 : 0)).ToString();
                }
            }

            return "";
        }
    }
}