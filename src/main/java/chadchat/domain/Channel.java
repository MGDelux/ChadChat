package chadchat.domain;

import java.util.ArrayList;
import java.util.Random;

public class Channel {
    private static ArrayList<Integer> channelIds = new ArrayList<>();
    private final int channelID;
    private final String channelName;
    public ArrayList<String> users;

    public Channel(int channelID, String channelName, ArrayList<String> users) {
        this.channelID = channelID;
        this.channelName = channelName;
        this.users = users;
    }

    public int getChannelID() {
        return channelID;
    }

    public String getChannelName() {
        return channelName;
    }


    @Override
    public String toString() {
        return "Channel{" +
                "channelID=" + channelID +
                ", channelName='" + channelName + '\'' +
                ", users=" + users +
                '}';
    }

    public static int generateId(){
        Random random = new Random();
        int id =  random.nextInt(100);
        if (channelIds.contains(id)){
            generateId();
        }else {
           channelIds.add(id);
           return id;
        }
        return -1;

    }
}
