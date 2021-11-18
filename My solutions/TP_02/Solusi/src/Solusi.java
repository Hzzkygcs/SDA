import javax.xml.validation.Validator;
import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Solusi {
    public static long __TESTCASE__ = -1;
    public static PrintWriter out = new PrintWriter(System.out);
//    public static PrintStream out = System.out;  // debug
    public static InputReader in = new InputReader(System.in);
    
    public static HashMap<String, Pulau> pulau_hm = new HashMap<>(100*1000 + 7);
    public static HashMap<String, Kuil> kuil_hm = new HashMap<>(300*1000 + 7);
    
    public static Dataran get_raiden_shogun() {
        return _raiden_shogun;
    }
    
    public static void set_raiden_shogun(Dataran raiden_shogun) {
        _raiden_shogun = raiden_shogun;
    }
    
    public static Dataran _raiden_shogun;
    
    public static void debug_print_semua_kuil(){
        Iterator<String> it = kuil_hm.keySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            String nama_kuil = it.next();
            System.out.printf("%s: \n", nama_kuil);
            debug_print_kuil(nama_kuil);
        }
    }
    
    public static void debug_print_kuil(String nama){
        Kuil kuil = kuil_hm.get(nama);
        System.out.println(kuil);
        System.out.println(kuil.tree);
        System.out.println();
    }
    
    public static void debug_print_isi_semua_pulau(){
        pulau_hm.forEach((key, value)->{
            System.out.print(value.nama + ":  ");
            value.forEach((kuil_bundel) -> {
                System.out.print(kuil_bundel.data.nama);
                System.out.print(", ");
            });
            System.out.println();
        });
    }
    
    
    public static void main(String[] args) {
        pulau_hm = new HashMap<>(100*1000 + 7);
        kuil_hm = new HashMap<>(300*1000 + 7);
        
        pulau_hm.clear();
        kuil_hm.clear();
        
        input_pulau_dan_kuil();
        input_informasi_raiden();
        input_query_peperangan();
        out.flush();
    }
    
    
    public static ArrayList<Long> to_be_sorted_arr = new ArrayList<>(400*1000 + 5);
    public static ArrayList<Long> temporary_arr = new ArrayList<>(400*1000 + 5);
    public static ArrayList<Long> temporary2_arr = new ArrayList<>(400*1000 + 5);
    
    public static void input_pulau_dan_kuil(){
        long banyak_pulau = in.nextLong();
        for (int i = 0; i < banyak_pulau; i++) {
            String nama_pulau = in.next();
            long banyak_dataran = in.nextLong();
            to_be_sorted_arr.clear();
            temporary2_arr.clear();
            temporary_arr.clear();
        
            Pulau pulau = new Pulau(nama_pulau);
            Kuil kuil = new Kuil(nama_pulau, pulau, null);
    
            long ketinggian;
            
            for (int j = 0; j < banyak_dataran; j++) {
                ketinggian = in.nextLong();
                to_be_sorted_arr.add(ketinggian);
                temporary2_arr.add(ketinggian);
            }
            sortings.merge_sort(to_be_sorted_arr, temporary_arr);
            sortings.splitter(to_be_sorted_arr, (index, height) -> {
                kuil.reserve_node(height);
            });
            for (int j = 0; j < temporary2_arr.size(); j++) {
                kuil.push_back(temporary2_arr.get(j));
            }
            
            
            pulau.add(kuil);
            kuil.__pulau_senarai_bundel = (DataBundel<Kuil>) pulau.tail.prev;
            
            pulau_hm.put(nama_pulau, pulau);
            kuil_hm.put(nama_pulau, kuil);
        }
//        debug_print_semua_kuil();
    }
    
    public static void input_informasi_raiden(){
        String pulau_raiden = in.next();
        long posisi_raiden = in.nextLong() - 1;  // mengubah one-based index menjadi zero based-index
        Kuil kuil_raiden = pulau_hm.get(pulau_raiden).get_first();
        set_raiden_shogun(kuil_raiden.get(posisi_raiden).data);
    }
    
    public static void input_query_peperangan(){
        long query_number = in.nextLong();
        for (int i = 0; i < query_number; i++) {
            String query = in.next();
        
            switch (query.charAt(0)){
                case 'P':
                    pisah();
                    break;
                case 'U':
                    unifikasi();
                    break;
                case 'R':
                    rise();
                    break;
                case 'Q':
                    quake();
                    break;
                case 'C':
                    crumble();
                    break;
                case 'S':
                    if (query.charAt(1) == 'T'){
                        stabilize();
                    }else{
                        sweeping();
                    }
                    break;
                case 'G':
                    gerak();
                    break;
                case 'T':
                    if (query.charAt(2) == 'B'){
                        tebas();
                    }else{
                        teleportasi();
                    }
                    break;
            }
        }
    }
    
    
    public static void pisah(){
        String nama_kuil = in.next();
        Kuil kuil = kuil_hm.get(nama_kuil);
        Pulau pulau_asal = kuil.__pulau;
        
        Pulau pulau_kiri = new Pulau(pulau_asal.nama);
        Pulau pulau_kanan = new Pulau(nama_kuil);
        
        Pulau pulau_to_be_inserted = pulau_kiri;
        
        AbstractBundel<Kuil> temp_abs = pulau_asal.head.next;
        while (temp_abs instanceof DataBundel){
            DataBundel<Kuil> temp = (DataBundel<Kuil>) temp_abs;
            if (temp.data == kuil) {
                // dijamin pisah bukan kuil paling kiri
//                DataBundel<Kuil> kuil_sebelumnya = ((DataBundel<Kuil>) temp.prev).data;
//                kuil_sebelumnya.
                
                pulau_to_be_inserted = pulau_kanan;
            }
            temp.data.__pulau_senarai_bundel = pulau_to_be_inserted.push_back(temp.data);
            temp.data.__pulau = pulau_to_be_inserted;
            pulau_to_be_inserted.banyak_dataran += temp.data.size();
            temp_abs = temp_abs.next;
        }
        
        pulau_hm.put(pulau_kiri.nama, pulau_kiri);
        pulau_hm.put(pulau_kanan.nama, pulau_kanan);
        
        out.print(pulau_kiri.banyak_dataran);
        out.print(' ');
        out.println(pulau_kanan.banyak_dataran);
    }
    
    public static void unifikasi(){
        int total_dataran_pulau_baru = 0;
        
        String nama_pulau_kiri = in.next();
        String nama_pulau_kanan = in.next();
        Pulau pulau_kiri = pulau_hm.get(nama_pulau_kiri);
        Pulau pulau_kanan = pulau_hm.get(nama_pulau_kanan);
        long banyak_kuil_yang_seharusnya = pulau_kiri.size() + pulau_kanan.size();
        
        pulau_kiri.banyak_dataran += pulau_kanan.banyak_dataran;
    
        AbstractBundel<Kuil> current = pulau_kanan.get_head().get_next();
        while (current instanceof DataBundel){
            DataBundel<Kuil> kuil_bundel = (DataBundel<Kuil>) current;
            DataBundel<Kuil> temp = pulau_kiri.push_back(kuil_bundel.get_data());
    
            kuil_bundel.data.__pulau_senarai_bundel = temp;
            kuil_bundel.data.__pulau = pulau_kiri;
            current = current.get_next();
        }
        
        pulau_hm.remove(nama_pulau_kanan);
        assert pulau_kiri.size() == banyak_kuil_yang_seharusnya;
        out.println(pulau_kiri.banyak_dataran);
    }
    
    public static void rise(){
        Pulau pulau = pulau_hm.get(in.next());
        long h_exclusive = in.nextLong();
        long x = in.nextLong();
        long total_affected_dataran = 0;
    
        AbstractBundel<Kuil> kuil_bundel = pulau.get_head().next;
        while (kuil_bundel instanceof DataBundel){
            Kuil kuil = ((DataBundel<Kuil>) kuil_bundel).data;
            total_affected_dataran += rise(kuil, h_exclusive+1, x);
            kuil_bundel = kuil_bundel.next;
        }
        out.println(total_affected_dataran);
    }
    
    public static long rise(Kuil kuil, long h_inclusive, long x){
        assert x > 0;
        KuilTree kuil_tree = kuil.tree;
        
        long number_of_affected_dataran = 0;
        long ketinggian_data = h_inclusive;
        long ketinggian_kumulatif = 0;
        
        // Harus right supaya rootnya bernilai positif (6 - 0 instead of 0 - 6)
        POSITION curr_position_in_parent = POSITION.LEFT;
        IBstNode<Long> prev = kuil_tree.head;
        BstData<Long> curr = kuil_tree.head.get_child(curr_position_in_parent);
        
        while (curr != null) {
            ketinggian_kumulatif += curr.get_data();
            
            if (ketinggian_data > ketinggian_kumulatif) {
                curr_position_in_parent = POSITION.RIGHT;
            }else if (ketinggian_data < ketinggian_kumulatif) {
                curr_position_in_parent = POSITION.LEFT;
    
                curr.data += x;
                number_of_affected_dataran += curr.get_count();
                if (curr.left != null) {
                    curr.left.data -= x;
                    number_of_affected_dataran -= curr.left.get_count();
        
                    // akan di-kanselasi di loop berikutnya karena kita mengubah curr.left.data
                    ketinggian_kumulatif += x;
                }
            }else {
                curr.data += x;
                number_of_affected_dataran += curr.get_count();
                if (curr.left != null) {
                    curr.left.data -= x;
                    number_of_affected_dataran -= curr.left.get_count();
                }
                break;  // berusaha menambahkan data yang sudah ada
            }
            curr = curr.get_child(curr_position_in_parent);
        }
        
        return number_of_affected_dataran;
    }
    
    
    public static void quake(){
        Pulau pulau = pulau_hm.get(in.next());
        long h_exclusive = in.nextLong();
        long x = -in.nextLong();
        long total_affected_dataran = 0;
        
        AbstractBundel<Kuil> kuil_bundel = pulau.get_head().next;
        while (kuil_bundel instanceof DataBundel){
            Kuil kuil = ((DataBundel<Kuil>) kuil_bundel).data;
            total_affected_dataran += quake(kuil, h_exclusive-1, x);
            kuil_bundel = kuil_bundel.next;
        }
        out.println(total_affected_dataran);
    }
    
    public static long quake(Kuil kuil, long h_inclusive, long x){
        assert x < 0;
        KuilTree kuil_tree = kuil.tree;
        
        long number_of_affected_dataran = 0;
        long ketinggian_data = h_inclusive;
        long ketinggian_kumulatif = 0;
        
        // Harus right supaya rootnya bernilai positif (6 - 0 instead of 0 - 6)
        POSITION curr_position_in_parent = POSITION.RIGHT;
        IBstNode<Long> prev = kuil_tree.head;
        BstData<Long> curr = kuil_tree.head.get_child(curr_position_in_parent);
        
        while (curr != null) {
            ketinggian_kumulatif += curr.get_data();
        
            if (ketinggian_data > ketinggian_kumulatif) {
                curr_position_in_parent = POSITION.RIGHT;
                curr.data += x;
                number_of_affected_dataran += curr.get_count();
                if (curr.right != null) {
                    curr.right.data -= x;
                    number_of_affected_dataran -= curr.right.get_count();
    
                    // akan di-kanselasi di loop berikutnya karena kita akan ngubah curr.right.data
                    ketinggian_kumulatif += x;
                }
            }else if (ketinggian_data < ketinggian_kumulatif)
                curr_position_in_parent = POSITION.LEFT;
            else {
                curr.data += x;
                number_of_affected_dataran += curr.get_count();
                if (curr.right != null) {
                    curr.right.data -= x;
                    number_of_affected_dataran -= curr.right.get_count();
                }
                break;  // berusaha menambahkan data yang sudah ada
            }
            curr = curr.get_child(curr_position_in_parent);
        }
    
        return number_of_affected_dataran;
    }
    
    public static void crumble(){
        if (get_raiden_shogun().is_dataran_for_kuil()) {
            out.println(0);
            return;
        }
        
        Dataran dataran_raiden = get_raiden_shogun();
        set_raiden_shogun(
                ((DataBundel<Dataran>) dataran_raiden.senarai_bundel.prev).data
        );
        out.println(dataran_raiden.get_ketinggian());
        dataran_raiden.get_pulau().banyak_dataran -= 1;
        dataran_raiden.delete_this();
        dataran_raiden.bst_node.update_this_and_parents_height_count();
        
        /* TODO */
    }
    
    public static void stabilize(){
        if (get_raiden_shogun().is_dataran_for_kuil()){
            out.println(0);
            return;
        }
        
        Kuil kuil = get_raiden_shogun().get_kuil();
        Dataran dataran_raiden = get_raiden_shogun();
        Dataran dataran_dikiri = get_raiden_shogun().dataran_di_kiri();
        long tinggi_dataran_raiden = dataran_raiden.get_ketinggian();
        long tinggi_dataran_dikiri = dataran_dikiri.get_ketinggian();
        
        Dataran dataran_yg_lebih_rendah = dataran_raiden;
        long tinggi_terendah = tinggi_dataran_raiden;
        if (tinggi_dataran_dikiri < tinggi_terendah){
            dataran_yg_lebih_rendah = dataran_dikiri;
            tinggi_terendah = tinggi_dataran_dikiri;
        }
        long banyak_dataran_old = dataran_yg_lebih_rendah.bst_node.get_internal_count();
        
        Dataran dataran_baru = new Dataran(null, null, null);
        DataBundel<Dataran> dataran_data_bundel_baru_bst = new DataBundel<>(dataran_baru);
        DataBundel<Dataran> dataran_data_bundel_baru_senarai = new DataBundel<>(dataran_baru);
        dataran_yg_lebih_rendah.bst_node_bundel.insert_after_this(dataran_data_bundel_baru_bst);
        
        // dimasukkan ke kanannya raiden
        dataran_raiden.senarai_bundel.insert_after_this(dataran_data_bundel_baru_senarai);
    
        dataran_baru.bst_node = dataran_yg_lebih_rendah.bst_node;
        dataran_baru.bst_node_bundel = dataran_data_bundel_baru_bst;
        dataran_baru.senarai_bundel = dataran_data_bundel_baru_senarai;
        dataran_yg_lebih_rendah.get_pulau().banyak_dataran += 1;
        dataran_baru.bst_node.update_this_and_parents_height_count();
    
    
        long banyak_dataran_new = dataran_yg_lebih_rendah.bst_node.get_internal_count();
        assert kuil.size() <= dataran_raiden.get_pulau().banyak_dataran;
        assert banyak_dataran_old + 1 == banyak_dataran_new;
        out.println(tinggi_terendah);
    }
    
    
    public static void gerak() {
        String arah = in.next();
        int banyak_langkah = in.nextInt();
        
        boolean ke_kiri = (arah.charAt(1) == 'I');
        for (int i = 0; i < banyak_langkah; i++) {
            gerak_sekali(ke_kiri);
        }
    
        out.println(get_raiden_shogun().senarai_bundel.data.get_ketinggian());
    }
    public static void gerak_sekali(boolean ke_kiri){
        Kuil kuil_saat_ini = (Kuil) get_raiden_shogun().senarai_bundel.senarai;
        
        if (ke_kiri){  // kiri
            if (get_raiden_shogun().senarai_bundel.prev instanceof DataBundel)  // kalau dikirinya masih bagian kuil yg sama (bukan HeadNode)
                set_raiden_shogun(
                        ((DataBundel<Dataran>) get_raiden_shogun().senarai_bundel.prev).data
                );
            else{
                DataBundel<Kuil> temp_kuil_lama = kuil_saat_ini.get_senarai_bundel_of_pulau();
                assert temp_kuil_lama != null;  // kalau fail, mungkin soalnya salah? atau ada bug di tempat lain
                
                if (temp_kuil_lama.get_prev() instanceof DataBundel) {  // masih ada kuil di sebelumnya
                    Kuil kuil_baru = ((DataBundel<Kuil>) temp_kuil_lama.get_prev()).data;
                    set_raiden_shogun(
                            ((DataBundel<Dataran>) kuil_baru.tail.prev).data
                    );
                }
            }
        }else{  // kanan
            if (get_raiden_shogun().senarai_bundel.next instanceof DataBundel)  // kalau dikirinya masih ada dataran (bukan HeadNode)
                set_raiden_shogun(
                        ((DataBundel<Dataran>) get_raiden_shogun().senarai_bundel.next).data
                );
            else{
                DataBundel<Kuil> temp_kuil_lama = kuil_saat_ini.get_senarai_bundel_of_pulau();
                assert temp_kuil_lama != null;  // kalau fail, mungkin soalnya salah? atau ada bug di tempat lain
                
                if (temp_kuil_lama.get_next() instanceof DataBundel) {  // masih ada kuil di sebelumnya
                    Kuil kuil_baru = ((DataBundel<Kuil>) temp_kuil_lama.get_next()).data;
                    set_raiden_shogun(
                            ((DataBundel<Dataran>) kuil_baru.head.next).data
                    );
                }
            }
        }
    }
    
    public static void tebas(){
        String arah = in.next();
        boolean ke_kiri = arah.charAt(1) == 'I';
        int banyak_langkah = in.nextInt();
    
        Dataran dataran_raiden_awalnya = get_raiden_shogun();
        long ketinggian_dataran = get_raiden_shogun().get_ketinggian();
        
        for (int i = 0; i < banyak_langkah; i++) {
            if (!tebas(ke_kiri, ketinggian_dataran))
                break;
        }
        
        if (dataran_raiden_awalnya == get_raiden_shogun()){  // tidak berubah datarannya
            out.println(0);
            return;
        }
        
        // harusnya kalau gaada bug, dataran_di_kanan maupun dataran_di_kiri tidak mengembalikan null
        if (ke_kiri){
            out.println(get_raiden_shogun().dataran_di_kanan().get_ketinggian());
        }else{
            Dataran temp = get_raiden_shogun().dataran_di_kiri();
            out.println(temp.get_ketinggian());
        }
    }
    
    public static boolean tebas(boolean ke_kiri, long ketinggian){
        if (ke_kiri){
            if (get_raiden_shogun().bst_node_bundel.prev instanceof DataBundel){
                set_raiden_shogun(
                        ((DataBundel<Dataran>) get_raiden_shogun().bst_node_bundel.prev).data
                );
                return true;
            }
            
            AbstractBundel<Kuil> current_kuil = get_raiden_shogun().get_bundel_of_pulau().prev;
            while (current_kuil instanceof DataBundel){
                Kuil kuil = ((DataBundel<Kuil>) current_kuil).data;
                DataranBstNode dataran_bst_node = kuil.tree.get_path_to(ketinggian).b;
                if (dataran_bst_node != null && dataran_bst_node.senarai.size() > 0){
                    set_raiden_shogun(dataran_bst_node.senarai.get_last());
                    return true;
                }
                current_kuil = current_kuil.prev;
            }
        }else{
            if (get_raiden_shogun().bst_node_bundel.next instanceof DataBundel){
                set_raiden_shogun(
                        ((DataBundel<Dataran>) get_raiden_shogun().bst_node_bundel.next).data
                );
                return true;
            }
            
            AbstractBundel<Kuil> current_kuil = get_raiden_shogun().get_bundel_of_pulau().next;
            while (current_kuil instanceof DataBundel){
                Kuil kuil = ((DataBundel<Kuil>) current_kuil).data;
                DataranBstNode dataran_bst_node = kuil.tree.get_path_to(ketinggian).b;
                if (dataran_bst_node != null && dataran_bst_node.senarai.size() > 0){
                    set_raiden_shogun(dataran_bst_node.senarai.get_first());
                    return true;
                }
                current_kuil = current_kuil.next;
            }
        }
        return false;
    }
    
    public static void teleportasi(){
        String nama_kuil = in.next();
        set_raiden_shogun(kuil_hm.get(nama_kuil).get_first());
        out.println(get_raiden_shogun().get_ketinggian());
    }
    
    public static void sweeping(){
        String nama_pulau = in.next();
        long tinggi_air = in.nextLong();
        Pulau pulau = pulau_hm.get(nama_pulau);
        
        long banyak_sihir_diperlukan = 0;
        
        AbstractBundel<Kuil> curr_bundel = pulau.head.next;
        while (curr_bundel instanceof DataBundel){
            Kuil kuil = ((DataBundel<Kuil>) curr_bundel).data;
            banyak_sihir_diperlukan += sweeping(kuil, tinggi_air-1);
            curr_bundel = curr_bundel.next;
        }
        out.println(banyak_sihir_diperlukan);
    }
    
    public static long sweeping(Kuil kuil, long h_inclusive){
        long number_of_affected_dataran = 0;
        long ketinggian_data = h_inclusive;
        long ketinggian_kumulatif = 0;
    
        // Harus right supaya rootnya bernilai positif (6 - 0 instead of 0 - 6)
        POSITION curr_position_in_parent = POSITION.RIGHT;
        IBstNode<Long> prev = kuil.tree.head;
        BstData<Long> curr = kuil.tree.head.get_child(curr_position_in_parent);
    
        while (curr != null) {
            ketinggian_kumulatif += curr.get_data();
        
            if (ketinggian_data > ketinggian_kumulatif) {
                curr_position_in_parent = POSITION.RIGHT;
                number_of_affected_dataran += curr.get_count();
                if (curr.right != null)
                    number_of_affected_dataran -= curr.right.get_count();
            }else if (ketinggian_data < ketinggian_kumulatif)
                curr_position_in_parent = POSITION.LEFT;
            else {
                number_of_affected_dataran += curr.get_count();
                if (curr.right != null)
                    number_of_affected_dataran -= curr.right.get_count();
                break;  // berusaha menambahkan data yang sudah ada
            }
            curr = curr.get_child(curr_position_in_parent);
        }
        
        return number_of_affected_dataran;
    }
    
    
    
    static class InputReader implements Closeable {
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
    
}



