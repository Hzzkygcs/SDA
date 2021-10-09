import java.util.Scanner;

public class Lab00 {
    
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            int n;
            long mod;
            
            n = scanner.nextInt();
            mod = scanner.nextLong();
            
            long multiplication = 1;
    
            for (int i = 0; i < n; i++) {
                long temp = scanner.nextLong();
                temp %= mod;
                multiplication *= temp;
                multiplication %= mod;
            }
    
            System.out.println(multiplication);
        }
    }
}
