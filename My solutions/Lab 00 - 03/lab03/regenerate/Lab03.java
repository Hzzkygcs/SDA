package lab03.regenerate;

import java.io.*;
import java.util.*;
import static java.lang.System.out;



/**
 *
 * PUJI TUHAN YESUS KRISTUS
 * AC sept 21, 2021. 02:26.
 * Dibantu Ridjky juga -> ngirim kode -> aku bisa regenereate ribuan testcase2 kecil -> solve problem
 * Ridjky Tegar Perkasa
 *
 */


enum Session {SIANG, MALAM, BOLOS}

class Data{
    int target_work;
    int i_th_day;
    Session sesi;
    
    public Data(int target_work, int i_th_day, Session sesi){
        this.target_work = target_work;
        this.i_th_day = i_th_day;
        this.sesi = sesi;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data)) return false;
        Data data = (Data) o;
        return target_work == data.target_work && i_th_day == data.i_th_day && sesi == data.sesi;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(target_work, i_th_day, sesi);
    }
}




public class Lab03 {
    
    
    public static InputReader in = new InputReader(System.in);
    public static int __TESTCASE__ = 0;
    public static long INF = 999999999999999L;  // 15 digit
    public static long INF_BOUND = 199999999999999L;  // 14 digit angka 9
    
    
    //    public static HashMap<Data, Long> memo = new HashMap<>(1000000);
    public static ArrayList<ArrayList<ArrayList<Long>>> memo = new ArrayList<>(1005);
    
    
    public static int n;
    public static ArrayList<Long> siang = new ArrayList<>(1005);
    public static ArrayList<Long> malam = new ArrayList<>(1005);
    public static ArrayList<Long> bolos = new ArrayList<>(1005);
    public static ArrayList<Long> bonus = new ArrayList<>(1005);
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Long>[] get_session = new ArrayList[]{siang, malam, bolos};
    
    
    static {
        for (int i = 0; i < 1005; i++) {
            bolos.add(0L);
        }
    }
    
    
    public static long recursive(int target_work, int i_th_day, Session session){
//        Data argument = new Data(target_work, i_th_day, session);
        {
            long temp = memo.get(target_work).get(i_th_day).get(session.ordinal());
            if (temp >= -INF_BOUND)
                return temp;
        }
        
        if (target_work == 0)
            return 0;
        
        if (i_th_day == 0 || i_th_day < target_work)
            return -INF;  // invalid because we haven't achieve the target_work
        
        
        long max_ = -INF;
        Session track = null;
        {
            long temp =  recursive(target_work, i_th_day - 1, Session.BOLOS);  // tidak_masuk
            if (temp > max_) {
                max_ = temp;
                track = Session.BOLOS;
            }
        }
        
        if (session.equals(Session.SIANG) || session.equals(Session.BOLOS)){
            // kalau sekarang siang, kemarinnya hanya boleh malam (atau bolos, sudah dihandle di atas)
            long temp = recursive(target_work - 1, i_th_day - 1, Session.MALAM);
            temp += get_session[Session.MALAM.ordinal()].get(i_th_day - 1);
            if (temp > max_) {
                max_ = temp;
                track = Session.SIANG;
            }
        }
        
        if (session.equals(Session.MALAM) || session.equals(Session.BOLOS)){
            // kalau sekarang malam, kemarinnya hanya boleh siang atau bolos
            long temp = recursive(target_work - 1, i_th_day - 1, Session.SIANG);
            temp += get_session[Session.SIANG.ordinal()].get(i_th_day - 1);
            if (temp > max_) {
                max_ = temp;
                track = Session.MALAM;
            }
        }

//        out.printf("%3d %3d %s  -->  %d %s %n", target_work, i_th_day, session, max_, track);
        memo.get(target_work).get(i_th_day).set(session.ordinal(), max_);
        return max_;
    }
    
    
    
    
    public static long max_total_profit_under_work_constraint(int expected_target_work, int day_rightmost_limit){
        // mencari jumlah keuntungan jika kita harus bekerja sesuai target work yang diinginkan
        // dan kita hanya bisa bekerja pada hari < day_rightmost_limit (EXCLUSIVE)
        
        // we can assume this day (at day day_rightmost_limit) is BOLOS because we have
        // added a zero column at the end of siang, malam, bolos, and bonus. And also
        // we can (and must) assume this day is BOLOS because day_rightmost_limit is exclusive
        long max_non_bonus_profit = recursive(expected_target_work, day_rightmost_limit, Session.BOLOS);
        long bonus_ = bonus.get(expected_target_work);

//        out.printf("%02d: %d %d %n", expected_target_work, max_non_bonus_profit, max_non_bonus_profit + bonus_);
//        out.println();
//        out.println();
        
        return max_non_bonus_profit + bonus_;
    }
    
    
    
    private static void solve() {
        long max_profit = -INF;
        long banyak_kerja = -1;
        for (int target_work = 0; target_work < n + 1; target_work++) {
            // we want to find the max profit from 0 to n-th day INCLUSIVE,
            // but batas_akhir_hari is exclusive. Therefore we must set the
            // right-most boundary as n+1 so that n will also included.
            long max_profit_local = max_total_profit_under_work_constraint(target_work, n + 1);

//            out.printf("%d %d %n", target_work, max_profit_local);
            
            if (max_profit_local > max_profit) {
                max_profit = max_profit_local;
                banyak_kerja = target_work;
            }
        }
        
        out.printf("%d %d%n", max_profit, banyak_kerja);
        
        return;
    }
    
    
    
    
    
    
    
    public static void main(String[] args) throws IOException{
        n = in.nextInt();
        
        memo.clear();
        for (int i = 0; i < n+5; i++) {  // harusnya +2. +5 buat jaga-jaga saja
            memo.add(new ArrayList<>(n+5));
            
            for (int j = 0; j < n+5; j++) {
                memo.get(i).add(new ArrayList<>(4));
                
                for (int k = 0; k < 3; k++) {
                    memo.get(i).get(j).add(-INF);
                }
            }
        }
        
        
        siang.clear(); siang.add(-INF);
        malam.clear(); malam.add(-INF);
        bonus.clear(); bonus.add(-INF);
        
        for (int i = 0; i < n; i++)
            siang.add(in.nextLong());
        for (int i = 0; i < n; i++)
            malam.add(in.nextLong());
        for (int i = 0; i < n; i++)
            bonus.add(in.nextLong());
        
        // must be done because function `max_total_profit_under_work_constraint` has argument
        // day_rightmost_limit which is exclusive. Therefore siang[n+1], malam[n+1], and bonus[n+1]
        // access is possible.
        siang.add(0L);
        malam.add(0L);
        bonus.add(0L);

//        out.println(Session.BOLOS);
//        out.println(Session.BOLOS.ordinal());
//        out.println(Session.SIANG);
//        out.println(Session.SIANG.ordinal());
//        out.println(Session.MALAM);
//        out.println(Session.MALAM.ordinal());
        
        solve();
    }
    
    
    public static class InputReader implements Closeable{
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
