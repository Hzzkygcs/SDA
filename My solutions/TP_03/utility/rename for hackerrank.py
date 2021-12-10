import os


directory = "tc/"

i = 0
in_file_name = directory + f"in_{i:02d}.txt"
out_file_name = directory + f"out_{i:02d}.txt"

while os.path.isfile(in_file_name):
    os.rename(in_file_name, directory + f"input{i:02d}.txt")
    os.rename(out_file_name, directory + f"output{i:02d}.txt")
    print(i)
    
    i += 1
    in_file_name = directory + f"in_{i:02d}.txt"
    out_file_name = directory + f"out_{i:02d}.txt"
