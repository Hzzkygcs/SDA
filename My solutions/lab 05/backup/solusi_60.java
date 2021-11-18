import java.io.*;
import java.util.*;

public class Solusi_60 {
    public static long __TESTCASE__ = -1;
    public static InputReader in = new InputReader(System.in);
    public static PrintWriter out = new PrintWriter(System.out);
//    public static PrintStream out = System.out;
    
    
    public static AVLTree<Kotak> kotak_tree = new AVLTree<>();
    public static HashMap<String, HargaDanTipe> tipe_from_name = new HashMap<>(10000+7);
    
    
    /*public static void main_(String[] args) {
        kotak_tree = new AVLTree<>();
        Integer[] arr = {3, 56,2, 15, 12, 23, 3, 4,35};
    
        for (int i = 0; i < arr.length; i++) {
            tree = AvlTree.insert((long) arr[i], tree);
        }
    
        
        int n = 10;
        for (int i = 0; i < n; i++) {
            tree = AvlTree.remove_data((long) in.nextInt(), tree);
            if (tree != null) {
                tree.print();
            }
            System.out.println();
        }
        
    }
    */
    
    public static void main(String[] args) {
        kotak_tree = new AVLTree<>();
        tipe_from_name.clear();
        
        
        //Menginisialisasi kotak sebanyak N
        int N = in.nextInt();
        for(int i = 0; i < N; i++){
            String nama = in.next();
            int harga = in.nextInt();
            int tipe = in.nextInt();
            handleStock(nama, harga, tipe);
        }
        
        //Query 
        //(method dan argumennya boleh diatur sendiri, sesuai kebutuhan)
        int NQ = in.nextInt();
        for(int i = 0; i < NQ; i++){
            String Q = in.next();
            if (Q.equals("BELI")){
                int L = in.nextInt();
                int R = in.nextInt();
                handleBeli(L, R);
                
            }else if(Q.equals("STOCK")){
                String nama = in.next();
                int harga = in.nextInt();
                int tipe = in.nextInt();
                handleStock(nama, harga, tipe);
                
            }else{ //SOLD_OUT
                String nama = in.next();
                handleSoldOut(nama);
                
            }
        }
        
        out.flush();
    }
    
    // TODO
    static void handleBeli(int L, int R){
        Kotak dummy = new Kotak(R, null);
        dummy.node_ini = new AvlNode<Kotak>(dummy);
        Kotak upper_bound = kotak_tree.upper_boundary(kotak_tree.root, dummy);
        dummy.harga = L;
        Kotak lower_bound  = kotak_tree.lower_boundary(kotak_tree.root, dummy);
        
        if (upper_bound == null || lower_bound == null){
            out.println("-1 -1");
            return;
        }
        
        if (upper_bound.compareTo(lower_bound) == 0){
            assert lower_bound == upper_bound;
            if (AVLTree.height_static(upper_bound.tipe_permen.root) <= 1){
                out.println("-1 -1");
            }else{
                out.print(lower_bound.harga);
                out.print(' ');
                out.println(upper_bound.harga);
            }
            return;
        }
    
        if (lower_bound.compareTo(upper_bound) > 0){
            out.println("-1 -1");
            return;
        }
    
        out.print(lower_bound.harga);
        out.print(' ');
        out.println(upper_bound.harga);
        
    }
    
    
    static void handleStock(String nama, int harga, int tipe){
        Kotak kotak = new Kotak(harga, null);
        kotak_tree.root = kotak_tree.insert(kotak_tree.root, kotak);
        AvlNode<Kotak> node = kotak_tree.get(kotak_tree.root, kotak);  // hati-hati node null.
        kotak.node_ini = node;
        kotak = node.key;  // bisa aja node.data != kotak, pada kasus ketika kotak dengan harga tsb sudah ada
        
        Permen permen = new Permen(tipe, 0);
        AvlNode<Permen> result = kotak.tipe_permen.get(kotak.tipe_permen.root, permen);
        if (result == null){
            kotak.tipe_permen.root = kotak.tipe_permen.insert(kotak.tipe_permen.root, permen);
        }else
            permen = result.key;
        permen.jumlah += 1;
        
        tipe_from_name.put(nama, new HargaDanTipe(harga, tipe));
    }
    
    // TODO
    static void handleSoldOut(String nama){
        long harga = tipe_from_name.get(nama).harga;
        long tipe = tipe_from_name.get(nama).tipe;
        
        Kotak kotak = new Kotak(harga, null);
        AvlNode<Kotak> kotak_node = kotak_tree.get(kotak_tree.root, kotak);
        kotak = kotak_node.key;
        Permen permen = kotak.tipe_permen.get(kotak.tipe_permen.root, new Permen(tipe)).key;
        permen.jumlah -= 1;
        if (permen.jumlah == 0){
            kotak.tipe_permen.root = kotak.tipe_permen.deleteNode(kotak.tipe_permen.root, permen);
            
            if (kotak.tipe_permen.root == null){
                kotak_tree.root = kotak_tree.deleteNode(kotak_tree.root, kotak);
            }
        }
    }
    
    
    // taken from https://codeforces.com/submissions/Petr
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



class HargaDanTipe {
    long harga;
    long tipe;
    
    
    HargaDanTipe(long harga, long tipe){
        this.harga = harga;
        this.tipe = tipe;
    }
    
