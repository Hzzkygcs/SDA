using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Dynamic;
using System.IO;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Threading.Tasks;
using System.Windows;
using Ionic.Zip;
using Newtonsoft.Json;
using Application = System.Windows.Application;
using MessageBox = System.Windows.MessageBox;

namespace HzzGrader.updater
{
    public class Updater
    {
        public static readonly string SPECIAL_REMOVE_FILE_NAME = "remove list.txt";
        public static readonly string SPECIAL_COPY_FILE_NAME = "copy to.txt";

        public string version_check_url = "http://localhost:8000/root/update_info.json";

        public UpdateInformation update_information = new UpdateInformation(
            "", "", "");

        public static Action<string> log_updater;
        public string working_directory;

        public Updater(string working_directory = null){
            if (working_directory == null)
                working_directory = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "update");

            update_information.version = Utility.read_embedded_resource("HzzGrader.updater.current_version.txt").Trim();
            version_check_url = Utility.read_embedded_resource("HzzGrader.updater.version_check_url.txt").Trim();

            this.working_directory = working_directory;
        }

        public async Task manage_update(int timeout = 15000){
            log_updater?.Invoke("Checking for update");
            try{
                var check_update = await check_for_update();

                log_updater?.Invoke("Update information: " + check_update.Item1 + "  " + check_update.Item2);

                if (check_update.Item1 == false)
                    return;

                if (check_update.Item2.forced == false){
                    log_updater?.Invoke("asking for user's permission");

                    var result = MessageBox.Show(
                        "A new update is found. Do you want to update? (" + check_update.Item2.version
                                                                          + ") \n\n" + check_update.Item2.update_note,
                        "New Update", MessageBoxButton.YesNo);

                    log_updater?.Invoke("user's permission = " + result);

                    if (result == MessageBoxResult.No)
                        return;
                }

                UpdateInformation update_information = check_update.Item2;
                log_updater?.Invoke("downloading update materials");

                string save_path = Path.Combine(working_directory,
                    Path.GetFileName(update_information.path));
                string extract_path = Path.Combine(working_directory, "extract");
                if (await download_update(check_update.Item2, save_path, timeout)){

                    log_updater?.Invoke("installing update");
                    if (Directory.Exists(extract_path))
                        Directory.Delete(extract_path, true);

                    string current_app_files = AppDomain.CurrentDomain.BaseDirectory;


                    List<string> dirs_to_be_removed = Directory.GetDirectories(current_app_files).ToList();
                    for (int i = dirs_to_be_removed.Count - 1; i >= 0; i--){
                        // don't remove itself's directory
                        if (extract_path.ToLower().Trim().StartsWith(dirs_to_be_removed[i].ToLower().Trim())){
                            dirs_to_be_removed.RemoveAt(i);
                        }
                    }

                    string[] files = Directory.GetFiles(current_app_files);
                    foreach (string file in files){
                        dirs_to_be_removed.Add(file);
                    }

                    install_update(save_path, extract_path, dirs_to_be_removed, AppDomain.CurrentDomain.BaseDirectory);
                }
            }
            catch (Exception e){
                log_updater?.Invoke("AN ERROR WAS FOUND WHEN MANAGING THE UPDATE");
                log_updater?.Invoke("======   message   ======");
                log_updater?.Invoke(e.Message);
                log_updater?.Invoke("====== stack trace ======");
                log_updater?.Invoke(e.StackTrace);
                log_updater?.Invoke("=========================");
            }
        }

        public async Task<Tuple<bool, UpdateInformation>> check_for_update(){
            try{
                string get_url = await get_request(version_check_url);
                if (get_url == null)
                    return new Tuple<bool, UpdateInformation>(false, null);

                UpdateInformation new_update_information = JsonConvert.DeserializeObject<UpdateInformation>(get_url);

                return new Tuple<bool, UpdateInformation>(new_update_information.CompareTo(update_information) > 0,
                    new_update_information);
            }
            catch (WebException e){
                log_updater?.Invoke("error on checking updates: WebException " + e.Status + "   " + e.Message);
                return new Tuple<bool, UpdateInformation>(false, null);
            }
            catch (Exception e){
                log_updater?.Invoke("error on checking updates: Exception " + e);
                return new Tuple<bool, UpdateInformation>(false, null);
            }
        }

