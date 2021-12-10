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

reserve_affinity = 0b_1111_1111_1111




print('.')
catched_pids = set()

for proc in psutil.process_iter():
    if not psutil.pid_exists(proc.pid):
        continue
    try:
        set_affinity(proc.pid, 0b_1111_1111_1111)
    except pywintypes.error:
        pass
        

print("reset")
        
