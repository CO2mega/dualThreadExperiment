import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;

public class DataProcessing {

    static Hashtable<String, AbstractUser> users;
    static Hashtable<String, Doc> docs;
    private static boolean connectToDB = true;

    static {
        users = new Hashtable<>();
        users.put("rose", new Browser("rose", "123", "browser"));
        users.put("jack", new Operator("jack", "123", "operator"));
        users.put("kate", new Administrator("kate", "123", "administrator"));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        docs = new Hashtable<>();
        docs.put("0001", new Doc("0001", "jack", timestamp, "Doc Source Java", "Doc.java"));
        docs.put("0002", new Doc("0002", "rose", timestamp, "Doc Source Python", "Doc.py"));

        init();
    }

    public static void init() {
        String projectPath = System.getProperty("user.dir");
        String docsFilePath = projectPath + File.separator + "Doc.out";
        String usersFilePath = projectPath + File.separator + "Users.out";

        try {
            docs = readHashtable(docsFilePath);
            users = readHashtable(usersFilePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static <K, V> Hashtable<K, V> readHashtable(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Hashtable<K, V>) in.readObject();
        }
    }

    public static void serializeDocs() {
        String projectPath = System.getProperty("user.dir");
        String docsFilePath = projectPath + File.separator + "Doc.out";
        String usersFilePath = projectPath + File.separator + "Users.out";

        try (ObjectOutputStream docsOut = new ObjectOutputStream(new FileOutputStream(docsFilePath));
             ObjectOutputStream usersOut = new ObjectOutputStream(new FileOutputStream(usersFilePath))) {
            docsOut.writeObject(docs);
            usersOut.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Doc searchDoc(String id) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        return docs.get(id);
    }

    public static Enumeration<Doc> listDoc() throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        return docs.elements();
    }

    public static boolean insertDoc(String id, String creator, Timestamp timestamp, String description, String filename) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        if (docs.containsKey(id)) {
            return false;
        } else {
            Doc doc = new Doc(id, creator, timestamp, description, filename);
            docs.put(id, doc);
            return true;
        }
    }

    public static AbstractUser searchUser(String name) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        return users.get(name);
    }

    public static AbstractUser searchUser(String name, String password) throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        AbstractUser user = users.get(name);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static Enumeration<AbstractUser> listUser() throws SQLException {
        if (!connectToDB) {
            throw new SQLException("Not Connected to Database");
        }
        return users.elements();
    }

    public static boolean updateUser(String name, String password, String role) throws SQLException {
        if (users.containsKey(name)) {
            AbstractUser user;
            switch (ROLE_ENUM.valueOf(role.toLowerCase())) {
                case administrator:
                    user = new Administrator(name, password, role);
                    break;
                case operator:
                    user = new Operator(name, password, role);
                    break;
                default:
                    user = new Browser(name, password, role);
            }
            users.put(name, user);
            return true;
        } else {
            return false;
        }
    }

    public static boolean insertUser(String name, String password, String role) throws SQLException {
        if (users.containsKey(name)) {
            return false;
        } else {
            AbstractUser user;
            switch (ROLE_ENUM.valueOf(role.toLowerCase())) {
                case administrator:
                    user = new Administrator(name, password, role);
                    break;
                case operator:
                    user = new Operator(name, password, role);
                    break;
                default:
                    user = new Browser(name, password, role);
            }
            users.put(name, user);
            return true;
        }
    }

    public static boolean deleteUser(String name) throws SQLException {
        if (users.containsKey(name)) {
            users.remove(name);
            return true;
        } else {
            return false;
        }
    }

    public static void disconnectFromDataBase() {
        if (connectToDB) {
            connectToDB = false;
        }
    }

    enum ROLE_ENUM {
        administrator("administrator"),
        operator("operator"),
        browser("browser");

        private final String role;

        ROLE_ENUM(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }
}