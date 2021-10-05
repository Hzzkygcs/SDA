using System;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace HzzGrader
{
    public partial class DownloadTestcaseWindow : Window
    {
        public Action<string> on_selected;
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

        public DownloadTestcaseWindow(string[] daftar_list_box, Action<string> on_selected=null){
            InitializeComponent();

            this.on_selected = on_selected;
            items = daftar_list_box;
            
        }

        private void List_box_OnSelectionChanged(object sender, SelectionChangedEventArgs e){
            on_selected?.Invoke(list_box.SelectedItems[0].ToString());
        }
    }
}