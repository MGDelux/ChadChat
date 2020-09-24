package chadchat.entries;

import chadchat.domain.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private final int PORT = 3400;
    private ServerSocket serverSocket;
    private final ArrayList<Socket> clients;
    private final String  serverLabel = "[SERVER]";
    public Set<User> activeUsers = new HashSet<>();
    PrintWriter out;

    public ChatServer() {
        try {

            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        clients = new ArrayList<Socket>();
    }

    public void startServer() throws IOException {
        System.out.println("[SERVER] server listing on " + PORT);
        while (true) {
            Socket client = serverSocket.accept();
            clients.add(client);
            System.out.println("[SERVER] accepted client " + client.getRemoteSocketAddress());
            System.out.println("[SERVER] client count: " + clients.size());
            ClientHandler clientHandler = new ClientHandler(client, this);
            Thread t = new Thread(clientHandler);
            t.start();
        }

    }

    public synchronized void sendServerNotification(String msg) throws IOException {
        for (Socket client : clients) {
            if (!client.isClosed()) {
                out = new PrintWriter((client.getOutputStream())); //fix
                out.println(serverLabel +" : "+ msg);
                out.flush();
            }
        }
    }
    public synchronized void sendmesgTest(String userName,String msg) throws IOException {
        for (Socket client : clients) {
            if (!client.isClosed() || userName.isEmpty()) {
                out = new PrintWriter((userName + " : " +  client.getOutputStream())); //fix
                out.println(msg);
                out.flush();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }

    public boolean setActiveUsers(User user) {
        if (activeUsers.contains(user))
        {
            System.out.println("[SERVER] User already in chat server");
            return true;
        }
        else {
            activeUsers.add(user);
            System.out.println("[SERVER] User ADDED  chat server");
            return false;
        }
    }
    public  boolean removePlayer(User player){
        if (activeUsers.contains(player)){
            activeUsers.remove(player);
            System.out.println("[SERVER] removed: "+player);
            return true;
        } else {
            return false;
        }
    }


    public Set<User> getActiveUsers() {
        return activeUsers;
    }

    public int tempAutoI(){ //update later for db info
        if (activeUsers.size() == 0){
            return 1;
        }
        else {
            return activeUsers.size();
        }
    }
}
