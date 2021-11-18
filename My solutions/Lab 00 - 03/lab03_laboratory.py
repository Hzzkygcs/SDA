
INF = 9999999999999999999999

# session
SIANG = 0
MALAM = 1
BOLOS = 2


siang = [0, 1, 5, 3, 4, 5, 0]  # always append 0 in the begining and the end
malam = [0, 1, 5, 6, 2, 1, 0]
bolos = [0, 0, 0, 0, 0, 0, 0]
bonus = [0, 1, 1, 1, 1, 1, 0]

get_session = (siang, malam, bolos)


def f(hari_i, target_work, session):
    
    if target_work == 0:
##        print(f"f({hari_i}, {target_work}, {session}) -> zero basecase")
        return 0
    
    if hari_i == 0:
        if target_work != 0:
            assert (target_work > 0)
##            print(f"f({hari_i}, {target_work}, {session}) -> invalid")
            return -INF  # invalid

##        print(f"f({hari_i}, {target_work}, {session}) -> {get_session[session][hari_i]}")
        return get_session[session][hari_i]

    tidak_masuk = f(hari_i - 1, target_work - 0, BOLOS)
    masuk_siang = masuk_malam = -INF
    
    if session in (SIANG, BOLOS):
        # kalau sekarang siang, kemarinnya hanya boleh malam atau bolos
        masuk_malam = f(hari_i - 1, target_work - 1, MALAM)
        masuk_malam += get_session[MALAM][hari_i-1]
    
    if session in (MALAM, BOLOS):
        # kalau sekarang malam, kemarinnya hanya boleh siang atau bolos
        masuk_siang = f(hari_i - 1, target_work - 1, SIANG)
        masuk_siang += get_session[SIANG][hari_i-1]

    ret = max(masuk_siang, masuk_malam, tidak_masuk)
    debug_ = pembantu(masuk_siang, masuk_malam, tidak_masuk)
##    print(f"f({hari_i}, {target_work}, {session}) -> {ret} : {debug_}")
    return ret


def pembantu(masuk_siang, masuk_malam, tidak_masuk):
    max_ = max(masuk_siang, masuk_malam, tidak_masuk)
    a = int(max_ == masuk_siang)
    b = int(max_ == masuk_malam)
    c = int(max_ == tidak_masuk)
    return f"{a}{b}{c}"


def max_total_profit_under_work_constraint(batas_akhir_hari, target_work_yang_diinginkan):
    # mencari jumlah keuntungan jika kita harus bekerja sesuai target work yang diinginkan
    # dan kita hanya bisa bekerja pada hari < batas_akhir_hari (EXCLUSIVE)

    # we can assume this day (at day batas_akhir_hari) is BOLOS because we have
    # added a zero column at the end of siang, malam, bolos, and bonus. And also
    # we can (and must) assume this day is BOLOS because batas_akhir_hari is exclusive
    max_non_bonus_profit = f(batas_akhir_hari, target_work_yang_diinginkan, BOLOS)
    
    bonus_ = bonus[target_work_yang_diinginkan]
    return max_non_bonus_profit + bonus_




def main():
    global siang, malam, bolos, bonus, get_session

    n = int(input())
    siang = [0]
    malam = [0]
    bolos = [0] * (n+2)  # +2 for extra item in the beginning and the end
    bonus = [0]
    get_session = (siang, malam, bolos)

    siang.extend(map(int, input().split()));  siang.append(0)
    malam.extend(map(int, input().split()));  malam.append(0)
    bonus.extend(map(int, input().split()));  bonus.append(0)
    # no need to call it for bonus, because we have give +2 when we multiplied it.
    

    assert n+1 < len(siang) == len(malam) == len(bonus)
    
    max_profit = -INF
    banyak_kerja = -1
    for target_work in range(0, n):
        # we want to find the max profit from 0 to n-th day INCLUSIVE,
        # but batas_akhir_hari is exclusive. Therefore we must set the
        # right-most boundary as n+1 so that n will also included.
        max_profit_local = max_total_profit_under_work_constraint(n+1, target_work)

        if max_profit_local > max_profit:
            max_profit = max_profit_local
            banyak_kerja = target_work
    print(max_profit, banyak_kerja)


main()

