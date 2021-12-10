from itertools import permutations
from random import randint, choice, choices, sample, shuffle
import multiprocessing as mp
from glob import glob
from copy import deepcopy
import os, io, sys, shutil, string
import dijkstar

path = r"D:\01 Kuliah\01 Dokumen\71 - Struktur Data Algoritma\08 TP\TP_03\tc"
run_main = True
run_multiprocessing = False
tc_count = 3

# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False
extra_space = True


def rand_kelompok_tc():
    return 3
    # return randint(1, 4)

def rand_query(kelompok:int):
    if kelompok == 1:
        return choice((TAMBAH,)*1 + (RESIGN,)*1 + (CARRY,)*1 + (SIMULASI,)*1)
    elif kelompok == 3:
        return choice((TAMBAH,)*1 + (RESIGN,)*1 + (SEBAR,)*60)
    else:
        raise Exception()

temp = True
temp2 = True

PANGKAT_INCREMENTAL = temp
PANGKAT_SAMA = not temp

LINEAR = temp2
TERPUSAT = not temp2



TAMBAH = 1
RESIGN = 2
CARRY = 3
BOSS = 4
SEBAR = 5
SIMULASI = 6
NETWORKING = 7

solve = None


def solve(input_seluruhnya):  # use input() and print()
    # pprint(repr(input_seluruhnya))
    banyak_node, banyak_edge, banyak_query = map(int, input().split())
    pangkat = [-999]
    pangkat.extend(map(int, input().split()))
    edges = []
    queries = []

    if extra_space:
        input()

    for i in range(banyak_edge):
        edges.append(tuple(map(int, input().split())))

    if extra_space:
        input()

    kelompok_tc = 1
    for i in range(banyak_query):
        inp = tuple(map(int, input().split()))
        queries.append(inp)
        if inp[0] == NETWORKING:
            kelompok_tc = 2
        if inp[0] == SEBAR:
            kelompok_tc = 3
        if inp[0] == BOSS:
            kelompok_tc = 4

    if kelompok_tc == 3:  # Sebar
        solve_kelompok_3(banyak_node, pangkat, edges, queries)


if tc_count <= 20:
    solve = None



def solve_kelompok_3(banyak_node: int, pangkat_list: list, edges: list, queries: list):
    graf = dijkstar.Graph(undirected=True)

    for i in range(1, banyak_node + 1):
        graf.add_node(i)

    for i in range(1, len(pangkat_list)):
        for j in range(i + 1, len(pangkat_list)):
            if pangkat_list[i] == pangkat_list[j]:
                graf.add_edge(i, j, 1)

    for edge in edges:
        try:
            graf.get_edge(edge[0], edge[1])
            # pprint('z', graf.get_edge(edge[0], edge[1]))
        except KeyError:
            # pprint("z error")
            graf.add_edge(edge[0], edge[1], 1)

    for query in queries:
        if query[0] == TAMBAH:
            try:
                graf.get_edge(query[1], query[2])
                # pprint('y', graf.get_edge(query[1], query[2]))
            except KeyError:
                # pprint("z error")
                graf.add_edge(query[1], query[2], 1)

        elif query[0] == RESIGN:
            yang_resign = query[1]
            graf.remove_node(yang_resign)
        elif query[0] == SEBAR:
            # get all nodes yang masih belom resign
            try:
                path = dijkstar.find_path(graf, query[1], query[2])
                print(max(0, path.total_cost - 1))  # 0 jika berusaha memanggil perintah sebar terhadap node itu sendiri
            except dijkstar.NoPathError:
                print(-1)
        else:
            raise Exception()



