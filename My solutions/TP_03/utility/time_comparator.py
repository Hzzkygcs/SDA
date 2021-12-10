import re

"""
format input

Hasil time limit attempt sebelumnya
<blank line>
Hasil time limit attempt setelahnya
<blank line>

Output:
Time limit setelahnya - time limit sebelumnya


contoh input:

1: AC (0.157)	51: TLE (3.128)
2: AC (0.191)	52: AC (2.553)
3: AC (0.181)	53: AC (2.972)
dst...
<blank line>
1: AC (0.185)	51: AC (2.796)
2: AC (0.170)	52: TLE (3.149)
3: AC (0.185)	53: TLE (3.260)
dst...
<blank line>
"""

print("input")
inp1 = []
while (temp := input()).strip() != "":
    inp1.append(temp)


inp2 = []
while (temp := input()).strip() != "":
    inp2.append(temp)

print()
print()


re_findall = re.compile("\\d+\\.\\d+")

for i in range(len(inp1)):
    inp1[i] = re_findall.findall(inp1[i])

    for j in range(len(inp1[i])):
        inp1[i][j] = float(inp1[i][j])



for i in range(len(inp2)):
    inp2[i] = re_findall.findall(inp2[i])

    for j in range(len(inp2[i])):
        inp2[i][j] = float(inp2[i][j])


min_inp_len = min(len(inp1), len(inp2))
for i in range(min_inp_len):
    min_inp_len_again = min(len(inp1[i]), len(inp2[i]))
    for j in range(min_inp_len_again):
        print(f"{i+1 + min_inp_len*j:02d}: ", end="")

        res = inp2[i][j] - inp1[i][j]
        print(f"{res: 03.03f}", end="     ")
    print()



