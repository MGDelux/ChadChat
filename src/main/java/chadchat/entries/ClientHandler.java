package chadchat.entries;

import chadchat.api.*;
import chadchat.domain.User;
import chadchat.domain.UserExists;
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
    private final ChatServer chatServer;
    private final chadchat chadchat;
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername; //temp

    public ClientHandler(ChatServer chatServer, chadchat chadchat, Socket client, ChatServer server) throws IOException {
        this.chatServer = chatServer;
        this.chadchat = chadchat;
        this.socket = client;
        this.server = server;
        in = new Scanner(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            login();
        } catch (Exception e) {
            out.println("ERROR IN LOGIN " + e);
            e.printStackTrace();
        }
        try {
        if (!clientUsername.equals(null)) {
            chatServer.setOnlineChads(clientUsername);
            chatServer.sendServerNotification(clientUsername+ " has joined the chat wooo!");
            Protocol p = new Protocol(this.clientUsername, this.in, this.out, this, chatServer);
            p.run();
        }
        out.println("REEEEEEEEEEEE");
    }catch (IOException e){
            out.println(e);
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

    public void login() throws UserExists { //REFACTOR
        out.println("Hello and welcome to the 4324234232334 iteration of chadchat ");
        out.println("Are you a [n]ew users? or an [e]xisting users?");
        loginMenu();
    }

    private void loginMenu() {
        String input = in.next();
        switch (input) {
            case "n":
                createNewUser();
                break;
            case "e":
                userLogin();
                break;
            default:
                out.println("ERROR INPUT");
                break;
        }
    }

    private void userLogin() {
        in.nextLine();
        out.println("Login:");
        out.println("Username:");
        String userName = in.nextLine();
        out.println("Password:");
        String passWord = in.nextLine();
        try {
            chadchat.login(userName,passWord);
        } catch (InvalidPassword invalidPassword) {
            out.println(invalidPassword);
        }
        clientUsername = userName;
    }

    private void createNewUser() {
        in.nextLine();
        out.println("Create new user:");
        out.println("New Username:");
        String userName = in.nextLine();
        out.println("New Password:");
        String passWord = in.nextLine();
        try {
            chadchat.createUser(userName,passWord);
        } catch (UserExists userExists) {
            out.println(userExists);
        }
        clientUsername = userName;
    }


    public void test(String clientUsername, String msg) throws IOException { //CHange
        server.sendMsgTest(clientUsername, msg);
    }
}