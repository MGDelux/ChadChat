package chadchat.domain;

public interface UserRepo extends UserFactory {
    User findUser(String name);
    Iterable<User> findAllUsers();

}
