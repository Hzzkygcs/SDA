import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;




public class Solusi {
    public static PrintWriter out;
    public static InputReader in = new InputReader(System.in);
    public static int __TESTCASE__ = -1;
    
    
    
    public static void main(String[] args) throws IOException{
        out = new PrintWriter(System.out);
        short banyak_mini_testcase = in.nextShort();  // istilah dari batch
    
        for (int i = 0; i < banyak_mini_testcase; i++) {
            mini_testcase();
        }
        
        out.flush();
    }
    
    
    public static void mini_testcase(){
        int banyak_murid = in.nextInt();
        Batch batch = new Batch();
        
        for (int i = 0; i < banyak_murid; i++) {
            String kode = in.next();
            char spesialisasi = in.next().charAt(0);
            
            Spesialisasi temp;
            if (spesialisasi == 'B') temp = Spesialisasi.BAKSO;
            else if (spesialisasi == 'S') temp = Spesialisasi.SIOMAY;
            else throw new IllegalStateException("Unknown specialization");
            
            batch.add(new Murid(temp, i, kode));
        }
        
        int banyak_hari = in.nextInt();
        for (int i = 0; i < banyak_hari; i++) {
            
            int banyak_event = in.nextInt();
            for (int j = 0; j < banyak_event; j++) {
                String kode = in.next();
                short kejadian = in.nextShort();
                if (kejadian == 0)
                    batch.move_up(kode);
                else
                    batch.move_down(kode);
            }
    
            batch.print_and_update_rank();
        }
        
        String perintah = in.next();
        switch (perintah.charAt(0)){
            case 'P':  // panutan q
                panutan(batch);
                break;
            case 'K':  // kompetitif
                kompetitif(batch);
                break;
            case 'E':  // evaluasi
                evaluasi(batch);
                break;
            case 'D':  // duo or deploy
                if (perintah.equals("DUO"))
                    duo(batch);
                else if (perintah.equals("DEPLOY"))
                    deploy(batch);
                else
                    throw new IllegalStateException("unknown command");
                break;
        }
    }
    
    public static void panutan(Batch batch){
        int ranking_teratas = in.nextInt();  // Q
        int banyak_bakso = 0;
        int banyak_siomay = 0;
    
        batch.prepare_iteration();
        Murid murid = batch.next();
        for (int i = 0; i < ranking_teratas; i++) {
            if (murid.spesialisasi == Spesialisasi.BAKSO)
                banyak_bakso++;
            else {
                assert (murid.spesialisasi == Spesialisasi.SIOMAY);
                banyak_siomay++;
            }
            if (batch.hasNext())
                murid = batch.next();
            else
                assert (i == ranking_teratas - 1);
        }
    
        out.print(banyak_bakso);
        out.print(' ');
        out.print(banyak_siomay);
        out.println();
    }
    
    public static void kompetitif(Batch batch){
        batch.prepare_iteration();
        Murid murid = batch.next();
        
        int max_call = murid.banyaknya_ditunjuk;
        Murid murid_max = murid;
    
        while (batch.hasNext()){
            murid = batch.next();
            
            if (murid.banyaknya_ditunjuk > max_call){
                max_call = murid.banyaknya_ditunjuk;
                murid_max = murid;
            }
        }
    
        out.print(murid_max.kode);
        out.print(' ');
        out.print(max_call);
        out.println();
    }
    
