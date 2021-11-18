package lab03;

import java.io.*;
import java.util.*;

import static java.lang.System.out;


class Data implements Cloneable{
    public long profit_with_bonus;
    public long profit_no_bonus;
    public long work_amm;
    
    public Data(){
        this(0,0,0);
    }
    
    public Data(long profit_with_bonus, long profit_no_bonus, long work_amm){
        this.profit_with_bonus = profit_with_bonus;
        this.profit_no_bonus = profit_no_bonus;
        this.work_amm = work_amm;
    }
    
    public Data copy(){
        return new Data(profit_with_bonus, profit_no_bonus, work_amm);
    }
    
    
    public static int compare_by_profit_with_bonus(Data o1, Data o2){
        return Long.signum(o1.profit_with_bonus - o2.profit_with_bonus);
    };
    
    public static int compare_by_profit_no_bonus(Data o1, Data o2){
        if (o1.profit_no_bonus != o2.profit_no_bonus)
            return Long.signum(o1.profit_no_bonus - o2.profit_no_bonus);
        return Long.signum(o2.work_amm - o1.work_amm);
    };
}



public class Lab03_old {
    
    
    public static int find_max(Data[] data_arr, Comparator<Data> comparator){
        Data maximum = data_arr[0];
        int max_index = 0;
        
        for (int i = 1; i < data_arr.length; i++) {
            if (comparator.compare(data_arr[i], data_arr[max_index]) > 0)
                max_index = i;
        }
        
        return max_index;
    }
    
    
    private static void solve() {
        ArrayList<ArrayList<Data>> arr = new ArrayList<>(5);
    
        for (int i = 0; i < 3; i++) {
            arr.add(new ArrayList<>(n+20));
            arr.get(i).add(new Data());
    
            for (int j = 0; j < n + 1; j++) {
                arr.get(i).add(new Data(-1,-1,-1));
            }
        }
    
        for (int x = 1; x <= n+1; x++) {
            for (int session = 0; session < 3; session++) {
    
                Data[] temp_arr;
                
                if (session == 2){  // bolos
                    temp_arr = new Data[]{
                            arr.get(2).get(x - 1),  // arr.get(2) must be in zero pos
                            arr.get(0).get(x - 1),
                            arr.get(1).get(x - 1)
                    };
                }else{  // session 0 -> siang. session 1 -> malam
                    temp_arr = new Data[]{
                            arr.get(2).get(x - 1),  // arr.get(2) must be in zero pos
                            arr.get(1 - session).get(x - 1)
                    };
                }
    
    
                long max_profit_w_bonus = temp_arr[
                        find_max(temp_arr, Data::compare_by_profit_with_bonus)
                        ].profit_with_bonus;
                int max_prof_n_bonus_pos = find_max(temp_arr, Data::compare_by_profit_no_bonus);
                
                long curr_profit_no_bonus = temp_arr[max_prof_n_bonus_pos].profit_no_bonus;
                
                if (session == 0)
                    curr_profit_no_bonus += siang.get(x);
                else if (session == 1)
                    curr_profit_no_bonus += malam.get(x);
                
                
                long curr_work_amm = temp_arr[max_prof_n_bonus_pos].work_amm;
                if (max_prof_n_bonus_pos != 0)  // if it's not from the arr.get(2)
                    curr_work_amm += 1;
                
                if (max_profit_w_bonus < curr_profit_no_bonus + bonus.get(x)){
                    max_profit_w_bonus = curr_profit_no_bonus + bonus.get(x);
                    curr_work_amm = temp_arr[max_prof_n_bonus_pos].work_amm + 1;
                }else if (max_profit_w_bonus == curr_profit_no_bonus + bonus.get(x)){
                    if (curr_work_amm > temp_arr[max_prof_n_bonus_pos].work_amm + 1)
                        curr_work_amm = temp_arr[max_prof_n_bonus_pos].work_amm + 1;
                }
    
    
                arr.get(session).set(x, new Data(
                                             max_profit_w_bonus,
                                             curr_profit_no_bonus,
                                             curr_work_amm
                                     )
                );
            }
        }
        
        Data ret = arr.get(2).get(n+1);
    
        out.printf("%d %d%n", ret.profit_with_bonus, ret.work_amm);
    }
    
    
    
    
    public static InputReader in = new InputReader(System.in);
    public static int __TESTCASE__ = 0;
    
    static int n;
    static ArrayList<Long> siang;
    static ArrayList<Long> malam;
    static ArrayList<Long> bonus;
    
    
    
    public static void main(String[] args) throws IOException{
        n = in.nextInt();
        siang = new ArrayList<>(1000);
        malam = new ArrayList<>(1000);
        bonus = new ArrayList<>(1000);
    
        siang.add(0L);
        malam.add(0L);
        bonus.add(0L);
    
        for (int i = 0; i < n; i++)
            siang.add(in.nextLong());
        for (int i = 0; i < n; i++)
            malam.add(in.nextLong());
        for (int i = 0; i < n; i++)
            bonus.add(in.nextLong());
    
        siang.add(0L);
        malam.add(0L);
        bonus.add(0L);
        
         solve();
    }
    
    
}






class InputReader implements Closeable{
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