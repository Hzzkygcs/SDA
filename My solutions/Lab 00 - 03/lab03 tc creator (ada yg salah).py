from itertools import permutations
from random import randint, choice, choices, sample
import multiprocessing as mp
from glob import glob
import os, io, sys, shutil




path = "D:\\01 Kuliah\\01 Dokumen\\71 - Struktur Data Algoritma\\09 Lab\\tc"
run_main = False
run_multiprocessing = False
tc_count = 150


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False




memo = set()
siang = [0]
malam = [0]
bolos = [0]
bonus = [0]
zeros = [0] * 1000

max_keuntungan = 0  # included bonus
banyak_kerja_diperlukan = 0
sequence = []
track = []

"""


def rec(hari_ke_i, sesi):
    global banyak_kerja_diperlukan, max_keuntungan

    # return tuple: (maks_without_bonus, maks_include_bonus, banyak_kerja)
    # sesi == 0: siang, sesi == 1: malam, sesi == 2: bolos

    # assert len(siang) == len(malam) == len(bonus)
    sequence.append(sesi)
    if hari_ke_i >= len(siang):
        sequence.pop()
        return [0, 0]

    ret = [-1, -1]  # [ret_maks_without_bonus, ret_banyak_kerja]

    process_data( *rec(hari_ke_i + 1, 2), ret)

    if sesi == 2:
        temp = rec(hari_ke_i + 1, 0)
        temp[0] += siang[hari_ke_i]
        temp[1] += 1
        process_data(*temp, ret)

        temp = rec(hari_ke_i + 1, 1)
        temp[0] += malam[hari_ke_i]
        temp[1] += 1
        process_data(*temp, ret)
    else:
        zz = (siang, malam)
        temp = rec(hari_ke_i + 1, 1 - sesi)
        temp[0] += zz[sesi][hari_ke_i]
        temp[1] += 1
        process_data(*temp, ret)

    print()
    sequence.pop()
    return ret

            
def process_data(maks_without_bonus, _bnyk_kerja_iter, ret_arr):
    global banyak_kerja_diperlukan, max_keuntungan
    # ret_arr [ret_maks_without_bonus, ret_banyak_kerja]

    temp = maks_without_bonus + bonus[_bnyk_kerja_iter]
    print(maks_without_bonus, temp, _bnyk_kerja_iter)

    if ret_arr[0] < maks_without_bonus:
        ret_arr[0] = maks_without_bonus
        ret_arr[1] = _bnyk_kerja_iter

    if temp > max_keuntungan:
        print(f"{temp} <- {max_keuntungan} : {_bnyk_kerja_iter}")
        max_keuntungan = temp
        banyak_kerja_diperlukan = _bnyk_kerja_iter
    elif temp == max_keuntungan and banyak_kerja_diperlukan > _bnyk_kerja_iter:
        banyak_kerja_diperlukan = _bnyk_kerja_iter

"""


def rec(session=-1, depth=0, profit=0, work_total=0):
    global max_keuntungan, banyak_kerja_diperlukan

    # arg = (session, depth, profit, work_total)
    # if arg in memo:
    #     return
    # memo.add(arg)

    if session == -1:  # 0 -> siang, 1->malam, -1 -> bolos
        temp = (0, 1, -1)
    else:
        temp = (1 - session, -1)

    # -1 because we added 0 to the siang, malam, and bonus after we received the input
    if depth >= len(siang)-1:  # assert len(siang) == len(malam) == len(bonus)
        try:
            profit_with_bonus = profit + bonus[work_total]
        except:
            print(track)

        if max_keuntungan < profit_with_bonus:
            max_keuntungan = profit_with_bonus
            banyak_kerja_diperlukan = work_total
        elif max_keuntungan == profit_with_bonus:
            banyak_kerja_diperlukan = min(banyak_kerja_diperlukan, work_total)
        return


    for next_session in temp:
        track.append(next_session)

        rec(next_session, depth+1, profit + (siang, malam, zeros)[next_session][depth+1],
            work_total + (next_session != -1))

        track.pop()






def solve():  # use input() and print()
    global siang, malam, bonus, banyak_kerja_diperlukan, max_keuntungan, track
    n = int(input())

    memo.clear()
    siang = [0]
    malam = [0]
    bolos = [0] * n
    bonus = [0]
    max_keuntungan = 0
    banyak_kerja_diperlukan = 0

    siang.extend(map(int, input().split()))
    malam.extend(map(int, input().split()))
    bonus.extend(map(int, input().split()))
    siang.append(0)
    malam.append(0)
    bonus.append(0)

    rec()
    print(max_keuntungan, banyak_kerja_diperlukan)

# ez
N_UPLIMIT = 5
S_M_B_UPLIMIT = 10  # upper limit for siang, malam, and bonus


# medium
N_UPLIMIT = 12
S_M_B_UPLIMIT = 1000  # upper limit for siang, malam, and bonus


# medium+
N_UPLIMIT = 21
S_M_B_UPLIMIT = 1000000000  # upper limit for siang, malam, and bonus
"""
"""

def generate_soal(nomor_soal):  # use randint() (or other random func) and print()
    n = randint(1, N_UPLIMIT)
    print(n)

    if randint(1, 100) < 20:
        print(" ".join(['1'] * n))
        print(" ".join(['1'] * n))
        print(" ".join(str(randint(1, S_M_B_UPLIMIT))  for i in range(n)))
    else:
        print(" ".join(str(randint(1, S_M_B_UPLIMIT))  for i in range(n)))
        print(" ".join(str(randint(1, S_M_B_UPLIMIT))  for i in range(n)))
        print(" ".join(str(randint(1, S_M_B_UPLIMIT))  for i in range(n)))


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
    generate_soal(tc)

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
