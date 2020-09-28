package chadchat.infrastructure;

import chadchat.domain.User;

import java.util.NoSuchElementException;

public interface DatabaseFind {
    User findUser(int id) throws NoSuchElementException;
}