    @Override
    public String toString() {
        return harga + ":" + tipe;
    }
}


class Permen implements Comparable<Permen> {
    long tipe;
    long jumlah;
    
    Permen(long tipe){
        this(tipe, 0L);
    }
    
    Permen(long tipe, long jumlah){
        this.tipe = tipe;
        this.jumlah = jumlah;
    }
    
    @Override
    public String toString() {
        return tipe + "@" + jumlah;
    }
    
    @Override
    public int compareTo(Permen o) {
        return Long.compare(this.tipe, o.tipe);
    }
}


class Kotak implements Comparable<Kotak> {
    long harga;
    AvlNode<Kotak> node_ini;
    AVLTree<Permen> tipe_permen = new AVLTree<>();
    
    
    Kotak(long harga, AvlNode<Kotak> node){
        this.harga = harga;
        this.node_ini = node;
    }
    
    @Override
    public int compareTo(Kotak o) {
        return Long.compare(harga, o.harga);
    }
    
    @Override
    public String toString() {
        return "K:" + harga;
    }
}

/*

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
            throw new RuntimeException("AvlNode does not exist");
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
            
            
            */
/*while (stack.size() >= 2){
                AvlNode<A> last = stack.get(stack.size() - 1);
                AvlNode<A> before_last = stack.get(stack.size() - 2);
                last.rebalance(before_last, before_last.right == last);
                stack.remove(stack.size()-1);
            }*//*

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
        return  avlNode.height;  */
/* TODO *//*

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
                str.get(i).add(String.format("%" + 5 + "s", data.toString()));
            }else{
                str.get(i).add("     ");
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

*/






// Reference https://www.geeksforgeeks.org/avl-tree-set-2-deletion/?ref=lbp
class AvlNode<T extends Comparable<T>>
{
    int height;
    AvlNode<T> left, right;
    T key;
    
    AvlNode(T d)
    {
        key = d;
        height = 1;
    }
    
    @Override
    public String toString() {
        return "[" + this.key + "]";
    }
}

class AVLTree<T extends Comparable<T>>
{
    AvlNode<T> root;
    
    // A utility function to get height of the tree
    int height(AvlNode<T> N)
    {
        if (N == null)
            return 0;
        return N.height;
    }
    
    
    // A utility function to get height of the tree
    static <T extends Comparable<T>> int height_static(AvlNode<T> N)
    {
        if (N == null)
            return 0;
        return N.height;
    }
    
    // A utility function to get maximum of two integers
    int max(int a, int b)
    {
        return Math.max(a, b);
    }
    
    // A utility function to right rotate subtree rooted with y
    // See the diagram given above.
    AvlNode<T> rightRotate(AvlNode<T> y)
    {
        AvlNode<T> x = y.left;
        AvlNode<T> T2 = x.right;
        
        // Perform rotation
        x.right = y;
        y.left = T2;
        
        // Update heights
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;
        
        // Return new root
        return x;
    }
    
    // A utility function to left rotate subtree rooted with x
    // See the diagram given above.
    AvlNode<T> leftRotate(AvlNode<T> x)
    {
        AvlNode<T> y = x.right;
        AvlNode<T> T2 = y.left;
        
        // Perform rotation
        y.left = x;
        x.right = T2;
        
        // Update heights
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;
        
        // Return new root
        return y;
    }
    
    // Get Balance factor of node N
    int getBalance(AvlNode<T> N)
    {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }
    
    
    
    AvlNode<T> get(AvlNode<T> root, T key){
        if (root == null)
            return null;
    
        if (key.compareTo(root.key) < 0)
            return get(root.left, key);
        else if (key.compareTo(root.key) > 0)
            return get(root.right, key);
        else
            return root;
        
    }
    
    AvlNode<T> insert(AvlNode<T> node, T key)
    {
        /* 1. Perform the normal BST rotation */
        if (node == null)
            return (new AvlNode<>(key));
        
        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key);
        else // Equal keys not allowed
            return node;
        
        /* 2. Update height of this ancestor node */
        node.height = 1 + max(height(node.left),
                              height(node.right));
 
        /* 3. Get the balance factor of this ancestor
        node to check whether this node became
        Wunbalanced */
        int balance = getBalance(node);
        
        // If this node becomes unbalanced, then
        // there are 4 cases Left Left Case
        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);
        
