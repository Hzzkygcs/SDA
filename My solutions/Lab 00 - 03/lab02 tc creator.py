from itertools import permutations
from random import randint, choice, choices, sample, shuffle
import multiprocessing as mp
from glob import glob
from collections import deque
import os, io, sys, shutil




path = "D:\\01 Kuliah\\01 Dokumen\\71 - Struktur Data Algoritma\\09 Lab\\tc"
run_main = True
run_multiprocessing = True
tc_count = 100


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False





def random_beauty_str(n):
    temp = randint(1, 100)

    ret = []
    cons = False
    konsonan = "BCDFGHJKLMNPQRSTVWXYZ"
    vokal = "AIUEO"
    
    if temp <= 70:
        cons = True

    for i in range(n):
        if not cons:
            ret.append(choice(vokal))
        else:
            ret.append(choice(konsonan))
        cons = not cons
    return "".join(ret)


def solve():  # use input() and print()
    gengs = {}
    n = int(input())

    queue = deque()

    for i in range(n):
        input_ = input().split()

        if input_[0] == "DATANG":
            geng = input_[1]
            number = int(input_[2])
            
            for i in range(number):
                queue.append(geng)
            print(len(queue))
            
        elif input_[0] == "LAYANI":
            number = int(input_[1])
            
            for i in range(number):
                geng = queue.popleft()
                gengs[geng] = gengs.get(geng, 0) + 1
            print(geng)
                
        elif input_[0] == "TOTAL":
            geng = input_[1]
            print(gengs.get(geng, 0))
        else:
            raise Exception("invalid input")
    

# hard
banyak_query = 35_000
banyak_x = 10_000
banyak_y = 30_000
banyak_geng_max = 250

# super ez
banyak_query = 10
banyak_x = 5
banyak_y = 15
banyak_geng_max = 4

def generate_soal():  # use randint() (or other random func) and print()
    gi_options = set()

    banyak_geng = randint(1, banyak_geng_max)

    while len(gi_options) < banyak_geng:
        gi_options.add(random_beauty_str(randint(1, 10)))
        
    geng = list(gi_options)

    n = randint(1, banyak_query)
    print(n)
    num_queue = 0
    for i in range(n):
        temp = randint(1, 100)

        if 1 <= temp <= 50:  # DATANG
            temp = randint(1, banyak_x)
            num_queue += temp
            print("DATANG", choice(geng), temp)
        elif temp <= 65 and num_queue >= 1:
            temp = randint(1, min(num_queue, banyak_y))
            num_queue -= temp
            print("LAYANI", temp)
        else:
            print("TOTAL", choice(geng))
    
    


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
    if not run_checkers:
        g = open(f"{path}/out_{tc:02d}.txt", "w")
    

    input_ = io.StringIO()
    if not run_checkers:
        kunci_jawaban = io.StringIO()

    stdout = input_
    generate_soal()

    if not run_checkers:
        input_.seek(0)
        stdout = kunci_jawaban
        stdin = input_

        solve()

        kunci_jawaban.seek(0)
        shutil.copyfileobj(kunci_jawaban, g, -1)
        
    stdin = sys.stdin
    stdout = sys.stdout    
    input_.seek(0)
    shutil.copyfileobj(input_, f, -1)
    
    
    f.close()
    if not run_checkers:
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
        with mp.Pool(4) as p:
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
