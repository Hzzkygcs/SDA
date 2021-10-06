using System;
using System.ComponentModel;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;

namespace HzzGrader.Windows
{
    public partial class ExtendedEditor : Window
    {
        public bool is_closed = false;
        public Action window_ready;

        public ExtendedEditor(){
            InitializeComponent();
            window_ready?.Invoke();
        }

        private void left_splitter_doubleClick(object sender, MouseButtonEventArgs e){
            main_grid.ColumnDefinitions[0].Width = new GridLength(1, GridUnitType.Star);
            main_grid.ColumnDefinitions[2].Width = new GridLength(1, GridUnitType.Star);
            main_grid.ColumnDefinitions[4].Width = new GridLength(0, GridUnitType.Star);
        }
        
        private void right_splitter_doubleClick(object sender, MouseButtonEventArgs e){
            main_grid.ColumnDefinitions[0].Width = new GridLength(0, GridUnitType.Star);
            main_grid.ColumnDefinitions[2].Width = new GridLength(1, GridUnitType.Star);
            main_grid.ColumnDefinitions[4].Width = new GridLength(1, GridUnitType.Star);
        }

        private void Expected_output_editor_OnMouseWheel(object sender, MouseWheelEventArgs e){
            double current_pos = expected_output_editor.VerticalOffset;
            Dispatcher.Invoke(async () =>
            {
                int cnt = 60;
                while (current_pos == expected_output_editor.VerticalOffset && cnt > 0){
                    await Task.Delay(20);
                    cnt--;
                }
                program_output_editor.ScrollToVerticalOffset(expected_output_editor.VerticalOffset);
            });
        }

        private void Program_output_editor_OnMouseWheel(object sender, MouseWheelEventArgs e){
            double current_pos = program_output_editor.VerticalOffset;
            Dispatcher.Invoke(async () =>
            {
                int cnt = 60;
                while (current_pos == program_output_editor.VerticalOffset && cnt > 0){
                    await Task.Delay(20);
                    cnt--;
                }
                expected_output_editor.ScrollToVerticalOffset(program_output_editor.VerticalOffset);
            });
        }

        private void ExtendedEditor_OnClosing(object sender, CancelEventArgs e){
            is_closed = true;
            base.OnClosed(e);
        }

        private void Control_OnMouseDoubleClick(object sender, MouseButtonEventArgs e){
            string status_title = " (pinned)";
            
            Topmost = !Topmost;
            if (Title.EndsWith(status_title))
                Title = Title.Substring(0, Title.Length - status_title.Length);
            else{
                Title += status_title;
            }
        }
        
    }
}