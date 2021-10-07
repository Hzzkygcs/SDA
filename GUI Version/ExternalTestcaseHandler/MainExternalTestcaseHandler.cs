using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Threading;
using HzzGrader.Models;
using Newtonsoft.Json;

namespace HzzGrader
{
    public static class MainExternalTestcaseHandler
    {
        public static Dictionary<string, string[]> cache_dictionary = new Dictionary<string, string[]>();
        public static readonly string root_url = "https://raw.githubusercontent.com/Hzzkygcs/SDA/master/";
        public static readonly string dir_list_file_name = ".testcases";



        private static DownloadTestcaseWindow download_testcase_window;


        public static async Task initial_caching(){
            try{
                string[] daftar = await get_lines(Path.Combine(root_url, dir_list_file_name));
                await get_lines(root_url + daftar[0]);
                await get_lines(root_url + daftar[daftar.Length - 1]);
            }catch(Exception e){}
        }
        
        public static async Task start_window(Action<bool, string> on_done, Action<Exception> on_error){
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
                if (index == 0){  // back to previous window
                    on_done?.Invoke(false, "");
                    download_testcase_window.Close();
                    return;
                }
                
                
                download_testcase_window.Close();
                string current_path = root_url + selected_dir_name;
                await on_sub_dir(current_path, on_done, on_error);
            
            };

            download_testcase_window = new DownloadTestcaseWindow(subdirs, on_select);
            download_testcase_window.Show();
            download_testcase_window.Activate();
        }

        public static async Task on_sub_dir(string total_path, Action<bool, string> on_done, Action<Exception> on_error){
            string[] files;
            try{        
                files = await get_lines(total_path + "/" + dir_list_file_name);
            } catch (Exception e){
                on_error?.Invoke(e);
                return;
            }

            Action<int, string> on_select = async (index, selected_dir_name) =>
            {
                download_testcase_window.Close();
                
                if (index == 0){  // back to previous window
                    download_testcase_window.Close();
                    await start_window(on_done, on_error);
                    return;
                }
                
                try{
                    string current_path = total_path + "/" + selected_dir_name;
                    await on_choose_testcase(current_path, on_done, on_error);
                } catch (Exception e){
                    on_error?.Invoke(e);
                    return;
                }
            };

            download_testcase_window = new DownloadTestcaseWindow(files, on_select);
            download_testcase_window.Show();
            download_testcase_window.Activate();
        }

        public static async Task on_choose_testcase(string total_path, Action<bool, string> on_done, Action<Exception> on_error){
            on_done?.Invoke(true, total_path);
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
        
        
        public static async Task<bool> download_testcase(MainWindow main_window, 
            Action<bool, string> on_success, Action<bool, string> on_failure, string url=null, int timeout=15000){
            if (url == null)
                url = root_url + main_window.tc_zip_path.Text;

            string path;
            path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "testcases", 
                Path.GetDirectoryName(main_window.tc_zip_path.Text));
            Directory.CreateDirectory(path);


            path = Path.Combine(path, Path.GetFileName(main_window.tc_zip_path.Text));
            using (WebClient web_client = new WebClient()){
                if (File.Exists(path))
                    File.Delete(path);
                
                var download = web_client.DownloadFileTaskAsync(url, 
                    path
                );
                
                await Task.WhenAny(Task.Delay(timeout), download);
                var exception = download.Exception;
                bool cancelled = exception != null || !download.IsCompleted;
                
                
                if (!cancelled){
                    string zip_path = path;
                    string extract_path =
                        Path.Combine(Path.GetDirectoryName(path), Path.GetFileNameWithoutExtension(path));

                    if (Directory.Exists(extract_path))
                        Directory.Delete(extract_path, true);
                    // Directory.CreateDirectory(extract_path);
                    if (!SevenZip.extract_file(zip_path, extract_path)){
                        on_failure?.Invoke(false, path);
                        return false;
                    }

                        string testcase_path = extract_path;
                    while (Directory.GetDirectories(testcase_path).Length == 1){
                        testcase_path = Path.Combine(testcase_path, Directory.GetDirectories(testcase_path)[0]);
                    }
                    
                    on_success?.Invoke(true, testcase_path);
                }else
                    on_failure?.Invoke(false, path);
                
                return !cancelled;
            }
            
        }
    }
}