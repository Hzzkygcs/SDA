import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class AvlClass {
}




class Bst<T extends Comparable<T>> {
    BstHead<T> head = new BstHead<>();
    
    public void insert_datas(T[] datas){
        for (int i = 0; i < datas.length; i++) {
            insert_data(datas[i]);
        }
    }
    
    public <U extends Iterable<T>> void insert_datas(U datas){
        Iterator<T> iterator = datas.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iterator.hasNext()){
            insert_data(iterator.next());
        }
    }
    public BstData<T> insert_data(T data){
        BstData<T> temp = new BstData<>(data);
        insert_data_node(temp);
        return temp;
    }
    public BstData<T> root(){
        return head.child;
    }
    
    /*public POSITION insert_data_node(BstData<T> new_node){
        // insert  anode into the tree. Return RIGHT if it's its parent's right child. LEFT otherwise
        assert new_node.is_leaf();
        assert new_node.get_parent() == null;
        
        POSITION curr_position_in_parent = POSITION.LEFT;  // RIGHT juga boleh. bebas
        IBstNode<T> prev = head;
        BstData<T> curr = head.get_child(curr_position_in_parent);
        while (curr != null){
            int temp = new_node.get_data().compareTo(curr.get_data());
            if (temp > 0)
                curr_position_in_parent = POSITION.RIGHT;
            else if (temp < 0)
                curr_position_in_parent = POSITION.LEFT;
            else
                return null;  // berusaha menambahkan data yang sudah ada
            prev = curr;
            curr = curr.get_child(curr_position_in_parent);
        }
    
        assert new_node.get_height() == 0;
        prev.set_child(curr_position_in_parent, new_node, true);
        
        return curr_position_in_parent;
    }*/
    
    public <BstDataTyped extends BstData<T>> POSITION insert_data_node(BstDataTyped new_node){
        return insert_data_node(new_node, true);
    }
    public <BstDataTyped extends BstData<T>> POSITION insert_data_node(BstDataTyped new_node,
                                                                       boolean auto_update_height){
        assert new_node.get_height() == 0;
        Triplet<BstData<T>, IBstNode<T>, POSITION> temp = digger((curr, parent, position) -> {
            return Bst.this.digger_normal_dig(new_node.get_data(), curr);
        });
        
        IBstNode<T> prev = temp.b;
        POSITION curr_position_in_parent = temp.c;
        prev.set_child(curr_position_in_parent, new_node, auto_update_height);
    
        return temp.c;
    }
    
    @SuppressWarnings("unchecked")
    public <BstDataTyped extends BstData<T> & IBstNode<T>>  // typed parameter
    Triplet<BstDataTyped, IBstNode<T>, POSITION>  // return type
    digger(ThreeParamFunc<POSITION, BstDataTyped, IBstNode<T>, POSITION> func){
        /*
         the func type parameter: first parameter is its return value. If it's left, then next we will
         go dig the left child. If it's right, then we will go dig the right child. otherwise (null), we will stop

         The second type parameter is the current node, possibly null.
         The third type parameter is the parent. It should be guaranteed to be not null
         The fourth type parameter is the current node's position relative to the parent.
        */
    
        POSITION curr_position_in_parent = POSITION.LEFT;  // RIGHT juga boleh. bebas
        IBstNode<T> prev = head;
        BstDataTyped curr = (BstDataTyped) head.get_child(curr_position_in_parent);
        while (curr != null){
            POSITION current_position = func.invoke(curr, prev, curr_position_in_parent);

            prev = curr;
            curr_position_in_parent = current_position;
            curr = (BstDataTyped) curr.get_child(curr_position_in_parent);
        }
        return new Triplet<BstDataTyped, IBstNode<T>, POSITION>(curr, prev, curr_position_in_parent);
    }
    
    public <BstDataTyped extends BstData<T>>
    POSITION digger_normal_dig(T compare_data, BstDataTyped current_node){
        if (compare_data == null)
            return null;
        
        int temp = compare_data.compareTo(current_node.get_data());
        if (temp > 0)
            return POSITION.RIGHT;
        if (temp < 0)
            return POSITION.LEFT;
        return null;
    }
    
    
    public BstData<T> find_node(T data){
        // return the node containing the data if exists. null otherwise
        
        BstData<T> temp = this.head.get_child();
        POSITION direction = POSITION.LEFT;
        while (temp != null && !temp.get_data().equals(data)){
            if (data.compareTo(temp.get_data()) > 0)
                direction = POSITION.RIGHT;
            else
                direction = POSITION.LEFT;
            temp = temp.get_child(direction);
        }
        
        return temp;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bst)) return false;
        Bst<?> bst = (Bst<?>) o;
        return Objects.equals(head.get_child(), bst.head.get_child());
    }
    
    public void print() {
        print(-1);
    }
    
    public void print(int data_length) {
        print(data_length, 1);
    }
    public void print(int data_length, int margin) {
        if (this.head.child == null)
            System.out.println("AvlEmpty");
        else
            this.head.child.print(data_length, margin);
    }
    
}

