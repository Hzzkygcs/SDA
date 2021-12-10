dct = {}
res = []

while (inp:=input().split()):
    inp = list(map(int, inp))

    if inp[0] > inp[1]:
        inp[0], inp[1] = inp[1], inp[0]
    res.append(tuple(inp))

    dct[inp[0]] = dct.get(inp[0], [])
    dct[inp[1]] = dct.get(inp[1], [])

    dct[inp[0]].append(inp[1])
    dct[inp[1]].append(inp[0])
    
print()
print()

dct = sorted(dct.items())

for key, value in dct:
    value.sort()
    print(f"{key} -> {' '.join(map(str, value))}")

print()
print()

res.sort()
for i in res:
    print(" ".join(map(str, i)))
    
print()
print()
