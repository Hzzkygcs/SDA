using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using HzzGrader.JavaRelated;
using MessageBox = System.Windows.Forms.MessageBox;

namespace HzzGrader
{
    public partial class MainWindow
    {
        public async Task compile_stress_test_native(){
            try{
                write_log("");
                clear_bin_folder();
                
                string compile_path = "";
                while (compile_path == "" || Directory.Exists(compile_path)) 
                    compile_path = Path.Combine(compile_dir_path, JavaExecute.random_string(8));
                Directory.CreateDirectory(compile_path);
                
                if (time_limited_chb.IsChecked == true)
                    __native_hzz_grader_src_code = Utility.read_embedded_resource("HzzGrader.ExecuteStresstest.HzzGraderTimeLimited.java");
                else
                    __native_hzz_grader_src_code = Utility.read_embedded_resource("HzzGrader.ExecuteStresstest.HzzGrader.java");

                if (!await test_if_javac_and_java_is_available())
                    return;
                information_label_set_str_content("check if there is any compile-time error");

                // just to check whether the syntax is valid or not
                string old_source_file_path = java_file_path.Text;
                string new_source_file_path = Path.Combine(compile_path, Path.GetFileName(old_source_file_path));

                string native_hzz_grader_path = Path.Combine(compile_path, "HzzGrader.java");
                File.Copy(old_source_file_path, new_source_file_path, true);
                File.Copy(old_source_file_path,
                    Path.Combine(src_code_backup_dir_path, Path.GetFileName(old_source_file_path)), true);

                write_log("checking for the original source syntax");
                if (!await compile_java_source_code(compile_path, new_source_file_path)){
                    copy_to_debug_directory(compile_path);
                    return;
                }
                write_log("no syntax error was found. Parsing java source file");


                information_label_set_str_content("parsing and wrapping your copied java source file");
                JavaMiniParser java_mini_parser = new JavaMiniParser(File.ReadAllText(new_source_file_path));
                java_mini_parser.parse();
                java_mini_parser.parse_tokenized_splitted();

                write_log("finished parsing");
                string input_reader_untuk_hzzgrader = "";

                if (JavaMiniParserUtil.get_static_assigned_var_dec_not_in_public_class(java_mini_parser).Count > 0){
                    string msg = "Variabel static hanya boleh dimiliki oleh public class, dan harus dideklarasikan " +
                                 "sebelum nested class (inner class). ";
                    MessageBox.Show(msg);
                    information_label_set_str_content(msg);
                    return;
                }

                List<AssignedVariableDeclaration> assigned_static_var_dec =
                    java_mini_parser.get_assigned_static_variable_declarations();

                string
                    static_field_reinitialization = ""; // we will put it at the first line of source's main() function
                foreach (var variable in assigned_static_var_dec){
                    if (variable.is_final)
                        continue;
                    static_field_reinitialization +=
                        String.Format("{0}={1};", variable.name, variable.assigned_value);
                }

                if (java_mini_parser.has_package_statement()){
                    MessageBox.Show("Please remove any package statement");
                    information_label_set_str_content("Please remove any package statement");
                    return;
                }

                {
                    write_log("trying to locate the main method from the java source file");
                    var temp = JavaMiniParser.GET_MAIN_METHOD_REGEX.Match(java_mini_parser.tokenized_str);
                    Debug.Assert(temp.Success);
                    int main_func_first_line = temp.Index + temp.Length;
                    string temp2 =
                        java_mini_parser.tokenized_str.Insert(main_func_first_line, static_field_reinitialization);
                    java_mini_parser.tokenized_str = temp2;
                }

                string nama_class_yang_mau_diuji = Path.GetFileNameWithoutExtension(new_source_file_path);

                write_log("writing HzzGrader.java");
                string information_token = JavaMiniParser.random_string(48);
                string input_token = JavaMiniParser.random_string(48);
                string program_output_token = JavaMiniParser.random_string(48);
                string expected_output_token = JavaMiniParser.random_string(48);
                string end_token = JavaMiniParser.random_string(48);
                string time_limit_ms = "6000";
                try{
                    write_log("reading " + time_limit_file_directory);
                    string temp = File.ReadAllText(time_limit_file_directory);
                    int n;
                    bool isNumeric = int.TryParse(temp, out n);
                    if (!isNumeric || n <= 0){
                        write_log("Invalid content: " + time_limit_file_directory);
                        MessageBox.Show(time_limit_file_directory + " must be a positive integer.");
                    }else
                        time_limit_ms = temp;
                }
                catch (IOException e){
                    string str = "error: " + time_limit_file_directory + "    " + e.Message;
                    write_log(str);
                    MessageBox.Show("error: " + time_limit_file_directory + " \n" + e.Message);
                }


                string hzz_grader_code = __native_hzz_grader_src_code.Replace("{{NAMA_CLASS}}",
                    nama_class_yang_mau_diuji);
                hzz_grader_code = hzz_grader_code.Replace("{{TARGET_DIRECTORY}}",
                    testcase_folder.Text.Replace("\\", "/").TrimEnd('/'));
                hzz_grader_code = hzz_grader_code.Replace("{{INPUT_READER}}",
                    input_reader_untuk_hzzgrader);
                hzz_grader_code = hzz_grader_code.Replace("{{INFORMATION_DELIMITER_TOKEN}}",
                    information_token);
                hzz_grader_code = hzz_grader_code.Replace("{{INPUT_DELIMITER_TOKEN}}",
                    input_token);
                hzz_grader_code = hzz_grader_code.Replace("{{PROGRAM_OUTPUT_DELIMITER_TOKEN}}",
                    program_output_token);
                hzz_grader_code = hzz_grader_code.Replace("{{EXPECTED_OUTPUT_TOKEN}}",
                    expected_output_token);
                hzz_grader_code = hzz_grader_code.Replace("{{END_DELIMITER_TOKEN}}",
                    end_token);
                hzz_grader_code = hzz_grader_code.Replace("{{TIME_LIMIT_MS}}",
                    time_limit_ms);
                


                information_label_set_str_content("compiling your java source code");
                File.WriteAllText(new_source_file_path, java_mini_parser.unparse());
                File.WriteAllText(native_hzz_grader_path, hzz_grader_code);

                write_log(new_source_file_path + "  and  " + native_hzz_grader_path + "  has been generated");
                if (!await compile_java_source_code(new string[] { "-Xlint:unchecked" },
                    compile_path, native_hzz_grader_path, new_source_file_path)){
                    copy_to_debug_directory(compile_path);
                    return;
                }

                information_label_set_str_content("running your java source code");
                write_log("executing HzzGrader.java");
                await execute_stress_test_native(compile_path, information_token, input_token,
                    program_output_token,
                    expected_output_token, end_token);
                copy_to_debug_directory(compile_path);
                write_log("Done!");
            }
            finally{
                finally_();
            }
        }


