package chadchat.domain;

public interface UserFactory {
    User createUser(String userName, byte[] salt, byte[] secret) throws UserExists;

}
