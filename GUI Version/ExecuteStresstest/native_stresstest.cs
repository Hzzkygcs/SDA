using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Forms;
using System.Windows.Input;
using HzzGrader.JavaRelated;
using MessageBox = System.Windows.Forms.MessageBox;

namespace HzzGrader
{
    
    
    public class ExecutionState
    {
        public Process current_process = null;
        public string compile_path;
        public string information_token;
        public string current_testcase_number_token;
        public string input_token;
        public string program_output_token;
        public string expected_output_token;
        public string end_token;
        public string testcase_success;
        public DateTime java_source_write_time;
        
        public int result_index = 0;
        public int result_max = Int32.MaxValue;
        public List<ExecutionResult> results = new List<ExecutionResult>(5);

        public ExecutionState(string compile_path, string information_token, string current_testcase_number_token,
            string input_token, string program_output_token, string expected_output_token, string end_token, string testcase_success,
            DateTime java_source_write_time){
            this.compile_path = compile_path;
            this.information_token = information_token;
            this.current_testcase_number_token = current_testcase_number_token;
            this.input_token = input_token;
            this.program_output_token = program_output_token;
            this.expected_output_token = expected_output_token;
            this.end_token = end_token;
            this.testcase_success = testcase_success;
            this.java_source_write_time = java_source_write_time;
        }
    }


    public class ExecutionResult
    {
        public string information;
        public string input;
        public string program_output;
        public string expected_output;
        public int tc_position=0;

        public ExecutionResult(string information, string input, string program_output, 
                string expected_output){
            this.information = information;
            this.input = input;
            this.program_output = program_output;
            this.expected_output = expected_output;
            // this.tc_position = tc_position;
        }

