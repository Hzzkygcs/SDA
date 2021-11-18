from itertools import permutations
from random import randint, choice, choices, sample
import multiprocessing as mp
from glob import glob
import os, io, sys, shutil, string




path = r"D:\01 Kuliah\01 Dokumen\71 - Struktur Data Algoritma\08 TP\TP_02\tc"
run_main = True
run_multiprocessing = True
tc_count = 1


# while run_checkers and force_checkers is true, never regenerate testcase,
# never remove output file
run_checkers = False
force_checkers = False

pprint_is_dummy_print = False



query_all = ("UNIFIKASI", "PISAH", "GERAK", "TEBAS", "TELEPORTASI", "RISE", "QUAKE",
             "CRUMBLE", "STABILIZE", "SWEEPING")
queries = query_all
queries = ( "CRUMBLE", "STABILIZE", "GERAK", "TEBAS", "TELEPORTASI")


number_pulau_rand     = lambda: randint(1, 9)
random_dataran_rand   = lambda: randint(1, 50)
dataran_number_rand   = lambda: randint(1, 9)
query_number_rand     = lambda: randint(1, 9)
rise_quake_rand       = lambda: randint(1, 2)

solve = None

def solve(input_seluruhnya):  # use input() and print()
    global ppulau
    
    banyak_pulau = int(input())
    pulau_dct = ppulau = {}
    for i in range(banyak_pulau):
        inp = input().split()
        pulau = Pulau(inp[0])
        pulau_dct[inp[0]] = pulau
        
        kuil = Kuil(inp[0], map(int, inp[2:]))
        pulau.append(kuil)

    nama_pulau_raiden, letak_raiden = input().split()
    pulau_raiden = pulau_dct[nama_pulau_raiden]
    letak_raiden = int(letak_raiden) - 1
    del nama_pulau_raiden

    banyak_query = int(input())
    for q_num in range(banyak_query):
        query, *other = input().split()

        if query == "UNIFIKASI":
            tinggi_pijakan_raiden_sebelum = pulau_raiden.get(letak_raiden)
            
            p1, p2 = other
            p1, p2 = pulau_dct[p1], pulau_dct[p2]

            if p2 == pulau_raiden:
                pulau_raiden = p1
                letak_raiden += p1.len_dataran()
            
            p1.extend(p2)
            pulau_dct.pop(p2.n)
            p2.clear()
            
            print(p1.len_dataran())
            try:
                tinggi_pijakan_raiden_sesudah = pulau_raiden.get(letak_raiden)
            except StopIteration:
                breakpoint()
            assert tinggi_pijakan_raiden_sebelum == tinggi_pijakan_raiden_sesudah
            
        elif query == "PISAH":
            tinggi_pijakan_raiden_sebelum = pulau_raiden.get(letak_raiden)
            
            pulau = None
            nama_kuil = other[0]
            for ii in pulau_dct.values():
                if ii.has_kuil(nama_kuil):
                    pulau = ii
                    break
            assert pulau is not None

            it = iter(enumerate(pulau))
            p_kiri = Pulau(pulau.n)
            p_kanan = Pulau(nama_kuil)
            for i, kuil in it:
                if kuil.n == nama_kuil:
                    break
                p_kiri.append(kuil)
            
            p_kanan.append(kuil)
            for i, kuil in it:
                p_kanan.append(kuil)
            pulau.clear()

            if pulau == pulau_raiden:
                if letak_raiden < p_kiri.len_dataran():  # raiden ada di pulau kiri
                    pulau_raiden = p_kiri
                else:
                    letak_raiden -= p_kiri.len_dataran()
                    pulau_raiden = p_kanan

            pulau_dct[p_kiri.n] = p_kiri
            pulau_dct[p_kanan.n] = p_kanan
            print(p_kiri.len_dataran(), p_kanan.len_dataran())
            
            tinggi_pijakan_raiden_sesudah = pulau_raiden.get(letak_raiden)
            assert tinggi_pijakan_raiden_sebelum == tinggi_pijakan_raiden_sesudah
            
        elif query == "GERAK":
            arah, banyak_langkah = other
            banyak_langkah = int(banyak_langkah)
            
            if arah == "KIRI":
                letak_raiden = max(0, letak_raiden - banyak_langkah)
            else:
                letak_raiden = min(pulau_raiden.len_dataran()-1, letak_raiden + banyak_langkah)

            print(pulau_raiden.get(letak_raiden))
            
        elif query == "TEBAS":
            arah, banyak_langkah = other
            banyak_langkah = int(banyak_langkah)

            ketinggian_saat_ini = pulau_raiden.get(letak_raiden)
            posisi_sblmny = letak_raiden
            steps = (range(letak_raiden-1, -1, -1),
                     range(letak_raiden+1, pulau_raiden.len_dataran(), 1))[arah == "KANAN"]
            list_ = pulau_raiden.dataran()
            for i in steps:
                if list_[i] == ketinggian_saat_ini:
                    banyak_langkah -= 1
                    letak_raiden = i
                    if banyak_langkah == 0:
                        break

            if posisi_sblmny == letak_raiden:
                print(0)
            else:
                if arah == "KIRI":
                    print(list_[letak_raiden+1])
                else:
                    print(list_[letak_raiden-1])
                    
        elif query == "TELEPORTASI":
            nama_kuil = other[0]
            for pulau in pulau_dct.values():
                if pulau.has_kuil(nama_kuil):
                    pulau_raiden = pulau
                    break
            else:
                pprint("TELEPORTASI", nama_kuil)
                raise Exception()
            acc = 0
            for kuil in pulau_raiden:
                if nama_kuil == kuil.n:
                    break
                acc += len(kuil)
            else:
                pprint("TELEPORTASI", other)
                raise Exception()
            letak_raiden = acc
            print(pulau_raiden.get(letak_raiden))
        
        elif query == "RISE":
            nama_pulau, h, x = other
            h, x = map(int, (h,x))
            pulau = pulau_dct[nama_pulau]

            affected = 0
            for kuil in pulau:
                for i in range(len(kuil)):
                    if kuil[i] > h:
                        kuil[i] += x
                        affected += 1
            print(affected)
        
        elif query == "QUAKE":
            nama_pulau, h, x = other
            h, x = map(int, (h,x))
            pulau = pulau_dct[nama_pulau]

            affected = 0
            for kuil in pulau:
                for i in range(len(kuil)):
                    if kuil[i] < h:
                        kuil[i] -= x
                        affected += 1
            print(affected)
            
        elif query == "CRUMBLE":
            if letak_raiden in pulau_raiden.kuil_positions():
                print(0)
                continue
            kuil, offset = pulau_raiden.get_kuil_which_has_dataran_index(letak_raiden)
            letak_raiden -= 1
            print(kuil.pop(offset))
            
        elif query == "STABILIZE":
            if letak_raiden in pulau_raiden.kuil_positions():
                print(0)
                continue
            kuil, offset = pulau_raiden.get_kuil_which_has_dataran_index(letak_raiden)
            tinggi_terendah = min(kuil[offset-1], kuil[offset])
            kuil.insert(offset+1, tinggi_terendah)
            print(tinggi_terendah)

        elif query == "SWEEPING":
            nama_pulau, h = other
            pulau = pulau_dct[nama_pulau]
            h = int(h)

            dataran = pulau.dataran()
            count = 0
            for i in dataran:
                if i < h:
                    count += 1
            print(count)
            
        else:
            raise Exception()


