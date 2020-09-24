package chadchat.entries;

import chadchat.domain.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;

public class ChatServer {
    private final int PORT = 3400;
    private ServerSocket serverSocket;
    private final ArrayList<Socket> clients;
    public final String serverLabel = "[SERVER]";
    public Set<User> activeUsers = new HashSet<>();
    public ArrayList<String> latestChatMsg = new ArrayList<>();
    Log log = new Log();
    PrintWriter out;

    public ChatServer() {
        try {

            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        clients = new ArrayList<>();
    }

    public void startServer() throws IOException {
        log.log("server listening on port " + PORT);
        while (true) {
            Socket client = serverSocket.accept();
            clients.add(client);
            log.log("client count: " + clients.size());
            ClientHandler clientHandler = new ClientHandler(client, this);
            Thread t = new Thread(clientHandler);
            t.start();
        }

    }

    public synchronized void sendServerNotification(String msg) throws IOException {
        for (Socket client : clients) {
            if (!client.isClosed()) {
                out = new PrintWriter(client.getOutputStream(), true);
                out.println(serverLabel + " : " + msg);
                out.flush();
            }
        }
    }

    public synchronized void sendMsgTest(String userName, String msg) throws IOException {
        for (Socket client : clients) {
            LocalTime localTime = LocalTime.now();
            if (!client.isClosed() || userName.isEmpty()) {
                out = new PrintWriter(client.getOutputStream(), true);
                out.println(localTime + " " + userName + " : " + msg);
                log.log(userName + "send: " + msg);
                if (latestChatMsg.size() > 20){
                    latestChatMsg.remove(1);
                }else {
                    latestChatMsg.add(localTime + " " + userName + " : " + msg);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }

    public void setActiveUsers(User user) {
        if (activeUsers.contains(user)) {
            log.log("User already in chat server");
        } else {
            activeUsers.add(user);
            log.log("User ADDED  chat server");
        }
    }

    public boolean removePlayer(User player) {
        if (activeUsers.contains(player)) {
            activeUsers.remove(player);
            log.log("removed: " + player);
            return true;
        } else {
            return false;
        }
    }


    public Set<User> getActiveUsers() {
        return activeUsers;
    }

    public int tempAutoI() { //update later for db info
        if (activeUsers.size() == 0) {
            return 1;
        } else {
            return activeUsers.size();
        }
    }

}
