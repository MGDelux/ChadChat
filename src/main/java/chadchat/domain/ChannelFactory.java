package chadchat.domain;

import java.util.ArrayList;

public interface ChannelFactory {
    Channel createChannel(int channelID, String channelName, ArrayList<String> users);
}
