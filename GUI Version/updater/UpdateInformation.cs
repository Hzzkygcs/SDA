using System;

namespace HzzGrader.updater
{
    public class UpdateInformation : IComparable<UpdateInformation>
    {
        public string version;
        public string path;
        public string update_note;
        public bool forced;

        public UpdateInformation(string version, string path, string update_note,
            bool forced = false){
            this.version = version;
            this.path = path;
            this.update_note = update_note;
            this.forced = forced;
        }


        public override string ToString(){
            return String.Format(
                "UpdateInformation(version=\"{0}\", path=\"{1}\", update_note=\"{2}\", forced=\"{3}\")",
                version,path, update_note, forced
                );
        }

        public int CompareTo(UpdateInformation other){
            return version_comparator(version, other.version);
        }
        
        
        public static int version_comparator(string version1, string version2){
            string[] temp1 = version1.Split('.');
            string[] temp2 = version2.Split('.');

            int[] split1 = new int[temp1.Length];
            int[] split2 = new int[temp2.Length];

            for (int i = 0; i < temp1.Length; i++)
                split1[i] = Int32.Parse(temp1[i]);
            for (int i = 0; i < temp2.Length; i++)
                split2[i] = Int32.Parse(temp2[i]);

            for (int i = 0; i < Math.Min(split1.Length, split2.Length); i++){
                if (temp1[i] != temp2[i])
                    return split1[i] - split2[i];
            }
            return split1.Length - split2.Length;
        }
    }
}