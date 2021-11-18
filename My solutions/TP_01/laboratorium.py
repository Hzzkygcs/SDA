
#                 #  1100 11  0  1 00  1
# sstr = [2, 2, 2, 1, 1, 2, 1]


# sstr = sstr = "1101101001"


# tipe hanya bernilai '0' atau '1'
# jumlah grup berupa integer, banyaknya grup yang mau dibuat
# posisi_akhir menyatakan posisi indeks terakhir dari grup saat ini
# Mengembalikan nilai


DEBUG = True



class Debugger():
    def __init__(self, debug_mode=True):
        self.depth = 0
        self.debug_mode = debug_mode
        self.connect_ = None
        self.stack = []
        

    def __call__(self, *args):
        if (self.debug_mode):
            print("   |" * self.depth, end="")
            print(*args)
    
    def in_(self, *args):
        self.stack.append(args)
        
        self.depth += 1
        if (self.connect_ is not None):
            self.connect_.in_(*args)
        
    def out(self, ret_val=None):
        self.depth -= 1
        self.stack.pop()
        
        if (self.connect_ is not None):
            self.connect_.out()
        return ret_val

    def connect(self, other_debugger):
        self.connect_ = other_debugger

    def is_root(self):
        return self.depth <= 1

    def check_parent(self, up_level=1):
        return len(self.stack) >= up_level + 1

    def parent_arg(self, up_level=1):
        return self.stack[-1 - up_level]


def rrec(jumlah_grup, sstr):
    debug2nd = Debugger(False)
    
    debugger = Debugger(False)
    debugger.connect(debug2nd)
    
    out = debugger.out

    def rec(jumlah_grup, posisi_awal=0, posisi_akhir=1):
        debugger.in_(jumlah_grup, posisi_awal, posisi_akhir)
        
        # debugger(jumlah_grup, posisi_awal, posisi_akhir)
        
        if (posisi_awal >= posisi_akhir):
            debugger(f"(A) rec({jumlah_grup}, {posisi_awal}, {posisi_akhir})  ->  {0}")
            return out(0)

        temp_zz = int(
                       jumlah_grup == 0
                       and  posisi_akhir == len(sstr) - 1
                       and  sstr[posisi_awal] == sstr[posisi_akhir]
                     )

        if (posisi_akhir >= len(sstr) - 1):
            if temp_zz > 0:
                debug2nd(posisi_awal, posisi_akhir)

            if debug2nd.check_parent(2):
                print(debug2nd.parent_arg(2),  debug2nd.parent_arg(1))
                if debug2nd.parent_arg(1)[1] == 4:
                    
                    if (posisi_awal == 12):
                        breakpoint()
            
            debugger(f"(B) rec({jumlah_grup}, {posisi_awal}, {posisi_akhir})  ->  {temp_zz}")
            return out(temp_zz)

        if jumlah_grup == 0 or sstr[posisi_awal] != sstr[posisi_akhir]:
            temp = rec(jumlah_grup, posisi_awal, posisi_akhir + 1)
            
            debugger(f"(C) rec({jumlah_grup}, {posisi_awal}, {posisi_akhir})  ->  {temp}")
            
            return out(temp)

        if debug2nd.is_root() or debug2nd.parent_arg()[1] != posisi_awal:
            debug2nd(posisi_awal, posisi_akhir)
        
        temp1 = rec(jumlah_grup, posisi_awal, posisi_akhir + 1)
        temp2 = rec(jumlah_grup - 1, posisi_akhir + 1, posisi_akhir + 2)
        temp3 = temp_zz
        
        ret = temp1 + temp2 + temp3
        debugger(f"(D) rec({jumlah_grup}, {posisi_awal}, {posisi_akhir})  ->  {ret}")
        return out(ret)

    return rec(jumlah_grup - 1)

TEST = False

if TEST:
    assert rrec(1, "11010011") == 1  # 1
    assert rrec(2, "11010011") == 0  # 0
    assert rrec(3, "11010011") == 2  # 2
    assert rrec(4, "11010011") == 0  # 0
    assert rrec(1, "11111") == 1  # 1
    assert rrec(2, "11111") == 2  # 2
    assert rrec(3, "11111") == 0  # 0
    assert rrec(2, "11111111") == 5  # 5
    assert rrec(3, "11111111") == 6  # 6
    assert rrec(3, "00000000") == 6  # 6
    assert rrec(1, "11110000") == 0  # 0
    assert rrec(1, "11100001") == 1  # 1
    assert rrec(2, "11100001") == 1  # 1
    assert rrec(2, "111000011") == 1  # 1
    assert rrec(2, "1111000011110000") == 2  # 2

# Harusnya 7
# UPDATE: UDAH BENER 10.
print(rrec(3, "1111000011110000"))  # 7
print("1111000011110000")
print("0123456789012345")

"""
11 11 000011110000
111 100001111 0000
1111 00 0011110000  
1111 000 011110000
111100001 111 0000
1111000011 11 0000
111100001111 00 00


UPDATE:
111100001111 00 00 (ada)
1111000011 11 0000 (ada)
111100001 111 0000 (ada)
1111 0000111100 00 (tidak ada waktu nguli manual)
1111 000011110 000 (tidak ada waktu nguli manual)
1111 000 011110000 (ada)
1111 00 0011110000 (ada)
111 100001111 0000 (ada)
11 1100001111 0000 (tidak ada waktu nguli manual)
11 11 000011110000 (ada)
"""
