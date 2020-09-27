package chadchat.ui;

import chadchat.domain.User;
import chadchat.entries.ChatServer;
import chadchat.entries.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Protocol extends Thread {
    private final User user;
    private final Scanner in;
    private final PrintWriter out;
    private final ClientHandler clientHandler;

    public Protocol(User user, Scanner in, PrintWriter out, ClientHandler clientH) {
        this.user = user;
        this.in = in;
        this.out = out;
        this.clientHandler = clientH;
    }


    private String getInput() {
        out.print("> ");
        return in.nextLine();
    }

    @Override
    public void run() {
        try {
            out.println(welcomeMessage());
            clientHandler.outLatestChatMsgs();
            String cmd = getInput();
            while (!cmd.equals("dc")) {
                switch (cmd) {
                    case "h":
                        out.println(helpMessage());
                        break;
                    case "chat":
                        while (true) {
                            out.print(">");
                            String a_msg = in.nextLine();
                            sendmesgTest(this.user.getName(), a_msg);
                        }
                    case "channels":
                        break;
                    default:
                        out.println("UNKOWN COMMAND " + cmd);
                        break;
                }
                out.flush();
                cmd = getInput();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendmesgTest(String name, String a_msg) throws IOException {
       clientHandler.test(name,a_msg);
    }

    private String welcomeMessage(){
        return "Welcome " + user.getName()
                + "\nPlease select a chat or pick a channel by typing the name or type help for more info";
    }

    private String helpMessage(){
        return "This is a help message!";
    }

    /*
    private String channelMessage(){
        return "You've joined " + channel.getName();
    }
     */

}

