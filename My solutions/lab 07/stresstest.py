from itertools import permutations
from random import randint, choice, choices, sample, shuffle
import multiprocessing as mp
from glob import glob
import os, io, sys, shutil, string




path = r"D:\01 Kuliah\01 Dokumen\71 - Struktur Data Algoritma\09 Lab\lab 07\tc"
run_main = True
run_multiprocessing = True
tc_count = 1


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False




def solve(input_seluruhnya):  # use input() and print()
    pass
solve = None


banyak_node_limit = (2, 9)
banyak_orang_limit = (1, 5)
banyak_tiket_toll_limit = (0, 10)


def banyak_edge_limit(banyak_node):
    n = banyak_node
    min_num = banyak_node-1
    return randint(min_num, max(min_num, min(11, n*(n-1)//4)))


# """
banyak_node_limit = (204, 204)
banyak_orang_limit = (97118, 97118)
banyak_tiket_toll_limit = (0, max(1, banyak_node_limit[1]//8))

def banyak_edge_limit(banyak_node):
    n = banyak_node
    min_num = banyak_node-1
    return 1000
# """



"""
banyak_node_limit = (4250, 4500)
banyak_orang_limit = (95_000, 100_000)
banyak_tiket_toll_limit = (90000, 100_000)

def banyak_edge_limit(banyak_node):
    n = banyak_node
    min_num = banyak_node-1
    return randint(min_num, max(min_num, min(9000, n*(n-1)//4)))
# """


def generate_soal():  # use randint() (or other random func) and print()
    global banyak_node, banyak_edge, graph
    
    banyak_node = randint(*banyak_node_limit)
    banyak_edge = banyak_edge_limit(banyak_node)
    banyak_orang = randint(*banyak_orang_limit)

    print(banyak_node, banyak_edge, banyak_orang)
    edges = []
    graph = MatrixGraph(banyak_node+1)
    graph.generate_spanning_tree_circular(randint(1, banyak_node), banyak_node, edges)
    
    banyak_additional_edge = banyak_edge - (banyak_node-1)
    graph.generate_random_unique_edge(banyak_additional_edge, banyak_node, edges)

    shuffle(edges)
    for edge in edges:
        if randint(0, 1) == 0:
            print(*edge)
        else:
            print(edge[1], edge[0], edge[2])

    for i in range(banyak_orang):
        kampung_a = randint(1, banyak_node)
        banyak_tiket_toll = randint(*banyak_tiket_toll_limit)
        while (kampung_b := randint(1, banyak_node)) == kampung_a:
            pass

        print(kampung_a, kampung_b, banyak_tiket_toll)




class Null():
    def __repr__(self):
        return "null"
    def __eq__(self, other):
        return self.__class__ == other.__class__
Null = Null()


class MatrixGraph():
    def __init__(self, node_number):
        self.list = [
            [
                Null for j in range(node_number)
            ] for i in range(node_number)
        ]

    def add_biedge(self, a, b, value):
        global zz, aa, bb
        
        self.list[a][b] = value
        self.list[b][a] = value
        zz = self.list
        aa = a
        bb = b

    def has_edge(self, from_, to):
        global ffrom, tto
        ffrom = from_
        tto = to
        return self.list[from_][to] != Null

    def generate_spanning_tree_circular(self, start, node_number, pairs:list):
        leaves = set()
        connected = set()
        not_connected = {i for i in range(1, node_number+1)}
        assert len(not_connected) == node_number

        leaves.add(start)
        connected.add(start)
        not_connected.remove(start)

        while len(not_connected) != 0:
            choosen = sample(not_connected, 1)[0]
            tree = sample(connected, 1)[0]
            toll = randint(0, 1)
            assert not self.has_edge(choosen, tree)

            pairs.append((choosen, tree, toll))
            self.add_biedge(choosen, tree, toll)

            leaves.add(choosen)
            connected.add(choosen)
            not_connected.remove(choosen)

    def generate_random_unique_edge(self, number_of_edge_, number_of_node, pairs:list):
        number_of_edge = number_of_edge_
        all_node = {i for i in range(1, number_of_node+1)}
        while number_of_edge > 0:
            choosen, tree = sample(all_node, 2)
            toll = randint(0, 1)

            if not self.has_edge(choosen, tree):
                self.add_biedge(choosen, tree, toll)
                number_of_edge -= 1
                pairs.append((choosen, tree, toll))
                


class Leaf:
    def __init__(self, key_value, additional):
        self.k = key
        self.a = additional

    def __eq__(self, other):
        return self.k == other.k

    def __hash__(self):
        return hash(self.k)


def random_str(taken_name:set):
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
                    print("CHECKERS IS NOT TRUE, tc:",tc, file=output_temp)
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
    if not run_checkers:
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



if __name__ == "__main__"  and run_main:
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
        with mp.Pool(5) as p:
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
    solve()
