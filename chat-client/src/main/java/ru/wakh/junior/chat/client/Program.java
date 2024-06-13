package ru.wakh.junior.chat.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {


            Scanner scn = new Scanner(System.in);
            System.out.print("Введите имя: ");
            String name = scn.nextLine();

            InetAddress address = InetAddress.getLocalHost();

            Socket socket = new Socket(address,5000);
            Client client = new Client(socket,name);
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println("InetAddress: " + inetAddress);
            String remoteIp = inetAddress.getHostAddress();
            System.out.println("Remote IP: " + remoteIp);
            System.out.println("LocalPort: " + socket.getLocalPort());

            client.listenForMessage();
            client.sendMessage();


        } catch (UnknownHostException ex){
            throw new RuntimeException(ex);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }
}