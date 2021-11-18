from itertools import permutations
from random import randint, choice, choices, sample, shuffle
import multiprocessing as mp
from glob import glob
from randomdict import RandomDict

import os, io, sys, shutil




path = "D:\\01 Kuliah\\01 Dokumen\\71 - Struktur Data Algoritma\\09 Lab\\lab 04\\tc"
run_main = True
run_multiprocessing = True
tc_count = 5000


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False






jatah_sketsa = 2000_000

def cari_yang_bisa_sketsa(agents):
    global jatah_sketsa

    if (jatah_sketsa < 10):
        return None
    
    
    for i in range(100):
        agent = agents.random_value()
        
        if (agent.jumlah_lantai <= jatah_sketsa):
            jatah_sketsa -= agent.jumlah_lantai
            return agent
    return None


class Agent():
    def __init__(self, kode_unik):
        self.jumlah_lantai = 0
        self.kode_unik = kode_unik

    def punya_lantai(self):
        return self.jumlah_lantai > 0

    def hancurkan(self):
        self.jumlah_lantai -= 1

    def bangun(self):
        self.jumlah_lantai += 1

    def timpa(self, other):
        self.jumlah_lantai += other.jumlah_lantai
        other.jumlah_lantai = 0

    def __repr__(self):
        return self.kode_unik





banyak_query_max = 35
banyak_query_max = 1000_000
banyak_query_max = 14


def generate_soal(tc):  # use randint() (or other random func) and print()
    global jatah_sketsa
    
    banyak_query = randint(max(6, banyak_query_max // 5), banyak_query_max)
    print(banyak_query)
    jatah_sketsa = 2_000_000
    kode_unik = set()
    
    hashmap = RandomDict()

    q = 0

    init_jumlah = randint(1, min(banyak_query//2+1, 20))
    for i in range(init_jumlah):
        new_kode = random_name(kode_unik)
        hashmap[new_kode] = Agent(new_kode)
        print(f"FONDASI {new_kode}")
    q += init_jumlah
    
    while q < banyak_query:
        def func():
            xx = randint(1, 100)

            if (xx <= 10 and hashmap):
                yang_akan_di_sketsa = cari_yang_bisa_sketsa(hashmap)
                if (yang_akan_di_sketsa is not None):
                    print(f"SKETSA {yang_akan_di_sketsa.kode_unik}")
                    return

            if (xx <= 24 and len(hashmap) >= 2):
                while True:
                    a, b = hashmap.random_value(), hashmap.random_value()
                    if (a != b):
                        break
            
                if (a.jumlah_lantai <= 0):
                    a, b = b, a
                if b.jumlah_lantai >= 0 and a.jumlah_lantai > 0:
                    print(f"TIMPA {a} {b}")
                    a.timpa(b)
                    hashmap.pop(b.kode_unik)
                    kode_unik.remove(b.kode_unik)
                    return
                
            if (xx <= 42 and randint(1, 100) <= 30):
                new_kode = random_name(kode_unik)
                hashmap[new_kode] = Agent(new_kode)
                print(f"FONDASI {new_kode}")
                return 

            if (not hashmap):
                return func()
            
            param1 = hashmap.random_value()

            if (xx <= 70 and param1.punya_lantai()):
                temp = ("ATAS", "BAWAH")[randint(0, 1)]
                print(f"LIFT {param1} {temp}")
                return

            if (xx <= 90 and param1.punya_lantai()):
                print(f"HANCURKAN {param1}")
                param1.hancurkan()
                return

            temp = choice("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
            param1.bangun()
            print(f"BANGUN {param1} {temp}")
            return
                
            
        func()
        q += 1

            

def random_name(daftar_kode_terpakai):
    while True:
        hasil = [chr(65 + randint(0, 25)) for i in range(randint(1, 3))]
        hasil = "".join(map(str, hasil))
        if hasil not in daftar_kode_terpakai:
            daftar_kode_terpakai.add(hasil)
            return hasil
        

    




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
