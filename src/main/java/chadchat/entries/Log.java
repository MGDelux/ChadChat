package chadchat.entries;

public class Log {
    public  String serverLabel = "[SERVER]";
    public void log(String msg){

        System.out.println(serverLabel + msg);
    }
}