def check_equal_ones_and_twos(input_string):
    # Initialize the difference counter
    difference = 0
    
    # Iterate over each character in the input string
    for symbol in input_string:
        if symbol == '1':
            difference += 1  # Increment difference if symbol is '1'
        elif symbol == '2':
            difference -= 1  # Decrement difference if symbol is '2'
        elif symbol == '0':
            break  # End marker encountered, stop processing

    # Check the final difference after processing all symbols
    if difference == 0:
        return 1  # The string is accepted
    else:
        return 0  # The string is rejected

# Example Usage
input_string1 = "11220"
input_string2 = "112120"

result1 = check_equal_ones_and_twos(input_string1)
result2 = check_equal_ones_and_twos(input_string2)

print(f"Input: {input_string1} -> Output: {result1}")  # Expected Output: 1 (Accepted)
print(f"Input: {input_string2} -> Output: {result2}")  # Expected Output: 0 (Rejected)
