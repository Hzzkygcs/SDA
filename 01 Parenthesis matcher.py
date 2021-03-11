

get_closing = {
    "(" : ")",
    "[" : "]",
    "{" : "}",
    "?": "ERROR"
    }

get_opening = {
    ")" : "(",
    "]" : "[",
    "}" : "{",
    "?": "ERROR"
    }


class InvalidParenthesis(Exception):
    pass

class Node():
    def __init__(self, value, start_index):
        self.nodes = []  # list of Node
        self.value = value
        self.start = start_index

    def add_node(self, new_node, index=tuple()):
        self.nodes.append(new_node)
        new_node._parent = self

    def get_parent(self):
        return self._parent

    def set_stop_index(self, stop_index):
        self.stop = stop_index

    def has_child(self):
        return len(self.nodes) > 0


class Root():
    def __init__(self):
        self.entry_root = Node("?", -1)
        self.pointer = self.entry_root
        self.index = 0
        

    def feed_new_data(self, value):
        if value in "({[":
            self.feed_new_opening(value)
        elif value in ")}]":
            self.feed_new_closing(value)

    def feed_new_opening(self, value):
        assert value in "({["
        self.index += 1
        new_node = Node(value, self.index)
        self.pointer.add_node(new_node)
        self.pointer = new_node

    def feed_new_closing(self, value):
        assert value in ")}]"
        self.index += 1
        if get_closing[self.pointer.value] != value:
            raise InvalidParenthesis()
        self.pointer.set_stop_index(self.index)
        self.pointer = self.pointer.get_parent()

    def close(self):
        if self.entry_root is not self.pointer:
            raise InvalidParenthesis()

    def print_all(self):
        print(self.index // 2)
        for child in self.entry_root.nodes:
            self._print_recursive(child)

    def _print_recursive(self, pointer):
        print(pointer.start, pointer.stop)

        if pointer.has_child():
            for child in pointer.nodes:
                self._print_recursive(child)
     

user_input = input()
root = Root()
try:
    for i in user_input:
        root.feed_new_data(i)

    root.close()
    print("VALID")
    root.print_all()
except InvalidParenthesis:
    print("INVALID")