class Pulau extends SenaraiBerantai<Kuil>{
    String nama;
    int banyak_dataran = 0;
    public Pulau(String nama){
        this.nama = nama;
    }
    
    DataBundel<Kuil> find_kuil(String nama_kuil){
        AbstractBundel<Kuil> temp_abs = this.head.next;
        while (temp_abs instanceof DataBundel){
            DataBundel<Kuil> temp = (DataBundel<Kuil>) temp_abs;
            if (temp.data.nama.equals(nama_kuil))
                return temp;
            temp_abs = temp_abs.next;
        }
        return null;
    }
    
    DataBundel<Kuil> find_kuil(Kuil kuil){
        AbstractBundel<Kuil> temp_abs = this.head.next;
        while (temp_abs instanceof DataBundel){
            DataBundel<Kuil> temp = (DataBundel<Kuil>) temp_abs;
            if (temp.data.equals(kuil))
                return temp;
            temp_abs = temp_abs.next;
        }
        return null;
    }
    
    
    Dataran get_dataran(long indeks_dataran){
        AbstractBundel<Kuil> temp = this.head.next;
        long indeks_dataran_temp = indeks_dataran;
        
        while (temp instanceof DataBundel){
            DataBundel<Kuil> kuil_bundel = (DataBundel<Kuil>) temp;
            if (kuil_bundel.data.size() < indeks_dataran_temp)
                break;
            indeks_dataran_temp -= kuil_bundel.data.size();
            temp = temp.next;
        }
        
        if (!(temp instanceof DataBundel))
            throw new IllegalStateException("Index out of range");
        DataBundel<Kuil> kuil_bundel = (DataBundel<Kuil>) temp;
        
        return kuil_bundel.data.get(indeks_dataran_temp).data;
    }
}


