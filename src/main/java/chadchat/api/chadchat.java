package chadchat.api;

import chadchat.domain.User;
import chadchat.domain.UserExists;
import chadchat.domain.UserRepo;

public class chadchat {
    private final UserRepo users;

    public chadchat(UserRepo users) {
        this.users = users;
    }

    public Iterable<User> findAllUsers() {
        return users.findAllUsers();
    }
    public User checkUser(String user){
        return users.findUser(user);
    }

    public User createUser(String name, String password) throws UserExists {
        byte[] salt = User.genereateSalt();
        byte[] secret = User.calculateSecret(salt, password);
        return users.createUser(name, salt, secret);
    }

    public User login(String username, String password) throws InvalidPassword {
        User user = users.findUser(username);
        if (user.isPasswordCorrect(password)) {
            return user;
        } else {
            throw new InvalidPassword();
        }
    }
}