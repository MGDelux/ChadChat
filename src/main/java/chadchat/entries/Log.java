package chadchat.entries;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public String serverLabel;

    public void log(String msg) {
        String localTime =LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
        serverLabel = "[SERVER]";
        System.out.println(serverLabel + " " + localTime + " " + msg);
    }

    public void dblog(String msg) {
        String localTime =LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
        serverLabel = "[SERVER][DB]";
        System.out.println(serverLabel + " " + localTime + " " + msg);
    }
}