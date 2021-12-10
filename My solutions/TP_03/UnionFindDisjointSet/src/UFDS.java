import java.util.ArrayList;
import java.util.Stack;

public class UFDS {
    public static void main(String[] args) {
    
    }
}


class UnionFindDisjointSet{
    ArrayList<Integer> arr;
    ArrayList<Integer> temp_stack = new ArrayList<>(50);
    
    UnionFindDisjointSet(int reserve){
        arr = new ArrayList<>(reserve);
    }
    
    int add_node(){
        int i = arr.size();
        arr.add(i);
        return i;
    }
    UnionFindDisjointSet add_n_node(int n){
        for (int i = 0; i < n; i++) {
            add_node();
        }
        return this;
    }
    
    
    int get_parent(int node){
        return arr.get(node);
    }
    
    int get_root(int node){
        temp_stack.add(node);
        while (node != get_parent(node)){
            node = get_parent(node);
            temp_stack.add(node);
        }
        int root = node;
        
        // path compression
        while (!temp_stack.isEmpty()){
            arr.set(temp_stack.get(temp_stack.size() - 1), root);
        }
        return root;
    }
    boolean is_same_set(int node_a, int node_b){
        return get_root(node_a) == get_root(node_b);
    }
    void union(int to, int from){
        arr.set(get_root(from), get_root(to));
    }
}