@FunctionalInterface
interface ThreeParamFunc<ret, T, U, V>{
    public ret invoke(T t, U u, V v);
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
}

interface AvlIterateFunction {
    <T extends Comparable<T>> boolean invoke(BstData<T> node, int depth);
}


interface IBstNode<T extends Comparable<T>> {
    BstData<T> get_child(POSITION child_position);
    BstData<T> get_left();
    BstData<T> get_right();
    void _set_child(POSITION child_position, BstData<T> new_child);
    default void set_child(POSITION child_position, BstData<T> new_child, boolean update_height){
        /*
         * Melepaskan child pada child_position saat ini, dan melepaskan new_child dari parentnya saat ini.
         * Setelah itu, menghubungkan new_child dengan node ini, dan meng-adjust ulang height node ini serta parentsnya
         */
        if (new_child == this)
            throw new IllegalStateException("Trying to make a circular child");
        if (this.get_child(child_position) != null)
            this.get_child(child_position).detach_from_parent(false);
        if (new_child != null){
            if (new_child.parent != null)
                new_child.detach_from_parent(true);
            new_child.parent = this;
        }
        
        this._set_child(child_position, new_child);
        if (update_height)
            this.update_this_and_parents_properties();
    }
    int get_height();
    void _set_height(int new_height);
    IBstNode<T> get_parent();
    void detach_from_parent();
    
    void _set_parent(IBstNode<T> new_parent);
    void set_parent(POSITION pos, IBstNode<T> new_parent, boolean update_parent_height);
    void update_this_properties();  // returns its height
    IBstNode<T> update_parents_properties();
    IBstNode<T> update_this_and_parents_properties();
    IBstNode<T> update_this_and_parents_properties(int initial_height);
    POSITION find_child_pos(IBstNode<T> child);
}


interface BstSpecialNode<T extends Comparable<T>> extends IBstNode<T> {
}

class BstHead<T extends Comparable<T>> implements BstSpecialNode<T> {
    BstData<T> child;
    
    @Override
    public BstData<T> get_child(POSITION child_position) {
        return child;
    }
    
    @Override
    public BstData<T> get_left() {
        return this.child;
    }
    
    @Override
    public BstData<T> get_right() {
        return this.child;
    }
    
    @Override
    public void _set_child(POSITION child_position, BstData<T> new_child) {
        this.child = new_child;
    }
    
    
    
    public BstData<T> get_child(){
        return child;
    }
    
    
    @Override
    public int get_height() {
        if (this.child == null)
            return -1;  // kalau BST hanya terdiri atas root, maka heightnya 0. Ini bahkan tidak punya root
        return this.child.get_height();
    }
    
    @Override
    public void _set_height(int new_height) {
    }
    
    @Override
    public IBstNode<T> get_parent() {
        return null;
    }
    
    @Override
    public void detach_from_parent() {
    }
    
