import win32api, win32con, win32process, pywintypes, psutil, time

"""
Isolate a core only for java.exe by setting its affinity to a core (let say A), and then
set all other processes including this python script to all other core except A
"""

def set_affinity(pid, mask):
    if not psutil.pid_exists(pid):
        return False
    handle = win32api.OpenProcess(win32con.PROCESS_ALL_ACCESS, True, pid)
    win32process.SetProcessAffinityMask(handle, mask)

def set_priority(pid, priority=win32process.HIGH_PRIORITY_CLASS):
    if not psutil.pid_exists(pid):
        return False
    handle = win32api.OpenProcess(win32con.PROCESS_ALL_ACCESS, True, pid)
    return win32process.SetPriorityClass(handle, priority)

def get_affinity(pid):
    if not psutil.pid_exists(pid):
        return False
    handle = win32api.OpenProcess(win32con.PROCESS_ALL_ACCESS, True, pid)
    return win32process.GetProcessAffinityMask(handle)


flag = 9

reserve_affinity = 0b_0000_1110_0000


self_pid = win32api.GetCurrentProcessId()
aff = get_affinity(self_pid)[0]
aff &= ~reserve_affinity
set_affinity(self_pid, aff)

allowed = {"java.exe", "hzzgrader.exe"}
# allowed = {"java.exe"}

while True:
    print('.')
    catched_pids = set()
    
    for proc in psutil.process_iter():
        if not psutil.pid_exists(proc.pid):
            continue
        if proc.name().lower() in allowed:
            print("setting ", proc.pid)
            temp_flag = True
            set_affinity(proc.pid, reserve_affinity)
            set_priority(proc.pid)
            catched_pids.add(proc.pid)
        else:
            try:
                aff = get_affinity(proc.pid)
                if isinstance(aff, bool):
                    continue
                aff = aff[0]
                aff &= ~reserve_affinity
                set_affinity(proc.pid, aff)
            except pywintypes.error:
                pass

    prev = catched_pids
    while prev == catched_pids:
        catched_pids = set()
        for proc in psutil.process_iter():
            if not psutil.pid_exists(proc.pid):
                continue
            if proc.name().lower() in allowed:
                catched_pids.add(proc.pid)
        _ = hash(tuple(catched_pids)) % 7000
        print(f".  {_}")
        time.sleep(0.11)
        
