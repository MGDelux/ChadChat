package chadchat.entries;

import chadchat.domain.User;
import chadchat.entries.test.Server;
import chadchat.ui.Protocol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

public class ClientHandler extends Thread {
    private Socket socket;
    private ChatServer server;
    private Scanner in;
    private PrintWriter out;
    private String clientUsername;
    private User newUser;

    public ClientHandler(Socket client, ChatServer server) throws IOException {
        this.socket = client;
        this.server = server;
        in = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public ChatServer getServer() {
        return server;
    }

    @Override
    public void run() {
        try {
            checkUser();
            Protocol p = new Protocol(this.newUser, in, out, this);
            p.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (server.removeInactiveUser(clientUsername)) {
                    server.removeInactiveUser(clientUsername);
                }else {
                    server.log.log("user not found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkUser() throws SQLException, ClassNotFoundException, IOException {
        out.println("What is your username?");
        clientUsername = in.nextLine();
        if (server.getActiveUsers(clientUsername)) {
            out.println("This user already a user of this chat\n  choose an other one loser");
            checkUser();
        } else {
            out.println("Welcome user " + clientUsername + " to the worst chat server on the planet");
            DBServer.setUser(clientUsername);
            newUser = new User(server.tempAutoI(), clientUsername);
            server.setActiveUsers(newUser);
            server.sendServerNotification(clientUsername + " Has joined the chat");
        }
    }


}