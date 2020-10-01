package chadchat.domain;

public interface UserRepo extends UserFactory {
    User findUser(String name) throws UserNotFound;
    Iterable<User> findAllUsers();

}
