  

def rrec(sstr, kelompok):
    depth = -1

    printed = []
    
    current = []
    printed2 = []
    prev_start = 0
    
    def rec(kelompok, start, target):
        nonlocal depth, printed, prev_start
        
        if start == len(sstr) - 1:  # kalo sekarang udah di akhir

            # kelompok-1 == 0 -> kelompok == 1
            # karena: ibaratnya 0010|1001|0110
            # karena kali ada 2 tangkai, membentuk 3 kelompok
            #
            # sstr[start] == target -> kalo kode awal pada grup skrg ini sama dengan
            # kode akhir grup skrg ini
            temp = sstr[start] == target and kelompok-1 == 0

            if (temp):
                current.append(sstr[prev_start : start+1])  # debug
                printed2.append(" ".join(current))  # debug
                current.pop()  # debug
            
            return temp  # return 1 kalo memenuhi syarat. 0 kalo tidak memenuhi syarat
        elif start >= len(sstr) or kelompok < 0:
            return 0

        depth += 1  # debug
        ret = 0

        if sstr[start] == target:
            current.append(sstr[prev_start : start+1])
            temp_ = prev_start
            prev_start = start + 1
            ret += rec(kelompok - 1, start + 2, sstr[start+1])
            prev_start = temp_
            current.pop()

        ret += rec(kelompok, start + 1, target)
        
        printed.append("    " * depth + f"{kelompok} {start} {target}  -->  {ret}")
        
        depth -= 1  # debug
        return ret
    
    ret = rec(kelompok, 1, sstr[0])

    for i in range(len(printed)):
        print(printed.pop())
    
    for i in range(len(printed2)):
        print(printed2.pop())
    
    return ret



sstr = "11001100"
#       01234567
print(rrec("1111000011110000", 3))  # 10



if True:
    assert rrec("11010011", 1) == 1  # 1
    assert rrec("11010011", 2) == 0  # 0
    assert rrec("11010011", 3) == 2  # 2
    assert rrec("11010011", 4) == 0  # 0
    assert rrec("11111", 1) == 1  # 1
    assert rrec("11111", 2) == 2  # 2
    assert rrec("11111", 3) == 0  # 0
    assert rrec("11111111", 2) == 5  # 5
    assert rrec("11111111", 3) == 6  # 6
    assert rrec("00000000", 3) == 6  # 6
    assert rrec("11110000", 1) == 0  # 0
    assert rrec("11100001", 1) == 1  # 1
    assert rrec("11100001", 2) == 1  # 1
    assert rrec("111000011", 2) == 1  # 1
    assert rrec("1111000011110000", 2) == 2  # 2
    print("DONE")


