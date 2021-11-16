using System.IO;
using System.Reflection;

namespace HzzGrader
{
    public partial class Utility
    {
        public static string read_embedded_resource(string resource_path){
            /*
             * 
             * jangan lupa set file resource_path jadi bertipe EmbededFile
             * resource_path merupakan path target file terhadap solution root, dimana slash diganti
             * jadi titik, kecuali root slash (dihapus). Contoh:
             * JavaExecute/MyEmbeddedFiles/embedded.txt menjadi JavaExecute.MyEmbeddedFiles.embedded.txt
             * 
             */
            var assembly = Assembly.GetExecutingAssembly();
            using (var temp = assembly.GetManifestResourceStream(resource_path))
            using (StreamReader stream_reader = new StreamReader(temp)){
                return stream_reader.ReadToEnd();
            }
        }
    }
}