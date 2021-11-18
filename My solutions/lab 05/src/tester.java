/*
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.sort;


public class tester {
    public static void main(String[] args) {
//        assert false;  // pastikan error ketika assert sama dengan false
//        assertTrue(false);  // pastikan error ketika assert sama dengan false
//        assertFalse(true);  // pastikan error
    
        test_avl_rebalance_insertion();
        test_setchild_unraw();
        test_input_datas_and_inorder_traversal();
//        test_node_rotation();
    }
    
    public static void assertTrue(boolean bool){
        assertTrue(bool, "");
    }
    public static void assertTrue(boolean bool, String message){
        if (!bool)
            throw new RuntimeException("Assertion error:  " + message);
    }
    public static void assertFalse(boolean bool){
        assertFalse(bool, "");
    }
    public static void assertFalse(boolean bool, String message){
        if (bool)
            throw new RuntimeException("Assertion error:  " + message);
    }
    
    public static void test_setchild_unraw(){
        Bst<Integer> bst1 = new Bst<>();
        bst1.insert_data(10);
        BstData<Integer> node_5 = bst1.insert_data(5);
        bst1.insert_data(4);
        bst1.insert_data(7);
        bst1.insert_data(6);
        bst1.insert_data(8);
        
//        bst1.print();
    
        bst1.find_node(10).set_child(POSITION.LEFT, bst1.find_node(7), true);
        assertTrue(bst1.find_node(7).get_left().get_data() == 6);
        assertTrue(bst1.find_node(7).get_right().get_data() == 8);
        assertTrue(((BstData<Integer>) bst1.find_node(7).get_parent()).get_data() == 10);
        assertTrue(bst1.find_node(10).get_left().get_data() == 7);
        assertTrue(node_5.get_parent() == null);
    }
    
    public static void test_avl_rebalance_insertion(){
        Integer[] source;
        Avl<Integer> avl1;
        
        source = new Integer[]{50, 2, 93, 4, 85, 90, 83, 40, 9, 3, 49, 52};
        avl1 = new Avl<>();
        avl1.insert_datas(source);
        avl1.print();
        
        source = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        avl1 = new Avl<>();
        avl1.insert_datas(source);
        avl1.print();
        
        avl1.find_node(5).remove_this();
        avl1.print();
        avl1.find_node(3).remove_this();
        avl1.print();
        
        
        source = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        avl1 = new Avl<>();
        avl1.insert_datas(source);
        avl1.print();
        
        avl1.find_node(4).remove_this();
        avl1.print();
        
    }
    
    public static void test_node_rotation(){  // may no longer valid because we've changed its insert implementation
        
        Integer[] source = {25, 50, 15, 35, 44, 70, 90, 66, 10, 4, 12, 22, 18, 24, 31};
        Avl<Integer> avl1 = new Avl<>();
        avl1.insert_datas(source);
        avl1.find_node(66).rotate_to_parent(true);
        avl1.find_node(70).rotate_to_parent(true);
    
        {
            Avl<Integer> avl_check = new Avl<>();
            avl_check.insert_datas(source);
            assertTrue(avl_check.equals(avl1));
        }
        
        avl1.find_node(4).rotate_to_parent(true);
        avl1.find_node(15).rotate_to_parent(true);
        avl1.find_node(44).rotate_to_parent(true);
        avl1.find_node(44).rotate_to_parent(true);
    
        {
            Avl<Integer> avl_check = new Avl<>();
            avl_check.insert_datas(new Integer[]{15, 4, 25, 10, 22, 44, 12, 18, 24, 35, 50, 31, 70, 66, 90});
            assertTrue(avl_check.equals(avl1));
        }
    }
    
    public static void test_input_datas_and_inorder_traversal(){
        Integer[] source = {25, 50, 15, 35, 44, 70, 90, 66, 10, 4, 12, 22, 18, 24, 31};
        Integer[] sorted = source.clone(); sort(sorted);
        List<Integer> source2 = Arrays.asList(source);
        
        Bst<Integer> bst1 = new Bst<>();
        Bst<Integer> bst2 = new Bst<>();
        
        bst1.insert_datas(source);
        bst2.insert_datas(source2);
    
        Iterator<Pair<BstData<Integer>, Integer>> it1 = bst1.head.child.inorder_iterator();
        Iterator<Pair<BstData<Integer>, Integer>> it2 = bst1.head.child.inorder_iterator();
        assertFalse(it1.equals(it2));
    
        for (int i = 0;it1.hasNext(); i++) {
            Integer temp = it1.next().a.get_data();
            assertTrue(sorted[i].equals(temp));
        }
        
        for (int i = 0; it2.hasNext(); i++){
            Integer temp = it2.next().a.get_data();
            assertTrue(sorted[i].equals(temp));
        }
        
        
        BstData<Integer> data1 = bst1.find_node(35);
        BstData<Integer> data2 = bst1.find_node(44);
        BstData<Integer> data3 = bst1.find_node(100);
        assertTrue(data1.get_data().equals(35));
        assertTrue(data2.get_data().equals(44));
        assertTrue(data3 == null);
        
        data1.detach_from_parent();
        {
            Integer[] result_check = {4, 10, 12, 15, 18, 22, 24, 25, 50, 66, 70, 90};
            it1 = bst1.head.child.inorder_iterator();
            for (int i = 0; it1.hasNext(); i++) {
                Pair<BstData<Integer>, Integer> temp = it1.next();
                assertTrue(result_check[i].equals(temp.a.data));
            }
        }
        
        assertTrue(! bst1.equals(bst2));
        
        data1 = bst2.find_node(35);
        data1.detach_from_parent();
        assertTrue(bst1.equals(bst2));
        
        bst1.find_node(12).remove_this();
        bst1.find_node(10).remove_this();
        bst1.find_node(25).remove_this();
        bst1.find_node(50).remove_this();
        
        {
            */
/*
                           24
                  15             70
                4       22    66    90
                     18
            *//*

            Bst<Integer> temp = new Bst<>();
            temp.insert_datas(new Integer[]{24, 15, 70, 4, 22, 66, 90, 18});
            assertTrue(temp.equals(bst1));
        }
        
        bst1.find_node(4).remove_this();
        bst1.find_node(22).remove_this();
        assertTrue(bst1.head.get_height() == 2);
        bst1.find_node(24).remove_this();
    
        {
            */
/*
                   18
                15       70
                      66    90
            *//*

            Bst<Integer> temp = new Bst<>();
            temp.insert_datas(new Integer[]{18, 15, 70, 66, 90});
            assertTrue(temp.equals(bst1));
        }
    }
    
}
*/
