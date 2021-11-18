/***
 *
 * Created by Hzzkygcs
 * on 07 september 2021.
 *
 */


import java.io.*;
import java.nio.file.Files;
import java.util.StringTokenizer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HzzGraderTCgenerator {
    static boolean OUTPUT_PERBANDINGAN = true;
    
    public static String[] parameter = new String[0];
    public static void main(String[] args){
        stress_test();
    }
    
    
    public static void stress_test(){
        System.out.println("Stress-testing \n\n");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(32768);
        
        PrintStream old_stdout = System.out;
        InputStream old_stdin = System.in;
        
        
        long start_time;
        long end_time;
        long slowest = -1;
        long total_elapsed = 0;
        
        
        try (PrintStream print_stream = new PrintStream(baos)){
            System.setOut(print_stream);
            
            
            int i = 0;
            File in_file = new File(String.format("tc/in_%02d.txt", i));
            while (in_file.isFile()){
                File out_file = new File(String.format("tc/out_%02d.txt", i));
                
                
                Solusi.__TESTCASE__ = i;
                
                var in_fstream = new FileInputStream(in_file);
                Solusi.in = new Solusi.InputReader(in_fstream);
                Solusi.main(parameter);
                
                String code_output = baos.toString(UTF_8);
                try (FileWriter out_file_writer = new FileWriter(out_file)){
                    out_file_writer.write(code_output);
                }
                baos.reset();
                
                
                
                in_fstream.close();
                ++i;
                in_file = new File("tc/in_%02d.txt".formatted(i));
            }
            
            System.setOut(old_stdout);
            System.out.printf("DONE!  Finished generating %d testcases%n", i);
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.setOut(old_stdout);
        }
    }
    
    public static String equality_without_trailing(String a, String b){
        StringTokenizer st_a = new StringTokenizer(a.strip(), "\n", false);
        StringTokenizer st_b = new StringTokenizer(b.strip(), "\n", false);
        
        long cnt = 0;
        
        while (st_a.hasMoreTokens() && st_b.hasMoreTokens()){
            cnt += 1;
            if (! st_a.nextToken().strip().equals(st_b.nextToken().strip())){
                return String.format("at line %d", cnt);
            }
        }
        
        if (st_a.hasMoreTokens() || st_b.hasMoreTokens())
            return "The number of lines is not equal";
        
        return "";
    }
    
    
}