        public async Task execute_stress_test_native(string compile_path, string information_token,
            string input_token, string program_output_token, string expected_output_token, string end_token){

            try{
                information_token = String.Format("{0}", information_token);
                input_token = String.Format("{0}", input_token);
                program_output_token = String.Format("{0}", program_output_token);
                expected_output_token = String.Format("{0}", expected_output_token);
                end_token = String.Format("{0}", end_token);


                /*
                 We would prefer to use JavaExecute() rather than execute_cmd because sometimes java is freezing the
                 output even after it finishes its tasks.
                */
                string command_run = "command run: JavaExecute() ~";
                Process process = initialize_cmd_process(new Process());
                JavaExecute java_execute = new JavaExecute(process, compile_path);
                java_execute.start();
                bool wait = true;
                java_execute.on_unblocked = execute => { wait = false; };
                java_execute.execute_custom_java_args("HzzGrader", "-cp \".;.\"");

                {
                    while (wait){
                        information_label_set_str_content(
                            String.Format("running ({0})", java_execute.number_of_received_output));
                        await Task.Delay(30);
                    }
                }

                Tuple<string, string> result = java_execute.flush();
                process.StandardInput.Close();
                process.Close();

                string item1 = result.Item1.Replace("\r", "");

                if (result.Item2.Length > 0){
                    MessageBox.Show("Unexpected error is found");
                    MessageBox.Show(result.Item2);
                    MessageBox.Show(command_run);

                    File.AppendAllText(log_file, "Unexpected error is found\n" + result.Item2 +
                                                 "\n" + command_run + "\n\n\n");

                    information_label_set_str_content("Unexpected error is found");
                    input_content.Text = "";
                    program_output_content.Text = "";
                    expected_output_content.Text = "";
                    return;
                }

                if (item1.Contains(information_token)){
                    string[] temp = item1.Split(new string[]
                    {
                        information_token, input_token,
                        program_output_token, expected_output_token, end_token
                    }, StringSplitOptions.RemoveEmptyEntries);
                    if (temp.Length < 5){
                        MessageBox.Show("error outputted value is not recognized");
                        information_label_set_str_content("error outputted value is not recognized");
                        MessageBox.Show(item1);
                        MessageBox.Show(command_run);
                        return;
                    }
                    information_label_set_str_content(temp[1].Trim());
                    input_content.Text = temp[2].Trim() + "\n";
                    program_output_content.Text = temp[3].Trim() + "\n";
                    expected_output_content.Text = temp[4].Trim() + "\n";

                    return;
                }
                else{
                    MessageBox.Show(information_token);
                    MessageBox.Show(item1);
                    MessageBox.Show(item1.Contains(information_token).ToString());
                }

            }
            finally{
                finally_();
            }
        }


        public void clear_bin_folder(){
            string folder = compile_dir_path;
            string[] directories = Directory.GetDirectories(folder);

            foreach (var directory in directories){
                try{
                    write_log("deleting " + directory);
                    Directory.Delete(directory, true);
                }
                catch (IOException e){
                    write_log("can't delete " + directory + " because of " + e.Message);
                }
            }
        }
        
        private void Native_hzzgrader_chb_changed(object sender, RoutedEventArgs e){
            if (native_hzzgrader_chb.IsChecked == true){
                if (time_limited_chb != null)
                    time_limited_chb.Visibility = Visibility.Visible;
            }
            else{
                if (time_limited_chb != null)
                    time_limited_chb.Visibility = Visibility.Hidden;
            }
        }
    }
}