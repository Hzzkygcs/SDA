import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;

public class dummy {
    public static Random random = new Random(Instant.now().truncatedTo(ChronoUnit.MICROS).getEpochSecond());
    
    
    public static int[] random_array_generator(){
        int n = 1 + random.nextInt(20);
        
        int[] ret = new int[n];
        
        for (int i = 0; i < n; i++) {
            ret[i] = random.nextInt(100) - 50;
        }
        
        return ret;
    }
    
    public static void MySort(int[] a) {
        for (int ii = 1; ii < a.length; ii++) {
            
            int temp = a[ii];
            int jj = ii - 1;
            
            while (jj == 0 && a[jj - 1] < temp) {
                a[jj] = a[jj + 1];
                jj--;
            }
            
            a[jj + 1] = temp;
        }
    }
    
    public static void main(String[] args) {
        int[] my_arr = random_array_generator();
        System.out.println(Arrays.toString(my_arr));
        MySort(my_arr);
        System.out.println(Arrays.toString(my_arr));
    }
    
}
