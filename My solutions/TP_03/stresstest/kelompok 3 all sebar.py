from itertools import permutations
from random import randint, choice, choices, sample, shuffle
import multiprocessing as mp
from glob import glob
from copy import deepcopy
import os, io, sys, shutil, string
import dijkstar

path = r"D:\01 Kuliah\01 Dokumen\71 - Struktur Data Algoritma\08 TP\TP_03\tc"
run_main = True
run_multiprocessing = True
tc_count = 14

# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False
extra_space = True



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
    else:
        raise Exception()


if tc_count <= 20:
    solve = None


def solve_kelompok_1(banyak_node: int, pangkat_list: list, edges: list, queries: list):
    graf = [set() for i in range(banyak_node + 1)]  # +1 karena biar one-based index
    graf[0] = None

    for edge in edges:
        graf[edge[0]].add(edge[1])
        graf[edge[1]].add(edge[0])

    def resign(yang_resign):
        graf[yang_resign] = None
        for neighbor_set in graf:
            if neighbor_set is None:
                continue
            neighbor_set.discard(yang_resign)

    def find_max(node):
        if not graf[node]:
            return None
        maximum = -9999
        for i in graf[node]:
            maximum = max(maximum, pangkat_list[i])
        assert maximum > 0
        return maximum

    def is_minder(node):
        assert graf[node] is not None
        if len(graf[node]) == 0:
            return False

        for i in graf[node]:
            if pangkat_list[i] < pangkat_list[node]:
                return False
        return True

    for query in queries:
        if query[0] == TAMBAH:
            graf[query[1]].add(query[2])
            graf[query[2]].add(query[1])
        elif query[0] == RESIGN:
            yang_resign = query[1]
            resign(yang_resign)

        elif query[0] == CARRY:
            yang_dicarry = query[1]
            res = find_max(yang_dicarry)
            if res is None:
                print(0)
            else:
                print(res)

        elif query[0] == SIMULASI:
            # get all nodes yang masih belom resign
            backup_graph = deepcopy(graf)

            nodes = [(pangkat_list[i], i) for i in range(1, banyak_node + 1) if graf[i] != None]
            nodes.sort()

            akan_resign = [0]
            while akan_resign != []:
                akan_resign.clear()
                for pangkat, node in nodes:
                    if graf[node] is not None and is_minder(node):
                        akan_resign.append(node)
                        # pprint("akan resign", node)
                for node in akan_resign:
                    resign(node)
                    # pprint("resign ", node)

            count = 0
            for i in range(1, len(graf)):
                if graf[i] is not None:
                    count += 1
            print(count)
            graf = backup_graph
            # pprint("simulasi end")
            pass
        else:
            raise Exception()


def solve_kelompok_2(banyak_node: int, pangkat_list: list, edges: list, queries: list):
    graf = Graph(banyak_node)  # zero based index

    temp = set()
    for edge in edges:
        if edge[0] > edge[1]:
            edge = (edge[1], edge[0])
        temp.add(edge)
        graf.addEdge(edge[0] - 1, edge[1] - 1, 0)
    edges = temp

    for i in range(1, banyak_node + 1):
        for j in range(i, banyak_node + 1):
            if (i, j) not in edges:
                graf.addEdge(i - 1, j - 1, abs(pangkat_list[i] - pangkat_list[j]))
    print(graf.KruskalMST())


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


def solve_kelompok_4(banyak_node: int, pangkat_list: list, edges: list, queries: list):
    graf = [set() for i in range(banyak_node + 1)]
    graf[0] = None

    for src, target in edges:
        graf[src].add(target)
        graf[target].add(src)

    def get_max(node_set: set) -> int:
        ret = -999999999
        for i in node_set:
            ret = max(ret, pangkat_list[i])
        return ret

    for query in queries:
        assert query[0] == BOSS
        if len(graf[query[1]]) == 0:
            print(0)
        else:
            visited = [False] * (banyak_node + 1)
            stack = list(graf[query[1]])
            visited[query[1]] = True
            maks = -9999
            while stack:
                curr = stack.pop()
                if visited[curr]:
                    continue
                visited[curr] = True
                maks = max(maks, pangkat_list[curr])
                stack.extend(graf[curr])
            print(maks)


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


