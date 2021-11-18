/*using System;
using System.Collections.Generic;
using System.Windows.Forms;
using Newtonsoft.Json;

namespace HzzGrader.Models
{
    public class SubDirectory : TestcasePath
    {
        public List<SubDirectory> sub_dirs = new List<SubDirectory>(5);
        public List<TestcaseDescriptor> testcase_descriptors = new List<TestcaseDescriptor>(5);

        
        public void show_peek_window(Action<string> process){
            List<string> combine = new List<string>();
            foreach (var dir in sub_dirs){
                combine.Add(dir.name);
            }
            foreach (var dir in testcase_descriptors){
                combine.Add(dir.name);
            }

            List<string> traversed_path = new List<string>();
            DownloadTestcaseWindow current_window = new DownloadTestcaseWindow(combine.ToArray(), 
                null);
            
            Action<int, string> rec_fallback = null;
            
            rec_fallback = 
               async (int index, string string_current_name) => {
                    traversed_path.Add(string_current_name);
                    current_window.Hide();

                    string current_path = String.Join("/", traversed_path.ToArray());
                    
                    if (index < sub_dirs.Count){  // another subdir
                        SubDirectory new_subdir = 
                            await MainExternalTestcaseHandler.getSubDirectory(current_path);
                        
                        
                        List<string> combine_2 = new List<string>();
                        foreach (var dir in new_subdir.sub_dirs)
                            combine.Add(dir.name);
                        foreach (var dir in new_subdir.testcase_descriptors)
                            combine.Add(dir.name);
                        
                        
                        DownloadTestcaseWindow next_window = 
                            new DownloadTestcaseWindow(combine_2.ToArray(), rec_fallback);
                        next_window.Show();
                    }else{
                        MessageBox.Show(current_path);
                    }
                    current_window.Close();
            };
            
            current_window.on_selected = rec_fallback;
            current_window.Show();
        }
        
    }
}*/