    // stack -> DFS. Mengavulasi infix postfix
    // Queue -> BFS
    public static void evaluasi(Batch batch){
        batch.prepare_iteration();  // O(N log N)
        Murid murid = batch.next();  // O(log N)
        
        ArrayList<Murid> murid_kena_eval = new ArrayList<>(200);
        
        while (true){  // N kali  -> O(N log N)
            if (!murid.pernah_naik_rank) murid_kena_eval.add(murid);
            if (!batch.hasNext()) break;
            murid = batch.next();  // O(log N)
        }
        
        if (murid_kena_eval.size() == 0)
            out.println("TIDAK ADA");
        else {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < murid_kena_eval.size(); i++) {  // O(N)
                out.print(murid_kena_eval.get(i).kode);
                out.print(' ');
            }
            out.println();
        }
    }
    
    public static void duo(Batch batch){
        ArrayList<Murid> bakso = new ArrayList<>(100);
        ArrayList<Murid> siomay = new ArrayList<>(100);
    
        batch.prepare_iteration();
        Murid murid = batch.next();
    
        while (true){
            if (murid.spesialisasi == Spesialisasi.BAKSO)  bakso.add(murid);
            else siomay.add(murid);
            
            if (!batch.hasNext()) break;
            murid = batch.next();
        }
        
        int min = Math.min(bakso.size(), siomay.size());
        int max = Math.max(bakso.size(), siomay.size());
        
        for (int i = 0; i < min; i++) {
            out.print(bakso.get(i).kode);
            out.print(' ');
            out.println(siomay.get(i).kode);
        }
        
        
        if (bakso.size() != siomay.size()){
            ArrayList<Murid> temp_arr = siomay;
            if (bakso.size() > siomay.size())
                temp_arr = bakso;
    
            out.print("TIDAK DAPAT:");
            for (int i = min; i < max; i++) {
                out.print(' ');
                out.print(temp_arr.get(i).kode);
            }
            out.println();
        }
    }
    
    public static void deploy(Batch batch){
        int k = in.nextInt();
        Deploy.preprocess_batch(batch);  // mengubah bakso simay menjadi 0 dan 1
        out.println(Deploy.recursive(k));
    }
    
    
    
    
    static class Batch{
        static int a = 3;
        
        // batas kiri dari pseudorank. Ketika siesta menaikkan rank, nilai ini akan di-assign kepada
        // orang yang naik rank tersebut. baru nilai ini dikurangi 1
        int left_boundary = 0;
    
        // batas kanan dari pseudorank. Dipakai ketika siesta menurunkan rank orang. Nanti nilai ini ditambahkan 1
        int right_boundary = 1;
        
        public PriorityQueue<Murid> pq = new PriorityQueue<>(1007);
        public Map<String, Murid> map = new HashMap<>();
        
        public Batch(){
        }
        
        public void add(Murid murid){
            // pseudorank: more negative -> higher rank. More positive : lower rank.
            
            murid.pseudorank = right_boundary;
            murid.rank = right_boundary++;
            map.put(murid.kode, murid);
        }
        
        
        public void move_down(String kode){
            Murid temp = map.get(kode);
            temp.pseudorank = right_boundary++;
            temp.banyaknya_ditunjuk++;
        }
        
        public void move_up(String kode){
            Murid temp = map.get(kode);
            temp.pseudorank = left_boundary--;
            temp.banyaknya_ditunjuk++;
        }
        
        // mempersiapkan untuk melakukan sort berdasarkan pseudorank murid
        public void prepare_iteration(){
            pq = new PriorityQueue<>(map.values());  // tetha(N) -> O(N log N)
        }
  
        public boolean hasNext(){
            return !pq.isEmpty();
        }
        
        public Murid next(){
            return pq.poll();
        }
        
        
        public void print_and_update_rank(){
            this.prepare_iteration();
            Murid temp = this.next();
            int rank = 1;
            
            while (true){
                if (temp.rank > rank) temp.pernah_naik_rank = true;
                temp.rank = rank++;
                
                out.print(temp.kode);
                out.print(' ');
                
                if (!this.hasNext()) break;
                temp = this.next();
            }
            out.println();
        }
    
        public int size() {
            return map.size();
        }
        
        /*
        public void print_all(){
            Iterator<Murid> iter = this.first();
            Murid temp = iter.next();
            while (true){
                out.println(temp);
                if (! iter.hasNext()) break;
                temp = iter.next();
            }
        }*/
    }
    
    
    
    static enum Spesialisasi{
        BAKSO, SIOMAY
    }
    
    static class Murid implements Comparable<Murid>{
        public boolean pernah_naik_rank = false;  // apakah pernah naik rank di akhir hari
        public int rank;
        public int pseudorank;
        
        protected Spesialisasi spesialisasi;
        protected int banyaknya_ditunjuk = 0;
        protected String kode;
        
        
        public Murid(Spesialisasi spesialisasi, int nomor, String kode){
            assert (spesialisasi == Spesialisasi.BAKSO || spesialisasi == Spesialisasi.SIOMAY);
            this.spesialisasi = spesialisasi;
            this.kode = kode;
        }
        
        
        @Override
        public String toString() {
            return "Murid{" + spesialisasi + ", " + kode + '}';
        }
    
        @Override
        public int compareTo(Murid o) {
            return pseudorank - o.pseudorank;
        }
    }
}


