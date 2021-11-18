import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class PriorityQueueTest {
    public static void main(String[] args) {
        test_1();
        test_2();
    }
    
    public static int randint(int lower, int upper){
        return ThreadLocalRandom.current().nextInt(lower, upper + 1);
    }
    
    public static void test_1(){
        MyPriorityQueue<Integer> queue = new MyPriorityQueue<>();
        int range_num = 10;
        
        int insert_cnt = randint(0, 20);
        for (int i = 0; i < insert_cnt; i++) {
            int random_number = randint(-range_num, range_num);
            queue.add(random_number);
        }
        
        int pop_cnt = randint(0, queue.size());
        for (int i = 0; i < pop_cnt; i++) {
            int curr = queue.pop();
            System.out.print(curr);
            System.out.print(' ');
        }
        System.out.println();
        
        
        insert_cnt = randint(0, 20);
        for (int i = 0; i < insert_cnt; i++) {
            int random_number = randint(-range_num, range_num);
            queue.add(random_number);
        }
        
        while (!queue.is_empty()){
            int curr = queue.pop();
            System.out.print(curr);
            System.out.print(' ');
        }
        System.out.println();
    }
    
    
    public static void test_2(){
        int range_num = 10;
        ArrayList<Integer> array_list = new ArrayList<>();
    
        int insert_cnt = randint(0, 20);
        for (int i = 0; i < insert_cnt; i++) {
            int random_number = randint(-range_num, range_num);
            array_list.add(random_number);
        }
    
        MyPriorityQueue<Integer> queue = MyPriorityQueue.from_array_unsafe(array_list);
        array_list = null;
        
        insert_cnt = randint(0, 20);
        for (int i = 0; i < insert_cnt; i++) {
            int random_number = randint(-range_num, range_num);
            queue.add(random_number);
        }
        
        int pop_cnt = randint(0, queue.size());
        for (int i = 0; i < pop_cnt; i++) {
            int curr = queue.pop();
            System.out.print(curr);
            System.out.print(' ');
        }
        System.out.println();
        
        
        insert_cnt = randint(0, 20);
        for (int i = 0; i < insert_cnt; i++) {
            int random_number = randint(-range_num, range_num);
            queue.add(random_number);
        }
        
        while (!queue.is_empty()){
            int curr = queue.pop();
            System.out.print(curr);
            System.out.print(' ');
        }
        System.out.println();
    }
    
    
}