        public void apply(MainWindow window){
            window.information_label_set_str_content(information);
            window.input_content.Text = input;
            window.program_output_content.Text = program_output;
            window.expected_output_content.Text = expected_output;
        }
    }

    
    public partial class MainWindow
    {
        private int compile_counter = 0;
        private ExecutionState execution_state;
        public async Task compile_stress_test_native(){
            try{
                write_log("");
                // clear_bin_folder();
                write_log("checking if there is a running process");

                try{
                    if (execution_state != null
                            && execution_state.current_process != null
                            && !execution_state.current_process.HasExited)
                        execution_state.current_process.Close();
                }
                catch (InvalidOperationException){}
                execution_state = null;


                string compile_path = "";
                while (compile_path == "" || Directory.Exists(compile_path)) 
                    compile_path = Path.Combine(compile_dir_path, 
                        String.Format("{0} {1}", 
                            compile_counter.ToString().PadLeft(3, '0'),
                            JavaExecute.random_string(8)));
                compile_counter++;
                Directory.CreateDirectory(compile_path);
                
                if (time_limited_chb.IsChecked == true)
                    __native_hzz_grader_src_code = Utility.read_embedded_resource("HzzGrader.ExecuteStresstest.HzzGraderTimeLimited.java");
                else
                    __native_hzz_grader_src_code = Utility.read_embedded_resource("HzzGrader.ExecuteStresstest.HzzGrader.java");

                if (!await test_if_javac_and_java_is_available())
                    return;
                information_label_set_str_content("checking syntax");

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
                write_log("no syntax error was found. Parsing the source file");
                
                
                information_label_set_str_content("parsing and wrapping your copied java source file");
                JavaMiniParser java_mini_parser = new JavaMiniParser(File.ReadAllText(new_source_file_path));
                java_mini_parser.parse();
                java_mini_parser.parse_tokenized_splitted();

                write_log("finished parsing");
                string input_reader_untuk_hzzgrader = "";

                
                // kalau belum punya public class
                if (java_mini_parser.get_public_class_name().Length == 0){
                    string msg = "public class tidak ditemukan. \nKode Anda harus punya tepat satu buah public class.";
                    MessageBox.Show(msg);
                    input_content.Text = msg;
                    information_label_set_str_content(msg);
                    return;
                }

                // cek apakah ada deklarasi variabel static yang tidak punya nilai awal
                {
                    MatchCollection matches = JavaMiniParser.GET_UNINITIALIZED_VARIABLE_DECLARATIONS_REGEX.Matches(
                        java_mini_parser.tokenized_str
                        );
                    bool stop_execution = false;
                    foreach (Match match in matches){
                        if (!match.Success)
                            continue;
                        UninitializedVariableDeclaration temp =
                            JavaMiniParser.get_uninitialized_variable_declaration_from_match(match);
                        if (temp.static_abstract != StaticAbstract.STATIC) continue;
                        if (!JavaMiniParser._JAVA_PRIMITIVE_WRAPPER.Contains(temp.type)
                                && !JavaMiniParser._JAVA_PRIMITIVE_TYPES.Contains(temp.type))
                            continue;
                        
                        stop_execution = true;
                        input_content.Text += "Deklarasi static variable: `" + match.Value + "` harus mempunyai nilai awal, \nmisalnya null atau 0.\n\n";
                    }
                    if (stop_execution){
                        // MessageBox.Show("Static variable ilegal ditemukan");
                        return;
                    }
                }

                // kalau ada satatic variabel selain di public class
                if (JavaMiniParserUtil.get_static_assigned_var_dec_not_in_public_class(java_mini_parser).Count > 0){
                    string msg = "static variable hanya boleh dimiliki oleh public class \nstatic variable harus dideklarasikan " +
                                 "sebelum nested class (inner class). ";
                    MessageBox.Show(msg);
                    input_content.Text = msg;
                    information_label_set_str_content(msg);
                    return;
                }
                
                // kalau ada variabel final
                if (java_mini_parser.tokenized_str.Contains("final static")
                    || java_mini_parser.tokenized_str.Contains("static final")){
                    
                    input_content.Text += "WARNING: \nSangat disarankan tidak ada variabel yang final";
                    await Task.Delay(2500);
                }else if (java_mini_parser.tokenized_str.Contains("final")){
                    input_content.Text += "WARNING: \nSangat disarankan tidak ada variabel yang final";
                    await Task.Delay(500);
                }

                List<AssignedVariableDeclaration> assigned_static_var_dec =
                    java_mini_parser.get_assigned_static_variable_declarations();

                string
                    static_field_reinitialization = ""; // we will put it at the first line of source's main() function
                foreach (var variable in assigned_static_var_dec){
                    if (variable.is_final){
                        continue;
                    }
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

                    {
                        // pastikan public void main() hanya ada 1
                        write_log("ensure that there's only one main function");
                        var result = JavaMiniParser.GET_MAIN_METHOD_REGEX.Match(
                            java_mini_parser.tokenized_str, main_func_first_line);
                        if (result.Success){
                            // ada > 1 main()
                            string msg = "Hanya boleh ada 1 fungsi static void main()";
                            MessageBox.Show(msg);
                            input_content.Text = msg;
                            return;
                        }
                    }
                    
                    string temp2 =
                        java_mini_parser.tokenized_str.Insert(main_func_first_line, static_field_reinitialization);
                    java_mini_parser.tokenized_str = temp2;
                }

                string nama_class_yang_mau_diuji = Path.GetFileNameWithoutExtension(new_source_file_path);

                write_log("writing HzzGrader.java");
                string information_token = JavaMiniParser.random_string(48);
                string current_testcase_number_token = JavaMiniParser.random_string(48);
                string input_token = JavaMiniParser.random_string(48);
                string program_output_token = JavaMiniParser.random_string(48);
                string expected_output_token = JavaMiniParser.random_string(48);
                string end_token = JavaMiniParser.random_string(48);
                string testcase_success = JavaMiniParser.random_string(48);
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
                hzz_grader_code = hzz_grader_code.Replace("{{CURRENT_TESTCASE_DELIMITER_TOKEN}}",
                    current_testcase_number_token);
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
                hzz_grader_code = hzz_grader_code.Replace("{{TESTCASE_SUCCESS}}",
                    testcase_success);
                
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
                execution_state = new ExecutionState(compile_path, information_token, 
                    current_testcase_number_token, input_token, program_output_token,
                    expected_output_token, end_token,  testcase_success, 
                    File.GetLastWriteTime(old_source_file_path));
                await execute_stress_test_native(0, 1, 5);
                copy_to_debug_directory(compile_path);
                write_log("Done!");
            }
            catch (Exception e){
                write_log("AN ERROR WAS FOUND WHEN COMPILING THE SOURCE");
                write_log("======   message   ======");
                write_log(e.Message);
                write_log("====== stack trace ======");
                write_log(e.StackTrace);
                write_log("=========================");
            }
            finally{
                finally_();
            }
        }



        