class Kuil extends SenaraiBerantai<Dataran>{
    String nama;
    Pulau __pulau;
    KuilTree tree = new KuilTree();
    protected DataBundel<Kuil> __pulau_senarai_bundel;
    
    public Kuil(String nama, Pulau pulau, DataBundel<Kuil> this_kuil_data_bundel){
        this.nama = nama;
        this.__pulau = pulau;
        __pulau_senarai_bundel = this_kuil_data_bundel;
    }
    
    public DataBundel<Kuil> get_senarai_bundel_of_pulau(){
        assert this.__pulau_senarai_bundel.senarai instanceof Pulau;
        assert this.__pulau_senarai_bundel.senarai == __pulau;
        return __pulau_senarai_bundel;
    }
    
    public void set_pulau_senarai_bundel(DataBundel<Kuil> new_value){
        __pulau_senarai_bundel = new_value;
    }
    
    public Pulau get_pulau(){
        assert this.__pulau_senarai_bundel.senarai instanceof Pulau;
        assert this.__pulau_senarai_bundel.senarai == __pulau;
        return __pulau;
    }
    
    
    public void reserve_node(Long ketinggian){
        tree.insert_data(ketinggian, new DataranDummyForReservingBstNode());
    }
    
    public Dataran push_back(Long ketinggian){
        Dataran dataran = new Dataran(null, null, null);
        dataran.senarai_bundel = super.push_back(dataran);
        dataran.bst_node = tree.insert_data(ketinggian, dataran);
        dataran.bst_node_bundel = (DataBundel<Dataran>) dataran.bst_node.senarai.tail.prev;
    
        __pulau.banyak_dataran += 1;
        assert this.size() <= __pulau.banyak_dataran;
        return dataran;
    }
    
