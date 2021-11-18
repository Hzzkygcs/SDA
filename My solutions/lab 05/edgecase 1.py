from itertools import permutations
from random import randint, choice, choices, sample
import multiprocessing as mp
from glob import glob
import os, io, sys, shutil, string
import itertools as it



path = r"D:\01 Kuliah\01 Dokumen\71 - Struktur Data Algoritma\09 Lab\lab 05\tc"
run_main = True
run_multiprocessing = True
tc_count = 2000


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False






def solve(input_seluruhnya):  # use input() and print()
    global kkotak
    kotak = kkotak = {}
    
    
    banyak_awal = int(input())
    for i in range(banyak_awal):
        inp = input().split()
        kotak[inp[0]] = Kotak(inp[0], int(inp[1]), int(inp[2]))

    banyak_query = int(input())
    for q in range(banyak_query):
        query, *other = input().split()

        if query == "STOCK":
            kotak[other[0]] = Kotak(other[0], int(other[1]), int(other[2]))
        elif query == "SOLD_OUT":
            kkotak.pop(other[0])
        elif query == "BELI":
            harga_kiri, harga_kanan = map(int, other)
            daftar_harga = sorted(kotak.values(),
                                  key=lambda x: x.harga)
            
            daftar_harga = [
                    i for i in daftar_harga
                    if harga_kiri <= i.harga <= harga_kanan
                ]

            selisih = -1
            jumlah = -1
            pasangan = None
            i = 0
            for comb1, comb2 in it.combinations(daftar_harga, 2):
                if comb1.is_compatible(comb2):
                    if comb1.compare_to(comb2) > 0:
                        comb1, comb2 = comb2, comb1

                    this_selisih = comb2.harga - comb1.harga
                    this_jumlah = comb1.harga + comb2.harga
                    
                    if this_selisih > selisih:
                        selisih = this_selisih
                        jumlah = this_jumlah
                        pasangan = (comb1, comb2)
                        
                    elif this_selisih == selisih:
                        if this_jumlah > jumlah:
                            jumlah = this_jumlah
                            pasangan = (comb1, comb2)
            if pasangan is None:
                print("-1 -1")
            else:
                print(pasangan[0].harga, pasangan[1].harga)
            



def random_harga_tipe():
    return (sample(pilihan_harga, 1)[0],
            randint(1, 300))


def random_query():
    temp = randint(1, 100)
    if temp <= 40:
        return "BELI"
    elif temp <= 74:
        return "STOCK"
    else:
        return "SOLD_OUT"
    

def generate_soal():
    global pilihan_harga
    pilihan_harga = {randint(1, 100000) for i in range(1, 7)}
    temp = randint(0, 40)
    if temp <= 100:
        generate_soal_tipe_pasti_beda()
    else:
        generate_soal_tipe_maks_8()


def generate_soal_tipe_pasti_beda():  # tipe pasti dijamin berbeda
    tipe_counter = 1
    taken_name = set()
    banyak_kotak_awal = randint(1, 12)
    print(banyak_kotak_awal)

    for i in range(banyak_kotak_awal):
        nama_kotak = random_str(taken_name)
        harga, tipe = random_harga_tipe()
        tipe = tipe_counter
        tipe_counter += 1
        
        print(nama_kotak, harga, tipe)

    banyak_query = randint(1, 20)
    print(banyak_query)
    while banyak_query > 0:
        tipe_query = random_query()
        
        if tipe_query == "STOCK":
            nama_kotak = random_str(taken_name)
            harga, tipe = random_harga_tipe()
            tipe = tipe_counter
            tipe_counter += 1
            print("STOCK", nama_kotak, harga, tipe)
            
        elif tipe_query == "SOLD_OUT":
            if len(taken_name) == 0:
                continue
            nama_kotak = sample(taken_name, 1)[0]
            taken_name.discard(nama_kotak)
            print("SOLD_OUT", nama_kotak)
            
        elif tipe_query == "BELI":
            harga_1 = random_harga_tipe()[0] + randint(-100, 100)
            harga_2 = random_harga_tipe()[0] + randint(-100, 100)

            harga_1 = harga_1 if harga_1 > 0 else 1
            harga_2 = harga_2 if harga_2 > 0 else 1

            if (harga_1 > harga_2):
                harga_1, harga_2 = harga_2, harga_1
            assert harga_1 <= harga_2

            print("BELI", harga_1, harga_2)
            
        banyak_query -= 1




def generate_soal_tipe_maks_8():  # tipe pasti dijamin berbeda

    daftar_tipe = {}
    taken_name = set()
    name_and_type = {}
    
    banyak_kotak_awal = randint(1, 8)
    print(banyak_kotak_awal)

    for i in range(banyak_kotak_awal):
        nama_kotak = random_str(taken_name)
        harga, tipe = random_harga_tipe()
        if len(daftar_tipe) >= 8:
            tipe = sample(daftar_tipe.keys(), 1)[0]
        daftar_tipe[tipe] = daftar_tipe.get(tipe, 0) + 1
        name_and_type[nama_kotak] = tipe
        
        print(nama_kotak, harga, tipe)

    banyak_query = randint(1, 8)
    print(banyak_query)
    while banyak_query > 0:
        tipe_query = random_query()
        
        if tipe_query == "STOCK":
            nama_kotak = random_str(taken_name)
            harga, tipe = random_harga_tipe()
            if len(daftar_tipe) >= 8:
                tipe = sample(daftar_tipe.keys(), 1)[0]
            daftar_tipe[tipe] = daftar_tipe.get(tipe, 0) + 1
            name_and_type[nama_kotak] = tipe
            print("STOCK", nama_kotak, harga, tipe)
            
        elif tipe_query == "SOLD_OUT":
            if len(taken_name) == 0:
                continue
            nama_kotak = sample(taken_name, 1)[0]
            taken_name.discard(nama_kotak)
            tipe = name_and_type.pop(nama_kotak)
            daftar_tipe[tipe] = daftar_tipe[tipe] - 1
            if daftar_tipe[tipe] == 0:
                daftar_tipe.pop(tipe)
            print("SOLD_OUT", nama_kotak)
            
        elif tipe_query == "BELI":
            harga_1 = random_harga_tipe()[1]
            harga_2 = random_harga_tipe()[1]

            if (harga_1 > harga_2):
                harga_1, harga_2 = harga_2, harga_1
            assert harga_1 <= harga_2

            print("BELI", harga_1, harga_2)
            
        banyak_query -= 1


class Kotak():
    def __init__(self, nama, harga, tipe):
        self.nama = nama
        self.harga = harga
        self.tipe = tipe

    def __repr__(self):
        return f"{self.harga}:{self.tipe}-{self.nama}"

    def is_compatible(self, other):
        return self.tipe != other.tipe
    
    def compare_to(self, other):
        return self.harga - other.harga
    


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
    solve("")
