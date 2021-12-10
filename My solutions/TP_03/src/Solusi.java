import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"ForLoopReplaceableByForEach", "UnnecessaryLocalVariable"})
public class Solusi {
    public static long __TESTCASE__ = -1;
    public static boolean DEBUG = false;
    
    public static InputReader in = new InputReader(System.in);
    public static PrintWriter out = new PrintWriter(System.out);
    
    public static KELOMPOK_TC kelompok_tc = KELOMPOK_TC.TAMBAH_RESIGN_SEBAR;
    
    public static void main(String[] args) {
        kelompok_tc = KELOMPOK_TC.TAMBAH_RESIGN_SEBAR;
        
        int banyak_node, banyak_edge, banyak_query;
        banyak_node = in.nextInt();
        banyak_edge = in.nextInt();
        banyak_query = in.nextInt();
        
        int[] pangkat_karyawan = new int[banyak_node + 2];
        int[] banyak_pemilik_pangkat = new int[banyak_node+2];
        pangkat_karyawan[0] = -99999999;
        for (int i = 1; i <= banyak_node; i++) {
            int temp = in.nextInt();
            pangkat_karyawan[i] = temp;
            ++banyak_pemilik_pangkat[temp];
        }
        
        int[] edges1 = new int[banyak_edge + 5];
        int[] edges2 = new int[banyak_edge + 5];
        edges1[0] = 2; edges1[1] = 2;  edges2[0] = 2; edges2[1] = 2;  // inisialisasi queue
        
        for (int i = 0; i < banyak_edge; i++) {
            int a, b;
            a = in.nextInt();
            b = in.nextInt();
            push(edges1, a);
            push(edges2, b);
        }
        
    
        PERINTAH[] DAFTAR_PERINTAH = new PERINTAH[]{
                null, PERINTAH.TAMBAH, PERINTAH.RESIGN, PERINTAH.CARRY, PERINTAH.BOSS,
                PERINTAH.SEBAR, PERINTAH.SIMULASI, PERINTAH.NETWORKING,
        };
        
        Queue<Triplet<PERINTAH, Integer, Integer>> queries = new ArrayDeque<>(banyak_query+5);
        for (int i = 0; i < banyak_query; i++) {
            PERINTAH perintah;
            perintah = DAFTAR_PERINTAH[in.nextInt()];
            
            switch (Objects.requireNonNull(perintah)){
                case TAMBAH:
                    queries.add(new Triplet<>(perintah, in.nextInt(), in.nextInt()));
                    break;
                case RESIGN:
                    queries.add(new Triplet<>(perintah, in.nextInt(), null));
                    break;
                case CARRY:
                    queries.add(new Triplet<>(perintah, in.nextInt(), null));
                    kelompok_tc = KELOMPOK_TC.TAMBAH_RESIGN_CARRY_SIMULASI;
                    break;
                case BOSS:
                    queries.add(new Triplet<>(perintah, in.nextInt(), null));
                    kelompok_tc = KELOMPOK_TC.BOSS;
                    break;
                case SEBAR:
                    queries.add(new Triplet<>(perintah, in.nextInt(), in.nextInt()));
                    kelompok_tc = KELOMPOK_TC.TAMBAH_RESIGN_SEBAR;
                    break;
                case SIMULASI:
                    queries.add(new Triplet<>(perintah, null, null));
                    kelompok_tc = KELOMPOK_TC.TAMBAH_RESIGN_CARRY_SIMULASI;
                    break;
                case NETWORKING:
                    queries.add(new Triplet<>(perintah, null, null));
                    kelompok_tc = KELOMPOK_TC.NETWORKING;
                    break;
            }
        }
    
        switch (kelompok_tc){
            case TAMBAH_RESIGN_CARRY_SIMULASI:
                TRCS.tambah_resign_carry_simulasi(banyak_node, pangkat_karyawan, edges1, edges2, queries);
                break;
            case TAMBAH_RESIGN_SEBAR:
                TRSebar.tambah_resign_sebar(banyak_node, pangkat_karyawan, banyak_pemilik_pangkat, edges1, edges2, queries);
                break;
            case NETWORKING:
                networking(banyak_node, pangkat_karyawan, edges1, edges2, queries);
                break;
            case BOSS:
                boss(banyak_node, pangkat_karyawan, edges1, edges2, queries);
                break;
        }
        out.flush();
    }
    
    
    
    // operasi untuk queue
    public static int pop(int[] queue){
        assert queue[0] != queue[1];  // artinya queue overflow
        int ret = queue[queue[0]++];
        if (queue[0] >= queue.length)
            queue[0] = 2;
        return ret;
    }
    
    public static void push(int[] queue, int data){
        queue[queue[1]++] = data;
        if (queue[1] >= queue.length)
            queue[1] = 2;
        assert queue[0] != queue[1];  // artinya queue overflow
    }
    
    public static boolean isEmpty(int[] queue){
        return queue[0] == queue[1];
    }
    
    
    @SuppressWarnings("UnusedAssignment")
    static class TRCS{
        public static void tambah_resign_carry_simulasi(
                int banyak_karyawan,
                int[] pangkat_karyawan,
                int[] edges1,
                int[] edges2,
                
                Queue<Triplet<PERINTAH, Integer, Integer>> _queries
        ){
            CustomEdgeRemovableAdjListGraph<Integer> karyawan = new CustomEdgeRemovableAdjListGraph<>(banyak_karyawan + 5);
            // + 1 pada 2 perintah di bawah ini karena one-based index
            karyawan.add_n_node(banyak_karyawan+1);
            ArrayList<Boolean> is_karyawan_tertinggi = new ArrayList<>(Collections.nCopies(banyak_karyawan+1, true));
            is_karyawan_tertinggi.set(0, false);  // index 0 is just a dummy node
            int karyawan_tersisa_setelah_simulasi = banyak_karyawan;
            
            for (int q=0; !isEmpty(edges1); q++){
                int temp_a = pop(edges1);
                int temp_b = pop(edges2);
                karyawan_tersisa_setelah_simulasi = trcs_tambah_teman(temp_a, temp_b, karyawan_tersisa_setelah_simulasi,
                                                                      pangkat_karyawan, is_karyawan_tertinggi, karyawan);
                DEBUG_trcs_validasi_karyawanTersisaSetelahSimulasi(karyawan_tersisa_setelah_simulasi,
                                                                   is_karyawan_tertinggi);
                DEBUG_trcs_validasi_ingoing_outgoing(karyawan);
            }
            
            for (int q = 0; !_queries.isEmpty() ; q++) {
                Triplet<PERINTAH, Integer, Integer> query = _queries.poll();
                
                switch (query.a){  // query.b dan query.c adalah parameter dari masing-masing perintah
                    case TAMBAH:
                        karyawan_tersisa_setelah_simulasi = trcs_tambah_teman(query.b, query.c,
                                                                              karyawan_tersisa_setelah_simulasi,
                                                                              pangkat_karyawan,
                                                                              is_karyawan_tertinggi, karyawan);
                        break;
                    case RESIGN:
                        karyawan_tersisa_setelah_simulasi = trcs_resign(query.b, karyawan_tersisa_setelah_simulasi, pangkat_karyawan,
                                                                        is_karyawan_tertinggi, karyawan);
                        break;
                    case CARRY:
                        if (karyawan._get_neighbor_of(query.b).size() == 0){
                            out.println(0);
                        }else{
                            ReferenceablePq<ComparableDupletNegatedItem1<Integer, Integer>>
                                    this_karyawan = karyawan.adjlist.get(query.b);
                            PqReference<ComparableDupletNegatedItem1<Integer, Integer>>
                                    tertinggi = this_karyawan.peek();
                            int pangkat_si_tertinggi = tertinggi.key.a;
                            out.println(pangkat_si_tertinggi);
                        }
                        break;
                    case SIMULASI:
                        out.println(karyawan_tersisa_setelah_simulasi);
                        break;
                }
                DEBUG_validate_custom_binary_heap(karyawan);
                DEBUG_trcs_validasi_karyawanTersisaSetelahSimulasi(karyawan_tersisa_setelah_simulasi,
                                                                   is_karyawan_tertinggi);
                DEBUG_trcs_validasi_ingoing_outgoing(karyawan);
            }
        }
        