def generate_soal():  # use randint() (or other random func) and print()
    kelompok_tc = 3

    if tc_count < 20:
        banyak_node = randint(95_000, 100_000)
        banyak_edge_awal = randint(20_000, max(1, min(200_000, banyak_node*(banyak_node-1) // 5)))
        pass
    else:
        banyak_node = randint(2, 10)
        # banyak_node = randint(2, 90)
        banyak_edge_awal = randint(1, max(1, min(200_000, banyak_node * (banyak_node - 1) // 5)))

    pangkat = [randint(1, banyak_node) for i in range(banyak_node)]

    if kelompok_tc == 3:
        generate_soal_kelompok_3(banyak_node, pangkat, banyak_edge_awal)
    else:
        raise Exception()


def get_random(set_or_dict, sample_number=1):
    if isinstance(set_or_dict, (set, list, tuple)):
        return tuple(sample(set_or_dict, sample_number))
    elif isinstance(set_or_dict, dict):
        return tuple(sample(set_or_dict.keys(), sample_number))
    elif isinstance(set_or_dict, RandomableSet):
        return set_or_dict.sample(sample_number)
    else:
        raise Exception()


def generate_soal_kelompok_1(banyak_node, pangkat, banyak_edge):
    if tc_count < 20:
        banyak_query = randint(97_000, 100_000)
    else:
        banyak_query = randint(1, 10)

    pangkat = [i for i in range(1, banyak_node+1)]
    shuffle(pangkat)

    to_be_printed = [f"{banyak_node} {banyak_edge} {banyak_query}"]
    to_be_printed.append(" ".join(map(str, pangkat)))

    if extra_space:
        to_be_printed.append("")

    nodes = {i for i in range(1, banyak_node + 1)}
    edges = set()

    def random_not_exist_edge(max_trial=100):
        while (rand_edge := frozenset(get_random(nodes, 2))) in edges:
            if (max_trial == 0):
                return None
            max_trial -= 1
        edges.add(rand_edge)
        return rand_edge

    for i in range(banyak_edge):
        rand_edge = random_not_exist_edge(1000)
        to_be_printed.append(" ".join(map(str, rand_edge)))

    if extra_space:
        to_be_printed.append("")

    q = 0
    while q < banyak_query:
        query = choice((TAMBAH, RESIGN, CARRY, SIMULASI))

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
        elif query == CARRY:
            to_be_printed.append(f"{CARRY} {get_random(nodes)[0]}")
        elif query == SIMULASI:
            to_be_printed.append(f"{SIMULASI}")
        else:
            raise Exception()
        q += 1
    # pprint(repr(to_be_printed))
    print("\n".join(to_be_printed))


def generate_soal_kelompok_2(banyak_node, pangkat, banyak_edge):
    banyak_query = 1

    print(f"{banyak_node} {banyak_edge} {banyak_query}")
    print(" ".join(map(str, pangkat)))

    if extra_space:
        print("")

    nodes = [i for i in range(1, banyak_node + 1)]
    edges = set()

    def random_not_exist_edge(max_trial=100):
        while (rand_edge := frozenset(get_random(nodes, 2))) in edges:
            if (max_trial == 0):
                return None
            max_trial -= 1
        edges.add(rand_edge)
        return rand_edge

    for i in range(banyak_edge):
        rand_edge = random_not_exist_edge(1000)
        print(*rand_edge)

    if extra_space:
        print("")

    print(NETWORKING)


def generate_soal_kelompok_3(banyak_node, pangkat, banyak_edge):
    if tc_count < 20:
        banyak_query = randint(195, 200)
    else:
        banyak_query = randint(1, 10)

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

    for i in range(banyak_edge):
        rand_edge = random_not_exist_edge(1000)
        to_be_printed.append(" ".join(map(str, rand_edge)))

    if extra_space:
        to_be_printed.append("")

    q = 0
    while q < banyak_query:
        query = SEBAR

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
            a = nodes.choice()
            b = nodes.choice()
            to_be_printed.append(f"{SEBAR} {a} {b}")
        else:
            raise Exception()
        q += 1
    # pprint(repr(to_be_printed))
    print("\n".join(to_be_printed))


def generate_soal_kelompok_4(banyak_node, pangkat, banyak_edge):
    if tc_count < 20:
        banyak_query = randint(95_000, 100_000)
    else:
        banyak_query = randint(1, 10)

    print(f"{banyak_node} {banyak_edge} {banyak_query}")
    print(" ".join(map(str, pangkat)))

    if extra_space:
        print("")

    nodes = RandomableSet([i for i in range(1, banyak_node + 1)])
    edges = RandomableSet()

    def random_not_exist_edge(max_trial=100):
        while (rand_edge := frozenset(get_random(nodes, 2))) in edges:
            if (max_trial == 0):
                return None
            max_trial -= 1
        edges.add(rand_edge)
        return rand_edge

    for i in range(banyak_edge):
        rand_edge = random_not_exist_edge(1000)
        print(" ".join(map(str, rand_edge)))

    if extra_space:
        print("")

    q = 0
    while q < banyak_query:
        query = choice((BOSS,))
        print(f"{BOSS} {nodes.choice()}")
        q += 1


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
