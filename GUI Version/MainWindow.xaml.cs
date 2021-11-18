// #define AUTO_UPDATE


using System;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Automation.Peers;
using System.Windows.Automation.Provider;
using System.Windows.Forms;
using System.Windows.Input;
using HzzGrader.updater;
using HzzGrader.Windows;
using Microsoft.Extensions.Primitives;
using Microsoft.Win32;
using Microsoft.WindowsAPICodePack.Dialogs;
using Application = System.Windows.Application;
using Button = System.Windows.Controls.Button;
using CheckBox = System.Windows.Controls.CheckBox;
using MessageBox = System.Windows.Forms.MessageBox;
using OpenFileDialog = Microsoft.Win32.OpenFileDialog;


namespace HzzGrader
{
    public partial class MainWindow
    {
        public static double header_section_panel_maxwidth;

        public static readonly String prev_source_code_directory = ".configurations/prevdir";
        public static readonly String prev_testcase_directory = ".configurations/tc_prevdir";
        public static readonly String time_limit_file_directory = ".configurations/time_limit_in_ms.txt";
        
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
            foreach (var file in Directory.GetFiles(debug_directory))
                File.Delete(file);
            foreach (var file in Directory.GetFiles(source_directory))
                File.Copy(file, Path.Combine(debug_directory, Path.GetFileName(file)));
            write_log("the resulting files:  \"" + source_directory + "\"   has been copied to " + debug_directory);
        }

