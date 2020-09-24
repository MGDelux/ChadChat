package chadchat.domain;

public class User {
    private final int id;
    private final String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return  name;


    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
