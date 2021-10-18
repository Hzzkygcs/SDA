using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Runtime.CompilerServices;
using System.Threading.Tasks;
using System.Windows;


namespace HzzGrader
{
    public static class MainExternalTestcaseHandler
    {
        public static Dictionary<string, string[]> cache_dictionary = new Dictionary<string, string[]>();
        
        public static readonly string root_url = "https://raw.githubusercontent.com/Hzzkygcs/SDA/master/";
        // public static readonly string root_url = "http://localhost:8000/root/";
        
        
        public static readonly string dir_list_file_name = ".tc_list";



        private static DownloadTestcaseWindow download_testcase_window;
        public static readonly string testcase_local_dir = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "testcases");
        public static readonly string testcase_local_version_filename = "version";


        public static async Task initial_caching(){  // automatically caching the main root and it's first and last children
            try{
                string[] daftar = await get_lines(Path.Combine(root_url, dir_list_file_name));
                
                // daftar[0] is the back button. no need to be cached. so daftar[1] is the first
                await get_lines(root_url + daftar[1] + "/" + dir_list_file_name);
                await get_lines(root_url + daftar[daftar.Length - 1] + "/" + dir_list_file_name);
            }catch(Exception e){MainWindow.write_log("initial caching failed: " + e.Message + "\n\n" + e.StackTrace);}
        }
        
        public static async Task start_window(Action<bool, string, string> on_done, Action<Exception> on_error){
            string path;
            string[] subdirs;
            
            try{
                path = Path.Combine(root_url, dir_list_file_name);
                subdirs = await get_lines(path);
            } catch (Exception e){
                on_error?.Invoke(e);
                return;
            }

            Action<int, string> on_select = async (index, selected_dir_name) =>
            {
                if (index == -1){  // back to previous window
                    on_done?.Invoke(false, "", "");
                    download_testcase_window.close_window();
                    return;
                }
                
                
                download_testcase_window.close_window();
                string current_path = root_url + selected_dir_name;
                await on_sub_dir(current_path, on_done, on_error);
            
            };

            download_testcase_window = new DownloadTestcaseWindow(subdirs, on_select, true,
                DownloadTestcaseWindow.WINDOW_POS_AS_MAIN_WINDOW);
            download_testcase_window.Show();
            download_testcase_window.Activate();
        }

        public static async Task on_sub_dir(string total_path, Action<bool, string, string> on_done, Action<Exception> on_error){
            string[] files_raw;
            try{        
                files_raw = await get_lines(total_path + "/" + dir_list_file_name);
            } catch (Exception e){
                on_error?.Invoke(e);
                return;
            }

            // each testcase has the following format inside file `.testcases`:
            // Name of the file::version number
            // where version number is just a regular integer without any dot. name of file will be stripped from 
            // excess spaces. 
            
            // We wan't to split those two
            string[] file_names = new string[files_raw.Length];
            string[] file_versions = new string[files_raw.Length];

            for (int i = 0; i < files_raw.Length; i++){
                string[] split = files_raw[i].Split(new[]{"::"}, StringSplitOptions.None);
                file_names[i] = split[0].Trim();
                if (split.Length > 1)
                    file_versions[i] = split[1];
                else{
                    file_versions[i] = "";
                }
            }


            Action<int, string> on_select = async (index, selected_dir_name) =>
            {
                download_testcase_window.close_window();
                
                if (index == -1){  // back to previous window
                    download_testcase_window.close_window();
                    await start_window(on_done, on_error);
                    return;
                }
                
                try{
                    string current_path = total_path + "/" + selected_dir_name;
                    await on_choose_testcase(current_path, file_versions[index], on_done, on_error);
                } catch (Exception e){
                    on_error?.Invoke(e);
                    return;
                }
            };

            download_testcase_window = new DownloadTestcaseWindow(file_names, on_select, true, DownloadTestcaseWindow.WINDOW_POS_AS_MAIN_WINDOW);
            download_testcase_window.Show();
            download_testcase_window.Activate();
        }

        public static async Task on_choose_testcase(string total_path, string version, Action<bool, string, string> on_done, 
            Action<Exception> on_error){
            on_done?.Invoke(true, total_path, version);
        }


        public static async Task<string[]> get_lines(string path){
            string URL = Path.Combine(path);
            if (cache_dictionary.ContainsKey(URL))
                return cache_dictionary[URL];
            cache_dictionary[URL] = (await send_GET_request(URL)).Trim().Split('\n');
            return cache_dictionary[URL];
        }

        
        public static async Task<string> send_GET_request(string url, int timeout=-1){
            if (timeout < 0)
                timeout = 5000;
            
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.AutomaticDecompression = DecompressionMethods.GZip;

            var req = request.GetResponseAsync();
            var result = await Task.WhenAny(req, Task.Delay(timeout));
            if (req != result)
                throw new TimeoutException("request timeout");
            
            using (HttpWebResponse response = (HttpWebResponse) req.Result)
            using (Stream stream = response.GetResponseStream())
            using (StreamReader stream_reader = new StreamReader(stream))
            {
                return stream_reader.ReadToEnd();
            }
        }
        
        
        public static async Task download_testcase(MainWindow main_window, 
            Action<bool, string> on_success, Action<bool, string> on_failure, string url=null, int timeout=15000){
            if (url == null)
                url = root_url + main_window.tc_zip_path.Text;

            string path;
            path = Path.Combine(testcase_local_dir, 
                Path.GetDirectoryName(main_window.tc_zip_path.Text));
            Directory.CreateDirectory(path);


            path = Path.Combine(path, Path.GetFileName(main_window.tc_zip_path.Text));
            using (WebClient web_client = new WebClient()){
                if (File.Exists(path))
                    File.Delete(path);
                
                var download = web_client.DownloadFileTaskAsync(url, 
                    path
                );

                main_window.information_label_set_str_content("downloading the testcase");

                Task.Run(async () =>
                {
                    await Task.WhenAny(Task.Delay(timeout), download);
                    var exception = download.Exception;
                    bool cancelled = exception != null || !download.IsCompleted;

                    if (!cancelled){
                        string zip_path = path;
                        string extract_path =
                            Path.Combine(Path.GetDirectoryName(path), Path.GetFileNameWithoutExtension(path));

                        main_window.Dispatcher.Invoke(() =>
                        {
                            main_window.information_label_set_str_content("extracting the testcase");
                        });
                    
                        if (Directory.Exists(extract_path))
                            Directory.Delete(extract_path, true);
                        if (!SevenZip.extract_file(zip_path, extract_path)){
                            main_window.Dispatcher.Invoke(() => on_failure?.Invoke(false, path));
                            return;
                        }
                        File.Delete(zip_path);

                        string testcase_path = traverse_one_child_directory(extract_path);
                        main_window.Dispatcher.Invoke(() => on_success?.Invoke(true, testcase_path));
                    }else
                        main_window.Dispatcher.Invoke(() => on_failure?.Invoke(false, path));
                });
            }
        }

        public static string traverse_one_child_directory(string testcase_path){
            // traverse the directory root until it has no subdir or it has more than one subdirs.
            // after finishes, it returns the new path, where the path's number of subdir is guaranteed != 1 
            string new_path = testcase_path;
            while (Directory.GetDirectories(new_path).Length == 1){
                new_path = Path.Combine(new_path, Directory.GetDirectories(new_path)[0]);
            }
            return new_path;
        }
        
    }
}