        public MainWindow(){
            InitializeComponent();

            File.AppendAllText(log_file,
                "\n\n============= started on " + DateTime.Now.ToString("dd-MM-yyyy hh:mm") + " =============\n");
            Dispatcher.Invoke(MainExternalTestcaseHandler.initial_caching);

#if AUTO_UPDATE
            Updater.log_updater = write_log;
            version_number_label.Text = updater.update_information.version;
#else
            version_number_label.Text = Utility.read_embedded_resource("HzzGrader.updater.current_version.txt").Trim();
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


                if (Directory.Exists(compile_dir_path)){
                    try{
                        write_log("deleting " + compile_dir_path);
                        Directory.Delete(compile_dir_path, true);
                    }
                    catch (IOException e){
                        write_log("can't delete " + compile_dir_path + " because of " + e.Message);
                    }
                }
                if (!Directory.Exists(compile_dir_path))
                    Directory.CreateDirectory(compile_dir_path);
                Directory.CreateDirectory(src_code_backup_dir_path);
                Directory.CreateDirectory(debug_directory);

                __native_hzz_grader_src_code = Utility.read_embedded_resource("HzzGrader.ExecuteStresstest.HzzGrader.java");
            }
            catch (Exception e){
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
            start_stress_test_previous.IsEnabled = false;
            start_stress_test_next.IsEnabled = false;
            
            start_stress_test_btn.IsEnabled = false;
            native_hzzgrader_chb.IsEnabled = false;
            java_file_path.IsEnabled = false;
            pick_java_file_btn.IsEnabled = false;
            testcase_folder.IsEnabled = false;
            pick_testcase_folder.IsEnabled = false;
            pick_testcase_zip_btn.IsEnabled = false;

            information_label_set_str_content("start stresstesting");
            input_content.Text = "";
            program_output_content.Text = "";
            expected_output_content.Text = "";
            
            start_stress_test_btn_content.Content = "Restart Test";

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


        private void Testcase_folder_OnLostFocus(object sender, RoutedEventArgs e){
            if (!testcase_folder.Text.Equals(default_testcase_directory))
                default_testcase_directory = testcase_folder.Text;
        }

        private void hzzgrader_label_on_click__open_repository(object sender, MouseButtonEventArgs e){
            Process.Start("https://github.com/Hzzkygcs/SDA");

        }

        private void hzzgrader_label_on_right_click__open_log_file(object sender, MouseButtonEventArgs e){
            string edit = (string)Registry.GetValue(@"HKEY_CLASSES_ROOT\SystemFileAssociations\text\shell\edit\command",
                null, null);
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


        private void open_external_window_panel_OnMouseUp__open_ExtendedEditor(object sender, MouseButtonEventArgs e){
            if (extended_editor == null || extended_editor.is_closed){
                Action restart_testcase = () =>
                {
                    if (!start_stress_test_btn.IsEnabled){
                        MessageBox.Show("Sorry, we're still running the previous check");
                        return;
                    }
                    start_stress_test_btn.RaiseEvent(new RoutedEventArgs(Button.ClickEvent));
                };
                
                Action<bool> switch_result = (bool next) =>
                {
                    if (next){
                        if (!start_stress_test_next.IsEnabled)
                            return;
                        Start_stress_test_next_OnMouseLeftButtonUp(null, null);
                    }
                    else{
                        if (!start_stress_test_previous.IsEnabled)
                            return;
                        Start_stress_test_previous_OnMouseLeftButtonUp(null, null);
                    }
                };

                extended_editor = new ExtendedEditor();
                input_content.extended_editor = extended_editor.input_editor;
                program_output_content.extended_editor = extended_editor.program_output_editor;
                expected_output_content.extended_editor = extended_editor.expected_output_editor;
                extended_editor.restart_stresstest = restart_testcase;
                extended_editor.switch_result = switch_result;
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

        private void finally_(){
            // might be called twice or more
            start_stress_test_btn.IsEnabled = true;
            native_hzzgrader_chb.IsEnabled = true;
            java_file_path.IsEnabled = true;
            pick_java_file_btn.IsEnabled = true;
            testcase_folder.IsEnabled = true;
            pick_testcase_folder.IsEnabled = true;
            pick_testcase_zip_btn.IsEnabled = true;

            extended_editor?.equalize_number_of_line();
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

            string maintain = "";

            Action<bool, string, string> on_done = async (success, url, file_version) =>
            {
                Show();
                if (!success){
                    // success doesn't guaranteed to be always true even if we have already had on_error()
                    // for now, the only case it will be false is when we go back to home/main window.
                    // it will be false but no error

                    keep_hide = false;
                    pick_testcase_zip_btn.IsEnabled = true;
                    start_stress_test_btn.IsEnabled = true;
                    return;
                }

                tc_zip_path.Text = url.Substring(MainExternalTestcaseHandler.root_url.Length);
                file_version = file_version.Trim();
                int file_version_int;

                string file_path_from_url_root =
                    url.Substring(MainExternalTestcaseHandler.root_url.Length).Replace('/', '\\');

                // different from MainExternalTestcaseHandler.testcase_local_dir, `this_...` refer to a more specific
                // path instead of MainExternalTestcaseHandler.testcase_local_dir
                string this_local_testcase_path = Path.Combine(MainExternalTestcaseHandler.testcase_local_dir,
                    Path.ChangeExtension(file_path_from_url_root, null));
                string this_local_version_file = Path.Combine(this_local_testcase_path,
                    MainExternalTestcaseHandler.testcase_local_version_filename);

                if (Int32.TryParse(file_version, out file_version_int) &&
                    File.Exists(this_local_version_file)){
                    string local_version = File.ReadAllText(this_local_version_file).Trim();
                    int local_version_int;
                    if (Int32.TryParse(local_version, out local_version_int)){
                        if (local_version_int >= file_version_int){
                            string new_tc_folder =
                                MainExternalTestcaseHandler.traverse_one_child_directory(this_local_testcase_path);
                            testcase_folder.Text = new_tc_folder;
                            information_label_set_str_content(
                                "The testcase has been downloaded. \nWe have loaded it from local storage successfully");
                            pick_testcase_zip_btn.IsEnabled = true;
                            start_stress_test_btn.IsEnabled = true;
                            write_log("testcase loaded from local successfully");
                            return;
                        }
                    }
                }


                Action<bool, string> on_testcase_downloaded = async (download_success, new_tc_folder) =>
                {
                    testcase_folder.Text = new_tc_folder;
                    pick_testcase_zip_btn.IsEnabled = true;
                    start_stress_test_btn.IsEnabled = true;
                    information_label_set_str_content("Downloaded and extracted successfully!");
                    File.WriteAllText(this_local_version_file, file_version);
                    write_log("finished downloading");
                };

                Action<bool, string> on_testcase_download_failed = async (download_success, new_tc_folder) =>
                {
                    tc_zip_path.Text = "";
                    pick_testcase_zip_btn.IsEnabled = true;
                    start_stress_test_btn.IsEnabled = true;
                    information_label_set_str_content("Download or extract failed.");
                };


                start_stress_test_btn.IsEnabled = false;
                information_label_set_str_content("getting the testcases");


                Dispatcher.BeginInvoke((Action)(
                    async () =>
                    {
                        await MainExternalTestcaseHandler.download_testcase(this,
                            on_testcase_downloaded,
                            on_testcase_download_failed);
                    }
                ));
            };


            Action<Exception> on_error = (exception) =>
            {
                bool is_connection_error = false;
                if (exception is AggregateException){
                    write_log("on_error download testcase: AggregateException");

                    AggregateException ae = (AggregateException)exception;
                    ae.Handle((handle_exception =>
                    {
                        write_log("======== ae handle ========");
                        write_log(handle_exception.StackTrace);

                        if (handle_exception is System.Net.WebException)
                            is_connection_error = true;
                        return handle_exception is System.Net.WebException;
                    }));
                    write_log("======== ae end ========");
                }


                keep_hide = false;
                pick_testcase_zip_btn.IsEnabled = true;
                start_stress_test_btn.IsEnabled = true;

                write_log("When fetching testcase list information from server, there's an error: \n" +
                          exception.Message);
                write_log(exception.StackTrace);

                if (is_connection_error){
                    MessageBox.Show("Sorry, we couldn't access " + MainExternalTestcaseHandler.root_url);
                }
                else{
                    MessageBox.Show(exception.Message);
                    MessageBox.Show(exception.StackTrace);
                }
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
            }
            else{
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

            if (e.NewSize.Height < 250){
                border_wrapper__input.Visibility = Visibility.Collapsed;
            }
            else{
                border_wrapper__input.Visibility = Visibility.Visible;
            }

            int LOWER_LIMIT = 450;
            int MAX_WIDTH_LOWER_LIMIT = 250;
            int MAX_WIDTH_UPPER_LIMIT = 500;
            int UPPER_LIMIT = 700;
            double width_percentage = (e.NewSize.Width - LOWER_LIMIT) / (UPPER_LIMIT - LOWER_LIMIT); 
            
            if (e.NewSize.Width < LOWER_LIMIT){
                brand_label.Visibility = Visibility.Collapsed;
                version_label.Visibility = Visibility.Collapsed;

                java_source_panel.MaxWidth = MAX_WIDTH_LOWER_LIMIT;
                tc_zip_panel.MaxWidth = MAX_WIDTH_LOWER_LIMIT;
                tc_folder_panel.MaxWidth = MAX_WIDTH_LOWER_LIMIT;
            }
            else{
                brand_label.Visibility = Visibility.Visible;
                version_label.Visibility = Visibility.Visible;

                if (e.NewSize.Width < UPPER_LIMIT){
                    // window's width is between 450 and 700

                    // To make it a linear smooth scaling instead of discrete scaling
                    int temp_size = MAX_WIDTH_LOWER_LIMIT + (MAX_WIDTH_UPPER_LIMIT - MAX_WIDTH_LOWER_LIMIT)
                        * ((Int32)(e.NewSize.Width) - LOWER_LIMIT) / (UPPER_LIMIT - LOWER_LIMIT);
                    java_source_panel.MaxWidth = temp_size;
                    tc_zip_panel.MaxWidth = temp_size;
                    tc_folder_panel.MaxWidth = temp_size;
                }
                else{
                    java_source_panel.MaxWidth = MAX_WIDTH_UPPER_LIMIT;
                    tc_zip_panel.MaxWidth = MAX_WIDTH_UPPER_LIMIT;
                    tc_folder_panel.MaxWidth = MAX_WIDTH_UPPER_LIMIT;
                }
            }
            start_test_button_dummy_content.MinWidth = 15 + 30 * clamp(0, width_percentage, 1);
        }
        
        public static double clamp(double a, double b, double c){
            return Math.Max(
                a, Math.Min(b, c)
            );
        }

        private double? prev_window_width = null;
        private double? prev_window_height = null;
        private bool anchor_at_top_left = false;

        private void Window_pin_cb_OnMouseUp(object sender, MouseButtonEventArgs e){
            if (e.ChangedButton == MouseButton.Right){
                if (prev_window_width == null || prev_window_height == null){
                    prev_window_height = MinHeight;
                    prev_window_width = MinWidth;
                }


                // we want the window position is anchored at top-right corner when we resize it
                if (!anchor_at_top_left)
                    Application.Current.MainWindow.Left += ActualWidth - (double)prev_window_width;

                double current_window_width = ActualWidth;
                double current_window_height = ActualHeight;

                Application.Current.MainWindow.Height = (double)prev_window_height;
                Application.Current.MainWindow.Width = (double)prev_window_width;

                prev_window_width = current_window_width;
                prev_window_height = current_window_height;
            }
            else if (e.ChangedButton == MouseButton.Middle){
                anchor_at_top_left = !anchor_at_top_left;
            }
        }


    }
}