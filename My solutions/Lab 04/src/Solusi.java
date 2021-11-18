import java.io.*;
import java.util.*;


public class Solusi {
    public static int __TESTCASE__ = 0;
    
    
    public static InputReader in = new InputReader(System.in);
    public static PrintWriter out = new PrintWriter(System.out);
//    public static PrintStream out = System.out;
    public static void debug(String ... arg){
        for (int i = 0; i < arg.length; i++) {
            System.out.print(arg[i]);
            System.out.print(" ");
        }
        System.out.println();
    }
    
    public static void main(String[] args) throws IOException{
//        test_senarai();
        
        long query_num = in.nextLong();
        
        HashMap<String, Agent> daftar_bangunan = new HashMap<>(1000 * 1000 + 7);
        
        String perintah, param1, param2;
        for (long q = 0; q < query_num; q++){
            perintah = in.next();
            param1 = in.next();
            
            switch (perintah.charAt(0)){
                case 'L':  // lift
                    param2 = in.next();
                    daftar_bangunan.get(param1).lift(param2.charAt(0) == 'A');
                    break;
                case 'B':  // bangun
                    param2 = in.next();
                    daftar_bangunan.get(param1).bangun(param2.charAt(0));
                    break;
                case 'H':  // hancurkan
                    daftar_bangunan.get(param1).hancurkan();
                    break;
                case 'F':
                    daftar_bangunan.put(param1, new Agent());
                    break;
                case 'T':  // timpa
                    param2 = in.next();
                    daftar_bangunan.get(param1).timpa(
                            daftar_bangunan.get(param2)
                    );
                    daftar_bangunan.remove(param2);
                    break;
                case 'S':  // sketsa
//                    daftar_bangunan.get(param1).sketsa();
                    SenaraiBerantai<Character> bangunan = daftar_bangunan.get(param1).bangunan;
                    AbstractBundel current = bangunan.get_head().get_next();
                    while (current instanceof DataBundel){
                        out.print(((DataBundel)current).get_data());
                        current = current.get_next();
                    }
                    out.println();
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        out.flush();
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
    
    public static void test_senarai(){
        {
            SenaraiBerantai<Integer> senarai = new SenaraiBerantai<>();
            AbstractBundel current = senarai.get_head();
            current.insert_after_this(new DataBundel<>(3));
            current.insert_after_this(new DataBundel<>(4));
            current.insert_after_this(new DataBundel<>(7));
            current = current.get_next();
            current.insert_after_this(new DataBundel<>(1));
            
            boolean temp = Arrays.equals(senarai.toArray(), new Object[]{7, 1, 4, 3});
//            debug(Arrays.toString(senarai.toArray()));
            if (!temp) throw new IllegalStateException();
        }
    
        {
            SenaraiBerantai<Integer> senarai = new SenaraiBerantai<>();
            AbstractBundel current = senarai.get_head();
            current.insert_after_this(new DataBundel<>(3));
            current.insert_after_this(new DataBundel<>(4));
            current.insert_after_this(new DataBundel<>(7));
            current = current.get_next();
            current.insert_after_this(new DataBundel<>(1));
            current.insert_before_this(new DataBundel<>(9));
            current.insert_after_this(new DataBundel<>(2));
        
            boolean temp = Arrays.equals(senarai.toArray(), new Object[]{9, 7, 2, 1, 4, 3});
//            debug(Arrays.toString(senarai.toArray()));
            if (!temp) throw new IllegalStateException();
        }
        
        {
            SenaraiBerantai<Integer> senarai = new SenaraiBerantai<>();
            AbstractBundel current = senarai.get_tail();
            current.insert_before_this(new DataBundel<>(3));
            current.insert_before_this(new DataBundel<>(4));
            current = current.get_prev();
            current = current.get_prev();
            current.insert_after_this(new DataBundel<>(7));
            current.insert_after_this(new DataBundel<>(1));
            current.insert_before_this(new DataBundel<>(2));
            current.insert_before_this(new DataBundel<>(0));
    
            boolean temp = Arrays.equals(senarai.toArray(), new Object[]{2, 0, 3, 1, 7, 4});
//            debug(Arrays.toString(senarai.toArray()));
            if (!temp) throw new IllegalStateException();
        }
        
        {
            boolean temp;
            
            SenaraiBerantai<Integer> senarai1 = SenaraiBerantai.fromArray(3, 5, 3, 2, 4);
            SenaraiBerantai<Integer> senarai2 = SenaraiBerantai.fromArray(7, 5, 54, 3, 9, 0, 1);
            
            senarai1.push_front(4);
            senarai1.push_back(9);
            temp = Arrays.equals(senarai1.toArray(), new Object[]{4,3, 5, 3, 2, 4, 9});
            if (!temp) throw new IllegalStateException();
            
            senarai2.push_front(-1);
            senarai2.push_back(-3);
            temp = Arrays.equals(senarai2.toArray(), new Object[]{-1, 7, 5, 54, 3, 9, 0, 1, -3});
            if (!temp) throw new IllegalStateException();
            
            senarai1.destructive_extend_from(senarai2);
    
            temp = Arrays.equals(senarai1.toArray(), new Object[]{4,3, 5, 3, 2, 4, 9, -1, 7, 5, 54, 3, 9, 0, 1, -3});
            if (!temp) throw new IllegalStateException();
            temp = Arrays.equals(senarai2.toArray(), new Object[]{});
            if (!temp) throw new IllegalStateException();
            
            senarai2.push_front(4);
            senarai2.push_back(9);
            senarai2.push_front(-1);
            senarai2.push_back(-3);
    
            temp = Arrays.equals(senarai2.toArray(), new Object[]{-1, 4, 9, -3});
            if (!temp) throw new IllegalStateException();
        }
    }
}



class Agent{
    SenaraiBerantai<Character> bangunan = new SenaraiBerantai<>();
    AbstractBundel posisi_agent = bangunan.get_head();
    
    void lift(boolean naik){
        if (bangunan.length == 0)
            throw new ArrayIndexOutOfBoundsException();
        
        if (naik){
            if (posisi_agent.next() instanceof DataBundel)
                posisi_agent = posisi_agent.next();
        }else{
            if (posisi_agent.prev() instanceof DataBundel)
                posisi_agent = posisi_agent.prev();
        }
        Solusi.out.println(
                ((DataBundel<Character>) posisi_agent).get_data()
        );
    }
    
    
    void bangun(char character){
        posisi_agent.insert_after_this(new DataBundel<Character>(character));
        posisi_agent = posisi_agent.get_next();
    }
    
    void hancurkan(){
        DataBundel<Character> yang_mau_dihancurkan = (DataBundel<Character>) posisi_agent;
        
        if (posisi_agent.get_prev() instanceof DataBundel)
            posisi_agent = posisi_agent.get_prev();
        else if (posisi_agent.get_next() instanceof DataBundel)
            posisi_agent = posisi_agent.get_next();
        else{
            if (bangunan.length != 1) throw new IllegalStateException();
            posisi_agent = posisi_agent.get_prev();
        }
    
        Solusi.out.println(yang_mau_dihancurkan.remove());
    }
    
    void timpa(Agent other){
        bangunan.destructive_extend_from(other.bangunan);
        other.bangunan = null;
        other.posisi_agent = null;
    }
    
    void sketsa(){
        Solusi.out.println(bangunan.toString(""));
    }
}




class SenaraiBerantai<T>{
    public HeadBundel head = new HeadBundel();
    public TailBundel tail = new TailBundel();
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
        AbstractBundel current = head.next();
        while (current != tail){
            if (o.equals(
                    ((DataBundel) current).get_data()
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
    public void push_back(T item){
        tail.insert_before_this(new DataBundel<T>(item));
    }
    public void push_front(T item){
        head.insert_after_this(new DataBundel<T>(item));
    }
    
    public HeadBundel get_head(){return head;}
    public TailBundel get_tail(){return tail;}
    public void extend(SenaraiBerantai<T> other){
        AbstractBundel current = other.get_head().get_next();
        while (current instanceof DataBundel){
            this.push_back(((DataBundel<T>) current).get_data());
            current = current.get_next();
        }
    }
    public void destructive_extend_from(SenaraiBerantai<T> other){
        if (other.length == 0)
            return;
        
        // not necessarily be a data (may be head or tail), but mostly it's data
        AbstractBundel this_first_data  = get_head().get_next();
        AbstractBundel this_last_data   = get_tail().get_prev();
        AbstractBundel other_first_data = other.get_head().get_next();
        AbstractBundel other_last_data  = other.get_tail().get_prev();
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
    
    public Object[] toArray(){
        Object[] arr = new Object[length];
        
        int i = 0;
        AbstractBundel current = get_head().get_next();
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
        
        AbstractBundel current = get_head().get_next();
        while (current instanceof DataBundel){
            string_builder.append(((DataBundel<T>) current).data);
            string_builder.append(delimiter);
            current = current.get_next();
        }
        
        return string_builder.toString();
    }
}



abstract class AbstractBundel{
    protected SenaraiBerantai senarai;
    protected AbstractBundel next;
    protected AbstractBundel prev;
    
    protected void increase_senarai_berantai_length(int by){
        senarai.length += by;
        if (senarai.length < 0)
            throw new RuntimeException();
    }
    public abstract void set_senarai(SenaraiBerantai new_senarai);
    protected void set_next(AbstractBundel bundel){
        bundel.set_senarai(this.senarai); next = bundel;
    }
    
    public AbstractBundel get_next(){
        return next();
    }
    
    protected void set_prev(AbstractBundel bundel){
        bundel.set_senarai(this.senarai); prev = bundel;
    }
    
    public AbstractBundel get_prev(){
        return prev();
    }
    
    public boolean insert_after_this(AbstractBundel bundel){
        if (bundel.prev != null || bundel.next != null || bundel.senarai != null)
            throw new IllegalStateException();
    
        bundel.senarai = senarai;
        AbstractBundel prev_before = get_prev();
        AbstractBundel next_before = get_next();
        
        set_next(bundel);
        bundel.set_prev(this);
        bundel.set_next(next_before);
        
        // if (next_before != null)
        next_before.set_prev(bundel);
        increase_senarai_berantai_length(1);
        return true;
    }
    
    public boolean insert_before_this(AbstractBundel bundel){
        if (bundel.prev != null || bundel.next != null || bundel.senarai != null)
            throw new IllegalStateException();
    
        bundel.senarai = senarai;
        AbstractBundel prev_before = prev;
        AbstractBundel next_before = next;
        
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
    public AbstractBundel next(){ next.set_senarai(this.senarai); return next; }
    public AbstractBundel prev(){ prev.set_senarai(this.senarai); return prev; }
    public AbstractBundel previous(){ return prev(); }
    
}
abstract class SpecializedBundel extends AbstractBundel {}
abstract class SpecializedHeadTailBundel extends SpecializedBundel{
    public void set_senarai(SenaraiBerantai new_senarai){  // do nothing
        return;
    }
}
class HeadBundel extends SpecializedHeadTailBundel{
    @Override
    public void set_prev(AbstractBundel bundel) {  // do nothing
    }
    @Override
    public AbstractBundel get_prev() {
        return null;
    }
    @Override
    public boolean insert_before_this(AbstractBundel bundel) {
        return false;
    }
}
class TailBundel extends SpecializedHeadTailBundel{
    @Override
    public void set_next(AbstractBundel bundel) {  // do nothing
    }
    @Override
    public AbstractBundel get_next() {
        return null;
    }
    @Override
    public boolean insert_after_this(AbstractBundel bundel) {
        return false;
    }
}
class DataBundel<T> extends AbstractBundel{
    T data;
    
    public DataBundel(T data) {
        super();
        this.data = data;
    }
    public void set_senarai(SenaraiBerantai new_senarai){
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
        
        AbstractBundel prev = this.get_prev();
        AbstractBundel next = this.get_next();
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


