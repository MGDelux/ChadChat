package chadchat.entries;

import chadchat.api.*;
import chadchat.domain.User;
import chadchat.ui.Protocol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ClientHandler extends Thread {
    private final chadchat chadchat;
    private Set<User> players;
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername;
    private User newUser;

    public ClientHandler(chadchat.api.chadchat chadchat, Socket client, ChatServer server) throws IOException {
        this.chadchat = chadchat;
        this.socket = client;
        this.server = server;
        in = new Scanner(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public ChatServer getServer() {
        return server;
    }

    @Override
    public void run() {
        try {
            login();
            Protocol p = new Protocol(this.newUser, in, out, this);
            p.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outLatestChatMsgs() {
        try {
            for (String s : server.latestChatMsg) {
                out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() {
       out.println("Welcome to *CHADCHAT* please login:");
       out.println("Username:");
       String username = in.nextLine();
       out.print("password:");
       String password = in.nextLine();
       try {
           chadchat.login(username,password);
       } catch (InvalidPassword invalidPassword) {
           invalidPassword.printStackTrace();
           out.println("ERROR IN LOGIN");
           login();
       }
       out.println("Welcome "+ username);
    }


    public synchronized void test(String clientUsername, String msg) throws IOException { //CHange
        server.sendMsgTest(clientUsername, msg);
    }
}