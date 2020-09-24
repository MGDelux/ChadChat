package chadchat.entries;

import chadchat.domain.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

public class ClientHandler extends Thread {
    private Set<User> players;
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername;


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
            while (true){
              String  msg = in.nextLine();
              server.sendmesgTest(clientUsername,msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClientUsername() throws IOException, SQLException, ClassNotFoundException {
        out.println("Hello and welcome please enter your Username:");
        clientUsername = in.nextLine();
        if(checkIfNewUser(clientUsername.toLowerCase()).equals(clientUsername.toLowerCase())){
            out.println("An user with the name " + clientUsername + " is already an user on this server..");
            out.println("is it you? 1. yes - 2. no --");
            int inP = in.nextInt();
            if (inP == 2){
                //do something
                out.println("Welcome new user");
            }else {
                out.println("Welcome back");
            }
        }
        out.println("Welcome user " + clientUsername);
        System.out.println("[SERVER] Adding user " + clientUsername);
        User newUser = new User(server.tempAutoI(), clientUsername);
        server.setActiveUsers(newUser);
        out.println("ONLINE USERS: " + server.activeUsers.toString()); //fix lol
        server.sendServerNotification(clientUsername + " Has joined the chat");
         DBServer.setUser(newUser);
    }

    private String checkIfNewUser(String clientUsername) throws SQLException, ClassNotFoundException {
         return DBServer.dbTest(clientUsername);

    }

    public Set<User> getPlayersInLobby() { //gets players on server
        players = server.getActiveUsers();
        return players;
    }
}