package others.ricky;

import java.io.*;
import java.util.*;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.System.out;


public class Lab03 {
    
    public static InputReader in;
    
    public static long __TESTCASE__;
    private static int galian;
    
    static private long findMaxBerlian(ArrayList<Integer> S, ArrayList<Integer> M, ArrayList<Integer> B) {
        long[][][] arr = new long[S.size() + 1][S.size() + 1][3];
        
        long max = 0;
        
        for (int i = 1; i < S.size() + 1; i++) {
            for (int j = 1; j < i + 1; j++) {
                // Isi array untuk siang
                arr[i][j][0] = j == 1 ? max(arr[i - 1][j - 1][1], arr[i - 1][j - 1][2]) + S.get(i - 1) + B.get(j - 1)
                        : max(arr[i - 1][j - 1][1], arr[i - 1][j - 1][2]) + S.get(i - 1) + B.get(j - 1) - B.get(j - 2);
                
                // Isi array untuk malam
                arr[i][j][1] = j == 1 ? max(arr[i - 1][j - 1][0], arr[i - 1][j - 1][2]) + M.get(i - 1) + B.get(j - 1)
                        : max(arr[i - 1][j - 1][0], arr[i - 1][j - 1][2]) + M.get(i - 1) + B.get(j - 1) - B.get(j - 2);
                
                // Isi array untuk bolos
                
                arr[i][j][2] = i == j ? 0 : max(max(arr[i - 1][j][0], arr[i - 1][j][1]), arr[i - 1][j][2]);
                
                long currentLargest = max(max(arr[i][j][0], arr[i][j][1]), arr[i][j][2]);
                
                // Updating max value and ammountOfDiggings
                if (currentLargest > max) {
                    max = currentLargest;
                    galian = j;
                }
                
            }
        }
        return max;
    }
    
    
    static private int findBanyakGalian(ArrayList<Integer> S, ArrayList<Integer> M, ArrayList<Integer> B) {
        return galian;
    }
    
    public static void main(String args[]) throws IOException {
        ArrayList<Integer> S = new ArrayList<>();
        ArrayList<Integer> M = new ArrayList<>();
        ArrayList<Integer> B = new ArrayList<>();
        
        int N = in.nextInt();
        
        for(int i=0;i<N;i++) {
            int tmp = in.nextInt();
            S.add(tmp);
        }
        
        for(int i=0;i<N;i++) {
            int tmp = in.nextInt();
            M.add(tmp);
        }
        
        for(int i=0;i<N;i++) {
            int tmp = in.nextInt();
            B.add(tmp);
        }
        
        long jawabanBerlian = findMaxBerlian(S,M,B);
        long jawabanGalian = findBanyakGalian(S,M,B);
        
        out.print(jawabanBerlian + " " + jawabanGalian);
        
        out.flush();
    }
    
    
    
    public static class InputReader implements Closeable {
        public BufferedReader bufferedReader;
        public StringTokenizer stringTokenizer;
        
        public InputReader(InputStream inputStream){
            this(inputStream, 32768);
        }
        
        public InputReader(InputStream inputStream, int buffer_size){
            // 32 MB buffer size
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream), buffer_size);
            stringTokenizer = null;
        }
        
        public String next(){
            try{
                while (stringTokenizer == null || !stringTokenizer.hasMoreTokens()){
                    stringTokenizer = new StringTokenizer(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return stringTokenizer.nextToken();
        }
        
        public int nextInt(){ return Integer.parseInt(next()); }
        public long nextLong(){ return Long.parseLong(next()); }
        public short nextShort(){ return Short.parseShort(next()); }
        public String nextLine() throws Exception { assert !stringTokenizer.hasMoreTokens(); return bufferedReader.readLine();}
        public boolean nextBoolean(){ return Boolean.parseBoolean(next());}
        
        @Override
        public void close() throws IOException {
            bufferedReader.close();
        }
    }
    
}


