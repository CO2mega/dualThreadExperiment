import java.io.*;
import java.sql.*;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class DataProcessing {

    public static boolean connectToDB = false;
    private static Connection connection;

    public static void connectToDatabase() {
    String driverName = "com.mysql.cj.jdbc.Driver";
    //String url = "jdbc:mysql://localhost:3306/document";
    //String user = "root";
    //String password = "wcp2624191818WC";
    String url="jdbc:mysql://8.146.198.87:10055/114514";
    String user="114514";
    String password="Shimokita_HOMO";

    long startTime = System.currentTimeMillis(); // Start timer

    try {
        Class.forName(driverName);
        connection = DriverManager.getConnection(url, user, password);
        connectToDB = true;
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
        connectToDB = false;
    } finally {
        long endTime = System.currentTimeMillis(); // End timer
        long duration = endTime - startTime; // Calculate duration
        System.out.println("Database connection time: " + duration + " ms");
    }
}


    public static Doc searchDoc(String id) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "SELECT * FROM doc_info WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String creator = rs.getString("creator");
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    String description = rs.getString("description");
                    String filename = rs.getString("filename");
                    return new Doc(id, creator, timestamp, description, filename);
                }
            }
        }
        return null;
    }

    public static Enumeration<Doc> listDoc() throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "SELECT * FROM doc_info";
        Vector<Doc> docList = new Vector<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String creator = rs.getString("creator");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String description = rs.getString("description");
                String filename = rs.getString("filename");
                Doc doc = new Doc(id, creator, timestamp, description, filename);
                docList.add(doc);
            }
        }
        return docList.elements();
    }

    public static boolean insertDoc(String id, String creator, Timestamp timestamp, String description, String filename) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "INSERT INTO doc_info (id, creator, timestamp, description, filename) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, creator);
            stmt.setTimestamp(3, timestamp);
            stmt.setString(4, description);
            stmt.setString(5, filename);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean updateDoc(String id, String creator, Timestamp timestamp, String description, String filename) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "UPDATE doc_info SET creator = ?, timestamp = ?, description = ?, filename = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, creator);
            stmt.setTimestamp(2, timestamp);
            stmt.setString(3, description);
            stmt.setString(4, filename);
            stmt.setString(5, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean deleteDoc(String id) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "DELETE FROM doc_info WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static AbstractUser searchUser(String name) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "SELECT * FROM user_info WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    return createUser(name, password, role);
                }
            }
        }
        return null;
    }

    public static AbstractUser searchUser(String name, String password) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "SELECT * FROM user_info WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    return createUser(name, password, role);
                }
            }
        }
        return null;
    }

    public static boolean insertUser(String name, String password, String role) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "INSERT INTO user_info (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setString(3, role);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean updateUser(String name, String password, String role) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "UPDATE user_info SET password = ?, role = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, password);
            stmt.setString(2, role);
            stmt.setString(3, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean deleteUser(String name) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "DELETE FROM user_info WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static void disconnectFromDataBase() {
        if (connectToDB) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connectToDB = false;
        }
    }

    private static AbstractUser createUser(String name, String password, String role) {
        switch (ROLE_ENUM.valueOf(role.toLowerCase())) {
            case administrator:
                return new Administrator(name, password, role);
            case operator:
                return new Operator(name, password, role);
            default:
                return new Browser(name, password, role);
        }
    }

    enum ROLE_ENUM {
        administrator("administrator"), operator("operator"), browser("browser");

        private final String role;

        ROLE_ENUM(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }
    public static Enumeration<AbstractUser> listUser() throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        String query = "SELECT * FROM user_info";
        Vector<AbstractUser> userList = new Vector<AbstractUser>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query);){
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                AbstractUser user = DataProcessing.createUser(username, password, role);
                userList.add(user);
            }
        }
        return userList.elements();
    }

}