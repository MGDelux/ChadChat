package chadchat.entries;

import chadchat.api.*;
import chadchat.domain.UserExists;
import chadchat.ui.Protocol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
            try {
                clientMenu();
            } catch (UserExists userExists) {
                userExists.printStackTrace();
            }
        } catch (IOException e) {
            out.println(e);
        }

    }

    private String getInput() {
        out.print("> ");
        return in.nextLine();
    }

    private void clientMenu() throws UserExists, IOException {
        if (!this.socket.isClosed()){
            try {

                out.println();
                out.println("Menu:");
                out.println("[E] For existing user ");
                out.println("[N] For new user ");
                out.println("[H] For more information ");
                printMenu();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else if (this.socket.isClosed()){
            in.close();
            out.close();
        }
    }

    private void printMenu() throws UserExists {
        if (!loggedIn) {
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
            } catch (Exception e) {
                out.println(e);
            }
        } else if (loggedIn) {
            Protocol p = new Protocol(name, this.in, this.out, this, this.chatServer);
            p.run();
        }
        clientMenu();
    }


    private void createUser() throws UserExists {
        out.println("Please enter your username: ");
        name = in.nextLine();
        out.println("Please pick your password: ");
        password = in.nextLine();
        if (chadchat.checkUser(name)) {
            throw new UserExists(name);
        } else {
            chadchat.createUser(name, password);
            loggedIn = true;
        }
        if (loggedIn = false){
            printMenu();
        }
        printMenu();
    }

    private void clientLogin() throws UserExists {

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