    @Override
    public void _set_parent(IBstNode<T> new_parent) {
    }
    
    @Override
    public void set_parent(POSITION pos, IBstNode<T> new_parent, boolean bool) {
    }
    
    @Override
    public void update_this_properties() {
        this.get_height();
    }
    
    @Override
    public IBstNode<T> update_parents_properties() {
        return null;
    }
    
    @Override
    public IBstNode<T> update_this_and_parents_properties() {
        return null;
    }
    
    @Override
    public IBstNode<T> update_this_and_parents_properties(int initial_height) {
        return null;
    }
    
    @Override
    public POSITION find_child_pos(IBstNode<T> child) {
        if (child != this.child)
            return null;
        return POSITION.LEFT;
    }
    
    @Override
    public String toString() {
        return "BstHead{}";
    }
}

class BstData<T extends Comparable<T>> implements IBstNode<T> {
    protected IBstNode<T> parent;
    protected BstData<T> left;
    protected BstData<T> right;
    protected int height = 0;
    public T data;
    
    public BstData(T data){
        this.data = data;
    }
    
    public BstData(IBstNode<T> parent, T data){
        this.parent = parent;
        this.data = data;
    }
    
    public void detach_from_parent(){
        detach_from_parent(true);
    }
    public void detach_from_parent(boolean adjust_parent_height){
        if (this.parent == null)
            return;
        
        POSITION position = this.parent.find_child_pos(this);
        assert position != null;
        
        if (adjust_parent_height) {
            int parent_new_height = 0;
            if (parent.get_child(position.complement()) != null)
                parent_new_height = parent.get_child(position.complement()).get_height() + 1;
            parent._set_height(parent_new_height);
            parent.update_parents_properties();
        }
        parent._set_child(position, null);
        this._set_parent(null);
    }
    
    
    
    
    public void _set_parent(IBstNode<T> new_parent) {
        this.parent = new_parent;
    }
    
    @Override
    public void set_parent(POSITION pos, IBstNode<T> new_parent, boolean update_height) {
        new_parent.set_child(pos, this, update_height);
    }
    
    
    
    
    public IBstNode<T> update_this_and_parents_properties(int initial_height){
        this._set_height(initial_height);
        return this.update_parents_properties();
    }
    
    public IBstNode<T> update_this_and_parents_properties(){
        this.update_this_properties();
        return this.update_parents_properties();
    }
    
    public int update_this_height(){
        int left_height = -1;
        int right_height = -1;
        
        if (this.left != null)
            left_height = this.left.height;
        if (this.right != null)
            right_height = this.right.height;
        
        this.height = Math.max(left_height, right_height) + 1;
        return this.height;
    }
    
    public void update_this_properties(){
        update_this_height();
    }
    
    public IBstNode<T> update_parents_properties(){
        /*
         * Return null if all of its parents are updated, or return the first unupdated parents
         * parents = predecessor. Not to be confused with 'predecessor' in predecessor_inorder()
         */
        
        if (this.parent == null)
            return null;
        IBstNode<T> temp_node = this.get_parent();
        int prev_height = temp_node.get_height();
    
        while (temp_node != null){
            temp_node.update_this_properties();
//            int curr_height =
//            if (curr_height == prev_height)
//                break;
            temp_node = temp_node.get_parent();
        }
        return temp_node;
    }
    
    
    public boolean is_leaf() {
        return this.left == null && this.right == null;
    }
    
    
    public void remove_this() {
        // remove_this != detach_this.
        // remove_this -> detach this and use its child (or its predecessor inorder)
        // to take its place.
        
        if (this.is_leaf()){
            this.detach_from_parent();
            return;
        }
        BstData<T> pengganti;
        BstData<T> this_left = this.left;
        BstData<T> this_right = this.right;
        IBstNode<T> this_parent = this.parent;
        POSITION this_pos_in_parent = this.parent.find_child_pos(this);
        
        if (this.left == null || this.right == null){  // kalau node yg mau dihapus punya satu anak
            this.detach_from_parent(false);
            
            pengganti = this.left;
            if (this.right != null)
                pengganti = this.right;
            pengganti.detach_from_parent(true);
            
            this_parent.set_child(this_pos_in_parent, pengganti, false);
        }else{  // kalau node yg mau dihapus punya dua anak
            pengganti = this.predecessor_inorder();
            assert pengganti.is_leaf();
            pengganti.detach_from_parent(true);
            this.detach_from_parent(false);
            this_left.detach_from_parent(false);
            this_right.detach_from_parent(false);
    
            this_parent._set_child(this_pos_in_parent, pengganti);
            pengganti._set_parent(this_parent);
            pengganti._set_child(POSITION.LEFT, this_left);
            pengganti._set_child(POSITION.RIGHT, this_right);
            this_left._set_parent(pengganti);
            this_right._set_parent(pengganti);
        }
        pengganti.update_this_and_parents_properties();
    }
    
