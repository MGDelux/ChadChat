package chadchat.ui;

import chadchat.domain.UserRepo;
import chadchat.entries.ChatServer;
import chadchat.entries.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Protocol extends Thread {
    private final String user;
    private final Scanner in;
    private final PrintWriter out;
    private final ClientHandler clientHandler;
    private final ChatServer chatServer;
    public boolean inChannel = false;

    public Protocol(String user, Scanner in, PrintWriter out, ClientHandler clientH, ChatServer chatServer) {
        this.user = user;
        this.in = in;
        this.out = out;
        this.clientHandler = clientH;
        this.chatServer = chatServer;
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
            switch (cmd) {
                case "h":
                    out.println(helpMessage());
                    break;
                case "chat":
                    if (inChannel) {
                        while (true) {
                            out.print(">");
                            String a_msg = in.nextLine();
                        }
                    } else {
                        out.println("not in channel");
                        break;
                    }

                case "channels":
                    break;
                case "show":
                    out.println("This will reprint commands");
                    break;
                default:
                    out.println("UNKOWN COMMAND " + cmd);
                    break;
            }
            out.flush();
            cmd = getInput();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String welcomeMessage(){
     return "Welcome " + this.user // fix
             + "\nPlease select chat or pick a channel by typing the name or type [h]elp for more info";
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

