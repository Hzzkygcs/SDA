import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Solusi {
    public static long __TESTCASE__ = -1;
    public static InputReader in = new InputReader(System.in);
    public static PrintWriter out = new PrintWriter(System.out);
    
    public static DataranPriorityQueue dataran_pq;
    public static ArrayList<Dataran> dataran_arr = new ArrayList<>(100*1000+10);
    
    public static void main(String[] args) {
        dataran_arr.clear();
        
        
        int bebatuan_awal = in.nextInt();
        {
            ArrayList<Dataran> temp = new ArrayList<>(100*1000 + 7);
            temp.clear();
            for (int i = 0; i < bebatuan_awal; i++) {
                long temp_int = in.nextLong();
                Dataran temp_dataran = new Dataran(temp_int, i);
                temp.add(temp_dataran);
                dataran_arr.add(temp_dataran);
            }
            dataran_pq = DataranPriorityQueue.from_array_unsafe_dataran(temp); temp = null;
        }
    
        for (int i = 0; i < dataran_pq.array_list.size(); i++) {
            dataran_pq.array_list.get(i).set_indeks_pq(i);
        }
        
        queries(in.nextInt());
        out.flush();
    }
    
    
    public static void queries(int number_of_query){
        for (int q = 0; q < number_of_query; q++) {
            char perintah = in.next().charAt(0);
            
            switch (perintah){
                case 'A':
                    angkat_dataran_baru();
                    break;
                case 'U':
                    ubah();
                    break;
                case 'R':
                    terendah();
                    break;
            }
            
            assert dataran_arr.get(dataran_arr.size() - 1)._indeks_pq >= 0;
        }
    }
    
    public static void angkat_dataran_baru(){
        long new_tinggi = in.nextLong();
        int indeks = dataran_arr.size();
        Dataran dataran = new Dataran(new_tinggi, indeks);
        dataran.set_indeks_pq(
                dataran_pq.add(dataran)
        );
        dataran_arr.add(dataran);
    }
    
    public static void ubah(){
        int indeks_arr = in.nextInt();
        long new_tinggi = in.nextLong();
    
        Dataran dataran = dataran_arr.get(indeks_arr);
        dataran.set_ketinggian(new_tinggi);
    }
    
    public static void terendah(){
        Dataran dataran_terendah = dataran_pq.pop();
        int dataran_terendah_indeks = dataran_terendah._index_array;
        
        long ketinggian_tertinggi = dataran_terendah.get_ketinggian();
        boolean di_kirinya_masih_ada = dataran_terendah_indeks-1 >= 0;
        boolean di_kanannya_masih_ada = dataran_terendah_indeks+1 < dataran_arr.size();
        
        if (di_kirinya_masih_ada){
            long kiri_dari_terendah = dataran_arr.get(dataran_terendah.get_index_array() - 1).get_ketinggian();
            ketinggian_tertinggi = Math.max(ketinggian_tertinggi, kiri_dari_terendah);
        }
        if (di_kanannya_masih_ada){
            long kanan_dari_terendah = dataran_arr.get(dataran_terendah.get_index_array() + 1).get_ketinggian();
            ketinggian_tertinggi = Math.max(ketinggian_tertinggi, kanan_dari_terendah);
        }
        
        dataran_terendah._set_ketinggian(ketinggian_tertinggi);
        dataran_terendah.set_indeks_pq(
                dataran_pq.insert(dataran_terendah)
        );
        
        if (di_kirinya_masih_ada)
            dataran_arr.get(dataran_terendah_indeks - 1).set_ketinggian(ketinggian_tertinggi);
        if (di_kanannya_masih_ada)
            dataran_arr.get(dataran_terendah_indeks + 1).set_ketinggian(ketinggian_tertinggi);
        out.print(ketinggian_tertinggi);
        out.print(' ');
        out.println(dataran_terendah_indeks);
    }
    
    
    static class InputReader {
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
        
        public void close() throws IOException {
            bufferedReader.close();
        }
    }
}