    @Override
    public DataBundel<Dataran> push_back(Dataran new_dataran){
        throw new IllegalStateException("unimplemented");
    }
}


class ComparableBundle<T extends Comparable<T>> extends DataBundel<T> implements Comparable<ComparableBundle<T>>{
    public ComparableBundle(T data) {
        super(data);
    }
    
    @Override
    public int compareTo(ComparableBundle<T> o) {
        return this.data.compareTo(o.data);
    }
}


class DataranBstNode extends BstData<Long> implements Comparable<DataranBstNode> {
    SenaraiBerantai<Dataran> senarai = new SenaraiBerantai<>();  // list dataran dengan ketinggian yang sama
    
    public DataranBstNode(long ketinggian_relatif) {
        super(ketinggian_relatif);
    }
    
    public long get_relative_height(){  // Not to be confused with get_height(). Both are two distinct items
        return data;
    }
    
    // bedanya get_height dan get_ketinggian adalah, get_height() merupakan jarak
    // node ini ke leaf node terjauh. Sementara, get_ketinggian() adalah ketinggian
    // dataran tersebut. Jadi dengan kata lain, get_height() has nothing to do with
    // this TP 02, sementara get_ketinggian() sangat diperlukan dalam TP 02.
    public int ___get_height(){
        return super.___get_height();
    }
    public int get_height(boolean dapatkan_ketinggian_node_dari_bawah){
        if (dapatkan_ketinggian_node_dari_bawah)
            return super.___get_height();
        throw new IllegalStateException("Konfirmasi bahwa anda benar-benar ingin menghitung 'tinggi' dari leaf");
    }
    
    public long get_ketinggian(){
        long height = 0;
        IBstNode<Long> curr = this;
        while (curr instanceof BstData){
            DataranBstNode temp = (DataranBstNode) curr;
            height += temp.get_relative_height();
            curr = curr.get_parent();
        }
        return height;
    }
    
    @Override
    public void set_child(POSITION position, BstData<Long> new_child){
        this.set_child(position, (DataranBstNode) new_child);
    }
    public void set_child(POSITION position, DataranBstNode new_child){
        super.set_child_unchecked(position, new_child);
    }
    
    @Override
    public long get_internal_count(){
        return this.senarai.size();
    }
    
    @Override
    public int compareTo(DataranBstNode o) {
        throw new IllegalStateException("Not implemented");
    }
}

class Dataran {
    DataranBstNode bst_node;  // node pada bst tersebut
    DataBundel<Dataran> bst_node_bundel;  // bundel = node linked list (bhs indonesianya)
    DataBundel<Dataran> senarai_bundel;  // senarai = linked list (bahasa indonesianya)
    
    Dataran(DataranBstNode dataran_bst_node, DataBundel<Dataran> bst_node_bundel, DataBundel<Dataran> data_bundel){
        bst_node = dataran_bst_node;
        this.bst_node_bundel = bst_node_bundel;
        this.senarai_bundel = data_bundel;
    }
    
    long get_ketinggian(){
        return bst_node.get_ketinggian();
    }
    
    Dataran dataran_di_kiri(){
        // return dataran di kirinya, bahkan jika dataran di kirinya sudah berbeda segmen kuil.
        // Jika di pulau sudah tidak ada lagi dataran di kirinya, maka return null
        
        if (senarai_bundel.prev instanceof DataBundel){
            return ((DataBundel<Dataran>) senarai_bundel.prev).data;
        }
        
        AbstractBundel<Kuil> kuil_sebelumnya_ = this.get_bundel_of_pulau().prev;
        if (!(kuil_sebelumnya_ instanceof DataBundel)){
            return null;
        }
        Kuil kuil_sebelumnya = ((DataBundel<Kuil>) kuil_sebelumnya_).data;
        return kuil_sebelumnya.get_last();
    }
    
    
    Dataran dataran_di_kanan(){
        // return dataran di kanannya, bahkan jika dataran di kanannya sudah berbeda segmen kuil.
        // Jika di pulau sudah tidak ada lagi dataran di kanannya, maka return null
        
        if (senarai_bundel.next instanceof DataBundel){
            return ((DataBundel<Dataran>) senarai_bundel.next).data;
        }
        
        AbstractBundel<Kuil> kuil_sebelumnya_ = this.get_bundel_of_pulau().next;
        if (!(kuil_sebelumnya_ instanceof DataBundel)){
            return null;
        }
        Kuil kuil_setelahnya = ((DataBundel<Kuil>) kuil_sebelumnya_).data;
        return kuil_setelahnya.get_first();
    }
    
    boolean is_dataran_for_kuil(){
        // cek apakah dataran yang diinjak saat ini merupakan kuil atau bukan.
        // Dengan kata lain, cek apakah dataran ini merupakan dataran paling kiri dalam class senarai Kuil
        return !(this.senarai_bundel.prev instanceof DataBundel);
    }
    
    Kuil get_kuil(){
        return (Kuil) senarai_bundel.senarai;
    }
    
    DataBundel<Kuil> get_bundel_of_pulau(){
        return get_kuil().get_senarai_bundel_of_pulau();
    }
    
    Pulau get_pulau(){
        return get_kuil().__pulau;
    }
    
    public void delete_this(){
        senarai_bundel.remove();
        bst_node_bundel.remove();
    }
    
    @Override
    public String toString() {
        if (bst_node == null)
            return "D:null";
        return "D:" + get_ketinggian();
    }
}

class DataranDummyForReservingBstNode extends Dataran{
    DataranDummyForReservingBstNode() {
        super(null, null, null);
    }
}

class KuilTree extends BST<Long>{  // BST<Long> == DataranBstNode
    
    Triplet<IBstNode<Long>, DataranBstNode, ArrayList<POSITION>> get_path_to(long ketinggian){
        // returns: prev, current, path
        
        long ketinggian_akumulatif = 0;
        
        ArrayList<POSITION> path = new ArrayList<>(30);
        POSITION curr_pos = POSITION.RIGHT;  // Harus right supaya rootnya bernilai positif (6 - 0 instead of 0 - 6)
        IBstNode<Long> prev_node = head;
        DataranBstNode curr_node = (DataranBstNode) head.child;
        
        while (curr_node != null){
            ketinggian_akumulatif += curr_node.data;
            if (ketinggian < ketinggian_akumulatif){
                curr_pos = POSITION.LEFT;
            }else if(ketinggian > ketinggian_akumulatif){
                curr_pos = POSITION.RIGHT;
            }else{
                break;
            }
            prev_node = curr_node;
            curr_node = (DataranBstNode) curr_node.get_child(curr_pos);
            path.add(curr_pos);
        }
        return new Triplet<>(prev_node, curr_node, path);
    }
    
    @Override
    public BstData<Long> insert_data(Long ketinggian) {
        throw new IllegalStateException("not implemented");
    }
    public DataranBstNode insert_data(Long ketinggian, Dataran dataran) {
        DataranBstNode temp = new DataranBstNode(ketinggian);
        //noinspection RedundantCast
        temp = insert_data_node((DataranBstNode) temp, dataran);
        return temp;
    }
    
    public DataranBstNode insert_data_node(DataranBstNode bst_data, Dataran dataran){
        // insert  anode into the tree. Return RIGHT if it's its parent's right child. LEFT otherwise

//        assert bst_data instanceof DataranBstNode;
        assert bst_data.is_leaf();
        assert bst_data.get_parent() == null;
        
        long ketinggian_data = bst_data.data;
        long ketinggian_kumulatif = 0;
    
        // Harus right supaya rootnya bernilai positif (6 - 0 instead of 0 - 6)
        POSITION curr_position_in_parent = POSITION.RIGHT;
        IBstNode<Long> prev = this.head;
        BstData<Long> curr = head.get_child(curr_position_in_parent);
        while (curr != null) {
            ketinggian_kumulatif += curr.get_data();
            
            if (ketinggian_data > ketinggian_kumulatif)
                curr_position_in_parent = POSITION.RIGHT;
            else if (ketinggian_data < ketinggian_kumulatif)
                curr_position_in_parent = POSITION.LEFT;
            else
                break;  // berusaha menambahkan data yang sudah ada
            prev = curr;
            curr = curr.get_child(curr_position_in_parent);
        }
    
        DataranBstNode current = (DataranBstNode) curr;
        if (current == null){  // jika DataranBstNode belum ada sebelumnya
            assert ketinggian_data != ketinggian_kumulatif;
            
            bst_data.set_data(ketinggian_data - ketinggian_kumulatif);
            current = bst_data;
            prev.set_child(curr_position_in_parent, bst_data, false);
        }
        
        if (!(dataran instanceof DataranDummyForReservingBstNode))
            current.senarai.add(dataran);
        current.update_this_and_parents_height_count();
        return current;
    }
    
