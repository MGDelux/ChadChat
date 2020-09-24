package chadchat.entries;

import chadchat.domain.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class ClientHandler extends Thread {
    private Set<User> players;
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername = "";


    public ClientHandler(Socket client, ChatServer server) throws IOException {
        this.socket = client;
        this.server = server;
        in = new Scanner(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            setClientUsername();
            out.println("Please chose a channel to connect to:");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClientUsername() {
        out.println("Hello and welcome please enter your Username:");
        System.out.println("[SERVER] . . . ");
        clientUsername = in.nextLine();
        out.println("Welcome user " + clientUsername);
        System.out.println("[SERVER] Adding user " + clientUsername);
        User newUser = new User(server.tempAutoI(), clientUsername);
        server.setActiveUsers(newUser);
        out.println("ONLINE USERS: " + server.activeUsers.toString()); //fix lol
    }

    public Set<User> getPlayersInLobby() { //gets players on server
        players = server.getActiveUsers();
        return players;
    }
}