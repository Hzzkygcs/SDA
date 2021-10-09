package lab02.templated;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

class PenguinePack{
    public String geng;
    public long count;
    
    public PenguinePack(String geng, long pack_count){
        this.geng = geng;
        count = pack_count;
    }
    
    
}


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

class Lab02_Templated {
    
    static ArrayQueue<PenguinePack> queue = new ArrayQueue<>(50);
//    static LinkedList<PenguinePack> queue = new LinkedList<>();
    static HashMap<String, Long> geng_cnt = new HashMap<>(207);
    static long total_penguine = 0;
    
    
    private static InputReader in;
    private static PrintWriter out;
    
    // TODO
    static private Long handleDatang(String geng, int number) {
        long temp_long;
        long temp_long2;
        String temp_str;
        PenguinePack temp_peng;
        
        total_penguine += number;
    
        if (queue.size() > 0 && queue.peekFirst().geng.equals(geng)){
            queue.peekFirst().count += number;
        }else
            queue.addFirst(new PenguinePack(geng, number));
    
        return total_penguine;
    }
    
    // TODO
    static private String handleLayani(int number) {
        long temp_long;
        long temp_long2;
        String temp_str;
        PenguinePack temp_peng;
        
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
        return  (queue.peekLast().geng);
    }
    
    // TODO
    static private Long handleTotal(String geng) {
        return (geng_cnt.getOrDefault(geng, 0L));
    }
    
    public static void main(String args[]) throws IOException {
        
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        int N;
        
        N = in.nextInt();
        
        for(int tmp=0;tmp<N;tmp++) {
            String event = in.next();
            
            if(event.equals("DATANG")) {
                String Gi = in.next();
                int Xi = in.nextInt();
                
                out.println(handleDatang(Gi, Xi));
            } else if(event.equals("LAYANI")) {
                int Yi = in.nextInt();
                
                out.println(handleLayani(Yi));
            } else {
                String Gi = in.next();
                
                out.println(handleTotal(Gi));
            }
        }
        
        out.flush();
    }
    
    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;
        
        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }
        
        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }
        
        public int nextInt() {
            return Integer.parseInt(next());
        }
        
    }
}