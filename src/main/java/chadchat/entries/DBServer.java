package chadchat.entries;

import chadchat.domain.User;

import java.sql.*;

public class DBServer {
    protected static final String password = "JaHaJe1988";
    // The entry point of the ChatChad server

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/chadchat";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = password;

    /**
     * This is purely a data base test. Given that you have created a
     * users table in chatchad with an id and name.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static String dbTest(String userclientUsername) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {  // FIX BAD METHODE FOR CONNECTING
            var stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name FROM users";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"));
                System.out.println(user);
                if (user.getName().equals(userclientUsername)) {
                    return user.getName();
                }
            }
        }
        return "TEST";
    }


    public static void setUser(User user) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        try {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // FIX BAD METHODE FOR CONNECTING
                String q = "INSERT INTO chadchat.users (name) " + " values (?)";
                PreparedStatement preparedStatement = conn.prepareStatement(q);
                preparedStatement.setString(1, user.toString());
                preparedStatement.execute();
                conn.close();
                System.out.println("DB ADDED " + user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    }


}
