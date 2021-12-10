import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class tes {
    static ArrayList<Integer>[] pangkat = null;
    
    
    public static void main(String[] args) throws IOException {
        int n = (int) Math.round(Math.random());
        pangkat = new ArrayList[n+5];
        graph graf = new graph();
        System.out.println(pangkat[0]);
        System.out.println(graf);
//        throw new IOException();
    }
}


class graph{
    ArrayList<Integer>[] pangkat;
    
    public void tes(String[] args) {
        pangkat = new ArrayList[5];
    }
}