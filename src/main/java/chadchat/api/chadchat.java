package chadchat.api;

import chadchat.domain.*;

import java.util.ArrayList;

public class chadchat {
    private final UserRepo users;

    public chadchat(UserRepo users) {
        this.users = users;
    }

    /**
     *
     * @param user
     * @return true if user exists
     */
    public boolean checkUser(String user) {
        try {
            users.findUser(user);
            return true;
        } catch (UserNotFound userNotFound) {
            return false;
        }
    }

    public User createUser(String name, String password) throws UserExists {
        byte[] salt = User.genereateSalt();
        byte[] secret = User.calculateSecret(salt, password);
        return users.createUser(name, salt, secret);
    }

    public User login(String username, String password) throws InvalidPassword, UserNotFound {
        User user = users.findUser(username);
        if (user.isPasswordCorrect(password)) {
            return user;
        } else {
            throw new InvalidPassword();
        }
    }
}