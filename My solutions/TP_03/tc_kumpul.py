import os


src_directory = "tc/"
dst_directory = "tc_kumpul/"



number_of_dst_file = 0
while os.path.isfile(dst_directory + f"in_{number_of_dst_file:02d}.txt"):    
    number_of_dst_file += 1




i = 0
in_file_name = src_directory + f"in_{i:02d}.txt"
out_file_name = src_directory + f"out_{i:02d}.txt"

while os.path.isfile(in_file_name):
    os.rename(out_file_name, dst_directory + f"out_{i + number_of_dst_file:02d}.txt")
    os.rename(in_file_name, dst_directory + f"in_{i + number_of_dst_file:02d}.txt")
    print(i)
    
    i += 1
    in_file_name = src_directory + f"in_{i:02d}.txt"
    out_file_name = src_directory + f"out_{i:02d}.txt"