    public Quadruplet<IBstNode<T>, POSITION, BstData<T>, BstData<T>> detach_this(boolean adjust_parent_height){
        // detach_this != remove_this
        // detach_this -> detach its parent and its children, returning those three as pieces.
        // the returned a quadruplet of its parent, its position relative to parent,
        // its left child, and its right child respectively
    
        Quadruplet<IBstNode<T>, POSITION, BstData<T>, BstData<T>> ret =
                new Quadruplet<>(this.parent,
                                 this.parent.find_child_pos(this),
                                 this.left,
                                 this.right);
        this.detach_from_parent(adjust_parent_height);
        if (this.left != null)
            this.left.detach_from_parent(false);
        if (this.right != null)
            this.right.detach_from_parent(false);
        return ret;
    }
    
    
    public Iterator<Pair<BstData<T>, Integer>> inorder_iterator() {
        return new InorderIterator();
    }
    
    
    public BstData<T> successor_inorder() {
        if (this.right == null)
            return null;
        BstData<T> temp = this.right;
        while (true){
            if (temp.get_left() == null)
                return temp;
            temp = temp.get_left();
        }
    }
    
    public BstData<T> predecessor_inorder() {
        if (this.left == null)
            return null;
        BstData<T> temp = this.left;
        while (true){
            if (temp.get_right() == null)
                return temp;
            temp = temp.get_right();
        }
    }
    
    
    class InorderIterator implements Iterator<Pair<BstData<T>, Integer>> {
        // Each iteration returns a tuple, where the left tuple is the iterated node, and the right tuple is the depth
        
        ArrayList<Triplet<BstData<T>, Boolean, Integer>> stack =
                new ArrayList<>(4 * BstData.this.get_height() + 5);
        
        public InorderIterator() {
            stack.add(new Triplet<>(BstData.this, false, 0));
        }
        
        public void process_next(){
            while (!stack.isEmpty()){
                if (stack.get(stack.size() - 1).b){
                    break;
                }else{
                    Triplet<BstData<T>, Boolean, Integer> current_tuple = stack.remove(stack.size() - 1);
                    
                    if (current_tuple.a.get_right() != null)
                        stack.add(new Triplet<>(current_tuple.a.get_right(), false, current_tuple.c + 1));
                    stack.add(new Triplet<>(current_tuple.a, true, current_tuple.c));
                    if (current_tuple.a.get_left() != null)
                        stack.add(new Triplet<>(current_tuple.a.get_left(), false, current_tuple.c + 1));
                }
            }
    
            assert stack.isEmpty() || stack.get(stack.size()-1).b;
        }
        
        
        public boolean hasNext() {
            process_next();
            return !stack.isEmpty();
        }
        
        
        public Pair<BstData<T>, Integer> next() {
            process_next();
            Triplet<BstData<T>, Boolean, Integer> popped = stack.remove(stack.size() - 1);
            return new Pair<>(popped.a, popped.c);
        }
        
        
        public void remove() {
            next().a.detach_from_parent();
        }
        
    }
    