        public async Task<bool> download_update(UpdateInformation update_information, string save_path,
            int timeout = 15000){
            Directory.CreateDirectory(Path.GetDirectoryName(save_path));

            using (WebClient web_client = new WebClient()){
                var download = web_client.DownloadFileTaskAsync(update_information.path,
                    save_path
                );

                await Task.WhenAny(Task.Delay(timeout), download);
                var exception = download.Exception;
                bool cancelled = exception != null || !download.IsCompleted;


                return !cancelled;
            }
        }

        public async Task<bool> install_update(string zip_file_path, string extract_path,
            List<string> dirs_to_be_removed, string current_app_pos, bool notify_user = true){
            try{
                ZipFile zip_file = new ZipFile(zip_file_path);

                if (Directory.Exists(extract_path)){
                    throw new Exception();
                }
                Directory.CreateDirectory(extract_path);
                zip_file.ExtractAll(extract_path);
                remove_parent_dir_with_only_single_subdir(extract_path);

                File.WriteAllText(Path.Combine(extract_path, SPECIAL_REMOVE_FILE_NAME),
                    String.Join("\n", dirs_to_be_removed));
                File.WriteAllText(Path.Combine(extract_path, SPECIAL_COPY_FILE_NAME), current_app_pos);


                if (notify_user)
                    MessageBox.Show("The application will restart soon to install the new update");

                string current_exe_name;
                string new_exe_path;
                Process process;
                ProcessStartInfo start_info;
                {
                    // give the user a prompt about we're updating the program (4 seconds)
                    current_exe_name = Path.GetFileName(System.Reflection.Assembly.GetEntryAssembly().Location);
                    new_exe_path = Path.Combine(extract_path, current_exe_name);
                    process = new Process();
                    start_info = new ProcessStartInfo();
                    start_info.WindowStyle = ProcessWindowStyle.Normal;
                    start_info.FileName = "cmd.exe";
                    start_info.Arguments =
                        String.Format(
                            "/C  echo \"We're updating. Your program should be ready within a few seconds\" & timeout 7 & exit",
                            new_exe_path);
                    process.StartInfo = start_info;
                    process.Start();
                }


                // schedule to run the newly extracted apps within a few seconds
                current_exe_name = Path.GetFileName(System.Reflection.Assembly.GetEntryAssembly().Location);
                new_exe_path = Path.Combine(extract_path, current_exe_name);
                process = new Process();
                start_info = new ProcessStartInfo();
                start_info.WindowStyle = ProcessWindowStyle.Hidden;
                start_info.FileName = "cmd.exe";
                start_info.Arguments = String.Format("/C timeout 2 & \"{0}\" & timeout 1", new_exe_path);
                process.StartInfo = start_info;
                process.Start();

                Application.Current.Shutdown();

                return true;
            }
            catch (Exception e){
                log_updater?.Invoke("AN ERROR WAS FOUND WHEN INSTALLING THE UPDATE");
                log_updater?.Invoke("======   message   ======");
                log_updater?.Invoke(e.Message);
                log_updater?.Invoke("====== stack trace ======");
                log_updater?.Invoke(e.StackTrace);
                log_updater?.Invoke("=========================");
            }
            return false;
        }


