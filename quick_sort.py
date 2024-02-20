def quick_sort(arr):
    if len(arr) <= 1:
        return arr
    else:
        pivot = arr[0]
        less_than_pivot = [x for x in arr[1:] if x <= pivot]
        greater_than_pivot = [x for x in arr[1:] if x > pivot]
        return quick_sort(less_than_pivot) + [pivot] + quick_sort(greater_than_pivot)

input_list = input("Enter the list of numbers by space : ").split()
input_list = [int(x) for x in input_list]

print("Original list : ", input_list)

sorted = quick_sort(input_list)

print("Sorted list : ", sorted)