    public BstData<T> get_child(POSITION child_position) {
        if (child_position == POSITION.LEFT)
            return this.left;
        return this.right;
    }
    
    public T get_data() {
        return this.data;
    }
    
    
    public void set_data(T new_data) {
        this.data = new_data;
    }
    
    
    public BstData<T> get_right() {
        return this.right;
    }
    
    @Override
    public void set_child(POSITION child_position, BstData<T> new_child, boolean update_properties){
        boolean check = true;
        
        if (new_child != null) {
            boolean trying_to_set_new_child_in_the_left = child_position.equals(POSITION.LEFT);
            boolean new_child_should_be_in_the_left = new_child.get_data().compareTo(this.get_data()) < 0;
            check = (trying_to_set_new_child_in_the_left == new_child_should_be_in_the_left);
        }
        
        if (check)
            IBstNode.super.set_child(child_position, new_child, update_properties);
        else
            throw new IllegalStateException("Trying to put child in the wrong position");
    }
    
    
    @Override
    public void _set_child(POSITION child_position, BstData<T> new_child) {
        if (child_position == POSITION.LEFT)
            this.left = new_child;
        else if (child_position == POSITION.RIGHT)
            this.right = new_child;
        else
            throw new IllegalStateException("Setting child to null position");
    }
    
    
    public void _set_right(BstData<T> new_data) {
        this.right = new_data;
    }
    
    public BstData<T> get_left() {
        return this.left;
    }
    
    public void _set_left(BstData<T> new_data) {
        this.left = new_data;
    }
    
    public int get_height() {
        return this.height;
    }
    
    public void _set_height(int new_height) {
        this.height = new_height;
    }
    
    public IBstNode<T> get_parent() {
        return parent;
    }
    
    
    public POSITION find_child_pos(IBstNode<T> child) {
        if (child == this.left)
            return POSITION.LEFT;
        if (child == this.right)
            return POSITION.RIGHT;
        return null;
    }
    
    
    public String toString() {
        return "<" + data + ">";
    }
    
    
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return this.equals(false, o);
    }
    
    
    public boolean equals(boolean check_height, Object o) {
        if (this == o) return true;
        if (!(o instanceof BstData)) return false;
        //noinspection unchecked
        BstData<T> avl_data = (BstData<T>) o;
        
        if (! avl_data.get_data().equals(this.get_data()))
            return false;
        
        if (!check_height && this.height != avl_data.height)
            return false;
        
    
        if (this.is_leaf())
            return true;
    
        boolean ret1 = true;
        boolean ret2 = true;
        
        if (this.left == null){
            if (avl_data.left != null)
                return false;
        }else if (! this.left.get_data().equals(avl_data.left.get_data()))
            return false;
        else if (this.left != null)
            ret1 = this.left.equals(check_height, avl_data.left);
        
        if (this.right == null){
            if (avl_data.right != null)
                return false;
        }else if (! this.right.get_data().equals(avl_data.right.get_data()))
            return false;
        else if (this.right != null)
            ret2 = this.right.equals(check_height, avl_data.right);
        
        
        return  ret1 && ret2;
    }
    
    
    public void print() {
        print(-1, 1);
    }
    
    
    public void print(int data_length, int margin_size) {
        System.out.println(print_str(data_length, margin_size));
    }
    
    public String print_str(int data_length, int margin_size) {
        int min_length = data_length;
        String padding = str_multiply(" ", data_length + margin_size);
        String margin = str_multiply(" ", margin_size);
        
        
        ArrayList<ArrayList<String>> arr = new ArrayList<>(this.height+2);
        for (int i = 0; i < this.height+1; i++) {
            arr.add(new ArrayList<>(this.height * 4));
        }
        
        
        InorderIterator it = new InorderIterator();
        while (it.hasNext()){
            Pair<BstData<T>, Integer> temp = it.next();
            int current_depth = temp.b;
    
            if (data_length == -1) {
                min_length = temp.a.get_data().toString().length();
                padding = str_multiply(" ", min_length + margin_size);
            }
            
            for (int i = 0; i < arr.size(); i++) {
                if (i != current_depth)
                    arr.get(i).add(padding);
                else {
                    String temp_output = temp.a.get_data().toString();
                    temp_output = String.format("%" + min_length + "s", temp_output);
                    arr.get(i).add(temp_output + margin);
                }
            }
        }
        
        StringBuilder ret = new StringBuilder(this.height * 20);
        for (int i = 0; i < arr.size(); i++) {
            for (int j = 0; j < arr.get(i).size(); j++) {
                ret.append(arr.get(i).get(j));
            }
            ret.append('\n');
        }
        return ret.toString();
    }
    
    public static String str_multiply(String str, int number){
        StringBuilder temp = new StringBuilder(number * str.length() + 2);
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < number; i++) {
            temp.append(' ');
        }
        return temp.toString();
    }
    
    
    public int hashCode() {
        return Objects.hash(data);
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
        return "[" + a + ", " + b + "]";
    }
}

