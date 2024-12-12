import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Vector;

public class ClientDataProcessing {

    private static Socket socket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {//测试环境
        try {
            initializeConnection();     //initializeConnection() Pass

        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            // Initialize connection
//            initializeConnection();
//
//            // Example usage
//            Enumeration<Doc> docs = listDoc();                    listDoc() Pass
//            while (docs.hasMoreElements()) {
//                System.out.println(docs.nextElement());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            System.out.println(searchDoc("1"));                   //searchDoc() Pass
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        try {
//            System.out.println(insertDoc("117", "1", Timestamp.valueOf("2021-06-01 00:00:00"), "1", "1"));
//        }                                                                                                         // insertDoc() Pass
//        catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

//        try {
//            AbstractUser user = searchUser("kate", "123");                                              //searchUser() Pass
//            System.out.println("name: " + user.name + " password: " + user.password + " role: " + user.role);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

//            try {
//                System.out.println(insertUser("pit", "123", "admin"));                                      //insertUser() Pass
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }

//        try {
//            System.out.println(updateUser("pit", "114514", "admin"));                                      //updateUser() Pass
//        }catch (SQLException e){
//            throw new RuntimeException(e);
//        }
//        try {
//            System.out.println(deleteUser("pit"));                                                          //deleteUser() Pass
//        }catch (SQLException e){
//            throw new RuntimeException(e);
//        }

        closeConnection();

    }

    private static void initializeConnection() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connection initialized.");
        }
//        try {
//            connectToDatabase();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println("Database connected.");
//        }
        //服务器端已经连接数据库

    }

    public static Enumeration<Doc> listDoc() throws IOException {
        sendRequest("LIST_DOCS", null);
        String responseLine = in.readLine();
        System.out.println("Raw response: " + responseLine);

        JSONObject response = JSON.parseObject(responseLine);
        if ("success".equals(response.getString("status"))) {
            JSONArray data = response.getJSONArray("data");
            Vector<Doc> docList = new Vector<>();
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonDoc = data.getJSONObject(i);
                Doc doc = new Doc(jsonDoc.getString("id"), jsonDoc.getString("creator"), Timestamp.valueOf(jsonDoc.getString("timestamp")), jsonDoc.getString("description"), jsonDoc.getString("filename"));
                docList.add(doc);
            }
            return docList.elements();
        } else {
            throw new IOException("Server error: " + response.getString("message"));
        }
    }

    public static Doc searchDoc(String id) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("id", id);
        sendRequest("SEARCH_DOC", payload);
        String responseLine = in.readLine();
        System.out.println("Raw response: " + responseLine);

        JSONObject response = JSON.parseObject(responseLine);
        if ("success".equals(response.getString("status"))) {
            JSONObject data = response.getJSONObject("data");
            return new Doc(data.getString("id"), data.getString("creator"), Timestamp.valueOf(data.getString("timestamp")), data.getString("description"), data.getString("filename"));
        } else {
            throw new IOException("Server error: " + response.getString("message"));
        }
    }

    public static boolean insertDoc(String id, String creator, Timestamp timestamp, String description, String filename) throws SQLException {
        JSONObject payload = new JSONObject();
        payload.put("id", id);
        payload.put("creator", creator);
        payload.put("timestamp", timestamp.toString());
        payload.put("description", description);
        payload.put("filename", filename);
        try {
            sendRequest("INSERT_DOC", payload);
            String responseLine = in.readLine();
            System.out.println("Raw response: " + responseLine);

            JSONObject response = JSON.parseObject(responseLine);
            if ("success".equals(response.getString("status"))) {
                return true;
            } else {
                throw new SQLException("Server error: " + response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AbstractUser searchUser(String name, String password) throws SQLException {
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("password", password);
        try {
            sendRequest("SEARCH_USER", payload);
            String responseLine = in.readLine();
            System.out.println("Raw response: " + responseLine);

            JSONObject response = JSON.parseObject(responseLine);
            if ("success".equals(response.getString("status"))) {
                JSONObject data = response.getJSONObject("data");
                return new AbstractUser(data.getString("name"), data.getString("password"), data.getString("role")) {
                    @Override
                    public void showMenu() {
                    }
                };
            } else {
                throw new SQLException("Server error: " + response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertUser(String name, String password, String role) throws SQLException {
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("password", password);
        payload.put("role", role);
        try {
            sendRequest("INSERT_USER", payload);
            String responseLine = in.readLine();
            System.out.println("Raw response: " + responseLine);

            JSONObject response = JSON.parseObject(responseLine);
            if ("success".equals(response.getString("status"))) {
                return true;
            } else {
                throw new SQLException("Server error: " + response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUser(String name, String password, String role) throws SQLException {
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("password", password);
        payload.put("role", role);
        try {
            sendRequest("UPDATE_USER", payload);
            String responseLine = in.readLine();
            System.out.println("Raw response: " + responseLine);

            JSONObject response = JSON.parseObject(responseLine);
            if ("success".equals(response.getString("status"))) {
                return true;
            } else {
                throw new SQLException("Server error: " + response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(String name) throws SQLException{
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        try {
            sendRequest("DELETE_USER", payload);
            String responseLine = in.readLine();
            System.out.println("Raw response: " + responseLine);

            JSONObject response = JSON.parseObject(responseLine);
            if ("success".equals(response.getString("status"))) {
                return true;
            } else {
                throw new SQLException("Server error: " + response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sendRequest(String type, JSONObject payload) throws IOException {
        JSONObject request = new JSONObject();
        request.put("type", type);
        if (payload != null) {
            request.put("payload", payload);
        }
        out.write(request.toJSONString() + "\n");
        out.flush();
        System.out.println("Request sent: " + request.toJSONString());
    }

    public static void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connectToDatabase() throws IOException {
        sendRequest("CONNECT_TO_DATABASE", null);
    }
}
