using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;

namespace HzzGrader
{
    public partial class DownloadTestcaseWindow : Window
    {
        public Action<int, string> on_selected;
        private bool add_back_button;
        private string[] _items;
        public string[] items{
            get{
                return _items;
            }
            set{
                _items = value;
                list_box.Items.Clear();
                for (int i = 0; i < value.Length; i++){
                    list_box.Items.Add(value[i]);
                }
            }
        }

        
        
        public DownloadTestcaseWindow(string[] daftar_list_box, Action<int, string> on_selected=null, bool add_back_button=true){
            InitializeComponent();
            
            this.on_selected = on_selected;
            if (!add_back_button)
                items = daftar_list_box;
            else{
                List<string> temp = new List<string>();
                temp.Add("←");
                temp.AddRange(daftar_list_box);
                items = temp.ToArray();
            }
        }

        private void List_box_OnSelectionChanged(object sender, SelectionChangedEventArgs e){
            on_selected?.Invoke(list_box.SelectedIndex, list_box.SelectedItems[0].ToString());
        }
    }
}