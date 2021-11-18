import java.lang.invoke.StringConcatException;
import java.sql.Statement;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class merge_sort {
    
    public static Scanner in = new Scanner(System.in);
    
    public static void main(String[] args) {
        ArrayList<Long> arr1 = new ArrayList<>();
        String baris_1 = in.nextLine();
        
        try(Scanner scanner = new Scanner(baris_1)){
            while (scanner.hasNext())
                arr1.add(scanner.nextLong());
        }
        
        merge_sort(arr1, new ArrayList<>());
        System.out.println(Arrays.toString(arr1.toArray()));
    
        splitter(arr1, (i, obj) -> {
            System.out.println(obj);
        });
    }
    
    
    
    
    
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

class Fiveplet<T, U, V, W, X>{
    public T a;
    public U b;
    public V c;
    public W d;
    public X e;
    
    public Fiveplet(T item_a, U item_b, V item_c, W item_d, X item_e){
        this.a = item_a;
        this.b = item_b;
        this.c = item_c;
        this.d = item_d;
        this.e = item_e;
    }
    
    
    @Override
    public String toString() {
        return "[" + a + ", " + b + ", " + c + "]";
    }
}