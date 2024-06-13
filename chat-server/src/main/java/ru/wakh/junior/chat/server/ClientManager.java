package ru.wakh.junior.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientManager implements Runnable {
    private Socket socket;
    private String name;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            sendBroadcastMessage("Server: " + name + "  подключился к чату.");

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        sendBroadcastMessage("Server: " + name + " покинул чат.");
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient == null) {
                    // Для MacOS
                    closeEverything(socket, bufferedWriter, bufferedReader);
                    break;
                }
                processMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    /**
     * Отправка сообщения всем слушателям
     *
     * @param message сообщение
     */

    private void sendUniqMessage(ClientManager client, String message) {
        try {
            client.bufferedWriter.write(message);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    private void sendBroadcastMessage(String message) {
        for (ClientManager client : clients) {
            if (!client.name.equals(name) && message != null) {
                sendUniqMessage(client, message);
            }
        }
    }


    private void processMessage(String message) {

        Pattern pattern = Pattern.compile("^@(\\p{Graph}+)\\p{Blank}+(\\p{Print}+)$", Pattern.UNICODE_CHARACTER_CLASS);

        Matcher matcher = pattern.matcher(message);
        String sendToUserName;
        String messageToSend;
        if (matcher.matches()) {
            sendToUserName = matcher.group(1);
            messageToSend = matcher.group(2);
            for (ClientManager client : clients) {
                if (sendToUserName.equals(client.name)) {
                    sendUniqMessage(client, name + ": " + messageToSend);
                    return;
                }
            }
            sendUniqMessage(this, "Server: Клиент с именем " + sendToUserName + " не найден.");

        } else {
            sendBroadcastMessage(name + ": " + message);
        }
    }


    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