    @Override
    public String toString() {
        if (this.head.child == null){
            return "BST-Empty";
        }
        return this.head.child.tree_str();
    }
}






class BST<T extends Comparable<T>> {
    BstHead<T> head = new BstHead<>();
    
    public void insert_datas(T[] datas){
        for (T data : datas) {
            insert_data(data);
        }
    }
    public <U extends Iterable<T>> void insert_datas(U datas){
        for (T data : datas) {
            insert_data(data);
        }
    }
    public BstData<T> insert_data(T data){
        BstData<T> temp = new BstData<>(data);
        insert_data_node(temp);
        return temp;
    }
    public BstData<T> root(){
        return head.child;
    }
    
    public POSITION insert_data_node(BstData<T> new_node){
        // insert  anode into the tree. Return RIGHT if it's its parent's right child. LEFT otherwise
        assert new_node.is_leaf();
        assert new_node.get_parent() == null;
        
        POSITION curr_position_in_parent = POSITION.LEFT;  // RIGHT juga boleh. bebas
        IBstNode<T> prev = head;
        BstData<T> curr = head.get_child(curr_position_in_parent);
        while (curr != null){
            int temp = new_node.get_data().compareTo(curr.get_data());
            if (temp > 0)
                curr_position_in_parent = POSITION.RIGHT;
            else if (temp < 0)
                curr_position_in_parent = POSITION.LEFT;
            else
                return null;  // berusaha menambahkan data yang sudah ada
            prev = curr;
            curr = curr.get_child(curr_position_in_parent);
        }
        
        assert new_node.___get_height() == 0;
        prev.set_child(curr_position_in_parent, new_node);
        
        return curr_position_in_parent;
    }
    
    public BstData<T> find_node(T data){
        // return the node containing the data if exists. null otherwise
        
        BstData<T> temp = this.head.get_child();
        POSITION direction = POSITION.LEFT;
        while (temp != null && !temp.get_data().equals(data)){
            if (data.compareTo(temp.get_data()) > 0)
                direction = POSITION.RIGHT;
            else
                direction = POSITION.LEFT;
            temp = temp.get_child(direction);
        }
        
        return temp;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BST)) return false;
        BST<?> BST = (BST<?>) o;
        return Objects.equals(head.get_child(), BST.head.get_child());
    }
    
    public void print() {
        print(-1);
    }
    
    public void print(int data_length) {
        print(data_length, 1, true);
    }
    public void print(int data_length, int margin, boolean show_count) {
        if (this.head.child == null)
            System.out.println("AvlEmpty");
        else
            this.head.child.print_tree(data_length, margin, show_count);
    }
}

enum POSITION {
    LEFT, RIGHT;
    
    public POSITION complement(){
        switch (this){
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
        }
        throw new IllegalStateException();
    }
}

interface BstIterateFunction {
    <T extends Comparable<T>> boolean invoke(BstData<T> node, int depth);
}


interface IBstNode<T extends Comparable<T>> {
    BstData<T> get_child(POSITION child_position);
    BstData<T> get_left();
    BstData<T> get_right();
    void _set_child(POSITION child_position, BstData<T> new_child);
    
    default void set_child(POSITION child_position, BstData<T> new_child){
        set_child(child_position, new_child, true);
    }
    default void set_child(POSITION child_position, BstData<T> new_child, boolean update_height){
        /*
         * Melepaskan child pada child_position saat ini, dan melepaskan new_child dari parentnya saat ini.
         * Setelah itu, menghubungkan new_child dengan node ini, dan meng-adjust ulang height node ini serta parentsnya
         */
        if (new_child == this)
            throw new IllegalStateException("Trying to make a circular child");
        if (this.get_child(child_position) != null)
            this.get_child(child_position).detach_from_parent(false);
        if (new_child != null){
            if (new_child.parent != null)
                new_child.detach_from_parent(true);
            new_child.parent = this;
        }
        this._set_child(child_position, new_child);
        if (update_height)
            this.update_this_and_parents_height_count();
    }
    int ___get_height();
    void _set_height(int new_height);
    IBstNode<T> get_parent();
    void detach_from_parent();
    
    void _set_parent(IBstNode<T> new_parent);
    void set_parent(POSITION pos, IBstNode<T> new_parent);
    int update_this_height_and_count();  // returns its height
    IBstNode<T> update_parents_height_and_count();
    IBstNode<T> update_this_and_parents_height_count();
    IBstNode<T> update_this_and_parents_height_count(int initial_height);
    POSITION find_child_pos(IBstNode<T> child);
    
    long get_count();
    void _set_count(long new_count);
    default long get_internal_count(){  // the weight of each node
        return 1;
    }
}

/*interface BstData<T extends Comparable<T>> extends IBstNode<T> {
    T get_data();
    void set_data(T new_data);
    BstData<T> get_right();
    void set_right(BstData<T> new_data);
    BstData<T> get_left();
    void set_left(BstData<T> new_data);
    
    boolean is_leaf();
    void detach_this_only();
    Iterator<Pair<BstData<T>, Integer>> inorder_iterator();
    BstData<T> successor_inorder();
    BstData<T> predecessor_inorder();
    
    boolean equals(boolean check_height, Object o);
    void print();
    void print(int data_length, int margin_size);
}*/

interface BstSpecialNode<T extends Comparable<T>> extends IBstNode<T> {
}

class BstHead<T extends Comparable<T>> implements BstSpecialNode<T> {
    BstData<T> child;
    
    @Override
    public BstData<T> get_child(POSITION child_position) {
        return child;
    }
    
    @Override
    public BstData<T> get_left() {
        return this.child;
    }
    
    @Override
    public BstData<T> get_right() {
        return this.child;
    }
    
    @Override
    public void _set_child(POSITION child_position, BstData<T> new_child) {
        this.child = new_child;
    }
    
    
    
    public BstData<T> get_child(){
        return child;
    }
    
    
    @Override
    public int ___get_height() {
        if (this.child == null)
            return -1;  // kalau BST hanya terdiri atas root, maka heightnya 0. Ini bahkan tidak punya root
        return this.child.___get_height();
    }
    
    @Override
    public void _set_height(int new_height) {
    }
    
    @Override
    public IBstNode<T> get_parent() {
        return null;
    }
    
    @Override
    public void detach_from_parent() {
    }
    
    @Override
    public void _set_parent(IBstNode<T> new_parent) {
    }
    
    @Override
    public void set_parent(POSITION pos, IBstNode<T> new_parent) {
    }
    
    @Override
    public int update_this_height_and_count() {
        return this.___get_height();
    }
    
    
    @Override
    public IBstNode<T> update_parents_height_and_count() {
        return null;
    }
    
    @Override
    public IBstNode<T> update_this_and_parents_height_count() {
        return null;
    }
    
    @Override
    public IBstNode<T> update_this_and_parents_height_count(int initial_height) {
        return null;
    }
    
    @Override
    public POSITION find_child_pos(IBstNode<T> child) {
        if (child != this.child)
            return null;
        return POSITION.LEFT;
    }
    
    @Override
    public long get_count() {
        if (this.child == null)
            return 0;
        return this.child.count;
    }
    
    @Override
    public void _set_count(long new_count) {
    }
    
    @Override
    public String toString() {
        return "BstHead{}";
    }
}

class BstData<T extends Comparable<T>> implements IBstNode<T> {
    protected IBstNode<T> parent;
    protected BstData<T> left;
    protected BstData<T> right;
    protected int height = 0;
    protected long count = 0;
    public T data;
    
    public BstData(T data){
        this.data = data;
    }
    
    public BstData(IBstNode<T> parent, T data){
        this.parent = parent;
        this.data = data;
    }
    
    public void detach_from_parent(){
        detach_from_parent(true);
    }
    public void detach_from_parent(boolean adjust_parent_height){
        if (this.parent == null)
            return;
        
        POSITION position = this.parent.find_child_pos(this);
        assert position != null;
        
        if (adjust_parent_height) {
            int parent_new_height = 0;
            if (parent.get_child(position.complement()) != null)
                parent_new_height = parent.get_child(position.complement()).___get_height() + 1;
            parent._set_height(parent_new_height);
            parent.update_parents_height_and_count();
        }
        parent._set_child(position, null);
        this._set_parent(null);
    }
    
    
    public void _set_parent(IBstNode<T> new_parent) {
        this.parent = new_parent;
    }
    
