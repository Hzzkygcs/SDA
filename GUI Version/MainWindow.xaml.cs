﻿// #define AUTO_UPDATE




using System;
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
using System.Windows.Media;
using HzzGrader.JavaRelated;
using HzzGrader.updater;
using HzzGrader.Windows;
using Microsoft.Extensions.Logging;
using Microsoft.Win32;
using Microsoft.WindowsAPICodePack.Dialogs;
using Button = System.Windows.Controls.Button;
using CheckBox = System.Windows.Controls.CheckBox;
using MessageBox = System.Windows.Forms.MessageBox;
using OpenFileDialog = Microsoft.Win32.OpenFileDialog;




namespace HzzGrader
{



    public partial class MainWindow
    {
        public static double header_section_panel_maxwidth;
        
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
        
#if AUTO_UPDATE
        public Updater updater = new Updater();
#endif
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


        public static void write_log(string str){
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

            File.AppendAllText(log_file, "\n\n============= started on " + DateTime.Now.ToString("dd-MM-yyyy hh:mm") + " =============\n");
            Dispatcher.Invoke(MainExternalTestcaseHandler.initial_caching);

#if AUTO_UPDATE
            Updater.log_updater = write_log;
            version_label.Text = updater.update_information.version;
#else
            version_number_label.Text = Updater.read_embedded_resource("HzzGrader.updater.current_version.txt").Trim();            
#endif

            try{
                header_section_panel_maxwidth = header_section_panel.MaxWidth;
                
                if (Directory.Exists(Path.Combine(current_app_dir, "update")))
                    Directory.Delete(Path.Combine(current_app_dir, "update"), true);

                Console.WriteLine("running");
                Updater.handle_remove_and_copy_to_requirements();
                initialize_large_textboxes();

#if AUTO_UPDATE
                updater.manage_update();
#endif

                if (File.Exists(prev_source_code_directory)){
                    string text = System.IO.File.ReadAllText(prev_source_code_directory);
                    text = text.Trim();
                    default_source_code_directory = text;
                    java_file_path.Text = text;
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
                using (var temp = assembly.GetManifestResourceStream("HzzGrader.ExecuteStresstest.HzzGrader.java"))
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
                java_file_path.Text = filePath;
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
        }

        private async void ButtonBase_start_test_OnClick(object sender, RoutedEventArgs e){
            start_stress_test_btn.IsEnabled = false;
            native_hzzgrader_chb.IsEnabled = false;
            java_file_path.IsEnabled = false;
            pick_java_file_btn.IsEnabled = false;
            testcase_folder.IsEnabled = false;
            pick_testcase_folder.IsEnabled = false;
            pick_testcase_zip_btn.IsEnabled = false;

            information_label.Content = "start stresstesting";
            input_content.Text = "";
            program_output_content.Text = "";
            expected_output_content.Text = "";

            Dispatcher.Invoke(invoke_stress_test);
        }

        public async Task invoke_stress_test(){
            if (!File.Exists(java_file_path.Text)){
                MessageBox.Show("Your java source code file is not found!", "File Not Found",
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                on_error();
                return;
            }
            
            if (!Directory.Exists(testcase_folder.Text)){
                MessageBox.Show("Your testcase folder is not found!", "File Not Found",
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                on_error();
                return;
            }
            
            if (native_hzzgrader_chb.IsChecked.Value)
                Dispatcher.Invoke(compile_stress_test_native);
            else{
                Dispatcher.Invoke(stress_test_non_native);
            }
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


        private ExtendedEditor extended_editor = null;


        private void label_input_panel_OnMouseUp__open_ExtendedEditor(object sender, MouseButtonEventArgs e){
            if (extended_editor == null  ||  extended_editor.is_closed){
                Action restart_testcase = () =>
                {
                    if (!start_stress_test_btn.IsEnabled){
                        MessageBox.Show("Sorry, we're still running the previous check");
                        return;
                    }
                    start_stress_test_btn.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                };
                
                extended_editor = new ExtendedEditor();
                input_content.extended_editor = extended_editor.input_editor;
                program_output_content.extended_editor = extended_editor.program_output_editor;
                expected_output_content.extended_editor = extended_editor.expected_output_editor;
                extended_editor.restart_stresstest = restart_testcase;
                extended_editor.Show();
            }
            
            

            Dispatcher.Invoke(async () =>
            {
                await extended_editor.equalize_number_of_line();
                await extended_editor?.update_differences_colouring();
            });
            
            extended_editor.Activate();
        }


        private void on_error(){
            finally_();
        }
        
        private void finally_(){  // might be called twice or more
            start_stress_test_btn.IsEnabled = true;
            native_hzzgrader_chb.IsEnabled = true;
            java_file_path.IsEnabled = true;
            pick_java_file_btn.IsEnabled = true;
            testcase_folder.IsEnabled = true;
            pick_testcase_folder.IsEnabled = true;
            pick_testcase_zip_btn.IsEnabled = true;

            extended_editor.equalize_number_of_line();
            extended_editor?.update_differences_colouring();
        }
        
        
        private void Label_testcase_zip_OnMouseUp(object sender, MouseButtonEventArgs e){
            tc_zip_panel.Visibility = Visibility.Hidden;
            tc_folder_panel.Visibility = Visibility.Visible;
        }

        private void Label_testcase_folder_OnMouseUp(object sender, MouseButtonEventArgs e){
            tc_folder_panel.Visibility = Visibility.Hidden;
            tc_zip_panel.Visibility = Visibility.Visible;
        }

        private async void Pick_testcase_zip_btn_OnClick(object sender, RoutedEventArgs e){
            bool keep_hide = true;
            pick_testcase_zip_btn.IsEnabled = false;
            start_stress_test_btn.IsEnabled = false;
            
            Action<bool, string> on_done = async (success, url) =>
            {
                Action<bool, string> on_testcase_downloaded = async (download_success, new_tc_folder) =>
                {
                    testcase_folder.Text = new_tc_folder;
                    pick_testcase_zip_btn.IsEnabled = true;
                    start_stress_test_btn.IsEnabled = true;
                    information_label.Content = "Downloaded and extracted successfully!";
                };
                
                Action<bool, string> on_testcase_download_failed = async (download_success, new_tc_folder) =>
                {
                    tc_zip_path.Text = "";
                    pick_testcase_zip_btn.IsEnabled = true;
                    start_stress_test_btn.IsEnabled = true;
                    information_label.Content = "Download or extract failed.";
                };


            
                Show();
                if (success){
                    start_stress_test_btn.IsEnabled = false;
                    information_label.Content = "getting the testcases";
                    tc_zip_path.Text = url.Substring(MainExternalTestcaseHandler.root_url.Length);

                    Dispatcher.BeginInvoke((Action)(
                        async () =>
                        {
                            await MainExternalTestcaseHandler.download_testcase(this,
                                on_testcase_downloaded,
                                on_testcase_download_failed);
                        }
                        ));
                }else{
                    keep_hide = false;
                    pick_testcase_zip_btn.IsEnabled = true;
                    start_stress_test_btn.IsEnabled = true;
                }
            
            };
            
            
            Action<Exception> on_error = (exception) =>
            {
                keep_hide = false;
                pick_testcase_zip_btn.IsEnabled = true;
                start_stress_test_btn.IsEnabled = true;
                
                write_log("When fetching testcase list information from server, there's an error: \n" + exception.Message);
                write_log(exception.StackTrace);
                MessageBox.Show(exception.Message);
                MessageBox.Show(exception.StackTrace);
                Show();
            };
            
            
            await MainExternalTestcaseHandler.start_window(on_done, on_error);
            if (keep_hide)
                Hide();
        }

        private void Window_pin_cb_check_changed(object sender, RoutedEventArgs e){
            CheckBox checkBox = sender as CheckBox;
            if (checkBox == null)
                return;
            
            string extra_label = " (pinned)";
            if (checkBox.IsChecked == true){
                Topmost = true;
                Title += extra_label;
            }else{
                Topmost = false;
                Title = Title.Substring(0, Title.Length - extra_label.Length);
            }
        }



        
        
        
        private void MainWindow_OnSizeChanged(object sender, SizeChangedEventArgs e){
            if (e.NewSize.Height < 400){
                core_grid.RowDefinitions[2].Height = new GridLength(0);
            }
            else{
                core_grid.RowDefinitions[2].Height = new GridLength(1, GridUnitType.Star);
            }

            if (e.NewSize.Height < 300){
                border_wrapper__input.Visibility = Visibility.Collapsed;
            }else{
                border_wrapper__input.Visibility = Visibility.Visible;
            }

            if (e.NewSize.Width < 450){
                brand_label.Visibility = Visibility.Collapsed;
                version_label.Visibility = Visibility.Collapsed;

                java_source_panel.MaxWidth = 250;
                tc_zip_panel.MaxWidth = 250;
                tc_folder_panel.MaxWidth = 250;
            }else{
                brand_label.Visibility = Visibility.Visible;
                version_label.Visibility = Visibility.Visible;
                
                if (e.NewSize.Width < 700){
                    java_source_panel.MaxWidth = 300;
                    tc_zip_panel.MaxWidth = 300;
                    tc_folder_panel.MaxWidth = 300;
                }else{
                    java_source_panel.MaxWidth = 500;
                    tc_zip_panel.MaxWidth = 500;
                    tc_folder_panel.MaxWidth = 500;
                }
            }
        }
    }

}