        public static <T extends Comparable<T>>
        void DEBUG_validate_custom_binary_heap(CustomEdgeRemovableAdjListGraph<T> graph){
            if (DEBUG){
                for (int i = 1; i < graph.adjlist.size(); i++) {
                    for (int j = graph.adjlist.get(i).size() - 1; j >= 1; j--) {
                        ReferenceablePq<ComparableDupletNegatedItem1<T, Integer>>
                                temp = graph.adjlist.get(i);
                        if (temp.compare(
                                    temp.get_value(temp.get_parent(j)),
                                    temp.get_value(j)
                                ) > 0){
                            System.out.println("zz");
                            System.out.println(temp.get_value(temp.get_parent(j)).key.a);
                            System.out.println(temp.get_value(j).key.a);
                            throw new IllegalStateException("binary heap error");
                        }
                    }
                }
            }
        }
        
        public static void DEBUG_trcs_validasi_karyawanTersisaSetelahSimulasi(
                int karyawan_tersisa_setelah_simulasi,
                ArrayList<Boolean> is_karyawan_tertinggi
        ){
            // cek apakah jumlah true pada is_karyawan_tertinggi sama dengan karyawan_tersisa_setelah_simulasi
            if (DEBUG) {
                int cnt = 0;
                for (int i = 0; i < is_karyawan_tertinggi.size(); i++) {
                    if (is_karyawan_tertinggi.get(i))
                        cnt += 1;
                }
                if (cnt != karyawan_tersisa_setelah_simulasi)
                    throw new IllegalStateException("error karyawan_tersisa_setelah_simulasi");
            }
        }
        
        public static void DEBUG_trcs_validasi_ingoing_outgoing(
                CustomEdgeRemovableAdjListGraph<Integer> karyawan
        ){
            if (DEBUG) {
                int cnt = 0;
                // cek apakah untuk setiap karyawan, jumlah incoming edge YANG VALID-nya sama dengan jumlah
                // outgoing edge
                for (int i = 1; i < karyawan.adjlist.size(); i++) {
                    cnt = 0;  // jumlah incoming edge yang valid
                    
                    for (int j = 0; j < karyawan.in_edges.get(i).size(); j++) {
                        if (karyawan.in_edges.get(i).get(j).__pq_pos >= 0)
                            cnt += 1;
                    }
                    if (cnt != karyawan.adjlist.get(i).my_pq.size())
                        throw new IllegalStateException("error in_edge yang valid");
                }
            }
        }
        
        public static int trcs_tambah_teman(
                int a, int b, int karyawan_tersisa_setelah_simulasi,
                int[] pangkat_karyawan,
                ArrayList<Boolean> is_karyawan_tertinggi,
                CustomEdgeRemovableAdjListGraph<Integer> karyawan
        ){
            
            if (pangkat_karyawan[a] <= pangkat_karyawan[b]){
                is_karyawan_tertinggi.set(a, false);
                if (trcs_apakah_karyawan_tertinggi(a, pangkat_karyawan, karyawan))
                    karyawan_tersisa_setelah_simulasi -= 1;
            }
            
            if (pangkat_karyawan[b] <= pangkat_karyawan[a]){
                is_karyawan_tertinggi.set(b, false);
                if (trcs_apakah_karyawan_tertinggi(b, pangkat_karyawan, karyawan))
                    karyawan_tersisa_setelah_simulasi -= 1;
            }
            
            karyawan.add_uniedge(a, b, pangkat_karyawan[b]);
            karyawan.add_uniedge(b, a, pangkat_karyawan[a]);
            
            return karyawan_tersisa_setelah_simulasi;
        }
        
        public static int trcs_resign(
                int yang_resign, int karyawan_tersisa_setelah_simulasi,
                int[] pangkat_karyawan,
                ArrayList<Boolean> is_karyawan_tertinggi,
                CustomEdgeRemovableAdjListGraph<Integer> karyawan
        ){
            // .key.item1 = key (pangkat), .key.item2 = id_teman
            ArrayList<PqReference<ComparableDupletNegatedItem1<Integer, Integer>>>
                    daftar_teman_yang_ditinggalkan = karyawan._get_neighbor_of(yang_resign);
            
            
            karyawan.delete_node(yang_resign, false);
            DEBUG_validate_custom_binary_heap(karyawan);
            
            if (is_karyawan_tertinggi.get(yang_resign)){
                karyawan_tersisa_setelah_simulasi -= 1;
                is_karyawan_tertinggi.set(yang_resign, false);
    
                DEBUG_validate_custom_binary_heap(karyawan);
            }
            
            for (int i = 0; i < daftar_teman_yang_ditinggalkan.size(); i++) {
                int id_teman = daftar_teman_yang_ditinggalkan.get(i).key.b;
                int pangkat_teman = daftar_teman_yang_ditinggalkan.get(i).key.a;
                
                
                boolean is_teman_sudah_resign = karyawan.is_invalid(id_teman);
                
                if (is_teman_sudah_resign) continue;
                if (pangkat_teman > pangkat_karyawan[yang_resign]) continue;
                if (!trcs_apakah_karyawan_tertinggi(id_teman, pangkat_karyawan, karyawan))
                    continue;
                is_karyawan_tertinggi.set(id_teman, true);
                karyawan_tersisa_setelah_simulasi += 1;
                
                DEBUG_validate_custom_binary_heap(karyawan);
            }
    
            
            
            // bersihkan secara manual, karena tadi pas delete, fungsi bersihin otomatisnya dibikin false
            karyawan.adjlist.get(yang_resign).my_pq.array_list.clear();
            karyawan.adjlist.set(yang_resign, karyawan.new RemovedNode());
            karyawan.in_edges.get(yang_resign).clear();
    
            DEBUG_validate_custom_binary_heap(karyawan);
            return karyawan_tersisa_setelah_simulasi;
        }
        
