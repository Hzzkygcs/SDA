import java.util.ArrayList;
import java.util.Scanner;

public class AVLTreeSDA {
    public static AvlNode<Long> tree;
    public static Scanner in = new Scanner(System.in);
    
    public static void main(String[] args) {
        Integer[] arr = {35, 25, 45, 15, 28, 38, 50, 10, 20, 30, 48, 12};
        
        for (int i = 0; i < arr.length; i++) {
            tree = AvlTree.insert((long) arr[i], tree);
        }
        
        
        /*int n = 20;
        for (int i = 0; i < n; i++) {
            if (tree != null) {
                tree.print();
            }
            System.out.print("insert: ");
            tree = AvlTree.insert((long) in.nextInt(), tree);
            System.out.println();
        }*/
        
        int n = 20;
        for (int i = 0; i < n; i++) {
            
            if (tree != null) {
                tree.print();
            }
            System.out.print("remove: ");
            tree = AvlTree.remove_data((long) in.nextInt(), tree);
            System.out.println();
        }
        
    }
}





class AvlTree<T extends Comparable<T>>{
    public static <A extends Comparable<A>>
    AvlNode<A> insert(A x, AvlNode<A> parent){
        if (parent == null){
            parent = new AvlNode<>(x, null, null);
        }else if (x.compareTo(parent.data) < 0){
            parent.left = insert(x, parent.left);
            if (Math.abs(AvlNode.height(parent.left) - AvlNode.height(parent.right)) == 2){
                if (x.compareTo(parent.left.data) < 0)
                    parent = single_rotate_with_left_child(parent);
                else
                    parent = double_rotate_with_left_child(parent);
            }
        }else if(x.compareTo(parent.data) > 0){
            parent.right = insert(x, parent.right);
            if (Math.abs(AvlNode.height(parent.left) - AvlNode.height(parent.right)) == 2){
                if (x.compareTo(parent.right.data) > 0)
                    parent = single_rotate_with_right_child(parent);
                else
                    parent = double_rotate_with_right_child(parent);
            }
        }else{
            assert x.compareTo(parent.data) == 0;
        }
        parent.update_height();
        return parent;
    }
    
    
    
    public static <A extends Comparable<A>>
    AvlNode<A> remove_data(A data, AvlNode<A> parent){
        if (parent == null)
            throw new RuntimeException("Node does not exist");
        if (data.compareTo(parent.data) > 0){
            parent.right = remove_data(data, parent.right);
        }else if (data.compareTo(parent.data) < 0){
            parent.left = remove_data(data, parent.left);
        }else{
            if (parent.left != null && parent.right != null){
                if (parent.left.right != null){
                    AvlNode<A> child_left = parent.left;
                    AvlNode<A> child_right = parent.right;
                    parent = predecessor_inorder(parent, true);
                    parent.left = child_left;
                    parent.right = child_right;
                }else{
                    AvlNode<A> child_right = parent.right;
                    parent = parent.left;
                    parent.right = child_right;
                }
            }else if (parent.left != null)
                parent = parent.left;
            else if (parent.right != null)
                parent = parent.right;
            else
                return null;
        }
        parent.update_height();
        parent = parent.rebalance(null, false);
        return parent;
    }
    
    
    public static <A extends Comparable<A>>
    AvlNode<A> get(A x, AvlNode<A> parent){
        if (parent == null)
            return null;
        if (x.compareTo(parent.data) > 0){
            return get(x, parent.right);
        }else if(x.compareTo(parent.data) < 0){
            return get(x, parent.left);
        }else
            return parent;
    }
    
    
    
