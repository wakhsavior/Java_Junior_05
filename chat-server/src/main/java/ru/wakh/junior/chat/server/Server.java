package ru.wakh.junior.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    /**
     * Серверный сокет
     */
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Подключен новый клиент!");
                ClientManager clientManager = new ClientManager(socket);
                Thread thread = new Thread(clientManager);
                thread.start();
            }
        } catch (IOException e){

        }
    }
    private void closeSocket(){
        try {
            if(serverSocket != null) serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
