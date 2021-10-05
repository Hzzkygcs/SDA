/*using System;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace HzzGrader
{
    public static class SevenZip
    {

        public static readonly string path_for_7za = Path.Combine(AppDomain.CurrentDomain.BaseDirectory,"7zip/7za.exe");
        
        public static bool extract_file(string sourceArchive, string destination){
            try
            {
                ProcessStartInfo pro = new ProcessStartInfo();
                pro.WindowStyle = ProcessWindowStyle.Hidden;
                pro.FileName = path_for_7za;
                pro.Arguments = string.Format("x \"{0}\" -y -o\"{1}\"", sourceArchive, destination);
                Process x = Process.Start(pro);
                x.WaitForExit();
                return true;
            }
            catch (System.Exception Ex) {
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
}*/