    public static <A extends Comparable<A>>
    AvlNode<A> predecessor_inorder(AvlNode<A> parent, boolean detach_from_parent){
        if (parent == null)
            return null;
        ArrayList<AvlNode<A>> stack = new ArrayList<>(10);
        stack.add(parent);
        AvlNode<A> curr = parent.left;
        assert curr.right != null;
        
        while (curr != null){
            stack.add(curr);
            curr = curr.right;
        }
        AvlNode<A> ret = stack.get(stack.size()-1);
        
        if (detach_from_parent){
            AvlNode<A> predecessor_indorder = stack.get(stack.size() - 1);
            AvlNode<A> parent_of_predecessor_inorder = stack.get(stack.size() - 2);
            if (parent_of_predecessor_inorder.left == predecessor_indorder)
                throw new IllegalStateException("");  // parent_of_predecessor_inorder.left = predecessor_indorder.left;
            else
                parent_of_predecessor_inorder.right = predecessor_indorder.left;
            
            
            /*while (stack.size() >= 2){
                AvlNode<A> last = stack.get(stack.size() - 1);
                AvlNode<A> before_last = stack.get(stack.size() - 2);
                last.rebalance(before_last, before_last.right == last);
                stack.remove(stack.size()-1);
            }*/
        }
        return ret;
    }
    
    
    public static <A extends Comparable<A>> AvlNode<A> single_rotate_with_left_child(AvlNode<A> avl_2){
        AvlNode<A> avl_1 = avl_2.left;
        avl_2.left = avl_1.right;
        avl_1.right = avl_2;
        
        avl_2.update_height();
        avl_1.update_height();
        return avl_1;
    }
    
    
    public static <A extends Comparable<A>> AvlNode<A> single_rotate_with_right_child(AvlNode<A> avl_2){
        AvlNode<A> avl_1 = avl_2.right;
        avl_2.right = avl_1.left;
        avl_1.left = avl_2;
        
        avl_2.update_height();
        avl_1.update_height();
        return avl_1;
    }
    
    
    public static <A extends Comparable<A>> AvlNode<A> double_rotate_with_left_child(AvlNode<A> avl_3){
        avl_3.left = single_rotate_with_right_child(avl_3.left);
        return single_rotate_with_left_child(avl_3);
    }
    
    
    public static <A extends Comparable<A>> AvlNode<A> double_rotate_with_right_child(AvlNode<A> avl_3){
        avl_3.right = single_rotate_with_left_child(avl_3.right);
        return single_rotate_with_right_child(avl_3);
    }
    
    public static <A extends Comparable<A>> AvlNode<A> upper_bound(AvlNode<A> avl_node, A bound){
        if (avl_node == null) {
            return null;
        }else if (avl_node.data.compareTo(bound) > 0){
            return upper_bound(avl_node.left, bound);
        }else if (avl_node.data.compareTo(bound) < 0){
            AvlNode<A> result = upper_bound(avl_node.right, bound);
            return (result == null)? avl_node:result;
        }else{
            return avl_node;
        }
    }
    
    public static <A extends Comparable<A>> AvlNode<A> lower_bound(AvlNode<A> avl_node, A bound){
        if (avl_node == null) {
            return null;
        }else if (avl_node.data.compareTo(bound) < 0){
            return lower_bound(avl_node.right, bound);
        }else if (avl_node.data.compareTo(bound) > 0){
            AvlNode<A> result = lower_bound(avl_node.left, bound);
            return (result == null)? avl_node:result;
        }else{
            return avl_node;
        }
    }
}



class AvlNode<T extends Comparable<T>>{
    AvlNode<T> left;
    AvlNode<T> right;
    T data;
    int height = 0;
    
    AvlNode(T data, AvlNode<T> left, AvlNode<T> right){
        this.data = data;
        this.left = left;
        this.right = right;
    }
    
    public void update_height(){
        this.height = 0;
        if (left != null && right != null) {
            this.height = Math.max(
                    height(this.left), height(this.right)) + 1;
            return;
        }
        if (left != null)
            this.height = this.left.height + 1;
        if (right != null)
            this.height = this.right.height + 1;
    }
    
    public static <A extends Comparable<A>> int height(AvlNode<A> avlNode){
        if (avlNode == null)
            return 0;
        return  avlNode.height;  /* TODO */
    }
    
    public AvlNode<T>
    rebalance(AvlNode<T> parent, boolean this_at_right){
        AvlNode<T> me = this;
        if (height(this.left) - height(this.right) == 2){
            if (height(this.left.left) > height(this.left.right)){
                me = AvlTree.single_rotate_with_left_child(this);
            }else{
                me = AvlTree.double_rotate_with_left_child(this);
            }
        }else if(height(this.right) - height(this.left) == 2){
            if (height(this.right.right) > height(this.right.left)){
                me = AvlTree.single_rotate_with_right_child(this);
            }else{
                me = AvlTree.double_rotate_with_right_child(this);
            }
        }
        
        if (parent != null) {
            if (this_at_right)
                parent.right = me;
            else
                parent.left = me;
        }
        return me;
    }
    
    public void print(){
        ArrayList<ArrayList<String>> str = new ArrayList<>(this.height + 2);
        for (int i = 0; i < this.height+1; i++) {
            str.add(new ArrayList<>(100));
        }
        print(0, str);
        for (int i = 0; i < str.size(); i++) {
            for (int j = 0; j < str.get(i).size(); j++) {
                System.out.print(str.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println();
    }
    public void print(long depth, ArrayList<ArrayList<String>> str){
        if (this.left != null)
            this.left.print(depth+1, str);
        
        for (int i = 0; i < str.size(); i++) {
            if (i == depth){
                str.get(i).add(String.format("%" + 2 + "s", data.toString()));
            }else{
                str.get(i).add("  ");
            }
        }
        
        if (this.right != null)
            this.right.print(depth+1, str);
    }
    
    @Override
    public String toString() {
        return ":" + data;
    }
}
