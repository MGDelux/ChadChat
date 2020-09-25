package chadchat.ui;

import chadchat.domain.User;
import chadchat.entries.ChatServer;
import chadchat.entries.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Protocol extends Thread {
    private boolean inChatChannel = false;
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
            out.println("Welcome " + user.getName());
           // clientHandler.outLatestChatMsgs();
            String cmd = getInput();
            while (!cmd.equals("dc")) {
                switch (cmd) {
                    case "h":
                        out.println("Help command infrom user bla bla");
                        break;
                    case "chat":
                        if (inChatChannel) {
                            while (inChatChannel) {
                                out.print(">");
                                String a_msg = in.nextLine();
                                sendmesgTest(this.user.getName(), a_msg);
                            }
                        }
                        else {
                            out.println("You need to chose a channel to join.");
                        }
                    case "channels":
                        out.println("Currently in a channel : " +inChatChannel);
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
     //  clientHandler.test(name,a_msg);
    }
}

