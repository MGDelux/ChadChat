package chadchat.entries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket server;
    private BufferedReader in;

    public ServerHandler(Socket server, BufferedReader in) throws IOException {
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {
        String bounceBack = null;
        try {
            while (true){
                bounceBack = in.readLine();
                if (bounceBack == null || bounceBack.isEmpty())break;

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
