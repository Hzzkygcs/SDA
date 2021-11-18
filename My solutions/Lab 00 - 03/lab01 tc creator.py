from itertools import permutations
from random import randint, choice, choices, sample
import multiprocessing as mp
from glob import glob
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






def solve():  # use input() and print()
    stasiun_cnt, place_cnt = map(int, input().split())

    L = lambda x: int(x) if x != "*" else x
    places = list(map(L, input().split()))

    arr = []
    
    temp = 0
    for i in range(1, len(places)):
        if places[i] == "*":
            arr.append(temp)
            temp = 0
        else:
            temp += places[i]

    max_sum = -99999999999999999999
    for i in range(len(arr)):
        summation = 0
        
        for j in range(i, len(arr)):
            summation += arr[j]

            if (max_sum < summation):
                max_sum = summation
    print(max_sum)
    


# banyak toko per stasiun (almost worst case) =
#   bnyk_toko_per_stasiun_max + (
#         2*untung_rugi_max / balancing_untung_rugi_min
#   )


# ez
bnyk_stasiun_max = 5
bnyk_toko_per_stasiun_max = 6  # ga persis. bisa lebih bisa kurang
untung_rugi_total = 100 # avg maximum total untung rugi tiap antar dua stasiun
untung_rugi_max = 100
balancing_untung_rugi_min = 50

# Hard core
bnyk_stasiun_max = 400
bnyk_toko_per_stasiun_max = 200  # ga persis. bisa lebih bisa kurang
untung_rugi_total = 100000 # avg maximum total untung rugi tiap antar dua stasiun
untung_rugi_max = 2000
balancing_untung_rugi_min = 100


def generate_soal():  # use randint() (or other random func) and print()
    raw_arr = []

    for i in range(randint(1, bnyk_stasiun_max - 1)):
        raw_arr.append(randint(-untung_rugi_total, untung_rugi_total))

    arr = []
    arr.append("*")

    for i in raw_arr:
        for j in range(randint(1, bnyk_toko_per_stasiun_max)):
            if -100 <= i <= 100:
                temp = randint(-untung_rugi_max, untung_rugi_max)
            elif i < - 100:
                temp = randint(1, untung_rugi_max)
            else:
                temp = - randint(1, untung_rugi_max)
                
            arr.append(temp)
            i += temp
            

        while (i != 0):
            temp = randint(balancing_untung_rugi_min,
                           untung_rugi_max)
            if i < 0:
                temp = min(temp, abs(i))
            else:
                temp = max(-temp, -abs(i))
            arr.append(temp)
            i += temp

        arr.append("*")

    print(len(raw_arr) + 1, len(arr))
    print(" ".join(map(str, arr)))
        


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
