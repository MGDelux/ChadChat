package chadchat.entries;

import chadchat.api.*;
import chadchat.domain.UserExists;
import chadchat.domain.UserNotFound;
import chadchat.ui.Protocol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private final ChatServer chatServer;
    private final chadchat chadchat;
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername = "null"; //temp
    private boolean loggedIn = false;
    private String name;
    private String password;

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
        out.println(motd());
        try {
            clientMenu();
        } catch (UserExists userExists) {
            userExists.printStackTrace();
        }

    }

    private String getInput() {
        out.print("> ");
        return in.nextLine();
    }

    private void clientMenu() throws UserExists {
        try {
            if (!socket.isOutputShutdown() && !loggedIn) {
                out.println();
                out.println("Menu:");
                out.println("[E] For existing user ");
                out.println("[N] For new user ");
                out.println("[H] For more information ");
                printMenu();
            } else {
                System.out.println("exit");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMenu() throws UserExists, IOException {
        if (!loggedIn && !socket.isClosed()) {
            try {
                String cmd = getInput();
                while (!cmd.toLowerCase().equals("quit")) {
                    switch (cmd.toLowerCase()) {
                        case "h":
                            printHelp();
                            printMenu();
                            break;
                        case "e":
                            clientLogin();
                            break;
                        case "n":
                            createUser();
                            break;
                        default:
                            out.println("Error in your input: " + cmd);
                            printMenu();
                            break;
                    }
                }
            } catch (IOException e) {
                in.close();
                out.close();
                out.println(e);
            }
        } else if (loggedIn) {
            chatServer.setOnlineChads(name);
            Protocol p = new Protocol(name, this.in, this.out, this, this.chatServer);
            p.run();
        }
    }


    private void createUser() throws UserExists, IOException {
        out.println("Please enter your username: ");
        name = in.nextLine();
        if (name.length() <= 3){
            out.println("Username has to be above 3 chars");
            printMenu();
        }
        out.println("Please pick your password: ");
        password = in.nextLine();
        if (chadchat.checkUser(name)) {
            out.println("user already a member of this chat");
            printMenu();
            throw new UserExists(name);
        } else {
            chadchat.createUser(name, password);
            loggedIn = true;
        }
        if (loggedIn = false) {
            printMenu();
        }
        clientLogin();
    }

    private void clientLogin() throws UserExists, IOException {
        try {
            out.println("Username:");
            name = in.nextLine();
            if (chadchat.checkUser(name)) {
                out.println("Password:");
                password = in.nextLine();
                chadchat.login(name, password);
                loggedIn = true;
            } else {
                out.println("does not exist");
            }
        } catch (InvalidPassword invalidPassword) {
            out.println(invalidPassword + " your password is invalid");
            printMenu();
        } catch (UserNotFound userNotFound) {
            out.println(userNotFound + " user does not exist ");
            printMenu();
        }
        printMenu();
    }

    private void printHelp() {
        out.println("Type either N,H,E for the options above and then enter.");
        out.println("Input:");
    }

    private String motd() {
        return "Welcome to Chadchat the one stop chat client for chads";
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

}