        public static boolean trcs_apakah_karyawan_tertinggi(
                int id_karyawan,
                int[] pangkat_karyawan,
                CustomEdgeRemovableAdjListGraph<Integer> karyawan
        ){
            ReferenceablePq<ComparableDupletNegatedItem1<Integer, Integer>>
                    this_karyawan = karyawan.adjlist.get(id_karyawan);
            if (this_karyawan.size() == 0)  // jika tidak punya teman
                return true;
            PqReference<ComparableDupletNegatedItem1<Integer, Integer>>
                    tertinggi = this_karyawan.peek();
            int pangkat_si_tertinggi = tertinggi.key.a;
            int pangkat_ini = pangkat_karyawan[id_karyawan];
            return pangkat_ini > pangkat_si_tertinggi;
        }
    }
    
    
    public static void networking(
            int banyak_karyawan,
            int[] pangkat_karyawan,
            int[] edges1,
            int[] edges2,

            Queue<Triplet<PERINTAH, Integer, Integer>> queries
            ){
        int banyak_node = banyak_karyawan;
        assert queries.size() == 1;
    
        ArrayList<ComparableTriplet<Integer, Integer, Integer>>
                edges_for_mst;  // we need these edges for the MST
        
        {
            // item1 = component id kecil, item2 = component id besar, item3 = difference pangkat
            ArrayList<ComparableTriplet<Integer, Integer, Integer>> sides = new ArrayList<>(banyak_node + 5);
        
            // item1 = pangkat. item2 = id komponen
            ArrayList<ComparableDuplet<Integer, Integer>> nodes = new ArrayList<>(banyak_node + 5);
            {
                ArrayList<Boolean> is_added = new ArrayList<>(Collections.nCopies(banyak_node + 1, false));
            
                AdjListGraph<Void> graph = new AdjListGraph<>(banyak_node + 2);
                graph.add_n_node(banyak_node + 1);  // + 1 karena one-based index
            
                // mensimulasikan penambahan edge
                while (!isEmpty(edges1)) {
                    graph.add_biedge(pop(edges1),
                                     pop(edges2),
                                     null);
                }
            
                // mengelompokkan node-node dari komponen yang sama menjadi satu kelompok dengan component_id tertentu
                ArrayList<Integer> stack = new ArrayList<>(4000);
                int component_id = 0;
                for (int i = 1; i <= banyak_node; i++) {
                    if (is_added.get(i)) continue;
                    component_id += 1;
                    stack.add(i);
                    is_added.set(i, true);
                
                    while (!stack.isEmpty()) {
                        int curr = stack.remove(stack.size() - 1);
                        
                        nodes.add(new ComparableDuplet<>(pangkat_karyawan[curr], component_id));
                        ArrayList<Duplet<Integer, Void>> neighbor = graph._get_neighbor_of(curr);
                    
                        for (int j = 0; j < neighbor.size(); j++) {
                            int neighbor_id = neighbor.get(j).a;
                            if (is_added.get(neighbor_id)) continue;
                            is_added.set(neighbor_id, true);
                            stack.add(neighbor_id);
                        }
                    }
                }
    
                // + 1 karena mau bikin jadi one-based. component_id saat ini = jumlah component saat ini.
                // dipake-nya nanti
                edges_for_mst = new ArrayList<>(component_id + 1);
            }
    
            
            // bingung cara jelasinnya gimana :'
            sortings.merge_sort(nodes, new ArrayList<>(banyak_node + 5));
            for (int i = 0; i < nodes.size() - 1; i++) {
                if (nodes.get(i).b.equals(nodes.get(i + 1).b)) continue;
                int min = Math.min(nodes.get(i).b, nodes.get(i + 1).b);
                int max = Math.max(nodes.get(i).b, nodes.get(i + 1).b);
                sides.add(new ComparableTriplet<>(
                        min, max, Math.abs(nodes.get(i).a - nodes.get(i + 1).a)
                ));
            }
        
            
            sortings.merge_sort(sides, new ArrayList<>(banyak_node + 5));
            if (sides.size() > 0){
                ComparableTriplet<Integer, Integer, Integer> current = sides.get(0);
                
                // menambahkan edge ke edges_for_mst dengan difference pangkat sbg weight-nya.
                // weight yang akan dipilih dari tiap edge adalah weight yg MINIMUM. Hal ini bisa dicapai
                // dengan mengurutkan scr ascending (a, b, c), dimana a dan b adalah pasangan component id, a < b,
                // dan c adalah difference pangkat. Lalu untuk setiap pasang a b, hanya tuple (a,b,c) yang pertama
                // kali muncul yang akan ditambahkan ke dalam graf (supaya minimum dan tidak ada edge duplikat).
                for (int i = 1; i < sides.size(); i++) {
                    if (current.a.equals(sides.get(i).a) && current.b.equals(sides.get(i).b))
                        continue;
                    edges_for_mst.add(new ComparableTriplet<>(current.a, current.b, current.c));
                    current = sides.get(i);
                }
                edges_for_mst.add(new ComparableTriplet<>(current.a, current.b, current.c));
            }
        }
    
        //noinspection ComparatorCombinators
        ArrayList<ComparableTriplet<Integer, Integer, Integer>> res = GraphAlgorithms.kruskal_mst(
                (ComparableTriplet<Integer, Integer, Integer> edge_1,
                ComparableTriplet<Integer, Integer, Integer> edge_2) -> edge_1.c - edge_2.c,
                (ComparableTriplet<Integer, Integer, Integer> node) -> node.a,
                (ComparableTriplet<Integer, Integer, Integer> node) -> node.b,
                banyak_node,
                edges_for_mst
        );
        
        int sum = 0;
        for (int i = 0; i < res.size(); i++) {
            sum += res.get(i).c;
        }
        out.print(sum);
    }
    
    
    
