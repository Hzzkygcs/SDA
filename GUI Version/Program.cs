using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace HzzGrader{
    

    class JavaExecute
    {
        public Action<JavaExecute> on_unblocked;
        public Process process;
        public string directory;
        private bool _blocked;
        public StringBuilder program_output = new StringBuilder(0x_FFFFF);  // ~2 mega bytes buffer
        public StringBuilder program_error = new StringBuilder(400);
        private string start_token = null;
        private string termination_token = null;

        public static readonly string START_CMD = "echo. & echo {0} &";
        public static readonly string COMMAND = "java \"{0}\" < \"{1}\"";
        public static readonly string TERMINATION_CMD = "& echo. & echo {0}";
        
        public JavaExecute(Process cmd_process, string directory){
            process = cmd_process;
            process.ErrorDataReceived += error_handler;
            process.OutputDataReceived += output_handler;
            process.StartInfo.WorkingDirectory = directory;
            this.directory = directory;
        }

        public void start(){
            process.Start();
            process.BeginOutputReadLine();
            process.BeginErrorReadLine();
            program_output.Clear();
            program_error.Clear();
        }

        public void execute_external_stdin(string java_class_name, string stdin_file_path){
            if (termination_token != null) throw new SynchronizationLockException();
            
            // indicate from which line (and up to what line) the output listener should listen to.
            // Every cmd's output between the starting and termination line will be captured
            // and stored to the string builder
            start_token = random_string(16);
            termination_token = random_string(16);
            
            string cmd = String.Format(START_CMD, start_token)
                         + String.Format(COMMAND, java_class_name, stdin_file_path)
                         + String.Format(TERMINATION_CMD, termination_token);
            process.StandardInput.WriteLine(cmd);
        }

        public bool is_blocked(){
            return termination_token != null;
        }
        
        public void output_handler(object sendingProcess, DataReceivedEventArgs data){
            if (start_token != null){
                if (!data.Data.TrimEnd().Equals(start_token))
                    return;
                start_token = null;
                return;
            }

            // Debug.Assert(termination_token != null);
            // if (termination_token == null) Console.WriteLine("\"{0}\"", data.Data);
            
            if (data.Data.TrimEnd().Equals(termination_token)){
                termination_token = null;
                Task.Run(async () => on_unblocked(this));
                return;
            }
            
            program_output.AppendLine(data.Data);
        }

        public void error_handler(object sendingProcess, DataReceivedEventArgs data){
            program_error.Append(data.Data);
        }

        public Tuple<string, string> flush(){
            Tuple<string, string> ret = new Tuple<string, string>(program_output.ToString(), program_error.ToString());
            program_output.Clear();
            program_error.Clear();
            return ret;
        }


        private static Random random = new Random();
        public static string random_string(int length)
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            return new string(Enumerable.Repeat(chars, length)
                .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }

}