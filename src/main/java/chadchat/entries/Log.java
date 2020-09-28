package chadchat.entries;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public String serverLabel;
    String localTime = "@" + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm"));

    public void log(String msg) {
        serverLabel = "[SERVER]";
        System.out.println(serverLabel + " " + localTime + " " + msg);
    }

    public void dblog(String msg) {
        serverLabel = "[SERVER][DB]";
        System.out.println(serverLabel + " " + localTime + " " + msg);
    }
}