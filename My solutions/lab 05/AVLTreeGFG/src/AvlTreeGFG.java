import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AvlTreeGFG {
    
    public static Scanner in = new Scanner(System.in);
    
    public static AVLTree<Long> tree = new AVLTree<>();
    
    public static void main(String[] args) {
        Integer[] arr = {35, 25, 45, 15, 28, 38, 50, 10, 20, 30, 48, 12};
        
        for (int i = 0; i < arr.length; i++) {
            tree.root = tree.insert(tree.root, (long) arr[i]);
        }
        
        
        /*int n = 20;
        for (int i = 0; i < n; i++) {
            if (tree != null) {
                tree.root.print();
            }
            System.out.print("insert: ");
            tree.root = tree.insert(tree.root, (long) in.nextInt());
            System.out.println();
        }*/
        
        int n = 20;
        for (int i = 0; i < n; i++) {
            
            if (tree.root != null) {
                tree.root.print();
            }
            System.out.print("remove: ");
            tree.root = tree.deleteNode(tree.root, (long) in.nextInt());
            System.out.println();
        }
        
    }
}




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
                str.get(i).add(String.format("%" + 2 + "s", this.key.toString()));
            }else{
                str.get(i).add("  ");
            }
        }
        
        if (this.right != null)
            this.right.print(depth+1, str);
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
    
    public void to_list(AvlNode<T> root, List<T> list){
        if (root == null)
            return;
        
        if (root.left != null)
            to_list(root.left, list);
        list.add(root.key);
        if (root.right != null)
            to_list(root.right, list);
    }
    
    
}
