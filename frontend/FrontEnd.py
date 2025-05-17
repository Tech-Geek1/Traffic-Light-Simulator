# This is purely and example of how we can integrate the java and python code, please feel free to update this code
# Maybe pygame is better im not sure, I just personally had openGL errors with pygame
import tkinter as tk
import socket
import threading
import select

ROWS = 10
COLS = 10
CELL_SIZE = 100
COLORS = {
    '0': 'green',
    '1': 'black',
    '2': 'blue'
}

class BoardApp:
    def __init__(self, root, initial_board):
        self.root = root
        self.canvas = tk.Canvas(root, width=COLS*CELL_SIZE, height=ROWS*CELL_SIZE)
        self.canvas.pack()
        self.board = initial_board
        self.draw_board()

    def draw_board(self):
        for row in range(ROWS):
            for col in range(COLS):
                color = COLORS.get(self.board[row][col], 'white')
                x1 = col * CELL_SIZE
                y1 = row * CELL_SIZE
                x2 = x1 + CELL_SIZE
                y2 = y1 + CELL_SIZE
                self.canvas.create_rectangle(x1, y1, x2, y2, fill=color, outline='white')

    def update_board(self, new_board):
        self.board = new_board
        self.canvas.delete("all")
        self.draw_board()

def handle_client(app, client_socket):
    try:
        data = client_socket.recv(1024)
        if data:
            board_data = data.decode('utf-8').split('\n')
            app.update_board(board_data)
    except socket.error:
        pass
    finally:
        client_socket.close()

def start_server(app):
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind(('localhost', 12345))
    server.listen(5)
    server.setblocking(False)
    print("Started")
    print("Waiting for connection...")

    while True:
        readable, _, _ = select.select([server], [], [], 1.0)
        if server in readable:
            client_socket, addr = server.accept()
            print(f"Connection established with {addr}")
            client_thread = threading.Thread(target=handle_client, args=(app, client_socket))
            client_thread.daemon = True
            client_thread.start()

def main():
    initial_board = [
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000",
        "0000000000"
    ]

    root = tk.Tk()
    app = BoardApp(root, initial_board)

    # Start server
    server_thread = threading.Thread(target=start_server, args=(app,))
    server_thread.daemon = True
    server_thread.start()

    root.mainloop()

if __name__ == "__main__":
    main()
