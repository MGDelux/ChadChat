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
    private final chadchat chadchat;
    private Set<String> players;
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername;

    public ClientHandler(chadchat chadchat, Socket client, ChatServer server) throws IOException {
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

    public void login() throws UserExists { //REFACTOR
        out.println("Welcome to *CHADCHAT* please choose:");
        out.println("1. For existing user ");
        out.println("2. For new user ");
        int menuC = in.nextInt();
        if (menuC == 1) {
            loginUserIn();
        } else if (menuC == 2) {
            createNewUser();
        }
     //   Protocol p = new Protocol()

    }

    private void loginUserIn() throws UserExists {
        in.nextLine();
        out.println("Username:");
        String username = in.nextLine();
        out.println("password:");
        String password = in.nextLine();
        try {
            chadchat.login(username, password);
        } catch (InvalidPassword invalidPassword) {
            invalidPassword.printStackTrace();
            out.println("ERROR IN LOGIN");
            login();
        }
    }


    private void createNewUser() throws UserExists {
        in.nextLine();
        out.println("new username:");
        String newUsername = in.nextLine();
        out.println("password:");
        String passWord = in.nextLine();
        chadchat.createUser(newUsername, passWord);
    }


    public synchronized void test(String clientUsername, String msg) throws IOException { //CHange
        server.sendMsgTest(clientUsername, msg);
    }
}