using System;
using System.ComponentModel;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Forms;
using System.Windows.Input;
using System.Windows.Media;
using ICSharpCode.AvalonEdit;
using ICSharpCode.AvalonEdit.Rendering;

namespace HzzGrader.Windows
{
    public partial class ExtendedEditor : Window
    {
        public bool is_closed = false;
        public Action window_ready;
        public Action restart_stresstest;

        public ExtendedEditor(){
            InitializeComponent();
            window_ready?.Invoke();
        }

        private void left_splitter_doubleClick(object sender, MouseButtonEventArgs e){
            GridLength prev1 = main_grid.ColumnDefinitions[0].Width;
            GridLength prev2 = main_grid.ColumnDefinitions[2].Width;
            GridLength prev3 = main_grid.ColumnDefinitions[4].Width;

            main_grid.ColumnDefinitions[0].Width = new GridLength(1, GridUnitType.Star);
            main_grid.ColumnDefinitions[2].Width = new GridLength(1, GridUnitType.Star);
            main_grid.ColumnDefinitions[4].Width = new GridLength(0, GridUnitType.Star);

            if (prev1 == main_grid.ColumnDefinitions[0].Width
                && prev2 == main_grid.ColumnDefinitions[2].Width
                && prev3 == main_grid.ColumnDefinitions[4].Width){
                // back to the normal 3-panel mode
                main_grid.ColumnDefinitions[4].Width = new GridLength(1, GridUnitType.Star);
            }
        }

        private void right_splitter_doubleClick(object sender, MouseButtonEventArgs e){
            GridLength prev1 = main_grid.ColumnDefinitions[0].Width;
            GridLength prev2 = main_grid.ColumnDefinitions[2].Width;
            GridLength prev3 = main_grid.ColumnDefinitions[4].Width;

            main_grid.ColumnDefinitions[0].Width = new GridLength(0, GridUnitType.Star);
            main_grid.ColumnDefinitions[2].Width = new GridLength(1, GridUnitType.Star);
            main_grid.ColumnDefinitions[4].Width = new GridLength(1, GridUnitType.Star);

            if (prev1 == main_grid.ColumnDefinitions[0].Width
                && prev2 == main_grid.ColumnDefinitions[2].Width
                && prev3 == main_grid.ColumnDefinitions[4].Width){
                // back to the normal 3-panel mode
                main_grid.ColumnDefinitions[0].Width = new GridLength(1, GridUnitType.Star);
            }
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


        public async Task equalize_number_of_line(TextEditor a = null, TextEditor b = null){
            if (a == null)
                a = program_output_editor;
            if (b == null)
                b = expected_output_editor;

            StringBuilder stringBuilder = new StringBuilder(100);
            if (a.LineCount < b.LineCount){
                for (int i = a.LineCount; i < b.LineCount; i++){
                    stringBuilder.Append('\n');
                }
                a.Text += stringBuilder.ToString();
            }
            else if (b.LineCount < a.LineCount){
                for (int i = b.LineCount; i < a.LineCount; i++){
                    stringBuilder.Append('\n');
                }
                b.Text += stringBuilder.ToString();
            }
        }


        public async Task update_differences_colouring(int start = 0, int length = -1,
            bool clear_previous_color = true){
            if (length < 0)
                length = Math.Max(program_output_editor.LineCount, expected_output_editor.LineCount);

            if (clear_previous_color){
                program_output_editor.TextArea.TextView.LineTransformers.Clear();
                expected_output_editor.TextArea.TextView.LineTransformers.Clear();
            }

            var program_output_lines = program_output_editor.Text.Split('\n');
            var expected_output_lines = expected_output_editor.Text.Split('\n');

            int i;
            for (i = start; i < start + length; i++){
                if (i >= program_output_lines.Length || i >= expected_output_lines.Length)
                    break;
                if (program_output_lines[i].Trim() != expected_output_lines[i].Trim()){
                    // + 1 karena LineColorizer mulai dari index 1
                    program_output_editor.TextArea.TextView.LineTransformers.Add(new LineColorizer(i + 1));
                    expected_output_editor.TextArea.TextView.LineTransformers.Add(new LineColorizer(i + 1));
                }
            }

            while (i < start + length){
                if (i < program_output_lines.Length){
                    program_output_editor.TextArea.TextView.LineTransformers.Add(new LineColorizer(i + 1));
                }
                else if (i < expected_output_lines.Length){
                    expected_output_editor.TextArea.TextView.LineTransformers.Add(new LineColorizer(i + 1));
                }
                i++;
            }

        }


        class LineColorizer : DocumentColorizingTransformer
        {
            // from: https://stackoverflow.com/a/29010731/7069108
            int lineNumber;

            public LineColorizer(int lineNumber){
                this.lineNumber = lineNumber;
            }

            protected override void ColorizeLine(ICSharpCode.AvalonEdit.Document.DocumentLine line){
                if (!line.IsDeleted && line.LineNumber == lineNumber){
                    ChangeLinePart(line.Offset, line.EndOffset, ApplyChanges);
                }
            }

            void ApplyChanges(VisualLineElement element){
                // This is where you do anything with the line
                element.TextRunProperties.SetForegroundBrush(Brushes.Red);
            }
        }

        private void Control_OnMouseUp(object sender, MouseButtonEventArgs e){
            if (e.ChangedButton == MouseButton.Right){
                restart_stresstest?.Invoke();
            }
        }
    }
}