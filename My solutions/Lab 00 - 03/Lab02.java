
import java.util.*;


class ArrayQueue<T>{
    LinkedList<ArrayList<T>> buffer;
    
    private int head_ptr;
    private int segment_cnt;
    private long cnt;
    
    
    public ArrayQueue(){
        this(200);
    }
    
    public ArrayQueue(int segment_cnt){
        this.buffer = new LinkedList<>();
        this.segment_cnt = segment_cnt;
        buffer.push(new ArrayList<T>(segment_cnt+1));
        head_ptr = 0;
        cnt = 0;
    }
    
    public T peekFirst(){
        return peekLeft();
    }
    public T peekLeft(){
        assert cnt > 0;
        ArrayList<T> arr = buffer.peekFirst();
        return arr.get(arr.size()-1);
    }
    

    
    public T peekLast(){
        return  peekRight();
    }
    
    public T peekRight(){
        assert cnt > 0;
        if (buffer.peekLast().size() == head_ptr)
            return peek_n_last(buffer, 1).get(0);
        return buffer.peekLast().get(head_ptr);
    }
    
    public void addFirst(T item){
        pushLeft(item);
    }
    public void pushLeft(T item){
        cnt += 1;
        if (buffer.peekFirst().size() >= segment_cnt)
            buffer.push(new ArrayList<T>(segment_cnt+1));
        
        buffer.peekFirst().add(item);
    }
    
    public T popLeft(){
        assert cnt > 0;
        cnt -= 1;
        if (buffer.peekFirst().size() == 0)
            buffer.removeFirst();
        ArrayList<T> arr = buffer.peekFirst();
        T ret = arr.remove(arr.size() - 1);
        return ret;
    }
    
    
    public T removeLast(){
        return popRight();
    }
    public T popRight(){
        assert cnt > 0;
        cnt -= 1;
        if (buffer.peekLast().size() == head_ptr) {
            buffer.removeLast();
            head_ptr = 0;
        }
        return buffer.peekLast().get(head_ptr++);
    }
    
    public long size(){
        return cnt;
    }
    
    protected static <T1> T1 peek_n_last(LinkedList<T1> queue, int n){  // n is 0 based. n==0 is equal to queue.peekLast()
        Iterator<T1> iter = queue.descendingIterator();
        
        T1 ret = iter.next();
        for (int i = 0; i < n; i++) {
            ret = iter.next();
        }
        
        return ret;
    }
}


class PenguinePack{
    public String geng;
    public long count;
    
    public PenguinePack(String geng, long pack_count){
        this.geng = geng;
        count = pack_count;
    }
    
    
}



public class Lab02 {
    
    
    public static Scanner in = new Scanner(System.in);
    public static int __TESTCASE__ = 0;
    
    
    
    public static void solve(){
        // Tulis solusi pemecahan masalah disini.
        // Lakukan permintaan input, output, dsb juga disini.
        
        long n = in.nextLong();
        in.nextLine();
        
        long total_penguine = 0;
        ArrayQueue<PenguinePack> queue = new ArrayQueue<>(40);
//        LinkedList<PenguinePack> queue = new LinkedList<>();
        HashMap<String, Long> geng_cnt = new HashMap<>(207);
        
        for (int i = 0; i < n; i++) {
            String command = in.next();
            String geng;
            
            long number;
            long temp_long;
            long temp_long2;
            String temp_str;
            PenguinePack temp_peng;
            
            switch (command.charAt(0)){
                case 'D':  // Datang
                    geng = in.next();
                    number = Long.parseLong(in.next());
    
                    total_penguine += number;
                    
                    if (queue.size() > 0 && queue.peekFirst().geng.equals(geng)){
                        queue.peekFirst().count += number;
                    }else
                        queue.addFirst(new PenguinePack(geng, number));
                    
                    
                    System.out.println(total_penguine);
                    
                    break;
                case 'L':  // Layani
                    number = Long.parseLong(in.next());
                    total_penguine -= number;
                    
                    while (number > queue.peekLast().count){
                        temp_peng = queue.removeLast();
                        temp_str = temp_peng.geng;
                        temp_long2 = temp_peng.count;
    
                        number -= temp_long2;
                        
                        temp_long = geng_cnt.getOrDefault(temp_str, 0L);
                        temp_long += temp_long2;
                        geng_cnt.put(temp_str, temp_long);
                        
                        
                    }
    
                    temp_long = geng_cnt.getOrDefault(queue.peekLast().geng, 0L) + number;
                    geng_cnt.put(queue.peekLast().geng, temp_long);
                    
                    queue.peekLast().count -= number;
                    System.out.println(queue.peekLast().geng);
                    
                    break;
                case 'T':
                    geng = in.next();
                    System.out.println(geng_cnt.getOrDefault(geng, 0L));
                    break;
    
            }
        }
        
    }
    
    
    public static void main(String[] args) {
        solve();
    }
    
    
}
