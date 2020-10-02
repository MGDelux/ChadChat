package chadchat.ui;

import chadchat.domain.Channel;
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
    private boolean inChat = false;

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
        out.println("Currently Online users: " + chatServer.onlineChads);
        clientHandler.outLatestChatMsgs();
        try {
            String cmd = getInput();
            while (!cmd.equals("dc")) {
                switch (cmd) {
                    case "h":
                        out.println(helpMessage());
                        break;
                    case "chat":
                        if (inChannel) {
                            inChat = true;
                            out.println("join chat #");
                            String a_msg;
                            while (inChat) {
                                out.print(">");
                                a_msg = in.nextLine();
                                if (!chatServer.checkIfCommand(a_msg)) {
                                    chatServer.sendMsgTest(this.user, a_msg);
                                } else {
                                    inChat = false;
                                    log.log("user left the chat");
                                    chatServer.sendServerNotification(user + " left the chat ");
                                }
                            }
                        } else {
                            out.println("not in channel");
                            break;
                        }
                    case "channels":
                        inChannel = true;
                        out.println("in channel");
                        break;
                    case "show":
                        out.println("This will reprint commands");
                        break;
                    case "create":
                        if (!inChannel) {
                            out.println("You're creating a channel, give it a name");
                            String channelName = in.nextLine();
                            Channel tmpChannel = new Channel(Channel.generateId(), channelName);
                        }
                    default:
                        out.println("UNKOWN COMMAND " + cmd);
                        break;
                }
                out.flush();
                cmd = getInput();
            }
        } catch (IOException e) {
            chatServer.removeInactiveUser(user);
            log.log("USER DISCONNECTED CLOSING CONNECTION : " + Thread.currentThread() + " - " + this.user);
            out.close();
            in.close();
            try {
                chatServer.sendServerNotification(user + " has left");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
    }

    private String welcomeMessage() {
        return "Welcome " + this.user // fix
                + "\nPlease select chat or pick a channel by typing the name or type [h]elp for more info";
    }

    private String helpMessage() {
        return "\nType channels then press return"
                + "\nType chat then press return";
    }

    /*
    private String channelMessage(){
        return "You've joined " + channel.getName();
    }
     */

}