class Graph:
    # https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/
    def __init__(self, vertices):
        self.V = vertices  # No. of vertices
        self.graph = []  # default dictionary
        # to store graph

    # function to add an edge to graph
    def addEdge(self, u, v, w):
        self.graph.append([u, v, w])

    # A utility function to find set of an element i
    # (uses path compression technique)
    def find(self, parent, i):
        if parent[i] == i:
            return i
        return self.find(parent, parent[i])

    # A function that does union of two sets of x and y
    # (uses union by rank)
    def union(self, parent, rank, x, y):
        xroot = self.find(parent, x)
        yroot = self.find(parent, y)

        # Attach smaller rank tree under root of
        # high rank tree (Union by Rank)
        if rank[xroot] < rank[yroot]:
            parent[xroot] = yroot
        elif rank[xroot] > rank[yroot]:
            parent[yroot] = xroot

        # If ranks are same, then make one as root
        # and increment its rank by one
        else:
            parent[yroot] = xroot
            rank[xroot] += 1

    # The main function to construct MST using Kruskal's
    # algorithm
    def KruskalMST(self):

        result = []  # This will store the resultant MST

        # An index variable, used for sorted edges
        i = 0

        # An index variable, used for result[]
        e = 0

        # Step 1:  Sort all the edges in
        # non-decreasing order of their
        # weight.  If we are not allowed to change the
        # given graph, we can create a copy of graph
        self.graph = sorted(self.graph,
                            key=lambda item: item[2])

        parent = []
        rank = []

        # Create V subsets with single elements
        for node in range(self.V):
            parent.append(node)
            rank.append(0)

        # Number of edges to be taken is equal to V-1
        while e < self.V - 1:

            # Step 2: Pick the smallest edge and increment
            # the index for next iteration
            u, v, w = self.graph[i]
            i = i + 1
            x = self.find(parent, u)
            y = self.find(parent, v)

            # If including this edge does't
            #  cause cycle, include it in result
            #  and increment the indexof result
            # for next edge
            if x != y:
                e = e + 1
                result.append([u, v, w])
                self.union(parent, rank, x, y)
            # Else discard the edge

        minimumCost = 0
        for u, v, weight in result:
            minimumCost += weight
        return minimumCost

def get_random(set_or_dict, sample_number=1):
    if isinstance(set_or_dict, (set, list, tuple)):
        return tuple(sample(set_or_dict, sample_number))
    elif isinstance(set_or_dict, dict):
        return tuple(sample(set_or_dict.keys(), sample_number))
    elif isinstance(set_or_dict, RandomableSet):
        return set_or_dict.sample(sample_number)
    else:
        raise Exception()

