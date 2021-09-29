using System;
using System.Diagnostics;
using System.Runtime.Remoting.Contexts;


namespace scratch
{
    internal class Program
    {
        public static void Main(string[] args){
            Process new_process = initialize_cmd_process(new Process());
            
            new_process.OutputDataReceived += output_handler;
            new_process.Start();
            new_process.BeginOutputReadLine();
            string input;

            while (!(input = Console.ReadLine()).Equals("EXIT")){
                new_process.StandardInput.WriteLine(input);
            }
            
            Console.WriteLine("DONE!");
        }


        public static void output_handler(object sendingProcess, DataReceivedEventArgs outLine){
            Console.Write("cmd.exe responded:  ");
            Console.WriteLine("\"{0}\"", outLine.Data);
        }
        
        public static Process initialize_cmd_process(Process process) {
            process.StartInfo.FileName = "cmd.exe";
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.CreateNoWindow = true;
            process.StartInfo.RedirectStandardOutput = true;
            process.StartInfo.RedirectStandardError = true;
            process.StartInfo.RedirectStandardInput = true;
            return process;
        }

    }
}