class Dataran implements Comparable<Dataran>{
    public long get_ketinggian() {
        return _ketinggian;
    }
    
    public void _set_ketinggian(long _ketinggian) {
        this._ketinggian = _ketinggian;
    }
    public void set_ketinggian(long _ketinggian) {
        Dataran dataran = this;
        int curr_node = dataran.get_indeks_pq();
        dataran._set_ketinggian(_ketinggian);
    
        curr_node = Solusi.dataran_pq.percolate_up(curr_node);
        curr_node = Solusi.dataran_pq.percolate_down(curr_node);
        dataran.set_indeks_pq(curr_node);
    }
    public int get_index_array() {
        return _index_array;
    }
    public void set_index_array(int _index_array) {
        this._index_array = _index_array;
    }
    long _ketinggian;
    int _index_array;
    
    public int get_indeks_pq() {
        return _indeks_pq;
    }
    public void set_indeks_pq(int _indeks_pq) {
        this._indeks_pq = _indeks_pq;
    }
    int _indeks_pq = -1;
    
    Dataran(long ketinggian, int indeks){
        assert indeks >= 0;
        this._set_ketinggian(ketinggian);
        set_index_array(indeks);
    }
    
    @Override
    public int compareTo(Dataran o) {
        // bandingkan ketinggian. Jika sama, bandingkan indeks (inversed)
        if (get_ketinggian() == o.get_ketinggian())
            return get_index_array() - o.get_index_array();
        return Long.compare(get_ketinggian(), o.get_ketinggian());
    }
    
    @Override
    public String toString() {
        return "[" + this._ketinggian + "," + this._index_array + "," + this._indeks_pq + "]";
    }
}



class DataranPriorityQueue extends MyPriorityQueue<Dataran>{
    public DataranPriorityQueue() {}
    public DataranPriorityQueue(int reserved_size) {super(reserved_size);}
    public DataranPriorityQueue(ArrayList<Dataran> array_list) {super(array_list);}
    
    public static
    DataranPriorityQueue from_array_unsafe_dataran(ArrayList<Dataran> array_list){
        return new DataranPriorityQueue(array_list);
    }
    public static
    DataranPriorityQueue from_array_dataran(ArrayList<Dataran> array_list){
        if (array_list == null)
            return new DataranPriorityQueue();
        //noinspection unchecked
        return new DataranPriorityQueue((ArrayList<Dataran>) array_list.clone());
    }
    
    @Override
    int swap_to(int current, POSITION position) {
        assert does_exist(current);
        assert has_child(current, position);
    
        Dataran child_val = get_value(get_child(current, position));
        Dataran curr_val = get_value(current);
        assert child_val != curr_val;
        
        child_val.set_indeks_pq(current);
        curr_val.set_indeks_pq(get_child(current, position));
        
        /*// swap their indeks pq too
        int indeks_pq_child = child_val.get_indeks_pq();
        child_val.set_indeks_pq(curr_val.get_indeks_pq());
        curr_val.set_indeks_pq(indeks_pq_child);*/
    
        return super.swap_to(current, position);
    }
    
    @Override
    Dataran pop() {
        array_list.get(array_list.size() - 1).set_indeks_pq(0);
        return super.pop();
    }
    
    @Override
    int swap_to_parent(int current) {
        assert does_exist(current);
        assert has_parent(current);
    
        Dataran current_value = get_value(current);
        Dataran parent_value = get_value(get_parent(current));
        
        parent_value.set_indeks_pq(current);
        current_value.set_indeks_pq(get_parent(current));
        
        /*int temp = current_value.get_indeks_pq();
        current_value.set_indeks_pq(parent_value.get_indeks_pq());
        parent_value.set_indeks_pq(temp);*/
        
        return super.swap_to_parent(current);
    }
    
    @Override
    int compare(Dataran a, Dataran b) {
        return a.compareTo(b);
    }
}







@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "unused"})
class MyPriorityQueue<T extends Comparable<T>>{
    ArrayList<T> array_list;
    
