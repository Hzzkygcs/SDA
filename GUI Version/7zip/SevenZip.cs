using System;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace HzzGrader
{
    public static class SevenZip
    {
        public static readonly string
            path_for_7za = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "7zip/7za.exe");

        public static bool extract_file(string sourceArchive, string destination){
            try{
                ProcessStartInfo pro = new ProcessStartInfo();
                pro.WindowStyle = ProcessWindowStyle.Hidden;
                pro.FileName = path_for_7za;
                pro.CreateNoWindow = true;
                pro.UseShellExecute = false;
                pro.RedirectStandardError = true;
                pro.RedirectStandardOutput = true;

                pro.Arguments = string.Format("x \"{0}\" -y -o\"{1}\"", sourceArchive, destination);
                Process x = Process.Start(pro);

                string output = x.StandardOutput.ReadToEnd();
                string error = x.StandardError.ReadToEnd();

                MainWindow.write_log("===== argument =====");
                MainWindow.write_log(pro.Arguments);
                MainWindow.write_log("===== zip output =====");
                MainWindow.write_log(output);

                if (error.Length > 0){
                    MainWindow.write_log("===== zip error =====");
                    MainWindow.write_log(error);

                    MessageBox.Show("7zip error: \n\n" + error);
                    MessageBox.Show("7zip output:\n\n" + output);
                    MainWindow.write_log("===== zip end =====");
                    return false;
                }

                MainWindow.write_log("===== zip end =====");

                x.WaitForExit();
                return true;
            }
            catch (System.Exception Ex){
                MainWindow.write_log("===== zip error exception =====");
                MainWindow.write_log(Ex.Message);
                MainWindow.write_log("");
                MainWindow.write_log(Ex.StackTrace);
                MainWindow.write_log("===== zip end =====");

                MessageBox.Show("7zip exception: \n\n" + Ex.Message + "\n\n\n" + Ex.StackTrace);

                return false;
            }
        }


        public static void CreateZip(string sourceName, string targetArchive){
            throw new NotImplementedException();
        }


        public static void tes(){
            CreateZip(@"C:\Users\Hzz\Documents\GitHub\SDA\GUI Version\bin\Release", "archive.7z");
        }
    }
}