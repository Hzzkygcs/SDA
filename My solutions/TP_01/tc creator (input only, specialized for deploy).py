from itertools import permutations
from random import randint, choice, choices, sample, shuffle
import multiprocessing as mp
from glob import glob
import os, io, sys, shutil




path = "D:\\01 Kuliah\\01 Dokumen\\71 - Struktur Data Algoritma\\08 TP\\TP_01\\tc"
run_main = True
run_multiprocessing = True
tc_count = 1


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False



forced_role = 1

""" easy debug
min_tc = 1
max_tc = 2

min_orang = 1
max_orang = 7

min_hari = 1
max_hari = 1
min_event = 1
max_event = 1
randomize_name = False
move_the_top_only = True
#"""

# """ time tester hardcore

min_tc = 5
max_tc = 5

min_orang = 998
max_orang = 1000

min_hari = 498
max_hari = 500
min_event = 995
max_event = 1000
randomize_name = False
move_the_top_only = False
# """


# deploy only
pisah_tc = [4] * tc_count
min_hari = 1 if min_hari is None else min_hari
min_event = 1 if min_event is None else min_event

kode_combinations = [chr(65 + i) for i in range(26)]
for i in range(26):
    temp1 = chr(65 + i)

    for j in range(26):
        temp2 = temp1 + chr(65 + j)
        kode_combinations.append(temp2)

        for k in range(26):
            temp3 = temp2 + chr(65 + k)
            kode_combinations.append(temp3)
kode_combinations.sort()


def generate_soal(nomor_soal):  # use randint() (or other random func) and print()
    internal_tc = randint(min_tc, max_tc)
    print(internal_tc)

    for tc in range(internal_tc):
        print()

        temp = min(max(1, nomor_soal + tc), max_orang)
        if min_orang is not None:
            temp = min_orang
        banyak_orang = randint(temp, max_orang)
        

##        banyak_orang = randint(min(max(1, nomor_soal + tc), max_orang),
##                               min(2 + nomor_soal**2 + tc, max_orang))

        if not randomize_name:
            orang_orang = [kode_combinations[i] for i in range(banyak_orang)]
        else:
            orang_orang = sample(kode_combinations, k=banyak_orang)

        if forced_role is None:
            kode_orang = [randint(0, 1) for i in range(banyak_orang)]
        else:
            kode_orang = [forced_role for i in range(banyak_orang)]

        print(banyak_orang)
        print("\n".join(map( lambda x: f"{x[0]} {('B', 'S')[x[1]]}",  zip(orang_orang, kode_orang))))

        banyak_hari = randint(min_hari, max_hari)
        print(banyak_hari)

        for hari in range(banyak_hari):
            banyak_event = randint(min_event, max_event)
            print(banyak_event)

            for event in range(banyak_event):
                if move_the_top_only:
                    random_name = orang_orang[0]
                    random_event = 0
                else:
                    random_name = choice(orang_orang)
                    random_event = randint(0, 1)

                print(random_name, random_event)

        kode_evaluasi_akhir = 4        
        print(("PANUTAN", "KOMPETITIF", "EVALUASI", "DUO", "DEPLOY")[kode_evaluasi_akhir], end='')

        if kode_evaluasi_akhir == 0:
            temp = randint(1, banyak_orang)
            print(f" {temp}")
        elif kode_evaluasi_akhir == 4:
            temp = randint(1, (banyak_orang + 2 - 1) // 2)
            print(f" {temp}")
        else:
            print()




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

    input_ = io.StringIO()
    if not run_checkers:
        kunci_jawaban = io.StringIO()

    stdout = input_
    generate_soal(tc)
        
    stdin = sys.stdin
    stdout = sys.stdout    
    input_.seek(0)
    shutil.copyfileobj(input_, f, -1)
    
    
    f.close()



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
