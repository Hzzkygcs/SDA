from itertools import permutations
from random import randint, choice, choices, sample
import multiprocessing as mp
from glob import glob
import os, io, sys, shutil, string
import itertools as it



path = r"D:\01 Kuliah\01 Dokumen\71 - Struktur Data Algoritma\09 Lab\lab 06\tc"
run_main = True
run_multiprocessing = True
tc_count = 10


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False


INF = 99999999999999999999999999999999999999999





def solve(input_seluruhnya):  # use input() and print()
    temp = int(input())  # dummy dataran_number
    
    dataran_list = list(map(int, input().split()))
    assert temp == len(dataran_list)

    query_number = int(input())
    for q in range(query_number):
        query, *other = input().split()

        if query == "A":
            new_land = int(other[0])
            dataran_list.append(new_land)
        elif query == "U":
            target = int(other[0])
            new_height = int(other[1])
            dataran_list[target] = new_height
        elif query == "R":
            lowest_dataran_loc = -1
            lowest_dataran = INF

            for i in range(len(dataran_list)):
                if dataran_list[i] < lowest_dataran:
                    lowest_dataran = dataran_list[i]
                    lowest_dataran_loc = i

            loc = lowest_dataran_loc
            highest = lowest_dataran

            if loc-1 >= 0:
                highest = max(highest, dataran_list[loc - 1])
            if loc+1 < len(dataran_list):
                highest = max(highest, dataran_list[loc + 1])

            dataran_list[loc] = highest
            if loc-1 >= 0:
                dataran_list[loc-1] = highest
            if loc+1 < len(dataran_list):
                dataran_list[loc+1] = highest
            print(highest, loc)
        else:
            raise Exception()

        if not run_main:
            pprint(dataran_list)

# solve = None


def random_query():
    return choice("UAR")

"""
    temp = randint(1, 100)
    if temp <= 27:
        return "A"
    elif temp <= 60:
        return "U"
    else:
        return "R"
        """

def random_ketinggian():
    return randint(1, 40)

def generate_soal():  # use randint() (or other random func) and print()
    dataran_number_awal = randint(1, 7)
    print(dataran_number_awal)
    dataran = [random_ketinggian() for i in range(dataran_number_awal)]
    print(" ".join(map(str, dataran)))

    query_number = randint(1, 8)
    banyak_dataran = dataran_number_awal
    
    print(query_number)
    for q in range(query_number):
        perintah = random_query()

        if perintah == "A":
            ketinggian = random_ketinggian()
            print("A", ketinggian)
            banyak_dataran += 1
            
        elif perintah == "U":
            target = randint(0, banyak_dataran-1)
            ketinggian_baru = random_ketinggian()
            print("U", target, ketinggian_baru)
        elif perintah == "R":
            print("R")
        else:
            raise Exception()




def random_str(taken_name:set):
    vowels = "AIUEO"
    non_vowels = "BCDFGHJKLMNPQRSTVWXYZ"
    
    while (
        temp := "".join((
            choice(non_vowels) + choice(vowels) for i in range(randint(1, 1))
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
    solve("")