    static class TRSebar{
    
    
        public static void tambah_resign_sebar(
                int banyak_karyawan,
                int[] pangkat_karyawan,
                int[] banyak_pemilik_pangkat,
                int[] edges1,
                int[] edges2,

                Queue<Triplet<PERINTAH, Integer, Integer>> queries
        ){
            TrsGraph karyawan =
                    new TrsGraph(banyak_karyawan+2);
            karyawan.add_n_node(banyak_karyawan+1);
            
            int[] queue = new int[banyak_karyawan + 20];
            queue[0] = 2;  // queue[0] = dequeue pointer
            queue[1] = 2;  // queue[1] = enqueue pointer
            
            boolean[] pangkat_tertentu_mark;
            int[][] pangkat_tertentu = new int[banyak_karyawan+2][];
            for (int i = 0; i <= banyak_karyawan; i++) {
                pangkat_tertentu[i] = new int[banyak_pemilik_pangkat[i]];
            }
            
            
            {
                int[] banyak_pemilik_pangkat_2 = new int[banyak_karyawan+2];
                // dimulai dari 1 karena index 0-nya adalah index dummy (dikosongkan supaya one-based index)
                for (int i = 1; i <= banyak_karyawan; i++) {
                    int temp = pangkat_karyawan[i];
                    pangkat_tertentu[temp][banyak_pemilik_pangkat_2[temp]] = i;
                    ++banyak_pemilik_pangkat_2[temp];
                }
            }
            
            for (int q=0; !isEmpty(edges1); q++){
                karyawan.add_biedge(pop(edges1),
                                    pop(edges2),
                                    0);
            }
            
            for (int q=0; !queries.isEmpty(); q++){
                Triplet<PERINTAH, Integer, Integer> _query = queries.poll();
                
                switch_break:
                switch (_query.a){
                    case TAMBAH:
                        karyawan.add_biedge(_query.b, _query.c, 0);
                        break;
                    case RESIGN:
                        karyawan.delete_node(_query.b);
                        break;
                    case SEBAR:
                    {
                        queue[0] = queue[1];  // clear the queue
                        assert isEmpty(queue);
    
                        if (_query.b.equals(_query.c)) {
                            out.println(0);
                            break;
                        }
                        pangkat_tertentu_mark = new boolean[banyak_karyawan+2];

                        ArrayList<Boolean> visited = new ArrayList<>(Collections.nCopies(banyak_karyawan + 1, false));
                        visited.set(_query.b, true);
                        
                        TrsGraph.GraphIterator iter = karyawan.out_iterator(_query.b);
                        while (iter.hasNext()){
                            int temp = iter.next();
                            if (visited.get(temp)) continue;
                            push(queue, temp);
                            visited.set(temp, true);
                            
                            if (temp == _query.c) {
                                out.println(0);
                                break switch_break;
                            }
                        }
                        
                        int[] sesama_pangkat = pangkat_tertentu[pangkat_karyawan[_query.b]];
                        pangkat_tertentu_mark[pangkat_karyawan[_query.b]] = true;
                        for (int i = 0; i < sesama_pangkat.length; i++) {
                            int temp = sesama_pangkat[i];
                            if (visited.get(temp)) continue;
                            push(queue, temp);
                            visited.set(temp, true);
                            
                            if (temp == _query.c) {
                                out.println(0);
                                break switch_break;
                            }
                        }
                        int result = -1;
                        int cost = 0;
                        push(queue, -1);
                        int curr;
                        
                        outter_bfs:
                        while (!isEmpty(queue)){
                            curr = pop(queue);
                            if (curr < 0) {
                                ++cost;
                                if (isEmpty(queue))
                                    break;
                                push(queue, -1);
                                continue;
                            }
                            
                            if (!pangkat_tertentu_mark[pangkat_karyawan[curr]]) {
                                pangkat_tertentu_mark[pangkat_karyawan[curr]] = true;
                                int[] arr = pangkat_tertentu[pangkat_karyawan[curr]];
                                for (int i = 0; i < arr.length; i++) {
                                    if (visited.get(arr[i])) continue;
                                    int temp = arr[i];
                                    visited.set(temp, true);
                                    
                                    push(queue, temp);
    
                                    if (temp == _query.c) {
                                        result = cost + 1;
                                        break outter_bfs;
                                    }
                                }
                            }
    
                            iter = karyawan.out_iterator(curr);
                            while (iter.hasNext()){
                                int neighbor_id = iter.next();
                                if (visited.get(neighbor_id)) continue;
                                visited.set(neighbor_id, true);
    
                                push(queue, neighbor_id);
    
                                if (neighbor_id == _query.c) {
                                    result = cost + 1;
                                    break outter_bfs;
                                }
                            }
                        }
                        
                        queue[0] = queue[1];  // clear the queue
                        out.println(result);
                    }
                    break;
                }
            }
        }
        
       
        public static class TrsGraph implements IGraph<Integer>{
            // duplet item1 id node, item2 indeksnya di dalam indegree node_tsb
            private ArrayList<Duplet<Integer, Integer>>[] _out;
            
            // duplet item1 id node, item2 indeksnya di dalam outdegree node_tsb
            private ArrayList<Duplet<Integer, Integer>>[] _in;
            
            int _size = 0;
            
            public ArrayList<Duplet<Integer, Integer>> _get_out(int node){
                return _out[node];
            }
    
            public ArrayList<Duplet<Integer, Integer>> _get_in(int node){
                return _in[node];
            }
            
            @SuppressWarnings("unchecked")
            public TrsGraph(int reserve){
                _out = new ArrayList[reserve];
                _in = new ArrayList[reserve];
            }
            
            @Override
            public void add_node(int reserve_neightbour_cnt) {
                _out[_size] = new ArrayList<>(reserve_neightbour_cnt);
                _in[_size] = new ArrayList<>(reserve_neightbour_cnt);
                _size += 1;
            }
            
            @Override
            public void add_uniedge(int from, int to, Integer item) {
                if (Solusi.DEBUG){
                    if (has_uniedge(from, to))  throw new IllegalStateException("Trying to add an existing edge");
                }
                
                ArrayList<Duplet<Integer, Integer>> src_out = _out[from];
                ArrayList<Duplet<Integer, Integer>> target_in = _in[to];
                int src_outdegree = src_out.size();
                int target_indegree = target_in.size();
                
                src_out.add(new Duplet<>(to, target_indegree));
                target_in.add(new Duplet<>(from, src_outdegree));
            }
            
            @SuppressWarnings("DuplicatedCode")
            @Override
            public boolean has_uniedge(int from, int to) {
                boolean ret = false;
                for (int i = 0; i < _out[from].size(); i++) {
                    Duplet<Integer, Integer> temp = _out[from].get(i);
                    if (temp != null && temp.a == to) {
                        ret = true;
                        break;
                    }
                }
                
                if (DEBUG) {
                    boolean ret2 = false;
                    for (int i = 0; i < _in[to].size(); i++) {
                        Duplet<Integer, Integer> temp = _in[to].get(i);
                        if (temp != null && temp.a == from) {
                            ret2 = true;
                            break;
                        }
                    }
                    assert ret2 == ret;
                }
                return ret;
            }
            
            @Override
            public ArrayList<Duplet<Integer, Integer>> _get_neighbor_of(int node) {
                return _out[node];
            }
            
            @Override
            public int size() {
                return _size;
            }
            
            static class RemovedNode extends ArrayList<Duplet<Integer, Integer>>{}
            
            public boolean is_invalid(int node){  // if has been deleted
                return _out[node] instanceof TrsGraph.RemovedNode;
            }
            
            @SuppressWarnings("DuplicatedCode")
            public void delete_node(int node){
                assert !is_invalid(node);
                ArrayList<Duplet<Integer, Integer>> temp = _out[node];
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).a < 0) continue;
                    Duplet<Integer, Integer> temp2 = _in[temp.get(i).a].get(temp.get(i).b);
                    temp2.a = -9999;
                    temp2.b = -9999;
                }
                