    MyPriorityQueue(){this(4);}
    MyPriorityQueue(int reserved_size){
        array_list = new ArrayList<T>(reserved_size);
    }
    protected MyPriorityQueue(ArrayList<T> array_list){
        if (array_list == null)
            array_list = new ArrayList<>(4);
        
        this.array_list = array_list;
        if (array_list.size() == 0)
            return;
        
        int start_pos = array_list.size() / 2;
        for (int i = start_pos; i >= 0; i--) {
            percolate_down(i);
        }
    }
    public static <T extends Comparable<T>>
    MyPriorityQueue<T> from_array_unsafe(ArrayList<T> array_list){
        return new MyPriorityQueue<>(array_list);
    }
    public static <T extends Comparable<T>>
    MyPriorityQueue<T> from_array(ArrayList<T> array_list){
        if (array_list == null)
            return new MyPriorityQueue<T>();
        //noinspection unchecked
        return new MyPriorityQueue<T>((ArrayList<T>) array_list.clone());
    }
    
    
    int compare(T a, T b){
        // dibikin method tersendiri supaya siapa tahu nanti mau ngubah jadi MaxPriorityQueue, dsb.
        // Jadi nanti bisa tinggal diextend dan dioverride
        return a.compareTo(b);
    }
    
    boolean is_empty(){return array_list.size() == 0;}
    int size(){return array_list.size();}
    int insert(T item){return add(item);}
    int add(T item){
        int item_pos = array_list.size();
        array_list.add(item);
        return percolate_up(item_pos);
    }
    T pop(){
        assert array_list.size() > 0;
        T ret = peek();
        T last = array_list.remove(array_list.size() - 1);
        if (!is_empty()) {
            array_list.set(0, last);
            percolate_down(0);
        }
        return ret;
    }
    T pop_or_default(T default_value){
        if (array_list.size() == 0)
            return default_value;
        return pop();
    }
    T nullable_pop(){return pop_or_default(null);}
    
    boolean does_exist(int curr){
        return curr >= 0 && curr < array_list.size();
    }
    boolean has_left(int curr){ return does_exist(_get_left(curr));}
    boolean has_right(int curr){ return does_exist(_get_right(curr));}
    boolean has_child(int curr, POSITION pos){ return does_exist(_get_child(curr, pos));}
    boolean has_child(int curr){ boolean ret = has_left(curr) || has_right(curr); assert !ret || has_left(curr); return ret;}
    boolean has_parent(int curr){return curr < array_list.size()  &&  !is_root(curr);}
    boolean is_root(int curr){ return curr == 0;}
    
    // unchecked access. That is, this methods won't throw any error regardless `current` validity
    int _get_left(int current){
        return 2*current + 1;
    }
    int _get_right(int current){
        return 2*current + 2;
    }
    int _get_child(int current, POSITION pos){return 2*current + 1 + pos.get_value();}
    int _get_parent(int current){
        return (current - 1)/2; }
    int _get_level(int current){
        int temp = current+1;
        int ret = 0;
        while (temp != 0){
            ret++;
            temp >>>= 1;
        }
        return ret - 1;  // index 0: level 0. index 1,2: level 1. index 3,...,6: level 3. etc...
    }
    
