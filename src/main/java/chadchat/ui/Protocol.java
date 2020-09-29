package chadchat.ui;

import chadchat.entries.ChatServer;
import chadchat.entries.ClientHandler;
import chadchat.entries.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Protocol extends Thread {
    Log log = new Log();
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
        log.log(this.user + this.in + this.out + this.clientHandler + this.chatServer);
        out.println(welcomeMessage());
        out.println(chatServer.onlineChads);
        clientHandler.outLatestChatMsgs();
            try {
                String cmd = getInput();
                while (!cmd.equals("quit")) {
                switch (cmd) {
                    case "h":
                        out.println(helpMessage());
                        break;
                    case "chat":
                        if (inChannel) {
                            out.println("join chat #");
                            while (true) {
                                out.print(">");
                                String a_msg = in.nextLine();
                                chatServer.sendMsgTest(this.user, a_msg);
                            }
                        } else {
                            out.println("not in channel");
                            break;
                        }
                    case "channels":
                        inChannel = true;
                        out.println("in channel");
                        out.println(chatServer.channel.toString());

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
            }
        }catch (Exception e){
                chatServer.removeInactiveUser(user);
                try {
                    chatServer.sendServerNotification(user+ " has left");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                log.log("USER DISCONNECTED CLOSING CONNECTION : " + Thread.currentThread() + " - "+ this.clientHandler.getName());
                out.close();
                in.close();
                out.println(e);
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