    @Override
    public void set_parent(POSITION pos, IBstNode<T> new_parent) {
        new_parent.set_child(pos, this);
    }
    
    
    
    
    public IBstNode<T> update_this_and_parents_height_count(int initial_height){
        this.update_this_height_and_count();
        this._set_height(initial_height);
        return this.update_parents_height_and_count();
    }
    
    public IBstNode<T> update_this_and_parents_height_count(){
        this.update_this_height_and_count();
        return this.update_parents_height_and_count();
    }
    
    public int update_this_height_and_count(){
        int left_height = -1;
        int right_height = -1;
        
        long left_count = 0;
        long right_count = 0;
        
        if (this.left != null) {
            left_height = this.left.height;
            left_count = this.left.get_count();
        }
        if (this.right != null) {
            right_height = this.right.height;
            right_count = this.right.get_count();
        }
        
        this.height = Math.max(left_height, right_height) + 1;
        this.count = left_count + right_count + get_internal_count();
        return this.height;
    }
    
    public IBstNode<T> update_parents_height_and_count(){
        /*
         * Return null if all of its parents are updated, or return the first unupdated parents
         * parents = predecessor. Not to be confused with 'predecessor' in predecessor_inorder()
         */
        
        if (this.parent == null)
            return null;
        IBstNode<T> temp_node = this.get_parent();
        int prev_height = temp_node.___get_height();
        
        while (temp_node != null){
            int curr_height = temp_node.update_this_height_and_count();
//            if (curr_height == prev_height)
//                break;
            temp_node = temp_node.get_parent();
        }
        return temp_node;
    }
    
    
    public boolean is_leaf() {
        return this.left == null && this.right == null;
    }
    
    
    public void detach_this_only() {
        if (this.is_leaf()){
            this.detach_from_parent();
            return;
        }
        BstData<T> pengganti;
        BstData<T> this_left = this.left;
        BstData<T> this_right = this.right;
        IBstNode<T> this_parent = this.parent;
        POSITION this_pos_in_parent = this.parent.find_child_pos(this);
        
        if (this.left == null || this.right == null){  // kalau node yg mau dihapus punya satu anak
            this.detach_from_parent(false);
            
            pengganti = this.left;
            if (this.right != null)
                pengganti = this.right;
            pengganti.detach_from_parent(true);
            
            this_parent.set_child(this_pos_in_parent, pengganti);
        }else{  // kalau node yg mau dihapus punya dua anak
            pengganti = this.predecessor_inorder();
            assert pengganti.is_leaf();
            pengganti.detach_from_parent(true);
            this.detach_from_parent(false);
            this_left.detach_from_parent(false);
            this_right.detach_from_parent(false);
            
            this_parent._set_child(this_pos_in_parent, pengganti);
            pengganti._set_parent(this_parent);
            pengganti._set_child(POSITION.LEFT, this_left);
            pengganti._set_child(POSITION.RIGHT, this_right);
            this_left._set_parent(pengganti);
            this_right._set_parent(pengganti);
        }
        pengganti.update_this_and_parents_height_count();
    }
    
    
    public Iterator<Pair<BstData<T>, Integer>> inorder_iterator() {
        return new InorderIterator();
    }
    
    
    public BstData<T> successor_inorder() {
        if (this.right == null)
            return null;
        BstData<T> temp = this.right;
        while (true){
            if (temp.get_left() == null)
                return temp;
            temp = temp.get_left();
        }
    }
    
    public BstData<T> predecessor_inorder() {
        if (this.left == null)
            return null;
        BstData<T> temp = this.left;
        while (true){
            if (temp.get_right() == null)
                return temp;
            temp = temp.get_right();
        }
    }
    
    
    class InorderIterator implements Iterator<Pair<BstData<T>, Integer>> {
        // Each iteration returns a tuple, where the left tuple is the iterated node, and the right tuple is the depth
        
        ArrayList<Triplet<BstData<T>, Boolean, Integer>> stack =
                new ArrayList<>(4 * BstData.this.___get_height() + 5);
        
        public InorderIterator() {
            stack.add(new Triplet<>(BstData.this, false, 0));
        }
        
        public void process_next(){
            while (!stack.isEmpty()){
                if (stack.get(stack.size() - 1).b){
                    break;
                }else{
                    Triplet<BstData<T>, Boolean, Integer> current_tuple = stack.remove(stack.size() - 1);
                    
                    if (current_tuple.a.get_right() != null)
                        stack.add(new Triplet<>(current_tuple.a.get_right(), false, current_tuple.c + 1));
                    stack.add(new Triplet<>(current_tuple.a, true, current_tuple.c));
                    if (current_tuple.a.get_left() != null)
                        stack.add(new Triplet<>(current_tuple.a.get_left(), false, current_tuple.c + 1));
                }
            }
            
            assert stack.isEmpty() || stack.get(stack.size()-1).b;
        }
        
        
        public boolean hasNext() {
            process_next();
            return !stack.isEmpty();
        }
        
        
        public Pair<BstData<T>, Integer> next() {
            process_next();
            Triplet<BstData<T>, Boolean, Integer> popped = stack.remove(stack.size() - 1);
            return new Pair<>(popped.a, popped.c);
        }
        
        
        public void remove() {
            next().a.detach_from_parent();
        }
        
    }

/*
    public boolean inorder_traversal(BstIterateFunction function) {
        ArrayList<Triplet<BstData<T>, Boolean, Integer>> stack = new ArrayList<>(4 * this.get_height() + 5);
        stack.add(new Triplet<>(this, false, 0));
        
        while (!stack.isEmpty()){
            Triplet<BstData<T>, Boolean, Integer> current_tuple = stack.remove(stack.size() - 1);
            if (current_tuple.b){
                function.invoke(current_tuple.a, current_tuple.c);
            }else{
                if (current_tuple.a.get_right() != null)
                    stack.add(new Triplet<>(current_tuple.a.get_right(), false, current_tuple.c + 1));
                stack.add(new Triplet<>(current_tuple.a, true, current_tuple.c));
                if (current_tuple.a.get_left() != null)
                    stack.add(new Triplet<>(current_tuple.a.get_left(), false, current_tuple.c + 1));
            }
        }
        
        int depth = 0;
        
        return false;
    }
    */
    
    public BstData<T> get_child(POSITION child_position) {
        if (child_position == POSITION.LEFT)
            return this.left;
        return this.right;
    }
    
    public T get_data() {
        return this.data;
    }
    
    
    public void set_data(T new_data) {
        this.data = new_data;
    }
    
    
    public BstData<T> get_right() {
        return this.right;
    }
    
    @Override
    public void set_child(POSITION child_position, BstData<T> new_child){
        boolean trying_to_set_new_child_in_the_left = child_position.equals(POSITION.LEFT);
        boolean new_child_should_be_in_the_left = new_child.get_data().compareTo(this.get_data()) < 0;
        
        if (trying_to_set_new_child_in_the_left == new_child_should_be_in_the_left)
            IBstNode.super.set_child(child_position, new_child);
        else
            throw new IllegalStateException("Trying to put child in the wrong position");
    }
    
    public void set_child_unchecked(POSITION child_position, BstData<T> new_child){
        IBstNode.super.set_child(child_position, new_child);
    }
    
    
    @Override
    public void _set_child(POSITION child_position, BstData<T> new_child) {
        if (child_position == POSITION.LEFT)
            this.left = new_child;
        else if (child_position == POSITION.RIGHT)
            this.right = new_child;
        else
            throw new IllegalStateException("Setting child to null position");
    }
    
    
    public void _set_right(BstData<T> new_data) {
        this.right = new_data;
    }
    
    
    public BstData<T> get_left() {
        return this.left;
    }
    
    
    public void _set_left(BstData<T> new_data) {
        this.left = new_data;
    }
    
    
    
    public int ___get_height() {
        return this.height;
    }
    
    
    public void _set_height(int new_height) {
        this.height = new_height;
    }
    
    
    public IBstNode<T> get_parent() {
        return parent;
    }
    
    
    
    
    public POSITION find_child_pos(IBstNode<T> child) {
        if (child == this.left)
            return POSITION.LEFT;
        if (child == this.right)
            return POSITION.RIGHT;
        return null;
    }
    
    @Override
    public long get_count() {
        return this.count;
    }
    