    // checked access. That is, if `current` is invalid, it will throw an error;
    int get_left(int current){
        if (!does_exist(current)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        int ret = _get_left(current);
        if (!does_exist(ret)) throw new ArrayIndexOutOfBoundsException("Trying to get a non-existing child");
        return ret;
    }
    int get_right(int current){
        if (!does_exist(current)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        int ret = _get_right(current);
        if (!does_exist(ret)) throw new ArrayIndexOutOfBoundsException("Trying to get a non-existing child");
        return ret;
    }
    int get_child(int current, POSITION pos){
        if (!does_exist(current)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        int ret = _get_child(current, pos);
        if (!does_exist(ret)) throw new ArrayIndexOutOfBoundsException("Trying to get a non-existing child");
        return ret;
    }
    POSITION get_pos_in_parent(int current){
        assert has_parent(current);
        int parent_node = get_parent(current);
        if (get_left(parent_node) == current) return POSITION.LEFT;
        if (get_right(parent_node) == current) return POSITION.RIGHT;
        
        // reference grammar: mini valen
        throw new IllegalStateException("Shouldn't be possible to execute this line.");
//        throw new IllegalStateException("Shouldn't be a bug here, but there's a bug");
    }
    int get_parent(int current){
        if (!does_exist(current)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        int ret = _get_parent(current);
        if (!does_exist(ret)) throw new ArrayIndexOutOfBoundsException("Trying to get a non-existing parent");
        return ret;
    }
    int get_level(int current){
        if (!does_exist(current)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        int ret = _get_level(current);
        return ret;
    }
    int get_tree_height(){
        int temp = array_list.size();
        return _get_level(temp-1);
    }
    
    void set_child(int current, POSITION position, T value){
        array_list.set(get_child(current, position), value);
    }
    void set_left(int current, T value){ array_list.set(get_left(current), value);}
    void set_right(int current, T value){ array_list.set(get_right(current), value);}
    
    T get_value(int curr){return array_list.get(curr);}  // automatically throws error if does_exist() equals to false
    T get_value_or_default(int curr, T default_value){if (does_exist(curr)) return array_list.get(curr); return default_value;}
    T peek(){if (!does_exist(0)) throw new ArrayIndexOutOfBoundsException("Trying to peek an empty Priority Queue!"); return array_list.get(0);}
    T peek_or_default(T default_value){ return get_value_or_default(0, default_value);}
    T nullable_peek(){if (!does_exist(0)) return null; return array_list.get(0);}
    
    int percolate_down(int node){
        if (!does_exist(node)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        
        int curr = node;
        T curr_value = get_value(node);
        while (has_child(curr)){
            boolean bigger_than_its_left  = compare(curr_value, get_value(get_left(curr))) > 0;
            boolean bigger_than_its_right = has_right(curr) && compare(curr_value, get_value(get_right(curr))) > 0;
            
            if (!bigger_than_its_left && !bigger_than_its_right)
                break;
            
            if (bigger_than_its_left && bigger_than_its_right){
                assert has_left(curr) && has_right(curr);
                
                if (compare(get_value(get_left(curr)),
                            get_value(get_right(curr))) < 0)
                    curr = swap_to_left(curr);
                else
                    curr = swap_to_right(curr);
                continue;
            }
            
            if (bigger_than_its_left)
                curr = swap_to_left(curr);
            else
                curr = swap_to_right(curr);
        }
        return curr;
    }
    int percolate_up(int node){
        if (!does_exist(node)) throw new ArrayIndexOutOfBoundsException("The current node is not exist");
        
        int curr = node;
        T curr_value = get_value(curr);
        while (has_parent(curr)){
            T parent_value = get_value(get_parent(curr));
            if (compare(curr_value, parent_value) < 0)
                curr = swap_to_parent(curr);
            else break;
        }
        return curr;
    }
    int swap_to(int current, POSITION position){
        assert does_exist(current);
        assert has_child(current, position);
        T temp = get_value(current);
        int child_node = get_child(current, position);
        array_list.set(current, get_value(child_node));
        array_list.set(child_node, temp);
        return child_node;
    }
    int swap_to_left(int current){return swap_to(current, POSITION.LEFT);}
    int swap_to_right(int current){return swap_to(current, POSITION.RIGHT);}
    int swap_to_parent(int current){
        assert does_exist(current);
        assert has_parent(current);
        
        T current_value = get_value(current);
        int parent = get_parent(current);
        set_child(parent, get_pos_in_parent(current), get_value(parent));
        array_list.set(parent, current_value);
        return parent;
    }
    
    @Override
    public String toString() {
        return "MyPriorityQueue{size=" + this.size() +"}";
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
    
    public int get_value(){
        switch (this){
            case LEFT:
                return 0;
            case RIGHT:
                return 1;
        }
        throw new IllegalStateException();
    }
}
