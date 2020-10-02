package chadchat.entries;

import chadchat.api.chadchat;
import chadchat.domain.UserRepo;
import chadchat.infrastructure.Database;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatServer {
    private final chadchat chadchat;
    private final int PORT = 3400;
    private ServerSocket serverSocket;
    private final ArrayList<Socket> clients;
    public final String serverLabel = "[SERVER]";
    public Set<String> onlineChads = new HashSet<>();
    public ArrayList<String> latestChatMsg = new ArrayList<>();
    Log log = new Log();
    Database db = new Database();
    PrintWriter out;


    public ChatServer() {
        chadchat = new chadchat(db, db);
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
            chadchat chat = new chadchat(db, db);
            ClientHandler clientHandler = new ClientHandler(this, chat, client, this);
            Thread t = new Thread(clientHandler);
            t.start();

        }

    }

    public synchronized void sendServerNotification(String msg) throws IOException {
        for (Socket client : clients) {
            if (!client.isClosed() && client.isConnected()) {
                out = new PrintWriter(client.getOutputStream(), true);
                out.println(serverLabel + " : " + msg);
            }
        }
    }


    public synchronized void sendMsgTest(String userName, String msg) throws IOException {
        for (Socket client : clients) {
            String localTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm"));
            if (!client.isClosed() || userName.isEmpty()) {
                if (!checkIfCommand(msg)) {
                    out = new PrintWriter(client.getOutputStream(), true);
                    out.println(localTime + " " + userName + " : " + msg);
                    log.log(userName + " said: " + msg);
                    lastTenMessages(localTime, userName, msg);
                } else {
                    out.println(localTime + " " + userName + " used the command : " + msg);
                    lastTenMessages(localTime, userName, msg);
                }
            }
        }
    }

    public boolean checkIfCommand(String msg) {
        try {
            String[] spilt = msg.split("!");
            String commad = spilt[spilt.length - 1];
            if (msg.startsWith("!")) {
                log.log("COMMAND: " + commad);
                if (commad.toLowerCase().equals("quit"));
                return true;

            } else return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
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

    public void setOnlineChads(String user) {
        if (onlineChads.contains(user)) {
            log.log("User already in chat server");
        } else {
            onlineChads.add(user);
            log.log("User ADDED  chat server");
        }
    }

    public boolean removeInactiveUser(String userName) {
        if (onlineChads.contains(userName)) {
            onlineChads.remove(userName);
            log.log("removed: " + userName);
            return true;
        } else {
            log.log("cannot find in online users" + userName);
            return false;
        }
    }


    public Set<String> getOnlineChads() {
        return onlineChads;
    }
}
