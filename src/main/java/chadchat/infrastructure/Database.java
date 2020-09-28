package chadchat.infrastructure;

import chadchat.domain.User;
import chadchat.domain.UserExists;
import chadchat.domain.UserRepo;
import chadchat.entries.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Database implements UserRepo {
    static Log log = new Log();
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/chadchat";
    private static final String USER = "chadchat";
    private static final int version = 2;

    public Database() {
        int getTempVer =getCurrentVersion();
        if (getTempVer != getVersion()) {
            log.dblog("Database in wrong state, expected: "
                    + getVersion() + ", got: " + getCurrentVersion());
            throw new IllegalStateException("Database in wrong state, expected:"
                    + getVersion() + ", got: " + getCurrentVersion());
        }
        log.dblog("Version match >" + getTempVer + " : " + version);
    }

    public static int getCurrentVersion() {
        try (Connection conn = getConnection()) {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT value FROM properties WHERE name = 'version';");
            log.dblog("Getting version");
            if (rs.next()) {
                String column = rs.getString("value");
                return Integer.parseInt(column);
            } else {
                log.dblog("No version in properties.");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, null);
    }

    public static int getVersion() {
        return version;
    }

    private User loadUser(ResultSet rs) throws SQLException {
        log.dblog("Loading users");
        return new User(
                rs.getInt("users.id"),
                rs.getString("users.name"),
                rs.getTimestamp("users.createdAt").toLocalDateTime(),
                rs.getBytes("users.salt"),
                rs.getBytes("users.secret"));
    }


    public User createUser(String name, byte[] PJsalt, byte[] secret) throws UserExists {
        log.dblog("Creating user " + name +"\n " + PJsalt + " , "+ secret);
        int tempId;
        try (Connection conn = getConnection()) {
            var ps = conn.prepareStatement("INSERT INTO users (name, salt, secret)" + "VALUE (?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setBytes(2, PJsalt);
            ps.setBytes(3, secret);
            try {
                ps.execute();

            } catch (SQLIntegrityConstraintViolationException e) {
                log.dblog("User exists : "+name);
                throw new UserExists(name);
            }
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                tempId = rs.getInt(1);
            } else {
                log.dblog("User exists : "+name);
                throw new UserExists(name);
            }

        } catch (SQLException throwables) {
            log.dblog("Database error in createUser");
            throw new RuntimeException(throwables);

        }

        return findUser(tempId);
    }
    public User findUser(int id) throws NoSuchElementException {
        return withConnection(conn -> {
            PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM users WHERE id = ?;");
            s.setInt(1, id);
            ResultSet rs = s.executeQuery();
            if(rs.next()) {
                return loadUser(rs);
            } else {
                log.dblog("No version in properties.");
                throw new NoSuchElementException("No user with id: " + id);
            }
        });
    }

    public <T, E extends Throwable> T withConnection(ConnectionHandler<T, E> sqlconnection) throws E { //stolen code lmao
        try (Connection conn = getConnection()) {
            return sqlconnection.sqlIsPOGGERS(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findUser(String name) {
        return withConnection(conn -> {
            PreparedStatement s = conn.prepareStatement(
                    "SELECT * FROM users WHERE name = ?;");
            s.setString(1, name);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                return loadUser(rs);
            } else {
                log.dblog("No version in properties.");
                throw new NoSuchElementException("No user with id: " + name);
            }
        });
    }

    @Override
    public Iterable<User> findAllUsers() {
        return withConnection(conn -> {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users;");
            ResultSet get = ps.executeQuery();
            ArrayList<User> tempUsers = new ArrayList<>();
            while (get.next()) {
                tempUsers.add(loadUser(get));

            }
            return tempUsers;
        });
    }

    interface ConnectionHandler<T, E extends Throwable> {
        T sqlIsPOGGERS(Connection conn) throws SQLException, E;
    } // shout to to christian cause idk how the fuck these 2 methodes works to be honest
}
