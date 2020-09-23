package chadchat.entries;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static final int PORT = 3600;
    private static Socket client;
    public static ArrayList<String> Clients = new ArrayList<>();
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket sever = new ServerSocket(PORT);
            System.out.println("[SERVER] Started on port " + PORT);
            while (true) {
                client = sever.accept();
                System.out.println("[SERVER] client joined");
                ClientHandler clientHandler = new ClientHandler(client, clientHandlers);
                clientHandlers.add(clientHandler);
                Thread t = new Thread(clientHandler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static boolean addUser(String user) {
        if (Clients.contains(user)) {
            System.out.println("excist");
            return true;
        } else {
            Clients.add(user);
            System.out.println("added");
            return false;

        }
    }
}
