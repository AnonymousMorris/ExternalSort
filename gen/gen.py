import random
import argparse

def generate_data(filename, N):
    with open(filename, 'w') as f:
        for i in range(9, N+10):
            # Generate a random long integer (you can adjust the range as needed)
            long_int = i#random.randint(1, 10**12)
            # Generate a random double (you can adjust the range as needed)
            double = i#random.uniform(0, 1e6)
            # Write the key and value on the same line
            f.write(f"{long_int} {double}\n")
        for i in range(N):
            # Generate a random long integer (you can adjust the range as needed)
            long_int = i#random.randint(1, 10**12)
            # Generate a random double (you can adjust the range as needed)
            double = i#random.uniform(0, 1e6)
            # Write the key and value on the same line
            f.write(f"{long_int} {double}\n")    


if __name__ == "__main__":
    generate_data("test.txt", 8*512)