class ComparablePair<T extends Comparable<T>, U extends Comparable<U>> extends Pair<T,U>
        implements Comparable<ComparablePair<T, U>>{
    public ComparablePair(T item_a, U item_b) {
        super(item_a, item_b);
    }
    
    @Override
    public int compareTo(ComparablePair<T, U> o) {
        int temp = a.compareTo(o.a);
        if (temp != 0)
            return temp;
        return b.compareTo(o.b);
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



class AvlData<T extends Comparable<T>> extends BstData<T>{
    public AvlData(T data) {
        super(data);
    }
    
    public AvlData(IBstNode<T> parent, T data) {
        super(parent, data);
    }
    
    public void rotate_to_parent(boolean update_height_of_this_and_prev_parent){
        assert this.parent instanceof BstData;
        
        // all terms here (sibling, parent, etc) are from the perspective of this node BEFORE rotating
        BstData<T> parent = (BstData<T>) this.parent;
        
        POSITION pos_rel_to_parent = parent.find_child_pos(this);
        Quadruplet<IBstNode<T>, POSITION, BstData<T>, BstData<T>> grand_parent_this_and_sibling =
                parent.detach_this(false);
        
        IBstNode<T> grand_parent = grand_parent_this_and_sibling.a;
        BstData<T> sibling;
        if (grand_parent_this_and_sibling.c != this) {
            sibling = grand_parent_this_and_sibling.c;
            parent.set_child(POSITION.LEFT, sibling, false);
            
            assert pos_rel_to_parent == POSITION.RIGHT;
        }else {
            sibling = grand_parent_this_and_sibling.d;
            parent.set_child(POSITION.RIGHT, sibling, false);
            
            assert pos_rel_to_parent == POSITION.LEFT;
        }
        
        // assigning its corresponding child to the parent
        // kalau node ini di kirinya parent, maka right child dari node ini akan di assign ke left child dari parent.
        // setelah itu parent di assign ke right child dari node ini. Hal yang sama berlaku sebaliknya
        
        parent.set_child(pos_rel_to_parent,
                         this.get_child(pos_rel_to_parent.complement()), false);
        this.set_child(pos_rel_to_parent.complement(), parent, false);
        grand_parent.set_child(grand_parent_this_and_sibling.b, this, false);
        
        if (update_height_of_this_and_prev_parent)
            parent.update_this_and_parents_properties();
    }
}

class Avl<T extends Comparable<T>> extends Bst<T>{
    public AvlData<T> insert_data(T data){
        AvlData<T> temp = new AvlData<>(data);
        insert_data_node(temp);
        return temp;
    }
    public POSITION insert_data_node(AvlData<T> new_node){
        return super.insert_data_node(new_node);
    }
    
    public AvlData<T> root(){
        return (AvlData<T>) head.child;
    }
    public AvlData<T> find_node(T data){return (AvlData<T>) super.find_node(data); }
    
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