        // Right Right Case
        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);
        
        // Left Right Case
        if (balance > 1 && key.compareTo(node.left.key) > 0)
        {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        
        // Right Left Case
        if (balance < -1 && key.compareTo(node.right.key) < 0)
        {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        
        /* return the (unchanged) node pointer */
        return node;
    }
    
    /* Given a non-empty binary search tree, return the
    node with minimum key value found in that tree.
    Note that the entire tree does not need to be
    searched. */
    AvlNode<T> minValueNode(AvlNode<T> node)
    {
        AvlNode<T> current = node;
        
        /* loop down to find the leftmost leaf */
        while (current.left != null)
            current = current.left;
        
        return current;
    }
    
    AvlNode<T> deleteNode(AvlNode<T> root, T key)
    {
        // STEP 1: PERFORM STANDARD BST DELETE
        if (root == null)
            return root;
        
        // If the key to be deleted is smaller than
        // the root's key, then it lies in left subtree
        if (key.compareTo(root.key) < 0)
            root.left = deleteNode(root.left, key);
            
            // If the key to be deleted is greater than the
            // root's key, then it lies in right subtree
        else if (key.compareTo(root.key) > 0)
            root.right = deleteNode(root.right, key);
            
            // if key is same as root's key, then this is the node
            // to be deleted
        else
        {
            
            // node with only one child or no child
            if ((root.left == null) || (root.right == null))
            {
                AvlNode<T> temp = null;
                if (temp == root.left)
                    temp = root.right;
                else
                    temp = root.left;
                
                // No child case
                if (temp == null)
                {
                    temp = root;
                    root = null;
                }
                else // One child case
                    root = temp; // Copy the contents of
                // the non-empty child
            }
            else
            {
                
                // node with two children: Get the inorder
                // successor (smallest in the right subtree)
                AvlNode<T> temp = minValueNode(root.right);
                
                // Copy the inorder successor's data to this node
                root.key = temp.key;
                
                // Delete the inorder successor
                root.right = deleteNode(root.right, temp.key);
            }
        }
        
        // If the tree had only one node then return
        if (root == null)
            return root;
        
        // STEP 2: UPDATE HEIGHT OF THE CURRENT NODE
        root.height = max(height(root.left), height(root.right)) + 1;
        
        // STEP 3: GET THE BALANCE FACTOR OF THIS NODE (to check whether
        // this node became unbalanced)
        int balance = getBalance(root);
        
        // If this node becomes unbalanced, then there are 4 cases
        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);
        
        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0)
        {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        
        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);
        
        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0)
        {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }
        
        return root;
    }
    
    // A utility function to print preorder traversal of
    // the tree. The function also prints height of every
    // node
    void preOrder(AvlNode<T> node)
    {
        if (node != null)
        {
            System.out.print(node.key + " ");
            preOrder(node.left);
            preOrder(node.right);
        }
    }
    
    
    
    // Function to find ceil of a given input in BST.
    // If input is more than the max key in BST,
    // return -1
    T lower_boundary(AvlNode<T> node, T input)
    {
        
        // Base case
        if (node == null) {
            return null;
        }
        
        // We found equal key
        if (node.key.compareTo(input) == 0) {
            return node.key;
        }
        
        // If root's key is smaller,
        // ceil must be in right subtree
        if (node.key.compareTo(input) < 0) {
            return lower_boundary(node.right, input);
        }
        
        // Else, either left subtree or root
        // has the ceil value
        T ceil = lower_boundary(node.left, input);
        
        return (ceil != null && ceil.compareTo(input) >= 0) ? ceil : node.key;
    }
    
    /*This function is used to find floor of a key*/
    <T extends Comparable<T>> T upper_boundary(AvlNode<T> root, T key)
    {
        if (root == null)
            return null;
        
        /* If root->data is equal to key */
        if (root.key.compareTo(key) == 0)
            return root.key;
        
        /* If root->data is greater than the key */
        if (root.key.compareTo(key) > 0)
            return upper_boundary(root.left, key);
 
        /* Else, the floor may lie in right subtree
           or may be equal to the root*/
        T floorValue = upper_boundary(root.right, key);
        return (floorValue != null && floorValue.compareTo(key) <= 0) ? floorValue : root.key;
    }
    
    
    public static void main(String[] args)
    {
        AVLTree<Integer> tree = new AVLTree<>();
        
        /* Constructing tree given in the above figure */
        tree.root = tree.insert(tree.root, 9);
        tree.root = tree.insert(tree.root, 5);
        tree.root = tree.insert(tree.root, 10);
        tree.root = tree.insert(tree.root, 0);
        tree.root = tree.insert(tree.root, 6);
        tree.root = tree.insert(tree.root, 11);
        tree.root = tree.insert(tree.root, -1);
        tree.root = tree.insert(tree.root, 1);
        tree.root = tree.insert(tree.root, 2);
 
        /* The constructed AVL Tree would be
        9
        / \
        1 10
        / \ \
        0 5 11
        / / \
        -1 2 6
        */
        System.out.println("Preorder traversal of "+
                                   "constructed tree is : ");
        tree.preOrder(tree.root);
        
        tree.root = tree.deleteNode(tree.root, 10);
 
        /* The AVL Tree after deletion of 10
        1
        / \
        0 9
        /     / \
        -1 5 11
        / \
        2 6
        */
        System.out.println();
        System.out.println("Preorder traversal after "+
                                   "deletion of 10 :");
        tree.preOrder(tree.root);
    }
}

// This code has been contributed by Mayank Jaiswal