                temp = _in[node];
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).a < 0) continue;
                    Duplet<Integer, Integer> temp2 = _out[temp.get(i).a].get(temp.get(i).b);
                    temp2.a = -9999;
                    temp2.b = -9999;
                }
                
                _out[node] = new RemovedNode();
                _in[node] = new RemovedNode();
            }
            
            static class GraphIterator implements Iterator<Integer>{
                ArrayList<Duplet<Integer, Integer>> in_or_out_array;
                int curr_position;
                public GraphIterator(ArrayList<Duplet<Integer, Integer>> in_or_out_array){
                    this.in_or_out_array = in_or_out_array;
                    curr_position = 0;
                    find_non_negative_forward();
                }
                
                private boolean _hasNext(){
                    return curr_position < in_or_out_array.size()-1;
                }
                
                @Override
                public boolean hasNext() {
                    return curr_position < in_or_out_array.size() && in_or_out_array.get(curr_position).a >= 0;
                }
                
                public void find_non_negative_forward(){
                    while (_hasNext()
                            && in_or_out_array.get(curr_position).a < 0)
                        curr_position++;
                }
    
                @Override
                public Integer next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    int ret = in_or_out_array.get(curr_position).a;
                    curr_position++;
                    find_non_negative_forward();
                    return ret;
                }
            }
            
            public GraphIterator out_iterator(int node){
                return new GraphIterator(_out[node]);
            }
    
            public GraphIterator in_iterator(int node){
                return new GraphIterator(_in[node]);
            }
        }
    }
    
    
    public static void boss(
            int banyak_karyawan,
            int[] pangkat_karyawan,
            int[] edges1,
            int[] edges2,
            Queue<Triplet<PERINTAH, Integer, Integer>> queries
            ){
        AdjListGraph<Void> karyawan = new AdjListGraph<>(banyak_karyawan+2);
        karyawan.add_n_node(banyak_karyawan+1);
        
        // item1 = maximum pangkat, item2 = node pemilik maksimum pangkat,
        // item3 = maximum pangkat urutan 2,  item4 = node pemilik maksimum pangkat urutan 2
        ArrayList<Quadruplet<Integer, Integer, Integer, Integer>> max_pangkat =
                new ArrayList<>(Collections.nCopies(banyak_karyawan+1, null));
        
        for (int q = 0; !isEmpty(edges1); q++) {
            karyawan.add_biedge(pop(edges1),
                                pop(edges2),
                                null);
        }
        
        ArrayList<Integer> stack = new ArrayList<>(100_000);
        
        for (int q = 0; !queries.isEmpty(); q++) {
            int target = queries.poll().b;
            
            if (karyawan.adjlist.get(target).size() == 0){
                out.println(0);
                continue;
            }
            
            if (max_pangkat.get(target) == null){
                Quadruplet<Integer, Integer, Integer, Integer>
                        quadruplet = new Quadruplet<>(pangkat_karyawan[target], target, -9999, -9999);
                stack.add(target);
    
                while (!stack.isEmpty()){
                    int curr = stack.remove(stack.size() - 1);
                    if (max_pangkat.get(curr) != null)  // has been visited
                        continue;
                    max_pangkat.set(curr, quadruplet);
        
                    // update quadruplet menjadi pangkat tertinggi dan pangkat kedua tertinggi
                    if (quadruplet.c < pangkat_karyawan[curr]
                            && curr != quadruplet.b
                            && curr != quadruplet.d){
                        if (quadruplet.a < pangkat_karyawan[curr]){
                            // shift right
                            quadruplet.c = quadruplet.a;
                            quadruplet.d = quadruplet.b;
                
                            quadruplet.a = pangkat_karyawan[curr];
                            quadruplet.b = curr;
                        }else{
                            quadruplet.c = pangkat_karyawan[curr];
                            quadruplet.d = curr;
                        }
                    }
        
                    ArrayList<Duplet<Integer, Void>> neighbor = karyawan.adjlist.get(curr);
                    for (int i = 0; i < neighbor.size(); i++) {
                        int curr_neighbor = neighbor.get(i).a;
                        if (max_pangkat.get(curr_neighbor) != null)
                            continue;  // sudah visited, lewati saja
                        stack.add(curr_neighbor);
                    }
                }
            }
    
            assert !max_pangkat.get(target).b.equals(max_pangkat.get(target).d);
    
            if (target == max_pangkat.get(target).b)
                out.println(max_pangkat.get(target).c);
            else
                out.println(max_pangkat.get(target).a);
        }
    }
    
    static class InputReader {
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
        
        public void close() throws IOException {
            bufferedReader.close();
        }
    }
    
}


class GraphAlgorithms{
    
    static <T>
    ArrayList<T> kruskal_mst(Comparator<T> comparator,
                             Function<T, Integer> get_src_node_id,
                             Function<T, Integer> get_target_node_id,
                             int node_number, ArrayList<T> edges){
        return kruskal_mst(comparator, get_src_node_id, get_target_node_id,
                           node_number, edges, new ArrayList<>(edges.size()+2));
    }
    
    
    /**
     * T merupakan object yang menyatakan edge.
     * get_src_node_id() haruslah berupa sebuah fungsi yang memetakan edge tipe T ke sebuah integer
     * yang menyatakan id dari node pangkal edge tersebut (tail vertex).
     * integer yang dikembalikan harus dalam range 0 <= x <= jumlah_node. Bebas mau zero-based index
     * ataupun one-based index.
     *
     * Sama seperti get_src_node_id(), get_target_node_id() juga mengembalikan id berupa integer 0 <= x <= jumlah_node.
     * Hanya saja node yang dimaksud adalah node tujuan (head vertex)
     */
    static <T>
    ArrayList<T> kruskal_mst(Comparator<T> comparator,
                             Function<T, Integer> get_src_node_id,
                             Function<T, Integer> get_target_node_id,
                             int node_number, ArrayList<T> edges, ArrayList<T> cache){
        if (edges.size() == 0)
            return new ArrayList<>();
        
        ArrayList<T> ret = new ArrayList<>(Math.min(node_number, edges.size()) + 2);  // + 2 hanya jaga-jaga
        sortings.merge_sort(comparator, edges);
        
        // + 1 karena mungkin aja get_node_id
        UnionFindDisjointSet ufds = new UnionFindDisjointSet(node_number+2).add_n_node(node_number+1);
    
        int root_ufds = get_src_node_id.apply(edges.get(0));
        assert 0 <= root_ufds && root_ufds <= node_number;
        
        for (   int i=0;
                ret.size() < node_number-1 && i < edges.size();
                i++){
            int curr_src = get_src_node_id.apply(edges.get(i));
            int curr_target = get_target_node_id.apply(edges.get(i));
            if (ufds.is_in_the_same_set(curr_src, curr_target))  // cyclic found
                continue;
            // ufds.union(root_ufds, curr_src);
            ufds.union(curr_src, curr_target);
            ret.add(edges.get(i));
        }
        return ret;
    }
}



enum KELOMPOK_TC{
    TAMBAH_RESIGN_CARRY_SIMULASI,
    NETWORKING,
    TAMBAH_RESIGN_SEBAR,
    BOSS,
}
enum PERINTAH{
    TAMBAH, RESIGN, CARRY, BOSS, SEBAR, SIMULASI, NETWORKING
}



@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "unused", "UnnecessaryLocalVariable"})
class MyPriorityQueue<T extends Comparable<T>>{
    ArrayList<T> array_list;
    
    MyPriorityQueue(){this(4);}
    MyPriorityQueue(int reserved_size){
        array_list = new ArrayList<>(reserved_size);
    }
    
    public static <T extends Comparable<T>>
    MyPriorityQueue<T> from_array(ArrayList<T> array_list){
        if (array_list == null)
            return new MyPriorityQueue<>();
    
        MyPriorityQueue<T> ret = new MyPriorityQueue<>(0);
        //noinspection unchecked
        ret.array_list = (ArrayList<T>) array_list.clone();
        return ret;
    }
    