        public async Task execute_stress_test_native(int tc_pos=0, int tc_step=1, int run_x_times=1, bool update_window=true){
            string compile_path, information_token, current_testcase_number_token;
            string input_token, program_output_token, expected_output_token, end_token;

            try{
                ExecutionState current_state = execution_state;
                information_token = String.Format("{0}", current_state.information_token);
                current_testcase_number_token = String.Format("{0}", current_state.current_testcase_number_token);
                input_token = String.Format("{0}", current_state.input_token);
                program_output_token = String.Format("{0}", current_state.program_output_token);
                expected_output_token = String.Format("{0}", current_state.expected_output_token);
                end_token = String.Format("{0}", current_state.end_token);


                /*
                 We would prefer to use JavaExecute() rather than execute_cmd because sometimes java is freezing the
                 output even after it finishes its tasks.
                */
                string command_run = "command run: JavaExecute() ~";
                Process process = initialize_cmd_process(new Process());
                current_state.current_process = process;
                JavaExecute java_execute = new JavaExecute(process, current_state.compile_path);
                java_execute.start();
                bool wait = true;
                java_execute.on_unblocked = execute => { wait = false; };
                java_execute.execute_custom_java_args("HzzGrader", "-cp \".;.\"", 
                    string.Format("{0} {1}", tc_pos, 
                        tc_step));

                {
                    while (wait){
                        if (update_window){
                            information_label_set_str_content(
                                String.Format("running ({0})", 
                                    Math.Max(0, java_execute.number_of_received_meaningful_output-1)));
                        }
                        await Task.Delay(30);
                    }
                }

                Tuple<string, string, string> result = java_execute.flush();
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
                        information_token, current_testcase_number_token, input_token,
                        program_output_token, expected_output_token, end_token
                    }, StringSplitOptions.RemoveEmptyEntries);
                    if (temp.Length < 5){
                        MessageBox.Show("error outputted value is not recognized");
                        information_label_set_str_content("error outputted value is not recognized");
                        MessageBox.Show(item1);
                        MessageBox.Show(command_run);
                        return;
                    }
                    
                    ExecutionResult execution_result = new ExecutionResult(
                        temp[1].Trim(), temp[3].Trim() + "\n",
                        temp[4].Trim() + "\n", temp[5].Trim() + "\n");
                    
                    if (update_window){
                        information_label_set_str_content(execution_result.information);
                        input_content.Text = execution_result.input;
                        program_output_content.Text = execution_result.program_output;
                        expected_output_content.Text = execution_result.expected_output;
                    }
                    
                    
                
                    int n;
                    if (!Int32.TryParse(temp[2].Trim(), out n)){
                        write_log("temp[2].Trim() :  `" + temp[2].Trim() + "`. Error");
                        MessageBox.Show("unexpected error: invalid line number");
                        return;
                    }
                    execution_result.tc_position = n;
                    
                    if (current_state.results.Count == 0 
                        || current_state.results[current_state.results.Count - 1]
                            .tc_position < n)
                        current_state.results.Add(execution_result);

