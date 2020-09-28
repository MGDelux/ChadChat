package chadchat.infrastructure;

import chadchat.domain.User;
import chadchat.domain.UserExists;

public interface DatabaseCreate {
    User createUser(String name, byte[] PJsalt, byte[] secret) throws UserExists;
}