    @Override
    public void _set_count(long new_count) {
        this.count = new_count;
    }
    
    
    public String toString() {
        return "<" + data + ">";
    }
    
    
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return this.equals(false, o);
    }
    
    
    public boolean equals(boolean check_height, Object o) {
        if (this == o) return true;
        if (!(o instanceof BstData)) return false;
        //noinspection unchecked
        BstData<T> avl_data = (BstData<T>) o;
        
        if (! avl_data.get_data().equals(this.get_data()))
            return false;
        
        if (!check_height && this.height != avl_data.height)
            return false;
        
        
        if (this.is_leaf())
            return true;
        
        boolean ret1 = true;
        boolean ret2 = true;
        
        if (this.left == null){
            if (avl_data.left != null)
                return false;
        }else if (! this.left.get_data().equals(avl_data.left.get_data()))
            return false;
        else if (this.left != null)
            ret1 = this.left.equals(check_height, avl_data.left);
        
        if (this.right == null){
            if (avl_data.right != null)
                return false;
        }else if (! this.right.get_data().equals(avl_data.right.get_data()))
            return false;
        else if (this.right != null)
            ret2 = this.right.equals(check_height, avl_data.right);
        
        
        return  ret1 && ret2;
    }
    
    
    public void print_tree() {
        print_tree(-1, 1, true);
    }
    
    
    public void print_tree(int data_length, int margin_size, boolean show_count) {
        System.out.println(tree_str(data_length, margin_size, show_count));
    }
    
    public String tree_str() {
        return tree_str(-1, 1, true);
    }
    
    public String tree_str(int data_length, int margin_size, boolean show_count) {
        int min_length = data_length;
        String padding = str_multiply(" ", data_length + margin_size);
        String margin = str_multiply(" ", margin_size);
        
        
        ArrayList<ArrayList<String>> arr = new ArrayList<>(this.height+2);
        for (int i = 0; i < this.height+1; i++) {
            arr.add(new ArrayList<>(this.height * 4));
        }
        
        
        InorderIterator it = new InorderIterator();
        while (it.hasNext()){
            Pair<BstData<T>, Integer> temp = it.next();
            int current_depth = temp.b;
    
    
            String outputted = temp.a.get_data().toString();
            if (show_count)
                outputted = String.format("%s:%d", temp.a.get_data().toString(), temp.a.get_count());
    
            
            if (data_length == -1) {
                min_length = outputted.length();
                padding = str_multiply(" ", min_length + margin_size);
            }
            
            for (int i = 0; i < arr.size(); i++) {
                if (i != current_depth)
                    arr.get(i).add(padding);
                else {
                    String temp_output = outputted;
                    temp_output = String.format("%" + min_length + "s", temp_output);
                    arr.get(i).add(temp_output + margin);
                }
            }
        }
        
        StringBuilder ret = new StringBuilder(this.height * 20);
        for (ArrayList<String> strings : arr) {
            for (int j = 0; j < strings.size(); j++) {
                ret.append(strings.get(j));
            }
            ret.append('\n');
        }
        return ret.toString();
    }
    
    @SuppressWarnings("StringRepeatCanBeUsed")
    public static String str_multiply(String str, int number){
        StringBuilder temp = new StringBuilder(number * str.length() + 2);
        for (int i = 0; i < number; i++) {
            temp.append(' ');
        }
        return temp.toString();
    }
    
    
    public int hashCode() {
        return Objects.hash(data);
    }
}


class sortings{
    public static <T extends Comparable<T>> void merge_sort(ArrayList<T> to_be_sorted){
        merge_sort(to_be_sorted, new ArrayList<>(to_be_sorted.size()+5));
    }
    public static <T extends Comparable<T>> void merge_sort(ArrayList<T> to_be_sorted, ArrayList<T> temporary){
        merge_sort(to_be_sorted, temporary, 0, to_be_sorted.size()-1);
    }
    
    public static <T extends Comparable<T>> void merge_sort(ArrayList<T> to_be_sorted, ArrayList<T> temporary,
                                                            int l, int r){
        ArrayList<Quadruplet<Integer, Integer, Boolean, Integer>>
                stack = new ArrayList<>(200);
        stack.add(new Quadruplet<>(l, r, false, -1));
        int m;
        
        while (!stack.isEmpty()){
            Quadruplet<Integer, Integer, Boolean, Integer> curr_task = stack.remove(stack.size() - 1);
            l = curr_task.a;
            r = curr_task.b;
            
            if (curr_task.c){  // c true -> merge. c false -> divide
                merge(to_be_sorted, temporary, curr_task);
            }else if (l != r){
                m = (curr_task.a + curr_task.b) / 2;
                stack.add(new Quadruplet<>(l, r, true, m));
                stack.add(new Quadruplet<>(l, m, false, -1));
                stack.add(new Quadruplet<>(m+1, r, false, -1));
            }
        }
    }
    
    public static <T extends Comparable<T>, U>
    void merge(ArrayList<T> arr, ArrayList<T> temporary,
               Quadruplet<Integer, Integer, U, Integer> segment){
        if (segment.a.equals(segment.b))
            return;
        temporary.clear();
        
        int kiri_start = segment.a;
        int kiri_end = segment.d;
        int kanan_start = segment.d + 1;
        int kanan_end = segment.b;
        
        loop_luar:
        while (kiri_start <= kiri_end && kanan_start <= kanan_end){
            while (arr.get(kiri_start).compareTo(arr.get(kanan_start)) <= 0){
                temporary.add(arr.get(kiri_start));
                kiri_start += 1;
                if (kiri_start > kiri_end)
                    break loop_luar;
            }
            while (arr.get(kanan_start).compareTo(arr.get(kiri_start)) < 0){
                temporary.add(arr.get(kanan_start));
                kanan_start += 1;
                if (kanan_start > kanan_end)
                    break loop_luar;
            }
        }
        
        // kalau kirinya masih ada, pindahin/copy ke temporary
        for (; kiri_start <= kiri_end; kiri_start++) {
            temporary.add(arr.get(kiri_start));
        }
        
        // pindahin kembali dari temporary ke sumber array
        int curr_index = segment.a;
        for (int i = 0; i < temporary.size(); i++) {
            arr.set(curr_index+i, temporary.get(i));
        }
    }
    
    public static <T extends Comparable<T>> void splitter(ArrayList<T> sorted_arr, BiConsumer<Integer, T> func){
        splitter(sorted_arr, func, new Pair<>(0, sorted_arr.size()-1));
    }
    public static <T extends Comparable<T>> void splitter(ArrayList<T> sorted_arr, BiConsumer<Integer, T> func,
                                                          Pair<Integer, Integer> segment){
        ArrayList<Pair<Integer, Integer>> stack = new ArrayList<>(200);
        stack.add(segment);
        
        while (!stack.isEmpty()){
            Pair<Integer, Integer> curr_segment = stack.remove(stack.size() - 1);
            
            if (curr_segment.a > curr_segment.b)
                continue;
            
            int m = (curr_segment.a + curr_segment.b) / 2;
            func.accept(m, sorted_arr.get(m));
            stack.add(new Pair<>(m+1, curr_segment.b));
            stack.add(new Pair<>(curr_segment.a, m-1));
        }
    }
    
}









class Pair<T, U>{
    public T a;
    public U b;
    
    public Pair(T item_a, U item_b){
        this.a = item_a;
        this.b = item_b;
    }
    
    @Override
    public String toString() {
        return "[" + a + " " + b + "]";
    }
}

class ComparablePair<T extends Comparable<T>, U extends Comparable<U>> extends Pair<T,U>
        implements Comparable<ComparablePair<T, U>>{
    public ComparablePair(T item_a, U item_b) {
        super(item_a, item_b);
    }
    
    @Override
    public int compareTo(ComparablePair<T, U> o) {
        int temp = a.compareTo(o.a);
        if (temp != 0)
            return temp;
        return b.compareTo(o.b);
    }
}


class Triplet<T, U, V>{
    public T a;
    public U b;
    public V c;
    
    public Triplet(T item_a, U item_b, V item_c){
        this.a = item_a;
        this.b = item_b;
        this.c = item_c;
    }
    
    
    @Override
    public String toString() {
        return "[" + a + ", " + b + ", " + c + "]";
    }
}

class Quadruplet<T, U, V, W>{
    public T a;
    public U b;
    public V c;
    public W d;
    
    
    public Quadruplet(T item_a, U item_b, V item_c, W item_d){
        this.a = item_a;
        this.b = item_b;
        this.c = item_c;
        this.d = item_d;
    }
    
    
    @Override
    public String toString() {
        return "[" + a + ", " + b + ", " + c + ", " + d + "]";
    }
}


