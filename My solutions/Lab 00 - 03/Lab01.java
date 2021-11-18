import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Lab01 {
    public static Scanner input = new Scanner(System.in);
    public static int __TESTCASE__ = 0;
    public static final long INF = 99999999999999L;
    
    public static void solve() {
        long terminal_cnt, lokasi_cnt;
        
        terminal_cnt = input.nextLong();
        lokasi_cnt = input.nextLong();
        
        ArrayList<Long> arr = new ArrayList<Long>((int)lokasi_cnt + 5);
        input.nextLine();
        
        
        {
            long temp = 0;
            
            for (long i = 0; i < lokasi_cnt; i++) {
                String str = input.next();
                if (str.equals("*")){
                    arr.add(temp);
                    temp = 0;
                }else
                    temp += Long.parseLong(str);
            }
        }
        
        
        ArrayList<Long> prefix_sum = new ArrayList<>(arr.size()+5);
        
        
        {
            long sum = 0;
            for (int i = 0; i < arr.size(); i++) {
                sum += arr.get(i);
                assert (prefix_sum.size() == i);
                prefix_sum.add(sum);
            }
        }
        
        
        
        long result = -INF;
        
        {
            long min_val = INF;
            int min_indx = -1;
    
    
            for (int i = 0; i < prefix_sum.size(); i++) {
                if (min_val > prefix_sum.get(i)){
                    long temp = prefix_sum.get(i) - min_val;
                    
                    min_val = prefix_sum.get(i);
                    min_indx = i;
    
                    if (temp > result)
                        result = temp;
                }else{
                    long temp = prefix_sum.get(i) - min_val;
                    if (temp > result)
                        result = temp;
                }
            }
            
            
        }
        
        
        /*{
    
            long min_val = INF;
            int min_indx = -1;
    
            for (int i = 0; i < prefix_sum.size(); i++){
                if (prefix_sum.get(i) < min_val){
                    min_val = prefix_sum.get(i);
                    min_indx = i;
                }
            }
    
    
    
            long max_val = -INF;
            int max_indx = -1;
    
            for (int i = 0; i < prefix_sum.size(); i++) {
                if (prefix_sum.get(i) > max_val){
                    max_val = prefix_sum.get(i);
                    max_indx = i;
                }
            }
            if (max_indx != -1) {
                result = max_val - min_val;
            }else
                result = min_val;
        }
        */
        /*
        long result2 = -2;
    
        {
    
    
            long max_val = -INF;
            int max_indx = -1;
    
            for (int i = 0; i < prefix_sum.size(); i++) {
                if (prefix_sum.get(i) > max_val){
                    max_val = prefix_sum.get(i);
                    max_indx = i;
                }
            }
            
            
            long min_val = INF;
            int min_indx = -1;
        
            for (int i = 0; i < max_indx; i++){
                if (prefix_sum.get(i) < min_val){
                    min_val = prefix_sum.get(i);
                    min_indx = i;
                }
            }
        
        
        
            if (max_indx != -1)
                result2 = max_val - min_val;
            else
                result2 = min_val;
        }
        
        if (result2 > result)
            result = result2;*/
        
        System.out.println(result);
    }
    
    
    public static void main(String[] args){
        if (args.length > 0  && args[0].equals("DEBUG"))
            stress_test();
        else
            solve();
    }
    
    public static void stress_test(){}
    
    /*
    public static void stress_test(){
        System.out.println("Stress-testing \n\n");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PrintStream old_stdout = System.out;
        InputStream old_stdin = System.in;
        
        
        try (PrintStream print_stream = new PrintStream(baos)){
            System.setOut(print_stream);
            
            
            int i = 0;
            File in_file = new File("tc/in_%02d.txt".formatted(i));
            while (in_file.isFile()){
                File out_file = new File("tc/out_%02d.txt".formatted(i));
                assert (out_file.isFile());
                __TESTCASE__ = i;
                
                var in_fstream = new FileInputStream(in_file);
                input = new Scanner(in_fstream);
                
                solve();
                
                String code_output = baos.toString(UTF_8);
                String expected_output = Files.readString(out_file.getAbsoluteFile().toPath());
                baos.reset();
                
                if (! code_output.equals(expected_output)){
                    System.setOut(old_stdout);
                    
                    System.out.println(in_file.getName());
                    System.out.println("============================");
                    System.out.println(Files.readString(in_file.getAbsoluteFile().toPath()));
                    System.out.println("============================");
                    System.out.println(code_output);
                    System.out.println("============================");
                    System.out.println(expected_output);
                    System.out.println("============================");
                    break;
                }
    
    
                in_fstream.close();
                ++i;
                in_file = new File("tc/in_%02d.txt".formatted(i));
            }
    
            System.setOut(old_stdout);
            System.out.println("DONE!  Finished checking %d testcases".formatted(i));
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.setOut(old_stdout);
        }
    }
    */
    
}
