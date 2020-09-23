package chadchat.entries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private final BufferedReader in;
    private final PrintWriter out;
    private final ArrayList<ClientHandler> clients;
    private String localClientUsername = null;

    public ClientHandler(Socket client, ArrayList<ClientHandler> clients) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.clients = clients;
    }

    @Override
    public void run() {
        out.println("Welcome");
        try {
            out.println("What is your username? ");
            String username = in.readLine();
            if (Server.addUser(username) || username.isEmpty()) {
                out.println("FEJL");
                run();
            }
            localClientUsername = username;
            while (true) {
                String userInput = in.readLine();
                int space = userInput.indexOf(" ");
                if (userInput.startsWith("say")) { //player broadcast msg
                    if (space != -1) {
                        bounceBack(userInput.substring(space + 1));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bounceBack(String msg) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.out.println("[SERVER] " + localClientUsername + " " + msg);
        }
    }
}