    void heapify(){
        int start_pos = array_list.size() / 2;
        for (int i = start_pos; i >= 0; i--) {
            percolate_down(i);
        }
    }
    
    int compare(T a, T b){
        // dibikin method tersendiri supaya siapa tahu nanti mau ngubah jadi MaxPriorityQueue, dsb.
        // Jadi nanti bisa tinggal diextend dan dioverride
        return a.compareTo(b);
    }
    
    boolean is_empty(){return array_list.size() == 0;}
    int size(){return array_list.size();}
    int insert(T item){return add(item);}
    int add(T item){
        int item_pos = array_list.size();
        array_list.add(item);
        return percolate_up(item_pos);
    }
    T pop(){
        assert array_list.size() > 0;
        return pop_arbitrary(0);
    }
    T pop_or_default(T default_value){
        if (array_list.size() == 0)
            return default_value;
        return pop();
    }
    T nullable_pop(){return pop_or_default(null);}
    T pop_arbitrary(int target){
        assert does_exist(target);
        boolean is_last = (target == size()-1);
        
        T ret = get_value(target);
        if (!is_last)
            swap_arbitrary(target, array_list.size() - 1);
        array_list.remove(array_list.size() - 1);
        if (!is_last) {  // if target is not the last item in the `array_list`
            percolate_up(target);
            percolate_down(target);
        }
        
        return ret;
    }
    T pop_or_default_arbitrary(int target, T default_value){
        if (array_list.size() == 0)
            return default_value;
        return pop_arbitrary(target);
    }
    
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
    int swap_arbitrary(int source, int target){
        assert does_exist(source);
        assert does_exist(target);
        assert source != target;
        
        T source_value = get_value(source);
        array_list.set(source, get_value(target));
        array_list.set(target, source_value);
        return target;  // return the new position of the source
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


class PqReference<K extends Comparable<K>> implements Comparable<PqReference<K>>{
    ReferenceablePq<K> pq;
    int __pq_pos;
    int get_pq_pos(){
        return __pq_pos;
    }
    void _set_pq_pos(int val){
        __pq_pos = val;
    }
    
    K key;
    K get_key(){
        return key;
    }
    void set_key(K new_key){
        key = new_key;
    }
    
    @Override
    public int compareTo(PqReference<K> o) {
        return key.compareTo(o.key);
    }
    
    PqReference(ReferenceablePq<K> ref_pq, K key){
        pq = ref_pq;
        this.key = key;
    }
    
    @Override
    public String toString() {
        return "pqpos:" + __pq_pos + "-" + key.toString();
    }
}
@SuppressWarnings("UnnecessaryLocalVariable")
class __ReferenceablePriorityQueue__<K extends Comparable<K>> extends MyPriorityQueue<PqReference<K>>{
    
    public __ReferenceablePriorityQueue__() {
    }
    
    public __ReferenceablePriorityQueue__(int reserved_size) {
        super(reserved_size);
    }
    
    @Override
    int swap_to(int current, POSITION position) {
        assert does_exist(current);
        assert has_child(current, position);
        
        int child = get_child(current, position);
        int parent = current;
        
        assert get_value(child) != get_value(parent);
        get_value(child)._set_pq_pos(parent);
        get_value(parent)._set_pq_pos(child);
        return super.swap_to(current, position);
    }
    
    @Override
    int swap_to_parent(int current) {
        assert does_exist(current);
        assert has_parent(current);
        
        int child = current;
        int parent = get_parent(current);
        
        assert get_value(child) != get_value(parent);
        get_value(child)._set_pq_pos(parent);
        get_value(parent)._set_pq_pos(child);
        return super.swap_to_parent(current);
    }
    
    @Override
    int swap_arbitrary(int source, int target) {
        assert source != target;
        
        int a = source;
        int b = target;
        
        assert get_value(a) != get_value(b);
        get_value(a)._set_pq_pos(b);
        get_value(b)._set_pq_pos(a);
        
        return super.swap_arbitrary(source, target);
    }
    
    @Override
    PqReference<K> pop_arbitrary(int target) {
        PqReference<K> ret = super.pop_arbitrary(target);
        ret._set_pq_pos(-99999999);
        return ret;
    }
}
class ReferenceablePq<K extends Comparable<K>>{
    __ReferenceablePriorityQueue__<K> my_pq;
    
    ReferenceablePq() {
        this(0);
    }
    
    ReferenceablePq(int reserved_size) {
        my_pq = new __ReferenceablePriorityQueue__<>(reserved_size);
    }
    
    PqReference<K> add(K item) {
        PqReference<K> ret = new PqReference<>(this, item);
        ret._set_pq_pos(size());
        my_pq.add(ret);
        return ret;
    }
    
    public static <T extends Comparable<T>> MyPriorityQueue<T> from_array(ArrayList<T> array_list) {
        return MyPriorityQueue.from_array(array_list);
    }
    public void heapify() {
        my_pq.heapify();
    }
    public int compare(PqReference<K> a, PqReference<K> b) {
        return my_pq.compare(a, b);
    }
    public boolean is_empty() {
        return my_pq.is_empty();
    }
    public int size() {
        return my_pq.size();
    }
    public int insert(PqReference<K> item) {
        return my_pq.insert(item);
    }
    public PqReference<K> pop() {
        return my_pq.pop();
    }
    public PqReference<K> pop_or_default(PqReference<K> default_value) {
        return my_pq.pop_or_default(default_value);
    }
    public PqReference<K> nullable_pop() {
        return my_pq.nullable_pop();
    }
    public PqReference<K> pop_or_default_arbitrary(int target, PqReference<K> default_value) {
        return my_pq.pop_or_default_arbitrary(target, default_value);
    }
    public boolean does_exist(int curr) {
        return my_pq.does_exist(curr);
    }
    public boolean has_left(int curr) {
        return my_pq.has_left(curr);
    }
    public boolean has_right(int curr) {
        return my_pq.has_right(curr);
    }
    public boolean has_child(int curr, POSITION pos) {
        return my_pq.has_child(curr, pos);
    }
    public boolean has_child(int curr) {
        return my_pq.has_child(curr);
    }
    public boolean has_parent(int curr) {
        return my_pq.has_parent(curr);
    }
    public boolean is_root(int curr) {
        return my_pq.is_root(curr);
    }
    public int _get_left(int current) {
        return my_pq._get_left(current);
    }
    public int _get_right(int current) {
        return my_pq._get_right(current);
    }
    public int _get_child(int current, POSITION pos) {
        return my_pq._get_child(current, pos);
    }
    public int _get_parent(int current) {
        return my_pq._get_parent(current);
    }
    public int _get_level(int current) {
        return my_pq._get_level(current);
    }
    public int get_left(int current) {
        return my_pq.get_left(current);
    }
    public int get_right(int current) {
        return my_pq.get_right(current);
    }
    public int get_child(int current, POSITION pos) {
        return my_pq.get_child(current, pos);
    }
    public POSITION get_pos_in_parent(int current) {
        return my_pq.get_pos_in_parent(current);
    }
    public int get_parent(int current) {
        return my_pq.get_parent(current);
    }
    public int get_level(int current) {
        return my_pq.get_level(current);
    }
    public int get_tree_height() {
        return my_pq.get_tree_height();
    }
    public void set_child(int current, POSITION position, PqReference<K> value) {
        my_pq.set_child(current, position, value);
    }
    public void set_left(int current, PqReference<K> value) {
        my_pq.set_left(current, value);
    }
    public void set_right(int current, PqReference<K> value) {
        my_pq.set_right(current, value);
    }
    public PqReference<K> get_value(int curr) {
        return my_pq.get_value(curr);
    }
    public PqReference<K> get_value_or_default(int curr, PqReference<K> default_value) {
        return my_pq.get_value_or_default(curr, default_value);
    }
    public PqReference<K> peek() {
        return my_pq.peek();
    }
    public PqReference<K> peek_or_default(PqReference<K> default_value) {
        return my_pq.peek_or_default(default_value);
    }
    public PqReference<K> nullable_peek() {
        return my_pq.nullable_peek();
    }
    public int percolate_down(int node) {
        return my_pq.percolate_down(node);
    }
    public int percolate_up(int node) {
        return my_pq.percolate_up(node);
    }
    public int swap_to_left(int current) {
        return my_pq.swap_to_left(current);
    }
    public int swap_to_right(int current) {
        return my_pq.swap_to_right(current);
    }
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder(200);
        for (int i = 0; i < my_pq.size(); i++) {
            stb.append(my_pq.array_list.get(i).toString());
            stb.append(",  ");
        }
        return stb.toString();
    }
    public int swap_to(int current, POSITION position) {
        return my_pq.swap_to(current, position);
    }
    public int swap_to_parent(int current) {
        return my_pq.swap_to_parent(current);
    }
    public int swap_arbitrary(int source, int target) {
        return my_pq.swap_arbitrary(source, target);
    }
    public PqReference<K> pop_arbitrary(int target) {
        return my_pq.pop_arbitrary(target);
    }
}


class Duplet<T, U>{
    public T a;
    public U b;
    
