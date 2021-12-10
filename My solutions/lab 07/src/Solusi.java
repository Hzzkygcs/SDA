import java.io.*;
import java.util.*;

public class Solusi {
    public static boolean DEBUG = false;
    public static int __TESTCASE__ = -1;
    
    public static InputReader in = new InputReader(System.in);
    public static PrintWriter out = new PrintWriter(System.out);
//    public static PrintStream out = System.out;

//public static PrintWriter out = new PrintWriter(new BufferedWriter(
//        new OutputStreamWriter(System.out), 58000), false);

    public static AdjListGraph<Integer> graph = new AdjListGraph<>(1005);
    public static MyPriorityQueue<GraphAlgorithms.ComparablePair>
            djikstra_pq = new MyPriorityQueue<>(3500);
    
    // key = from. value = to
    public static HashMap<Integer, List<Integer>> minimum_ticket_memo =
            new HashMap<>(5107, 0.95f);
    
    /*
    Data statistik:
    node edge orang ->  approximate number of iterations (num_of_iteration):
    
    984 10240 96809 ->   35255736 -> approx: 1000 ms
    2420 3000 96983 ->   25402740
    
    4304 8582 97118 ->  129408368
    4304 6282 97118 ->   94571792
    4304 5274 97118 ->   79507792
    
    4304 8582 97118 ->  129408368
    3304 8582 97118 ->   98991144
    2304 8582 97118 ->   69302016
    1304 8582 97118 ->   39232144
    
    204 1000 97118  ->     711960
    
    approximasi kompleksitas adalah O(NM) untuk N banyak node dan M banyak edge.
    keyakinanku atas kompleksitas ini adalah 96%
     */
    
    
    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equals("DEBUG")) DEBUG = true;
        int banyak_node, banyak_edge, banyak_orang;
        
        minimum_ticket_memo.clear();
        djikstra_pq.array_list.clear();
        graph.adjlist.clear();
        
        banyak_node = in.nextInt();
        banyak_edge = in.nextInt();
        banyak_orang = in.nextInt();
        
        graph.add_n_node(banyak_node+2);  // +1 karena one based index. +1 lagi buat jaga-jaga
        for (int i = 0; i < banyak_edge; i++) {
            int node1, node2, is_toll;
            node1 = in.nextInt();
            node2 = in.nextInt();
            is_toll = in.nextInt();
            graph.add_biedge(node1, node2, is_toll);
        }
    
        for (int i = 1; i < banyak_node + 1; i++) {
            if (minimum_ticket_memo.getOrDefault(i, null) != null)
                continue;
            ArrayList<Integer> shortest_path = new ArrayList<>(graph.adjlist.size()+2);
            for (int j = 0; j < graph.adjlist.size(); j++) {
                shortest_path.add(-1);
            }
            minimum_ticket_memo.put(i, shortest_path);
            update_neighbor(i);
        }
    
        for (int i = 0; i < banyak_orang; i++) {
            int from, to, banyak_tiket;
            from = in.nextInt();
            to = in.nextInt();
            banyak_tiket = in.nextInt();
            
            if (!minimum_ticket_memo.containsKey(from) || minimum_ticket_memo.get(from).get(to) == -1){
                find_minimum_ticket(from);
            }
            if (minimum_ticket_memo.get(from).get(to) <= banyak_tiket)
                out.println(1);
            else
                out.println(0);
        }
        out.flush();