                    if (item1.Contains(current_state.testcase_success)){
                        current_state.result_max = current_state.results.Count - 1;
                        if (current_state.result_index >= current_state.results.Count-1)
                            start_stress_test_next.IsEnabled = false;
                    }else{
                        if (current_state.result_index < current_state.results.Count-1
                            && current_state == execution_state)
                            start_stress_test_next.IsEnabled = true;
                        
                        if (run_x_times > 0 && current_state == execution_state)
                            Dispatcher.Invoke(async () =>
                            {
                                await execute_stress_test_native_continue_last_testcase(
                                    run_x_times - 1);
                            });
                    }
                }
            }catch (Exception e){
                write_log("AN ERROR WAS FOUND WHEN EXECUTING THE JAVA SOURCE FILE");
                write_log("======   message   ======");
                write_log(e.Message);
                write_log("====== stack trace ======");
                write_log(e.StackTrace);
                write_log("=========================");
            }
            finally{
                if (update_window){
                    finally_();
                }
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
                if (start_stress_test_previous != null)
                    start_stress_test_previous.Visibility = Visibility.Visible;
                if (start_stress_test_next != null)
                    start_stress_test_next.Visibility = Visibility.Visible;
                if (start_test_button_dummy_content != null)
                    start_test_button_dummy_content.Visibility = Visibility.Visible;
                
            }
            else{
                if (time_limited_chb != null)
                    time_limited_chb.Visibility = Visibility.Hidden;
                if (start_stress_test_previous != null)
                    start_stress_test_previous.Visibility = Visibility.Collapsed;
                if (start_stress_test_next != null)
                    start_stress_test_next.Visibility = Visibility.Collapsed;
                if (start_test_button_dummy_content != null)
                    start_test_button_dummy_content.Visibility = Visibility.Collapsed;
            }
        }

        
        private void Start_stress_test_previous_OnMouseLeftButtonUp(object sender, MouseButtonEventArgs e){
            execution_state.result_index--;
            execution_state.results[execution_state.result_index].apply(this);
            
            if (execution_state.result_index+1 < execution_state.results.Count)
                start_stress_test_next.IsEnabled = true;

            if (execution_state.result_index == 0){
                start_stress_test_previous.IsEnabled = false;
            }
        }
        
        private void Start_stress_test_next_OnMouseLeftButtonUp(object sender, MouseButtonEventArgs e){
            next_testcase();
        }

        private void next_testcase(){
            
            
            if (execution_state.result_index+1 < execution_state.results.Count){
                execution_state.result_index++;
                execution_state.results[execution_state.result_index].apply(this);
                start_stress_test_previous.IsEnabled = (execution_state.result_index > 0);
            }
            if (execution_state.result_index + 1 >= execution_state.result_max
                || execution_state.result_index+1 >= execution_state.results.Count)
                start_stress_test_next.IsEnabled = false;

            Action async_func = async () =>
            {
                if (start_stress_test_btn.IsEnabled){
                    // start_stress_test_btn.IsEnabled = false;
                    start_stress_test_next.IsEnabled = false;
                    await execute_stress_test_native_continue_last_testcase(10);
                }
            };
            
            if (execution_state.result_index+1 >= execution_state.results.Count){
                Debug.Assert(execution_state != null);
                try{
                    if (execution_state.current_process == null
                        || execution_state.current_process.HasExited)
                        Dispatcher.Invoke(async_func);
                }
                catch (InvalidOperationException e){
                    Dispatcher.Invoke(async_func);
                }
                
            }
        }
        
        
        public async Task execute_stress_test_native_continue_last_testcase(int run_x_times=5){
            if (execution_state.result_max == Int32.MaxValue)
            await execute_stress_test_native(
                execution_state.results[execution_state.results.Count-1].tc_position + 1,
                1, run_x_times, false);
        }
        
        
    }
}