def generate_soal():  # use randint() (or other random func) and print()
    kelompok_tc = 3

    if tc_count < 20:
        banyak_node = randint(95_000, 100_000)
        # banyak_edge_awal = randint(1, max(1, min(5_000, banyak_node * (banyak_node - 1) // 5)))
        banyak_edge_awal = randint(185_000, max(1, min(200_000, banyak_node * (banyak_node - 1) // 5)))
        pass
    else:
        banyak_node = randint(2, 12)
        banyak_edge_awal = randint(1, max(1, min(24, banyak_node*(banyak_node-1) // 5)))

    if PANGKAT_SAMA:
        assert not PANGKAT_INCREMENTAL
        temp = randint(1, banyak_node)
        pangkat = [temp for i in range(banyak_node)]
    elif PANGKAT_INCREMENTAL:
        pangkat = [i+1 for i in range(banyak_node)]
    else:
        pangkat = [randint(1, banyak_node) for i in range(banyak_node)]

    if kelompok_tc == 3:
        generate_soal_kelompok_3(banyak_node, pangkat, banyak_edge_awal)
    else:
        raise Exception()


def generate_soal_kelompok_3(banyak_node, pangkat, banyak_edge):
    if tc_count < 20:
        banyak_query = randint(190, 200)
    else:
        banyak_query = randint(1, 10)

    if LINEAR:
        banyak_edge = -1
    if TERPUSAT:
        assert banyak_node >= 2
        banyak_edge = banyak_node-1 + banyak_node-2

    to_be_printed = [f"{banyak_node} {banyak_edge} {banyak_query}"]
    to_be_printed.append(" ".join(map(str, pangkat)))

    if extra_space:
        to_be_printed.append("")

    nodes = RandomableSet([i for i in range(1, banyak_node + 1)])
    edges = RandomableSet()

    def random_not_exist_edge(max_trial=100):
        while (rand_edge := frozenset(get_random(nodes, 2))) in edges:
            if (max_trial == 0):
                return None
            max_trial -= 1
        edges.add(rand_edge)
        return rand_edge

    edges_to_be_printed = []

    if LINEAR:
        assert not TERPUSAT
        banyak_edge = 0
        for i in range(banyak_node-1):
            edges_to_be_printed.append(f"{i+1} {i+2}")  # +1 karena i zero based, sedangkan ini harus one-based
            banyak_edge += 1
        for i in range(banyak_node-5):
            edges_to_be_printed.append(f"{i+1} {i+3}")  # +1 karena i zero based, sedangkan ini harus one-based
            banyak_edge += 1
        to_be_printed[0] = f"{banyak_node} {banyak_edge} {banyak_query}"
    elif TERPUSAT:
        cnt = 0
        for i in range(2, banyak_node+1):  # 2 sampai banyak_node-1
            edges_to_be_printed.append(f"{1} {i}")
            cnt += 1
        for i in range(3, banyak_node+1):
            edges_to_be_printed.append( f"{2} {i}")
            cnt += 1
        assert cnt == banyak_edge
    else:
        for i in range(banyak_edge):
            rand_edge = random_not_exist_edge(1000)
            to_be_printed.append(" ".join(map(str, rand_edge)))
    shuffle(edges_to_be_printed)
    to_be_printed.extend(edges_to_be_printed)

    if extra_space:
        to_be_printed.append("")

    q = 0
    while q < banyak_query:
        query = rand_query(3)

        if len(nodes) == 0:
            banyak_query = q
            to_be_printed[0] = f"{banyak_node} {banyak_edge} {banyak_query}"
            break

        if query == TAMBAH:
            if len(nodes) <= 1:
                continue

            rand_edge = random_not_exist_edge()
            if rand_edge is None:
                continue

            rand_edge = list(rand_edge)
            shuffle(rand_edge)
            to_be_printed.append(f"{TAMBAH} {rand_edge[0]} {rand_edge[1]}")

        elif query == RESIGN:
            yang_resign = get_random(nodes)[0]
            nodes.remove(yang_resign)
            to_be_printed.append(f"{RESIGN} {yang_resign}")
        elif query == SEBAR:
            if LINEAR:
                to_be_printed.append(f"{SEBAR} {1} {banyak_node}")
            elif TERPUSAT:
                if (randint(1, 100) < 50):
                    to_be_printed.append(f"{SEBAR} {1} {banyak_node}")
                else:
                    to_be_printed.append(f"{SEBAR} {2} {banyak_node}")
            else:
                a = nodes.choice()
                b = nodes.choice()
                to_be_printed.append(f"{SEBAR} {a} {b}")
        else:
            raise Exception()
        q += 1
    # pprint(repr(to_be_printed))
    print("\n".join(to_be_printed))



def random_str(taken_name: set):
    vowels = "AIUEO"
    non_vowels = "BCDFGHJKLMNPQRSTVWXYZ"

    while (
            temp := "".join((
                    choice(non_vowels) + choice(vowels) for i in range(randint(1, 4))
            ))
    ) in taken_name:
        pass
    taken_name.add(temp)
    return temp


# check the out file at `path`. return False if it's not valid.
# firstly receive the input from stdin input(), then call switch_stdin() and then receive
# the output that's need to be checked which is also from stdin input()
def checkers(tc):
    switch_stdin()
    return False


class RandomableSet(object):
    # https://stackoverflow.com/a/15993515/7069108
    def __init__(self, initial_value=None):
        self.item_to_position = {}
        self.items = []
        if initial_value is not None:
            for i in initial_value:
                self.add(i)

    def add(self, item):
        if item in self.item_to_position:
            return
        self.items.append(item)
        self.item_to_position[item] = len(self.items) - 1

    def remove(self, item):
        position = self.item_to_position.pop(item)
        last_item = self.items.pop()
        if position != len(self.items):
            self.items[position] = last_item
            self.item_to_position[last_item] = position

    def choice(self):
        return choice(self.items)

    def sample(self, k):
        return sample(self.items, k)

    def __contains__(self, item):
        return item in self.item_to_position

    def __iter__(self):
        return iter(self.items)

    def __len__(self):
        return len(self.items)

    def __repr__(self):
        return str(set(self.item_to_position.keys()))


BREAK_WORKER = False


def worker(tc):
    global min_, stdin, stdout, BREAK_WORKER, switch_stdin

    if BREAK_WORKER:
        return

    if run_checkers and (force_checkers or os.path.isfile(f"{path}/out_{tc:02d}.txt")):
        assert os.path.isfile(f"{path}/out_{tc:02d}.txt"), "output file is not available"
        pprint("checkers is run")

        with open(f"{path}/in_{tc:02d}.txt", 'r') as input_tc:
            with open(f"{path}/out_{tc:02d}.txt", 'r') as output_tc:  # yg perlu di cek
                stdout = output_temp = io.StringIO()
                stdin = io.StringIO(input_tc.read())
                output_ = io.StringIO(output_tc.read())
                assert stdin is not None and output_ is not None

                def switch_stdin():
                    global stdin
                    stdin = output_

                result = checkers(tc)
                stdin = sys.stdin
                stdout = sys.stdout

                if result is not True:
                    input_tc.seek(0)
                    output_tc.seek(0)
                    print("CHECKERS IS NOT TRUE, tc:", tc, file=output_temp)
                    print(result, file=output_temp)
                    print("=============  inp  =============", file=output_temp)
                    print(input_tc.read(), file=output_temp)
                    print("=============  out  =============", file=output_temp)
                    print(output_tc.read(), file=output_temp)  # output
                    print("=================================", file=output_temp)
                    BREAK_WORKER = True
                    output_temp.seek(0)
                    raise Exception("\n\n\n\n\n\n\n\n" + output_temp.read()) from None
        return

    f = open(f"{path}/in_{tc:02d}.txt", "w")
    if not run_checkers and solve is not None:
        g = open(f"{path}/out_{tc:02d}.txt", "w")

    input_ = io.StringIO()
    if not run_checkers and solve is not None:
        kunci_jawaban = io.StringIO()

    stdout = input_
    generate_soal()

    if not run_checkers and solve is not None:
        input_.seek(0)
        stdout = kunci_jawaban
        stdin = input_

        str_input = stdin.read()  # untuk debug kalau perlu
        stdin.seek(0)

        solve(str_input)

        kunci_jawaban.seek(0)
        shutil.copyfileobj(kunci_jawaban, g, -1)

    stdin = sys.stdin
    stdout = sys.stdout
    input_.seek(0)
    shutil.copyfileobj(input_, f, -1)

    f.close()
    if not run_checkers and solve is not None:
        g.close()


_print = print
_input = iinput = input
stdout = sys.stdout


def print(*args, sep=' ', end='\n', file=None, flush=False):
    if file is None:
        file = stdout
    _print(*args, sep=sep, end=end, file=file, flush=flush)


def dummy_print(*args, sep=' ', end='\n', file=None, flush=False):
    pass


if not pprint_is_dummy_print:
    pprint = _print
else:
    pprint = dummy_print

stdin = sys.stdin


def input(prompt=None, /):
    if prompt is not None:
        print(prompt, end='')
    return stdin.readline().rstrip("\n")


if __name__ == "__main__" and run_main:

    if not os.path.exists(path):
        os.makedirs(path)

    if not run_checkers:
        # clear all txt in the path
        for file in glob(f"{path}/*.txt"):
            if os.path.isfile(file):
                os.remove(file)

    if not run_multiprocessing:
        for iii in range(tc_count):
            worker(iii)
        print("DONE")
    else:
        with mp.Pool(7) as p:
            p.map(worker, list(range(tc_count)))
        sys.stdout.flush()
        print("DONE")

    if run_checkers and not force_checkers:
        if os.path.isfile(f"{path}/out_{00:02d}.txt"):
            print("Finished checking.")
            print("DELETING OUTPUT FILES")
        else:
            print("Finished GENERATING.")
        for tc in range(tc_count):
            temp = f"{path}/out_{tc:02d}.txt"
            if os.path.isfile(temp):
                os.remove(temp)
    elif run_checkers:
        print("Finished checking")

elif __name__ == "__main__":
    solve("")