class Deploy{
    public static final long MODULO = 1000 * 1000 * 1000 + 7;
    public static char[] array;  // '0' means Bakso, '1' means Siomay
    public static Long[][][] memo;
    
    
    
    /*
        Mengubah sederet urutan ranking bakso siomay menjadi sederet string 0 dan 1, biar lebih gampang dibayangkan.
        Sekaligus juga mempersiapkan array memo untuk nanti.
     */
    public static void preprocess_batch(Solusi.Batch batch){
        // +5 dan +2 hanya untuk jaga-jaga
        memo = new Long[batch.size() + 5][batch.size() + 5][2];
        int temp = batch.size() + 2;
        
        for (int i = 0; i < temp; i++) {
            for (int j = 0; j < temp; j++) {
                memo[i][j][0] = null;
                memo[i][j][1] = null;
            }
        }
        
        
        array = new char[batch.size()];
        int i = 0;
        
        batch.prepare_iteration();
        Solusi.Murid murid = batch.next();
        while (true){
            array[i] = murid.spesialisasi.equals(Solusi.Spesialisasi.SIOMAY)? '1' : '0';
            i++;
            if (!batch.hasNext()) break;
            murid = batch.next();
        }
    }
    
    
    public static long recursive(int banyak_kelompok){
        // start dimulai dari 1 karena indeks 0-nya udah di ambil sebagai parameter target
        return recursive(banyak_kelompok, 1, array[0]);
    }
    
    
    /*
    banyak_kelompok = target banyaknya kelompok yg mau dibentuk
    start = sekarang ada di posisi berapa. Dengan kata lain, kita mau memproses string mulai dari indeks start sampai end
    target = grup yang nanti akan terbentuk harus memiliki kode/char apa  (target char apa)
     */
    public static long recursive(int banyak_kelompok, int start, char target){
    
        if (start == array.length - 1)
            
            // kelompok-1 == 0 -> kelompok == 1
            // karena: ibaratnya 0010|1001|0110
            // karena kali ada 2 tangkai, membentuk 3 kelompok
            //
            // sstr[start] == target -> kalo kode awal pada grup skrg ini sama dengan
            // kode akhir grup skrg ini
            
            return (array[start] == target && banyak_kelompok -1 == 0)? 1:0;
        else if (start >= array.length || banyak_kelompok < 0)  // base case tapi invalid
            return 0;
        
        if (memo[banyak_kelompok][start][target - '0'] != null)  // cek ada di memo atau enggak
            return memo[banyak_kelompok][start][target - '0'];
        
        long ret = 0;
        if (array[start] == target)
            // ibaratkan kita memberikan pembatas grup (|) pada posisi saat ini. grup baru yang terbentuk
            // setelah pembatas baru ini harus diakhiri dengan kode array elemen pertama pada grup tersebut
            // (alias array[start+1])
            ret += recursive(banyak_kelompok - 1, start + 2, array[start+1]);

        // lanjut 'memperluas' grup yang ada saat ini, TANPA memberikan pembatas grup (|)
        ret += recursive(banyak_kelompok, start + 1, target);
        ret %= MODULO;
        memo[banyak_kelompok][start][target - '0'] = ret;
        return ret;
    }
}


class InputReader implements Closeable{
//    public static string something = "aku";
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