@SuppressWarnings("unchecked")
class SenaraiBerantai<T>{
    public HeadBundel<T> head = new HeadBundel<>();
    public TailBundel<T> tail = new TailBundel<>();
    public int length = 0;
    public SenaraiBerantai(){
        head.set_next(tail); head.senarai = this;
        tail.set_prev(head); tail.senarai = this;
    }
    
    public int size() {
        return length;
    }
    public boolean isEmpty() {
        return length == 0;
    }
    public boolean contains(Object o) {
        AbstractBundel<T> current = head.next();
        while (current != tail){
            if (o.equals(
                    ((DataBundel<T>) current).get_data()
            ))
                return true;
        }
        return false;
    }
    public T get_first(){
        if (!(head.get_next() instanceof DataBundel))
            return null;
        return ((DataBundel<T>) head.get_next()).get_data();
    }
    public T get_last(){
        if (!(tail.get_prev() instanceof DataBundel))
            return null;
        return ((DataBundel<T>) tail.get_prev()).get_data();
    }
    
    public void add(T item){push_back(item);}
    public DataBundel<T> push_back(T item){
        DataBundel<T> temp = new DataBundel<>(item);
        tail.insert_before_this(temp);
        return temp;
    }
    public DataBundel<T> push_front(T item){
        DataBundel<T> temp = new DataBundel<>(item);
        head.insert_after_this(temp);
        return temp;
    }
    
    public HeadBundel<T> get_head(){return head;}
    public TailBundel<T> get_tail(){return tail;}
    public void extend(SenaraiBerantai<T> other){
        AbstractBundel<T> current = other.get_head().get_next();
        while (current instanceof DataBundel){
            this.push_back(((DataBundel<T>) current).get_data());
            current = current.get_next();
        }
    }
    public void destructive_extend_from(SenaraiBerantai<T> other){
        if (other.length == 0)
            return;
        
        // not necessarily be a data (may be head or tail), but mostly it's data
        AbstractBundel<T> this_first_data  = get_head().get_next();
        AbstractBundel<T> this_last_data   = get_tail().get_prev();
        AbstractBundel<T> other_first_data = other.get_head().get_next();
        AbstractBundel<T> other_last_data  = other.get_tail().get_prev();
        assert (other_first_data instanceof DataBundel);  // karena length > 0
        assert (other_last_data instanceof DataBundel);
        
        
        // this_last_data.set_next(other_first_data)    MUST come before    other_first_data.set_prev(this_last_data)
        // because  this_last_data.set_next()  will set the other_first_data's senarai as this senarai.
        // But if we set the  other_first_data.set_prev()  first, then  other_first_data  will influence its
        // old senarai (`other` object instead of `this`). Therefore it will influence the senarai that will be
        // emptied instead of `this` alive senarai.  The same goes for tail.set_prev() and other_last_data.set_next()
        this_last_data.set_next(other_first_data);
        other_first_data.set_prev(this_last_data);
        tail.set_prev(other_last_data);
        other_last_data.set_next(tail);
        
        other.get_head().set_next(other.get_tail());
        other.get_tail().set_next(other.get_head());
        this.length += other.length;
        other.length = 0;
    }
    public DataBundel<T> get(long i){
        assert i < size();
        AbstractBundel<T> temp = get_head().next;
        for (int j = 0; j < i; j++) {
            temp = temp.next;
        }
        return (DataBundel<T>) temp;
    }
    
    public Object[] toArray(){
        Object[] arr = new Object[length];
        
        int i = 0;
        AbstractBundel<T> current = get_head().get_next();
        while (current instanceof DataBundel){
            arr[i++] = ((DataBundel<T>) current).data;
            current = current.get_next();
        }
        
        return arr;
    }
    public static <T> SenaraiBerantai<T> fromArray(T ... array){
        SenaraiBerantai<T> ret = new SenaraiBerantai<>();
        for (int i = 0; i < array.length; i++) {
            ret.push_back(array[i]);
        }
        return ret;
    }
    
    
    public String toString(){return toString(" ");}
    public String toString(String delimiter){
        StringBuilder string_builder = new StringBuilder(length * 5);
        
        AbstractBundel<T> current = get_head().get_next();
        while (current instanceof DataBundel){
            string_builder.append(((DataBundel<T>) current).data);
            string_builder.append(delimiter);
            current = current.get_next();
        }
        
        return string_builder.toString();
    }
    
    public void forEach(Consumer<DataBundel<T>> consumer){
        AbstractBundel<T> curr = this.head.next;
        while (curr instanceof DataBundel){
            consumer.accept((DataBundel<T>) curr);
            curr = curr.next;
        }
    }
}

abstract class AbstractBundel<T>{
    protected SenaraiBerantai<T> senarai;
    protected AbstractBundel<T> next;
    protected AbstractBundel<T> prev;
    
    protected void increase_senarai_berantai_length(int by){
        senarai.length += by;
        if (senarai.length < 0)
            throw new RuntimeException();
    }
    public abstract void set_senarai(SenaraiBerantai<T> new_senarai);
    protected void set_next(AbstractBundel<T> bundel){
        bundel.set_senarai(this.senarai); next = bundel;
    }
    
    public AbstractBundel<T> get_next(){
        return next();
    }
    
    protected void set_prev(AbstractBundel<T> bundel){
        bundel.set_senarai(this.senarai); prev = bundel;
    }
    
    public AbstractBundel<T> get_prev(){
        return prev();
    }
    
    public boolean insert_after_this(AbstractBundel<T> bundel){
        if (bundel.prev != null || bundel.next != null || bundel.senarai != null)
            throw new IllegalStateException();
        
        bundel.senarai = senarai;
        AbstractBundel<T> prev_before = get_prev();
        AbstractBundel<T> next_before = get_next();
        
        set_next(bundel);
        bundel.set_prev(this);
        bundel.set_next(next_before);
        
        // if (next_before != null)
        next_before.set_prev(bundel);
        increase_senarai_berantai_length(1);
        return true;
    }
    
    public boolean insert_before_this(AbstractBundel<T> bundel){
        if (bundel.prev != null || bundel.next != null || bundel.senarai != null)
            throw new IllegalStateException();
        
        bundel.senarai = senarai;
        AbstractBundel<T> prev_before = prev;
        AbstractBundel<T> next_before = next;
        
        set_prev(bundel);
        bundel.set_next(this);
        bundel.set_prev(prev_before);
        // if (prev_before != null)
        prev_before.set_next(bundel);
        increase_senarai_berantai_length(1);
        return true;
    }
    
    public boolean hasNext(){
        return next != null;
    }
    public boolean hasPrevious(){return prev != null; }
    public AbstractBundel<T> next(){ next.set_senarai(this.senarai); return next; }
    public AbstractBundel<T> prev(){ prev.set_senarai(this.senarai); return prev; }
    public AbstractBundel<T> previous(){ return prev(); }
    
}
abstract class SpecializedBundel<T> extends AbstractBundel<T> {}
abstract class SpecializedHeadTailBundel<T> extends SpecializedBundel<T>{
    public void set_senarai(SenaraiBerantai<T> new_senarai){  // do nothing
    }
}
class HeadBundel<T> extends SpecializedHeadTailBundel<T>{
    @Override
    public void set_prev(AbstractBundel<T> bundel) {  // do nothing
    }
    @Override
    public AbstractBundel<T> get_prev() {
        return null;
    }
    @Override
    public boolean insert_before_this(AbstractBundel<T> bundel) {
        return false;
    }
}
class TailBundel<T> extends SpecializedHeadTailBundel<T>{
    @Override
    public void set_next(AbstractBundel<T> bundel) {  // do nothing
    }
    @Override
    public AbstractBundel<T> get_next() {
        return null;
    }
    @Override
    public boolean insert_after_this(AbstractBundel<T> bundel) {
        return false;
    }
}
class DataBundel<T> extends AbstractBundel<T>{
    T data;
    
    public DataBundel(T data) {
        super();
        this.data = data;
    }
    public void set_senarai(SenaraiBerantai<T> new_senarai){
        senarai = new_senarai;
    }
    public void set_data(T new_data){
        data = new_data;
    }
    public T get_data(){
        return data;
    }
    public T remove(){
        T ret = data;
        
        AbstractBundel<T> prev = this.get_prev();
        AbstractBundel<T> next = this.get_next();
        prev.set_next(next);
        next.set_prev(prev);
        increase_senarai_berantai_length(-1);
        
        this.next = null;
        this.prev = null;
        this.data = null;
        return ret;
    }
    
    public boolean has_next_data() {
        return get_next() instanceof DataBundel;
    }
}


