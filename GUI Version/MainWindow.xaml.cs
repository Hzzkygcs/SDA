﻿using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Forms;
using System.Windows.Input;
using HzzGrader.updater;
using Microsoft.Extensions.Logging;
using Microsoft.Win32;
using Microsoft.WindowsAPICodePack.Dialogs;
using MessageBox = System.Windows.Forms.MessageBox;
using OpenFileDialog = Microsoft.Win32.OpenFileDialog;


namespace HzzGrader
{



    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow
    {
        public static readonly String prev_source_code_directory = "prevdir";
        public static readonly String prev_testcase_directory = "tc_prevdir";
        private string __native_hzz_grader_src_code;
        private string __default_source_code_directory = AppDomain.CurrentDomain.BaseDirectory;
        private string __default_testcase_directory = AppDomain.CurrentDomain.BaseDirectory;

        public static string current_app_dir = AppDomain.CurrentDomain.BaseDirectory;
        public static string debug_directory = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "debug");
        public static string log_file = Path.Combine(current_app_dir, "log.txt");

        public readonly string src_code_backup_dir_path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "backup");

        public readonly string compile_dir_path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "bin");
        // string compile_dir_path = System.IO.Path.GetTempPath();
        // string compile_dir_path = @"D:\05 Projects\02 C#\HzzGrader\bin\Debug";


        public Updater updater = new Updater();
        
        
        public string default_source_code_directory{
            get{
                return __default_source_code_directory;
            }
            set{
                if (!__default_source_code_directory.Equals(value.Trim())){
                    __default_source_code_directory = value.Trim();
                    using (StreamWriter writer = new StreamWriter(prev_source_code_directory, false)){
                        writer.WriteLine(value);
                    }
                }
            }
        }
        public string default_testcase_directory{
            get{
                return __default_testcase_directory;
            }
            set{
                if (!__default_testcase_directory.Equals(value.Trim())){
                    __default_testcase_directory = value.Trim();
                    using (StreamWriter writer = new StreamWriter(prev_testcase_directory, false)){
                        writer.WriteLine(value);
                    }
                }
            }
        }


        public void write_log(string str){
            File.AppendAllText(log_file, str + "\n");
        }

        public void copy_to_debug_directory(string source_directory){
            foreach(var file in Directory.GetFiles(debug_directory))
                File.Delete(file);
            foreach(var file in Directory.GetFiles(source_directory))
                File.Copy(file, Path.Combine(debug_directory, Path.GetFileName(file)));
            write_log("the resulting files:  \"" + source_directory + "\"   has been copied to " + debug_directory);
        }
        public MainWindow(){
            InitializeComponent();

            /*DownloadTestcaseWindow mini_window = new DownloadTestcaseWindow(new string[]{"halo", "balo", "calo"});
            mini_window.on_selected = (string x) =>
            {
                mini_window.Close();
                MessageBox.Show(x);
            };
            mini_window.Show();*/
            
            
            
            File.AppendAllText(log_file, "\n\n============= started on " + DateTime.Now.ToString("dd-MM-yyyy hh:mm") + " =============\n");
            Updater.log_updater = write_log;
            version_label.Text = updater.update_information.version;
            try{
                if (Directory.Exists(Path.Combine(current_app_dir, "update")))
                    Directory.Delete(Path.Combine(current_app_dir, "update"), true);

                Console.WriteLine("running");
                Updater.handle_remove_and_copy_to_requirements();
                initialize_large_textboxes();
                
                updater.manage_update();
                
                if (File.Exists(prev_source_code_directory)){
                    string text = System.IO.File.ReadAllText(prev_source_code_directory);
                    text = text.Trim();
                    default_source_code_directory = text;
                    file_path.Text = text;
                }
                if (File.Exists(prev_testcase_directory)){
                    string text = System.IO.File.ReadAllText(prev_testcase_directory);
                    text = text.Trim();
                    default_testcase_directory = text;
                    testcase_folder.Text = text;
                }
                if (!File.Exists(log_file)){
                    File.WriteAllText(log_file, "");
                }


                if (Directory.Exists(compile_dir_path))
                    Directory.Delete(compile_dir_path, true);
                Directory.CreateDirectory(compile_dir_path);
                Directory.CreateDirectory(src_code_backup_dir_path);
                Directory.CreateDirectory(debug_directory);

                var assembly = Assembly.GetExecutingAssembly();
                using (var temp = assembly.GetManifestResourceStream("HzzGrader.HzzGrader.java"))
                using (StreamReader stream_reader = new StreamReader(temp)){
                    // jangan lupa set HzzGrader/HzzGrader.java jadi bertipe EmbededFile
                    __native_hzz_grader_src_code = stream_reader.ReadToEnd();
                }
            }catch (Exception e){
                write_log("AN ERROR WAS FOUND WHEN RUNNING THE APPS");
                write_log("======   message   ======");
                write_log(e.Message);
                write_log("====== stack trace ======");
                write_log(e.StackTrace);
                write_log("=========================");
            }
        }

        private void ButtonBase_get_path_OnClick(object sender, RoutedEventArgs e){
            OpenFileDialog openFileDialog = new OpenFileDialog();
            
            openFileDialog.InitialDirectory = Path.GetDirectoryName(default_source_code_directory);
            openFileDialog.Filter = "java files (*.java)|*.java|All files (*.*)|*.*";
            openFileDialog.FilterIndex = 0;
            openFileDialog.RestoreDirectory = false;
            
            if (openFileDialog.ShowDialog() == true){
                //Get the path of specified file
                string filePath = openFileDialog.FileName;
                file_path.Text = filePath;
                default_source_code_directory = filePath;
            }
        }

        
        private void ButtonBase_get_tc_folder_OnClick(object sender, RoutedEventArgs e){
            CommonOpenFileDialog dialog = new CommonOpenFileDialog();
            dialog.IsFolderPicker = true;
            dialog.InitialDirectory = default_testcase_directory;

            if (dialog.ShowDialog() == CommonFileDialogResult.Ok){
                default_testcase_directory = dialog.FileName;
                testcase_folder.Text = dialog.FileName;
            }


            /*var dialog = new FolderBrowserDialog();
            dialog.SelectedPath = default_testcase_directory;
            
            dialog.ShowNewFolderButton = true;
            if (dialog.ShowDialog() == System.Windows.Forms.DialogResult.OK){
                default_testcase_directory = dialog.SelectedPath;
                testcase_folder.Text = dialog.SelectedPath;
            }*/
        }

        private async void ButtonBase_start_test_OnClick(object sender, RoutedEventArgs e){
            start_stress_test_btn.IsEnabled = false;
            information_label.Content = "start stresstesting";
            input_content.Text = "";
            program_output_content.Text = "";
            expected_output_content.Text = "";

            Dispatcher.Invoke(invoke_stress_test);
        }

        public async Task invoke_stress_test(){
            // await Task.Run(async () => {


            if (!File.Exists(file_path.Text)){
                MessageBox.Show("Your java source code file is not found!", "File Not Found",
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            
            if (!Directory.Exists(testcase_folder.Text)){
                MessageBox.Show("Your testcase folder is not found!", "File Not Found",
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            

            if (native_hzzgrader_chb.IsChecked.Value)
                Dispatcher.Invoke(compile_stress_test_native);
            else{
                Dispatcher.Invoke(stress_test_non_native);
            }
            
        }


        public async Task compile_stress_test_native(){
            try{
                if (!await test_if_javac_and_java_is_available())
                    return;
                information_label.Content = "check if there is any compile-time error";

                // just to check whether the syntax is valid or not
                string old_source_file_path = file_path.Text;
                string new_source_file_path = Path.Combine(compile_dir_path, Path.GetFileName(old_source_file_path));

                string native_hzz_grader_path = Path.Combine(compile_dir_path, "HzzGrader.java");
                File.Copy(old_source_file_path, new_source_file_path, true);
                File.Copy(old_source_file_path,
                    Path.Combine(src_code_backup_dir_path, Path.GetFileName(old_source_file_path)), true);

                write_log("checking for the original source syntax");
                if (!await compile_java_source_code(compile_dir_path, new_source_file_path)){
                    copy_to_debug_directory(compile_dir_path);
                    return;
                }
                write_log("no syntax error was found. Parsing java source file");


                information_label.Content = "parsing and wrapping your copied java source file";
                JavaMiniParser java_mini_parser = new JavaMiniParser(File.ReadAllText(new_source_file_path));
                java_mini_parser.parse();
                java_mini_parser.parse_tokenized_splitted();

                write_log("finished parsing");
                string input_reader_untuk_hzzgrader = "";

                if (JavaMiniParserUtil.get_static_assigned_var_dec_not_in_public_class(java_mini_parser).Count >
                    0){
                    MessageBox.Show("All static fields must belong to the public class and " +
                                    "should be put before any inner classes");
                    information_label.Content = "All static fields must belong to the public class and " +
                                                "should be put before any inner classes";
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
                        String.Format("\n{0}={1};", variable.name, variable.assigned_value);
                }

                if (java_mini_parser.has_package_statement()){
                    MessageBox.Show("Please remove any package statement");
                    information_label.Content = "Please remove any package statement";
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


                information_label.Content = "compiling your java source code";
                File.WriteAllText(new_source_file_path, java_mini_parser.unparse());
                File.WriteAllText(native_hzz_grader_path, hzz_grader_code);

                write_log(new_source_file_path + "  and  " + native_hzz_grader_path + "  has been generated");
                if (!await compile_java_source_code(new string[] { "-Xlint:unchecked" },
                    compile_dir_path, native_hzz_grader_path, new_source_file_path)){
                    copy_to_debug_directory(compile_dir_path);
                    return;
                }

                information_label.Content = "running your java source code";
                write_log("executing HzzGrader.java");
                await execute_stress_test_native(new_source_file_path, information_token, input_token,
                    program_output_token,
                    expected_output_token, end_token);
                copy_to_debug_directory(compile_dir_path);
                write_log("Done!");
            }
            finally{
                start_stress_test_btn.IsEnabled = true;
            }
        }


        public async Task execute_stress_test_native(string new_source_file_path, string information_token,
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
                JavaExecute java_execute = new JavaExecute(process, compile_dir_path);
                java_execute.start();
                bool wait = true;
                java_execute.on_unblocked = execute => { wait = false; };
                java_execute.execute_custom_java_args("HzzGrader", "-cp \".;.\"");

                {
                    while (wait){
                        information_label.Content =
                            String.Format("running ({0})", java_execute.number_of_received_output);
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

                    information_label.Content = "Unexpected error is found";
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
                        information_label.Content = "error outputted value is not recognized";
                        MessageBox.Show(item1);
                        MessageBox.Show(command_run);
                        return;
                    }
                    information_label.Content = temp[1].Trim();
                    input_content.Text = temp[2].Trim();
                    program_output_content.Text = temp[3].Trim();
                    expected_output_content.Text = temp[4].Trim();

                    return;
                }
                else{
                    MessageBox.Show(information_token);
                    MessageBox.Show(item1);
                    MessageBox.Show(item1.Contains(information_token).ToString());
                }

            }
            finally{
                start_stress_test_btn.IsEnabled = true;
            }
        }
        
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
        
        

        string compare_two_list_of_string(string[] list1, string[] list2, bool one_based=true){
            if (list1.Length != list2.Length)
                return "Line length is not the same";
            for (int i = 0; i < list1.Length; i++){
                if (!list1[i].Trim().Equals(list2[i].Trim())){
                    return (i + (one_based? 1:0)).ToString();
                }
            }

            return "";
        }


        private void Testcase_folder_OnLostFocus(object sender, RoutedEventArgs e) {
            if (!testcase_folder.Text.Equals(default_testcase_directory))
                default_testcase_directory = testcase_folder.Text;
        }

        private void hzzgrader_label_on_click__open_repository(object sender, MouseButtonEventArgs e){
            Process.Start("https://github.com/Hzzkygcs/SDA");

        }

        private void hzzgrader_label_on_right_click__open_log_file(object sender, MouseButtonEventArgs e){
            string edit = (string)Registry.GetValue(@"HKEY_CLASSES_ROOT\SystemFileAssociations\text\shell\edit\command", null, null);
            edit = edit.Replace("%1", log_file);

            ProcessStartInfo process_start_info = new ProcessStartInfo("cmd.exe", "/c " + edit);
            process_start_info.UseShellExecute = false;
            process_start_info.CreateNoWindow = true;
            Process process = Process.Start(process_start_info);
            process.Close();
        }

        private void hzzgrader_label_on_mouseUp__open_debug_directory(object sender, MouseButtonEventArgs e){
            if (e.ChangedButton == MouseButton.Middle && e.ButtonState == MouseButtonState.Released){
                Process.Start(current_app_dir);
            }
        }
    }

}