//        System.out.println(num_of_iteration);
    }
    
    
    public static int num_of_iteration = 0;
    public static ArrayList<Integer> stack = new ArrayList<>(1000);
    public static void find_minimum_ticket(int from){
        num_of_iteration += 1;
        
        ArrayList<Boolean> visited = new ArrayList<>(graph.adjlist.size()+2);
        List<Integer> shortest_path = minimum_ticket_memo.get(from);
        shortest_path.set(0, 1);
    
        for (int i = 0; i < graph.adjlist.size(); i++) {
            visited.add(false);
        }
    
        Queue<GraphAlgorithms.ComparablePairInt>
                want_to_be_visited_stack = new LinkedList<>();
        want_to_be_visited_stack.add(new GraphAlgorithms.ComparablePairInt(from, 0));
        while (!want_to_be_visited_stack.isEmpty()){
            flood_fill_no_toll_connection(visited, shortest_path, want_to_be_visited_stack, from);
        }
        
        minimum_ticket_memo.put(from, shortest_path);
//        update_neighbor(from);  // tambahin ini mempercepat sekitar 300 sampai 700 ms (constraint node 2000-an)
    }
    
    public static void flood_fill_no_toll_connection(List<Boolean> visited,
                                                     List<Integer> shortest_path,
                                                     Queue<GraphAlgorithms.ComparablePairInt>
                                                        want_to_be_visited,
                                                     int from){
        assert stack.size() == 0;
        assert !want_to_be_visited.isEmpty();
        num_of_iteration += 1;
        
        Pair<Integer, Integer> curr_pair
                = want_to_be_visited.poll();
        int starting_node = curr_pair.a;
        int cost = curr_pair.b;
        stack.add(starting_node);
        
        
        // flood fill semua node yang terhubung tanpa toll 0
        while (!stack.isEmpty()){
            num_of_iteration += 1;
            int curr = stack.remove(stack.size() - 1);
            if (visited.get(curr))
                continue;
            visited.set(curr, true);
            shortest_path.set(curr, cost);
            minimum_ticket_memo.get(curr).set(from, cost);
            
            ArrayList<Pair<Integer, Integer>> neighbor = graph._get_neighbor_of(curr);
            for (int i = 0; i < neighbor.size(); i++) {
                num_of_iteration += 1;
                if (visited.get(neighbor.get(i).a))
                    continue;
                if (neighbor.get(i).b == 1)
                    want_to_be_visited.add(new GraphAlgorithms.ComparablePairInt(neighbor.get(i).a, cost + 1));
                else{
                    stack.add(neighbor.get(i).a);
                }
            }
        }
    }
    
    public static void update_neighbor(int from){
        assert stack.size() == 0;
        List<Integer> arr = minimum_ticket_memo.get(from);
        stack.add(from);
        
        while (!stack.isEmpty()){
            int curr = stack.remove(stack.size() - 1);
            if (minimum_ticket_memo.getOrDefault(curr, null) == arr)
                continue;
            minimum_ticket_memo.put(curr, arr);
            
            ArrayList<Pair<Integer, Integer>> neighbor = graph._get_neighbor_of(curr);
            for (int i = 0; i < neighbor.size(); i++) {
                if (neighbor.get(i).b == 0)
                    stack.add(neighbor.get(i).a);
            }
        }
    }
    
    
    public static long cost_function(Integer integer){
        return integer;
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



interface IGraph<T>{
    void add_n_node(int n);
    void add_n_node(int n, int reserve_cnt);
    void add_node();
    void add_node(int reserve_neightbour_cnt);
    void add_uniedge(int from, int to, T item);
    void add_biedge(int node1, int node2, T item);
    boolean has_uniedge(int from, int to);
    boolean has_biedge(int node1, int node2);
    ArrayList<Pair<Integer, T>> _get_neighbor_of(int node);  // dangerous: just return a reference. So it may alter the graph
    int size();
    int node_number();
}

class AdjListGraph<T> implements IGraph<T>{
    ArrayList<ArrayList<Pair<Integer, T>>> adjlist;
    
    AdjListGraph(int reserve_node_cnt){
         adjlist = new ArrayList<>(reserve_node_cnt + 1);
    }
    
    public void add_n_node(int n){
        add_n_node(n, 0);
    }
    public void add_n_node(int n, int reserve_cnt){
        for (int i = 0; i < n; i++) {
            this.add_node(reserve_cnt);
        }
    }
    public void add_node(){
        add_node(0);
    }
    public void add_node(int reserve_neightbour_cnt){
        adjlist.add(new ArrayList<>(reserve_neightbour_cnt));
    }
    public void add_uniedge(int from, int to, T item){  // unidirectional edge
        if (!(0 <= from && from < adjlist.size())) throw new IndexOutOfBoundsException("`from` is not registered");
        if (!(0 <= to && to < adjlist.size())) throw new IndexOutOfBoundsException("`to` is not registered");
        if (Solusi.DEBUG){
            if (has_uniedge(from, to))  throw new IllegalStateException("Trying to add an existing edge");
        }
        adjlist.get(from).add(new Pair<>(to, item));
    }
    public void add_biedge(int node1, int node2, T item){  // bidirectional edge
        // no need to check whether the edge  has already exist or not. It will be checked in register_new_uniedge()
        add_uniedge(node1, node2, item);
        add_uniedge(node2, node1, item);
    }
    public boolean has_uniedge(int from, int to){
        if (!(0 <= from && from < adjlist.size())) throw new IndexOutOfBoundsException("`from` is not registered");
        if (!(0 <= to && to < adjlist.size())) throw new IndexOutOfBoundsException("`to` is not registered");
        
        for (int i = 0; i < adjlist.get(from).size(); i++) {
            if (adjlist.get(from).get(i).a == to)
                return true;
        }
        return false;
    }
    public boolean has_biedge(int node1, int node2){
        return has_uniedge(node1, node2) && has_uniedge(node1, node2);
    }
    public ArrayList<Pair<Integer, T>> _get_neighbor_of(int node){
        // dangerous: just return a reference. So it may alter the graph
        return adjlist.get(node);
    }
    public int size(){
        return adjlist.size();
    }
    public int node_number(){
        return size();
    }
}
@FunctionalInterface
interface GetGraphCost<T>{
    long get_cost(T item);
}


class GraphAlgorithms{
    
    static class ComparablePairInt extends Pair<Integer, Integer> implements Comparable<ComparablePairInt>{
        public ComparablePairInt(Integer item_a, Integer item_b) {
            super(item_a, item_b);
        }
    
        @Override
        public int compareTo(ComparablePairInt o) {
            return Integer.compare(this.b, o.b);
        }
    }
    
    static class ComparablePair extends Pair<Integer, Long> implements Comparable<ComparablePair>{
        public ComparablePair(Integer item_a, Long item_b) {
            super(item_a, item_b);
        }
    
        @Override
        public int compareTo(ComparablePair o) {
            return Long.compare(this.b, o.b);
        }
    }
    
    public static <T extends Comparable<T>>
    List<Long> djikstra(IGraph<T> graph, int from, GetGraphCost<T> cost_func){
        return djikstra(graph, from, cost_func, graph.size()*2);
    }
    public static <T extends Comparable<T>>
    List<Long> djikstra(IGraph<T> graph, int from, GetGraphCost<T> cost_func,
                     int stack_initial_size){
        ArrayList<Long> shortest = new ArrayList<>(graph.size()+2);
    
        for (int i = 0; i < graph.size(); i++) {
            shortest.add(Long.MAX_VALUE);
        }
    
        Solusi.djikstra_pq.array_list.clear();
        MyPriorityQueue<ComparablePair> pq = Solusi.djikstra_pq;
        pq.add(new ComparablePair(from, 0L));
        
        while (!pq.isEmpty()){
            ComparablePair current = pq.pop();
            
            if (shortest.get(current.a).compareTo(current.b) <= 0)
                // berarti solusi di pq yg sekarang ga lebih optimal dari yang udah pernah kita hitung. Skip aja
                continue;
            
            shortest.set(current.a, current.b);
//            long minimum = Long.MAX_VALUE;
//            int node_with_minimum = -1;
            
            ArrayList<Pair<Integer, T>> neighbors = graph._get_neighbor_of(current.a);
            for (int i = 0; i < neighbors.size(); i++) {
                long to_neighbor_cost = cost_func.get_cost(neighbors.get(i).b);
                long neighbor_total_cost = current.b + to_neighbor_cost;
                pq.add(new ComparablePair(neighbors.get(i).a, neighbor_total_cost));
            }
        }
        
        return shortest;
    }
    
    public static <T extends Comparable<T>>
    Set<Integer> all_zero_cost_node(IGraph<T> graph, int from, GetGraphCost<T> cost_func){
        Set<Integer> nodes = new HashSet<>(32);
        ArrayList<Pair<Integer, Long>> stack = new ArrayList<>(64);
        stack.add(new Pair<>(from, 0L));
        
        while (!stack.isEmpty()){
            Pair<Integer, Long> current = stack.remove(stack.size() - 1);
            nodes.add(current.a);
            ArrayList<Pair<Integer, T>> neighbor = graph._get_neighbor_of(current.a);
            for (int i = 0; i < neighbor.size(); i++) {
                if (nodes.contains(neighbor.get(i).a))
                    continue;
                long neighbor_cost = cost_func.get_cost(neighbor.get(i).b);
                if (neighbor_cost > 0)
                    continue;
                stack.add(new Pair<>(neighbor.get(i).a, neighbor_cost));
            }
        }
        
        return nodes;
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



@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "unused"})
class MyPriorityQueue<T extends Comparable<T>>{
    ArrayList<T> array_list;
    
    MyPriorityQueue(){this(4);}
    MyPriorityQueue(int reserved_size){
        array_list = new ArrayList<>(reserved_size);
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
            return new MyPriorityQueue<>();
        //noinspection unchecked
        return new MyPriorityQueue<>((ArrayList<T>) array_list.clone());
    }
    
    
    int compare(T a, T b){
        // dibikin method tersendiri supaya siapa tahu nanti mau ngubah jadi MaxPriorityQueue, dsb.
        // Jadi nanti bisa tinggal diextend dan dioverride
        return a.compareTo(b);
    }
    
    boolean is_empty(){return array_list.size() == 0;}
    boolean isEmpty(){return is_empty();}
    int size(){return array_list.size();}
    int insert(T item){return add(item);}
    int add(T item){
        int item_pos = array_list.size();
        array_list.add(item);
        return percolate_up(item_pos);
    }
    T poll(){
        return pop();
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