        public static void handle_remove_and_copy_to_requirements(){

            string current_app_path = AppDomain.CurrentDomain.BaseDirectory;

            string remove_indicator = Path.Combine(current_app_path, SPECIAL_REMOVE_FILE_NAME);
            string copy_indicator = Path.Combine(current_app_path, SPECIAL_COPY_FILE_NAME);


            bool close_this_app_and_run_copied_app = false;


            if (File.Exists(remove_indicator)){
                // should be run only if we're currently working on the new version

                string[] list_of_dirs = File.ReadAllText(remove_indicator).Split('\n');
                File.Move(remove_indicator, Path.Combine(Path.GetDirectoryName(remove_indicator),
                    "old " + Path.GetFileName(remove_indicator)));


                foreach (string dir_path_or_file_path in list_of_dirs){
                    if (File.Exists(dir_path_or_file_path)){
                        // it is a file
                        File.Delete(dir_path_or_file_path);
                    }
                    else{
                        Debug.Assert(Directory.Exists(dir_path_or_file_path));
                        Directory.Delete(dir_path_or_file_path, true);
                    }
                }

            }


            if (File.Exists(copy_indicator)){
                string copy_to = File.ReadAllText(copy_indicator);
                File.Move(copy_indicator, Path.Combine(Path.GetDirectoryName(copy_indicator),
                    "old " + Path.GetFileName(copy_indicator)));

                CopyFilesRecursively(new DirectoryInfo(current_app_path), new DirectoryInfo(copy_to));

                // schedule to run the newly extracted apps (3 seconds)
                string current_exe_name = Path.GetFileName(System.Reflection.Assembly.GetEntryAssembly().Location);
                string new_exe_path = Path.Combine(copy_to, current_exe_name);
                Process process = new Process();
                ProcessStartInfo startInfo = new ProcessStartInfo();
                startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                startInfo.FileName = "cmd.exe";
                startInfo.Arguments = String.Format("/C timeout 2 & \"{0}\"", new_exe_path);


                process.StartInfo = startInfo;
                process.Start();
                Application.Current.Shutdown();
            }


        }

        public static void CopyFilesRecursively(DirectoryInfo source, DirectoryInfo target){
            // credit: https://stackoverflow.com/a/58779/7069108

            foreach (DirectoryInfo dir in source.GetDirectories())
                CopyFilesRecursively(dir, target.CreateSubdirectory(dir.Name));
            foreach (FileInfo file in source.GetFiles())
                file.CopyTo(Path.Combine(target.FullName, file.Name));
        }


        public static void remove_parent_dir_with_only_single_subdir(string source_root, string move_to = null,
            string dir_name = null){
            if (move_to == null || dir_name == null){
                move_to = Path.GetDirectoryName(source_root);
                dir_name = Path.GetFileName(source_root);
            }

            var files = Directory.GetFiles(source_root);
            var directories = Directory.GetDirectories(source_root);


            if (files.Length == 0 && directories.Length == 1){
                remove_parent_dir_with_only_single_subdir(
                    Path.Combine(source_root, Directory.GetDirectories(source_root)[0]
                    ), move_to, dir_name);
            }
            else{
                if (source_root != Path.Combine(move_to, dir_name)){
                    string temp_dir_name = Path.Combine(move_to, random_string(20));
                    Directory.Move(source_root, temp_dir_name);
                    Directory.Delete(Path.Combine(move_to, dir_name), true);
                    Directory.Move(temp_dir_name, Path.Combine(move_to, dir_name));
                }
            }
        }


        public static T[] concat_array<T>(T[] arr1, T[] arr2){
            T[] ret = new T[arr1.Length + arr2.Length];
            arr1.CopyTo(ret, 0);
            arr2.CopyTo(ret, arr1.Length);
            return ret;
        }

        public static async Task<string> get_request(string url){
            HttpWebRequest http_request = (HttpWebRequest)WebRequest.Create(url);
            http_request.AutomaticDecompression = DecompressionMethods.Deflate | DecompressionMethods.GZip;

            using (HttpWebResponse web_response = (HttpWebResponse)await http_request.GetResponseAsync()){
                if (web_response.StatusCode == HttpStatusCode.OK){
                    using (Stream stream = web_response.GetResponseStream())
                    using (StreamReader stream_reader = new StreamReader(stream)){
                        return stream_reader.ReadToEnd();
                    }
                }

                return null;
            }
        }


        private static Random random = new Random();

        public static string random_string(int length){
            const string chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
            return new string(Enumerable.Repeat(chars, length)
                .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }
}