﻿/***
 *
 * Template created by Hzzkygcs
 * on 07 september 2021.
 *
 * Automatically generated by HzzGrader GUI version
 *
 */



import java.io.*;
import java.nio.file.Files;
import java.util.StringTokenizer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


import static java.nio.charset.StandardCharsets.UTF_8;

public class HzzGrader {
    static boolean OUTPUT_PERBANDINGAN = true;
    
    public static String[] parameter = new String[0];
    public static void main(String[] args){
        if (args.length > 0){
            assert args.length >= 2;
            stress_test(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }else{
            stress_test();
        }
    }
    
    
    public static void stress_test(){
        stress_test(0, 1);
    }
    public static void stress_test(int initial_tc_number, int step){
        ByteArrayOutputStream baos = new ByteArrayOutputStream(32768);
        
        PrintStream old_stdout = System.out;
        InputStream old_stdin = System.in;
    
    
        final AtomicLong start_time = new AtomicLong(-100);
        final AtomicLong end_time = new AtomicLong(-1);
        long slowest = -1;
        long total_elapsed = 0;
    
    
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Object> future = null;
        Callable<Object> task;
        
        final long time_limit_ms = {{TIME_LIMIT_MS}}L; 
        
        try {
            int i = initial_tc_number;
            File in_file = new File("{{TARGET_DIRECTORY}}" + String.format("/in_%02d.txt", i));
            while (in_file.isFile()){
                File out_file = new File("{{TARGET_DIRECTORY}}" + String.format("/out_%02d.txt", i));
                assert (out_file.isFile());
    
                
               old_stdout.println(i);
                
                try (var in_fstream = new FileInputStream(in_file)){
                    try (PrintStream print_stream = new PrintStream(baos)){
                        task = new Callable<Object>() {
                            public Object call() throws Exception {
                                System.setOut(print_stream);
                                System.setIn(in_fstream);
                                
                                
                                start_time.set(System.nanoTime());
                                {{NAMA_CLASS}}.main(parameter);
                                end_time.set(System.nanoTime());
                                return null;
                            }
                        };
                        future = executor.submit(task);
    
                        try{
                            future.get(time_limit_ms, TimeUnit.MILLISECONDS);
                        }catch (TimeoutException e){
                            System.setOut(old_stdout);
                            System.out.println("{{INFORMATION_DELIMITER_TOKEN}}");
                            System.out.println("error");
                            System.out.println(in_file.getName());
                            
                            System.out.println("{{CURRENT_TESTCASE_DELIMITER_TOKEN}}");
                            System.out.println(i);
    
                            System.out.println("{{INPUT_DELIMITER_TOKEN}}");
                            System.out.println(Files.readString(in_file.getAbsoluteFile().toPath()));
                            System.out.println("{{PROGRAM_OUTPUT_DELIMITER_TOKEN}}");
                            System.out.println("\nTime Limit Exceeded  > " + time_limit_ms + " ms\n");
                            System.out.println("{{EXPECTED_OUTPUT_TOKEN}}");
                            System.out.println(Files.readString(out_file.getAbsoluteFile().toPath()));
    
                            System.out.println("{{END_DELIMITER_TOKEN}}");
                            System.exit(0);
                            return;
                        }catch (InterruptedException ignored){
                        }
                    } catch (Exception e){
                        System.setOut(old_stdout);
                        System.out.println("{{INFORMATION_DELIMITER_TOKEN}}");
                        System.out.println("error");
                        System.out.println(in_file.getName());
                        
                        System.out.println("{{CURRENT_TESTCASE_DELIMITER_TOKEN}}");
                        System.out.println(i);
                        System.out.println("{{INPUT_DELIMITER_TOKEN}}");
                        System.out.println(Files.readString(in_file.getAbsoluteFile().toPath()));
                        
                        System.out.println("{{PROGRAM_OUTPUT_DELIMITER_TOKEN}}");
                        StringWriter string_writer = new StringWriter();
                        PrintWriter print_writer = new PrintWriter(string_writer);
                        e.printStackTrace(print_writer);
                        System.out.println(string_writer);
                        
                        System.out.println("{{EXPECTED_OUTPUT_TOKEN}}");
                        System.out.println(Files.readString(out_file.getAbsoluteFile().toPath()));
                        
                        System.out.println("{{END_DELIMITER_TOKEN}}"); 
                        System.exit(0);
                        return;
                    }finally{
                        if (future != null)
                            future.cancel(true);
                    }
                    
                    long elapsed = end_time.get() - start_time.get();
                    total_elapsed += elapsed;
                    if (elapsed > slowest)
                        slowest = elapsed;
                    
                    String code_output = baos.toString(UTF_8);
                    String expected_output = Files.readString(out_file.getAbsoluteFile().toPath());
                    baos.reset();
                    
                    String difference_line = equality_without_trailing(code_output, expected_output);
                    if (difference_line.length() != 0){
                        System.setOut(old_stdout);
                        
                        System.out.println();
                        System.out.println("{{INFORMATION_DELIMITER_TOKEN}}");
                        System.out.println(in_file.getName());
                        System.out.println(difference_line);
                        System.out.println("{{CURRENT_TESTCASE_DELIMITER_TOKEN}}");
                        System.out.println(i);
                        if (OUTPUT_PERBANDINGAN){
                            System.out.println("{{INPUT_DELIMITER_TOKEN}}");
                            System.out.println(Files.readString(in_file.getAbsoluteFile().toPath()));
                            System.out.println("{{PROGRAM_OUTPUT_DELIMITER_TOKEN}}");
                            System.out.println(code_output);
                            System.out.println("{{EXPECTED_OUTPUT_TOKEN}}");
                            System.out.println(expected_output);
                            System.out.println("{{END_DELIMITER_TOKEN}}");
                        }
                        System.exit(0);
                        return;
                    }
                  
                    i += step;
                    in_file = new File("{{TARGET_DIRECTORY}}" + String.format("/in_%02d.txt", i));
                }
            }
            
            System.setOut(old_stdout);
            
            System.out.println("dummy");
            System.out.println("dummy");
            System.out.println("{{INFORMATION_DELIMITER_TOKEN}}");
            System.out.printf("DONE! There's no wrong answer.  Finished checking %d testcases%n", (i - initial_tc_number)/step);
            
            System.out.println("{{CURRENT_TESTCASE_DELIMITER_TOKEN}}");
            System.out.println(i);
            System.out.println("{{INPUT_DELIMITER_TOKEN}}");
            System.out.println("{{PROGRAM_OUTPUT_DELIMITER_TOKEN}}");
            System.out.println("{{EXPECTED_OUTPUT_TOKEN}}");
            
            long slowest_ms = slowest / 1000_000;
            long total_elapsed_ms = total_elapsed / 1000_000;
            System.out.printf("Slowest running time %d ms %n", slowest_ms);
            System.out.printf("Running time in total %d ms %n", total_elapsed_ms);
            
            System.out.println("{{END_DELIMITER_TOKEN}}");
            System.out.println("{{TESTCASE_SUCCESS}}");
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.setOut(old_stdout);
            System.setIn(old_stdin);
            System.exit(0);
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
