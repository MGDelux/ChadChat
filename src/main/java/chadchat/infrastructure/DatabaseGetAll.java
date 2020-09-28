package chadchat.infrastructure;

import chadchat.domain.User;

public interface DatabaseGetAll {
    Iterable<User> getAllUsers();
}
