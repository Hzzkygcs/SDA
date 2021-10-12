using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace HzzGrader
{
    public partial class DownloadTestcaseWindow
    {
        public Action<int, string> on_selected;
        private bool _add_back_button;
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

        

        public static readonly Tuple<double, double> WINDOW_POS_AS_MAIN_WINDOW = new Tuple<double, double>(-999, -999);
        public DownloadTestcaseWindow(string[] daftar_list_box,
            Action<int, string> on_selected = null, bool add_back_button = true, Tuple<double, double> window_pos = null){

            InitializeComponent();
            _add_back_button = add_back_button;

            this.on_selected = on_selected;
            if (!add_back_button)
                items = daftar_list_box;
            else{
                List<string> temp = new List<string>();
                temp.Add("←");
                temp.AddRange(daftar_list_box);
                items = temp.ToArray();
            }

            if (window_pos != null){
                if (window_pos == WINDOW_POS_AS_MAIN_WINDOW){
                    window_pos = new Tuple<double, double>(Application.Current.MainWindow.Left,
                        Application.Current.MainWindow.Top);
                }
                
                Left = window_pos.Item1;
                Top = window_pos.Item2;
            }
        }

        private void List_box_OnSelectionChanged(object sender, SelectionChangedEventArgs e){
            Dispatcher.Invoke(
                async () =>
                {
                    while (Mouse.LeftButton == MouseButtonState.Pressed)
                        await Task.Delay(40);
                    await Task.Delay(30);
                    Dispatcher.Invoke(
                        () => on_selected?.Invoke(list_box.SelectedIndex - (_add_back_button ? 1 : 0),
                                                  list_box.SelectedItems[0].ToString())
                        );
                    
                }
            );
        }


        public void close_window(){
            is_closed_by_red_x_button = false;
            Close();
        }

        public bool is_closed_by_red_x_button = true;
        private void DownloadTestcaseWindow_OnClosed(object sender, EventArgs e){
            if (is_closed_by_red_x_button)
                Application.Current.Shutdown();
        }
    }
}