def generate_soal():  # use randint() (or other random func) and print()
    global ppulau
    
    taken_name = set()
    number_pulau = number_pulau_rand()
    pulau = ppulau = {}
    print(number_pulau)
    
    for i in range(number_pulau):
        random_dataran = [random_dataran_rand()
                          for i in range(dataran_number_rand())]
        rnd_nm = random_str(taken_name)
        
        pulau[rnd_nm] = temp = Pulau(rnd_nm)
        temp.append(Kuil(rnd_nm, random_dataran))
        print(rnd_nm, len(random_dataran), " ".join(map(str, random_dataran)))
        
    pulau_raiden = sample(pulau.keys(), 1)[0]
    dataran_raiden = randint(0, pulau[pulau_raiden].len_dataran()-1)
    print(pulau_raiden, dataran_raiden+1)

    query_number = query_number_rand()
    print(query_number)
    
    while query_number > 0:
        query = choice(queries)

        if query == "UNIFIKASI":
            if len(pulau) <= 1:
                continue
            p1, p2 = sample(list(pulau.values()), 2)
            if len(p1) + len(p2) > 20:
                continue
            print("UNIFIKASI", p1.n, p2.n)
            p1.extend(p2)
            pulau.pop(p2.n)
            
        elif query == "PISAH":
            p = sample(set(pulau.values()), 1)[0]
            if len(p) <= 1:
                continue
            pisah = randint(1, len(p)-1)
            kuil_kanan = p[pisah]
            nama_pulau_kanan = kuil_kanan.n
            print("PISAH", kuil_kanan.n)

            p_kiri = Pulau(p.n)
            p_kanan = Pulau(nama_pulau_kanan)
            for i in range(pisah):
                p_kiri.append(p[i])
            for i in range(pisah, len(p)):
                p_kanan.append(p[i])
            pulau[p_kiri.n] = p_kiri
            pulau[p_kanan.n] = p_kanan
            
        elif query == "GERAK":
            s = randint(1, 10)
            arah = choice(("KIRI", "KANAN"))
            print("GERAK", arah, s)
            
        elif query == "TEBAS":
            s = randint(1, 10)
            arah = choice(("KIRI", "KANAN"))
            print("TEBAS", arah, s)
            
        elif query == "TELEPORTASI":
            p = sample(set(pulau.values()), 1)[0]
            print("TELEPORTASI", choice(p).n)
        
        elif query == "RISE":
            p = sample(set(pulau.values()), 1)[0]
            h = randint(1, p.max() + 10)
            x = rise_quake_rand()
            print("RISE", p.n, h, x)
        
        elif query == "QUAKE":
            p = sample(set(pulau.values()), 1)[0]
            x = rise_quake_rand()
            h = randint(1, p.max() + 10)
            temp = p.min()
            if temp < h and temp <= x:
                continue
            print("QUAKE", p.n, h, x)
            
        elif query == "CRUMBLE":
            print("CRUMBLE")
        elif query == "STABILIZE":
            print("STABILIZE")
        elif query == "SWEEPING":
            p = sample(set(pulau.values()), 1)[0]
            h = randint(1, p.max() + 10)
            print("SWEEPING", p.n, h)
            
        query_number -= 1
        
