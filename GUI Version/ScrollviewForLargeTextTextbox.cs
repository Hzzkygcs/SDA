
using System;
using System.Collections.Generic;
using System.Net.Mime;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using HzzGrader.Windows;
using ICSharpCode.AvalonEdit;
using TextBox = System.Windows.Controls.TextBox;


namespace HzzGrader
{
    public class TextboxLargeContent
    {
        public List<string> lines;
        public TextBox text_box;
        private TextEditor _extended_editor;
        private int _line_pos;  // current line position
        private int line_count = 20;  // current line position

        public int line_pos{
            get{
                return _line_pos;
            }
            set{
                int max_line = lines.Count;
                _line_pos = value;
                _line_pos = (_line_pos < 0) ? 0 : _line_pos;
                _line_pos = (_line_pos > max_line) ? max_line : _line_pos;
                update_textbox_line(_line_pos, line_count);
            }
        }
        public string Text{
            get{
                return String.Join("\n", lines);
            }

            set{
                if (_extended_editor != null){
                    _extended_editor.Text = value;
                }
                
                lines = new List<string>(value.Split('\n'));
                line_pos = line_pos;  // update the content of the TextBox
            }
        }
        public TextEditor extended_editor{
            get{
                return _extended_editor;
            }
            set{
                _extended_editor = value;
                _extended_editor.Text = Text;
                Window.GetWindow(_extended_editor).Closed += (sender, args) =>
                {
                    this._extended_editor = null;
                };
            }
        }
        
        
        public TextboxLargeContent(TextBox text_box, TextEditor extended_editor=null, int line_count=50){
            _line_pos = 0;
            lines = new List<string>(10);
            this.text_box = text_box;
            this._extended_editor = extended_editor;
            this.line_count = line_count;
        }
        
        public void update_textbox_line(int line_index, int line_count = 50){
            TextBox text_box = this.text_box;
            List<string> lines = this.lines;
            
            StringBuilder stringBuilder = new StringBuilder(line_count * 50);

            for (int i = line_index; i < line_index + line_count && i < lines.Count; i++){
                stringBuilder.Append(lines[i]);
                stringBuilder.Append("\n");
            }

            text_box.Text = stringBuilder.ToString();
        }
        
        
    }


    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow
    {
        public TextboxLargeContent input_content;
        public TextboxLargeContent program_output_content;
        public TextboxLargeContent expected_output_content;

        public void initialize_large_textboxes(){
            input_content = new TextboxLargeContent(input);
            program_output_content = new TextboxLargeContent(program_output);
            expected_output_content = new TextboxLargeContent(expected_output);
        }


        public static int scroll_line_distance = 4;
        private void on_scroll__large_input_textbox(object event_sender, MouseWheelEventArgs e){
            TextBox textbox = event_sender as TextBox;
            if (textbox == null)
                return;

            TextboxLargeContent content = get_textbox_large_content(textbox);

            if (content == null)
                throw new Exception();

            int prev_pos = content.line_pos;
            content.line_pos += (e.Delta < 0) ? scroll_line_distance : -scroll_line_distance;

            write_log("scrolled: prev="+prev_pos + "  current=" + content.line_pos);
            
            if (prev_pos != content.line_pos)
                e.Handled = true;
            else{
                e.Handled = false;
            }
        }

        public TextboxLargeContent get_textbox_large_content(TextBox textbox){
            if (textbox == null)
                return null;
            
            if (textbox.Equals(input))
                return input_content;
            
            if (textbox.Equals(program_output))
                return program_output_content;
            
            if (textbox.Equals(expected_output))
                return expected_output_content;
            return null;
        }

        private void on_click__large_input_textbox(object sender, MouseButtonEventArgs e){
            if (e.ButtonState == MouseButtonState.Pressed){
                TextBox textbox = sender as TextBox;
                if (textbox == null) return;
                // textbox.Select(0, textbox.Text.Length);
                textbox.SelectAll();

                Dispatcher.Invoke(async () =>
                {
                    await Task.Delay(150);
                    textbox.SelectAll();
                });
            }
        }



        private void on_dblclick__large_input_textbox(object sender, MouseButtonEventArgs e){
            TextBox textbox = sender as TextBox;
            TextboxLargeContent content = get_textbox_large_content(textbox);
            if (content == null) return;
            Clipboard.SetText(content.Text);
        }

        private void on_key_down__large_input_textbox(object sender, KeyEventArgs e){
            TextBox textbox = sender as TextBox;
            if (textbox == null) return;

            if ((Keyboard.Modifiers & ModifierKeys.Control) == ModifierKeys.Control){
                if (e.Key == Key.C){
                    TextboxLargeContent content = get_textbox_large_content(textbox);
                    Clipboard.SetText(content.Text);
                    e.Handled = true;
                }
            }

            if (e.Key == Key.End){
                TextboxLargeContent content = get_textbox_large_content(textbox);
                content.line_pos = content.lines.Count - 5;
            }
            
            if (e.Key == Key.Home){
                TextboxLargeContent content = get_textbox_large_content(textbox);
                content.line_pos = 0;
            }
        }

        
        public static T get_child_of_type<T>(DependencyObject dependency_object) where T: DependencyObject{
            if (dependency_object == null) return null;

            for (int i = 0; i < VisualTreeHelper.GetChildrenCount(dependency_object); i++)
            {
                var child = VisualTreeHelper.GetChild(dependency_object, i);

                var result = (child as T) ?? get_child_of_type<T>(child);
                if (result != null) return result;
            }
            return null;
        }
        
        public static List<T> get_all_child_of_type<T>(DependencyObject dependency_object) where T: DependencyObject{
            if (dependency_object == null)
                return null;
            List<T> ret = new List<T>(5);
            
            for (int i = 0; i < VisualTreeHelper.GetChildrenCount(dependency_object); i++){
                var child = VisualTreeHelper.GetChild(dependency_object, i);
                var child_as_T = child as T;
                if (child_as_T != null) 
                    ret.Add(child_as_T);
            }
            
            return ret;
        }
    }

    
    
}