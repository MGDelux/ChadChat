package chadchat.entries;

import chadchat.domain.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
            }
        }
    }

    public synchronized void sendMsgTest(String userName, String msg) throws IOException {
        for (Socket client : clients) {
            String localTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm"));
            if (!client.isClosed() || userName.isEmpty()) {
                out = new PrintWriter(client.getOutputStream(), true);
                out.println(localTime + " " + userName + " : " + msg);
                log.log(userName + "send: " + msg);
                lastTenMessages(localTime,userName,msg);
            }
        }
    }

    public synchronized void lastTenMessages(String localTime, String userName, String message) throws IOException {
        if (latestChatMsg.size() > 11) {
            latestChatMsg.remove(1);
        } else {
            latestChatMsg.add(localTime + " " + userName + " : " + message);
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

    public boolean removeInactiveUser(String userName) {
        if (activeUsers.contains(userName)) {
            activeUsers.remove(userName);
            log.log("removed: " + userName);
            return true;
        } else {
            return false;
        }
    }


    public boolean getActiveUsers(String userName) {
        for (User u: activeUsers){
            if (u.getName().contains(userName)){
                return true;
            }else return false;
        }return false;
    }

    public int tempAutoI() { //update later for db info
        if (activeUsers.size() == 0) {
            return 1;
        } else {
            return activeUsers.size();
        }
    }

}