dictio = {
        "UNIFIKASI": "U",
        "PISAH": "P",
        "GERAK": "G",
        "TEBAS": "Teb",
        "TELEPORTASI": "Tel",
        "RISE": "R",
        "QUAKE": "Q",
        "CRUMBLE": "C",
        "STABILIZE": "St",
        "SWEEPING": "Sw"
    }
queries = sorted(queries, key=lambda x: query_all.index(x))
kode_tc = "".join(map(lambda x: dictio[x.upper()], queries))


class Pulau(list):
    def __init__(self, name):
        self.n = name
        self.s = set()
        
    def append(self, value):
        self.s.add(value)
        super().append(value)
        
    def remove(self, value):
        return NotImplemented
    
    def extend(self, value):
        self.s |= value.s
        super().extend(value)
        
    def len_dataran(self):
        return sum((len(i) for i in super().__iter__()))
    
    def get(self, index):
        curr, index = self.get_kuil_which_has_dataran_index(index)
        return curr[index]
    
    def get_kuil_which_has_dataran_index(self, index):
        itr = super().__iter__()
        curr = next(itr)
        while index >= len(curr):
            index -= len(curr)
            curr = next(itr)
        return curr, index

    def dataran(self):
        ret = []
        for i in super().__iter__():
            ret.extend(i)
        return ret

    def kuil_positions(self):
        ret = []
        acc = 0
        for kuil in super().__iter__():
            ret.append(acc)
            acc += len(kuil)
        return ret

    def has_kuil(self, kuil_name:str):
        dummy = Kuil(kuil_name, [])
        return dummy in self.s
        # return kuil_name in (i.n for i in super().__iter__())
    
    def __contains__(self, value):
        if isinstance(value, int):
            return any(((value in x) for x in super().__iter__()))
        return value in self.s
        
    def __repr__(self):
        temp = list.__repr__(self)
        return f"{self.n}: {temp}"
    def min(self):
        return min((min(i) for i in super().__iter__()))
    def max(self):
        return max((max(i) for i in super().__iter__()))
    def __eq__(self, other):
        return type(self) == type(other) and self.n == other.n
    def __hash__(self):
        return hash(self.n)


class Kuil(list):
    def __init__(self, name, base_list=[]):
        self.n = name
        super().extend(base_list)
    def __eq__(self, other):
        return type(self) == type(other) and self.n == other.n
    def __hash__(self):
        return hash(self.n)


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
    if not run_checkers:
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
    print(kode_tc)


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
