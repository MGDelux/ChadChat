package chadchat.domain;

public interface ChannelRepo extends ChannelFactory {
    Channel getChannel(int id);
    Iterable<Channel> getAllChannels();
}