    public Duplet(T item_a, U item_b){
        this.a = item_a;
        this.b = item_b;
    }
    
    @Override
    public String toString() {
        return "[" + a + " " + b + "]";
    }
}

class ComparableDuplet<T extends Comparable<T>, U extends Comparable<U>>
        implements  Comparable<ComparableDuplet<T, U>>{
    public T a;
    public U b;
    
    public ComparableDuplet(T item_a, U item_b){
        this.a = item_a;
        this.b = item_b;
    }
    
    @Override
    public String toString() {
        return "[" + a + " " + b + "]";
    }
    
    @Override
    public int compareTo(ComparableDuplet<T, U> o) {
        if (a.compareTo(o.a) != 0)
            return a.compareTo(o.a);
        return b.compareTo(o.b);
    }
}



class ComparableDupletNegatedItem1<T extends Comparable<T>, U extends Comparable<U>>
        implements  Comparable<ComparableDupletNegatedItem1<T, U>>{
    public T a;
    public U b;
    
    public ComparableDupletNegatedItem1(T item_a, U item_b){
        this.a = item_a;
        this.b = item_b;
    }
    
    @Override
    public String toString() {
        return "[" + a + " " + b + "]";
    }
    
    @Override
    public int compareTo(ComparableDupletNegatedItem1<T, U> o) {
        if (a.compareTo(o.a) != 0)
            return o.a.compareTo(a);
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

class ComparableTriplet<T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>>
        implements Comparable<ComparableTriplet<T, U, V>>{
    public T a;
    public U b;
    public V c;
    
    public ComparableTriplet(T item_a, U item_b, V item_c){
        this.a = item_a;
        this.b = item_b;
        this.c = item_c;
    }
    
    
    @Override
    public String toString() {
        return "[" + a + ", " + b + ", " + c + "]";
    }
    
    @Override
    public int compareTo(ComparableTriplet<T, U, V> o) {
        if (a.compareTo(o.a) != 0)
            return a.compareTo(o.a);
        if (b.compareTo(o.b) != 0)
            return b.compareTo(o.b);
        return c.compareTo(o.c);
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


@SuppressWarnings("DuplicatedCode")
class sortings{
    public static <T extends Comparable<T>> void merge_sort(ArrayList<T> to_be_sorted){
        merge_sort(to_be_sorted, new ArrayList<>(to_be_sorted.size()+5));
    }
    public static <T extends Comparable<T>> void merge_sort(ArrayList<T> to_be_sorted, ArrayList<T> temporary){
        merge_sort(to_be_sorted, temporary, 0, to_be_sorted.size()-1);
    }
    
    public static <T extends Comparable<T>> void merge_sort(ArrayList<T> to_be_sorted, ArrayList<T> temporary,
                                                            int l, int r){
        if (r < l)
            return;
        
        ArrayList<Quadruplet<Integer, Integer, Boolean, Integer>>
                stack = new ArrayList<>(300);
        stack.add(new Quadruplet<>(l, r, false, -1));
        int m;
        
        while (!stack.isEmpty()){
            Quadruplet<Integer, Integer, Boolean, Integer> curr_task = stack.remove(stack.size() - 1);
            l = curr_task.a;
            r = curr_task.b;
            
            if (curr_task.c){  // c true -> merge. c false -> divide
                merge(to_be_sorted, temporary, curr_task);
            }else if (l != r){
                assert l < r;
                m = (curr_task.a + curr_task.b) / 2;
                stack.add(new Quadruplet<>(l, r, true, m));
                stack.add(new Quadruplet<>(l, m, false, -1));
                stack.add(new Quadruplet<>(m+1, r, false, -1));
            }
        }
    }
    
    public static <T> void merge_sort(Comparator<T> comparator, ArrayList<T> to_be_sorted){
        merge_sort(comparator, to_be_sorted, new ArrayList<>(to_be_sorted.size()+5));
    }
    public static <T> void merge_sort(Comparator<T> comparator, ArrayList<T> to_be_sorted, ArrayList<T> temporary){
        merge_sort(comparator, to_be_sorted, temporary, 0, to_be_sorted.size()-1);
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
    
    
    
    public static <T> void merge_sort(Comparator<T> comparator, ArrayList<T> to_be_sorted, ArrayList<T> temporary,
                                                            int l, int r){
        ArrayList<Quadruplet<Integer, Integer, Boolean, Integer>>
                stack = new ArrayList<>(300);
        stack.add(new Quadruplet<>(l, r, false, -1));
        int m;
        
        while (!stack.isEmpty()){
            Quadruplet<Integer, Integer, Boolean, Integer> curr_task = stack.remove(stack.size() - 1);
            l = curr_task.a;
            r = curr_task.b;
            
            if (curr_task.c){  // c true -> merge. c false -> divide
                merge(comparator, to_be_sorted, temporary, curr_task);
            }else if (l != r){
                m = (curr_task.a + curr_task.b) / 2;
                stack.add(new Quadruplet<>(l, r, true, m));
                stack.add(new Quadruplet<>(l, m, false, -1));
                stack.add(new Quadruplet<>(m+1, r, false, -1));
            }
        }
    }
    
    
    public static <T, U>
    void merge(Comparator<T> comparator, ArrayList<T> arr, ArrayList<T> temporary,
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
            while (comparator.compare(arr.get(kiri_start), arr.get(kanan_start)) <= 0){
                temporary.add(arr.get(kiri_start));
                kiri_start += 1;
                if (kiri_start > kiri_end)
                    break loop_luar;
            }
            while (comparator.compare(arr.get(kanan_start), arr.get(kiri_start)) < 0){
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
        splitter(sorted_arr, func, new Duplet<>(0, sorted_arr.size()-1));
    }
    public static <T extends Comparable<T>> void splitter(ArrayList<T> sorted_arr, BiConsumer<Integer, T> func,
                                                          Duplet<Integer, Integer> segment){
        ArrayList<Duplet<Integer, Integer>> stack = new ArrayList<>(200);
        stack.add(segment);
        
        while (!stack.isEmpty()){
            Duplet<Integer, Integer> curr_segment = stack.remove(stack.size() - 1);
            
            if (curr_segment.a > curr_segment.b)
                continue;
            
            int m = (curr_segment.a + curr_segment.b) / 2;
            func.accept(m, sorted_arr.get(m));
            stack.add(new Duplet<>(m+1, curr_segment.b));
            stack.add(new Duplet<>(curr_segment.a, m-1));
        }
    }
    
}

interface IGraph<T>{
    default void add_n_node(int n){
        add_n_node(n, 0);
    }
    default void add_n_node(int n, int reserve_cnt){
        for (int i = 0; i < n; i++) {
            add_node(reserve_cnt);
        }
    }
    default void add_node(){
        add_node(0);
    }
    void add_node(int reserve_neightbour_cnt);
    void add_uniedge(int from, int to, T item);
    default void add_biedge(int node1, int node2, T item){
        add_uniedge(node1, node2, item);
        add_uniedge(node2, node1, item);
    }
    boolean has_uniedge(int from, int to);
    default boolean has_biedge(int node1, int node2){
        return has_uniedge(node1, node2) && has_uniedge(node1, node2);
    }
    ArrayList<Duplet<Integer, T>> _get_neighbor_of(int node);  // dangerous: just return a reference. So it may alter the graph
    int size();
    default int node_number(){
        return size();
    }
}

@SuppressWarnings("DuplicatedCode")
class AdjListGraph<T> implements IGraph<T>{
    ArrayList<ArrayList<Duplet<Integer, T>>> adjlist;
    
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
        adjlist.get(from).add(new Duplet<>(to, item));
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
    public ArrayList<Duplet<Integer, T>> _get_neighbor_of(int node){
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

class CustomEdgeRemovableAdjListGraph<K extends Comparable<K>>{
    ArrayList<ReferenceablePq<ComparableDupletNegatedItem1<K, Integer>>> adjlist;
    ArrayList<ArrayList<PqReference<ComparableDupletNegatedItem1<K, Integer>>>> in_edges;
    
    CustomEdgeRemovableAdjListGraph(int reserve_node_cnt){
        adjlist = new ArrayList<>(reserve_node_cnt + 1);
        in_edges = new ArrayList<>(reserve_node_cnt + 1);
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
        adjlist.add(new ReferenceablePq<>(reserve_neightbour_cnt));
        in_edges.add(new ArrayList<>(reserve_neightbour_cnt));
        
        assert in_edges.size() == adjlist.size();
    }
    public void delete_node(int node){
        delete_node(node, true);
    }
    public void delete_node(int node, boolean alter_adjlist_and_inEdge){
        // incoming_edge_array = in_edge
        
        int this_incoming_node_number = in_edges.get(node).size();
        for (int i = this_incoming_node_number-1; i >= 0; i--) {
            if (in_edges.get(node).get(i).__pq_pos < 0)
                continue;  // kalau node-nya emang udah dihapus sebelumnya
            delete_incoming_edge(node, i, alter_adjlist_and_inEdge);
        }
        
        // dibikin invalid dulu
        adjlist.get(node).my_pq.array_list.forEach(
                (element) -> element.__pq_pos = -99999999
        );
        if (alter_adjlist_and_inEdge) {
            adjlist.get(node).my_pq.array_list.clear();
            adjlist.set(node, new RemovedNode());
        }
    }
    public void delete_incoming_edge(int node, int edge_index, boolean alter_in_edge){
        PqReference<ComparableDupletNegatedItem1<K, Integer>> to_be_removed;
        
        if (alter_in_edge)
            to_be_removed = in_edges.get(node).remove(edge_index);
        else
            to_be_removed = in_edges.get(node).get(edge_index);
  
        to_be_removed.pq.pop_arbitrary(to_be_removed.get_pq_pos());
        Solusi.TRCS.DEBUG_validate_custom_binary_heap(this);
    }
    
    
    public void add_uniedge(int from, int to, K item){  // unidirectional edge
        assert in_edges.size() == adjlist.size();

        
        if (!(0 <= from && from < adjlist.size())) throw new IndexOutOfBoundsException("`from` is not registered");
        if (!(0 <= to && to < adjlist.size())) throw new IndexOutOfBoundsException("`to` is not registered");
        if (Solusi.DEBUG){
            if (has_uniedge(from, to))  throw new IllegalStateException("Trying to add an existing edge");
        }
        PqReference<ComparableDupletNegatedItem1<K, Integer>>
                ret = adjlist.get(from).add(new ComparableDupletNegatedItem1<>(item, to));
        in_edges.get(to).add(ret);
    }
    public void add_biedge(int node1, int node2, K item){  // bidirectional edge
        // no need to check whether the edge  has already exist or not. It will be checked in register_new_uniedge()
        add_uniedge(node1, node2, item);
        add_uniedge(node2, node1, item);
    }
    public boolean has_uniedge(int from, int to){
        assert in_edges.size() == adjlist.size();
        
        if (!(0 <= from && from < adjlist.size())) throw new IndexOutOfBoundsException("`from` is not registered");
        if (!(0 <= to && to < adjlist.size())) throw new IndexOutOfBoundsException("`to` is not registered");
        
        for (int i = 0; i < adjlist.get(from).size(); i++) {
            if (adjlist.get(from).my_pq.array_list.get(i).key.b == to)
                return true;
        }
        return false;
    }
    public boolean has_biedge(int node1, int node2){
        return has_uniedge(node1, node2) && has_uniedge(node1, node2);
    }
    public ArrayList<PqReference<ComparableDupletNegatedItem1<K, Integer>>> _get_neighbor_of(int node){
        assert in_edges.size() == adjlist.size();
        
        // dangerous: just return a reference. So it may alter the graph
        return adjlist.get(node).my_pq.array_list;
    }
    
    public class RemovedNode extends ReferenceablePq<ComparableDupletNegatedItem1<K, Integer>>{}
    public boolean is_invalid(int node){
        return adjlist.get(node) instanceof CustomEdgeRemovableAdjListGraph.RemovedNode;
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
            arr.set(temp_stack.remove(temp_stack.size() - 1), root);
        }
        return root;
    }
    boolean is_in_the_same_set(int node_a, int node_b){
        return get_root(node_a) == get_root(node_b);
    }
    
    
    void union(int to, int from){
        if (is_in_the_same_set(from, to))
            return;  // do nothing kalo mereka udah di dalam set yang sama
        arr.set(get_root(from), get_root(to));
    }
}
