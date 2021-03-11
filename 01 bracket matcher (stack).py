# import SDALabADT as ADT

stack_index = list()  # stack representative
stack_value = list()  # stack representative

def stack_write(stack, x):
    stack.append(x)

def stack_read(stack):
    return stack[-1]

def stack_pop(stack):
    return stack.pop(-1)





user_input = input()



opening_to_closing = opening = {
    "(" : ")",
    "[" : "]",
    "{" : "}",
    "?": "ERROR"
    }

closing_to_opening = closing = {
    ")" : "(",
    "]" : "[",
    "}" : "{",
    "?": "ERROR"
    }


output = []


index = 0
for i in user_input:
    index += 1
    
    if i in opening:
        stack_write(stack_value, i)
        stack_write(stack_index, index)
    elif i in closing:
        if len(stack_index) == 0:
            print("INVALID")
            break
        elif closing_to_opening[i] == stack_read(stack_value):
            stack_pop(stack_value)
            opening_index = stack_pop(stack_index)
            output.append((opening_index, index))
        else:
            print("INVALID")
            break

        
    else:
        print(i)
        raise Exception()
else:
    if len(stack_index) == 0:
        output.sort(key = lambda x: x[0])
        print("VALID")
        print(len(output))
        print("\n".join(map(lambda x: f"{x[0]} {x[1]}", output)))
    else:
        print("INVALID")
