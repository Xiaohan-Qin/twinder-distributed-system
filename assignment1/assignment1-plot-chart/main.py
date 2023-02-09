import csv
import matplotlib.pyplot as plt
import math

# List to store the start and end times of each request
times = []

# Open the CSV file for reading
with open('../assignment1-client2/res/records/100_500000.csv', 'r') as csvfile:
    # Create a CSV reader object
    reader = csv.reader(csvfile)

    # Skip the header row
    next(reader)

    # Iterate through the rows in the CSV file
    for row in reader:
        start_time = math.floor(int(row[1]) / 1000)
        end_time = math.floor(int(row[2]) / 1000)
        times.append((start_time, end_time))

    max_end_time = max(end_time for _, end_time in times)
    min_start_time = min(start_time for start_time, _ in times)
    throughput_per_second = [0] * int(max_end_time - min_start_time + 1)
    for start_time, end_time in times:
        throughput_per_second[end_time - min_start_time] += 1

    # Plot the chart
    x = range(int(max_end_time - min_start_time + 1))
    y = throughput_per_second
    plt.plot(x, y)
    plt.xlabel("Time (seconds)")
    plt.ylabel("Throughput/second")
    plt.show()
