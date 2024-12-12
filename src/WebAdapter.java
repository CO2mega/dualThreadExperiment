import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class WebAdapter {

    private static Connection connection;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started on port 12345");
            DataProcessing.connectToDatabase();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String requestLine;
            while ((requestLine = in.readLine()) != null) {
                System.out.println("Request received: " + requestLine);

                try {
                    // 解析请求为 JSON 对象
                    JSONObject request = JSONObject.parseObject(requestLine);
                    String type = request.getString("type");
                    JSONObject response = new JSONObject();

                    switch (type) {
                        case "LIST_DOCS":
                            JSONArray docList = listDocs();
                            response.put("status", "success");
                            response.put("data", docList);
                            break;
                        case "CONNECT_TO_DATABASE":
                            DataProcessing.connectToDatabase();
                            response.put("status", "success");
                            break;
                        case "SEARCH_DOC":
                            String id = request.getJSONObject("payload").getString("id");
                            System.out.println("id:" + id);
                            Doc doc = DataProcessing.searchDoc(id);
                            if (doc != null) {
                                response.put("status", "success");
                                response.put("data", doc.toJSON());
                            } else {
                                response.put("status", "error");
                                response.put("message", "Document not found");
                            }
                            break;
                        case "INSERT_DOC":
                            JSONObject payload = request.getJSONObject("payload");
                            String creator = payload.getString("creator");
                            Timestamp timestamp = Timestamp.valueOf(payload.getString("timestamp"));
                            String description = payload.getString("description");
                            String filename = payload.getString("filename");
                            String idi = payload.getString("id");
                            if (DataProcessing.insertDoc(idi, creator, timestamp, description, filename)) {
                                response.put("status", "success");
                            } else {
                                response.put("status", "error");
                                response.put("message", "Failed to insert document");
                            }
                            break;
                        case "SEARCH_USER":
                            String name = request.getJSONObject("payload").getString("name");
                            System.out.println("name:" + name);
                            AbstractUser user = DataProcessing.searchUser(name);
                            if (user != null) {
                                response.put("status", "success");
                                response.put("data", user.toJSON());
                            } else {
                                response.put("status", "error");
                                response.put("message", "User not found");
                            }
                            break;
                        case "INSERT_USER":
                            JSONObject payload1 = request.getJSONObject("payload");
                            String name1 = payload1.getString("name");
                            String password = payload1.getString("password");
                            String role = payload1.getString("role");
                            if (DataProcessing.insertUser(name1, password, role)) {
                                response.put("status", "success");
                            } else {
                                response.put("status", "error");
                                response.put("message", "Failed to insert user");
                            }
                            break;
                        case "UPDATE_USER":
                            JSONObject payload2 = request.getJSONObject("payload");
                            String name2 = payload2.getString("name");
                            String password2 = payload2.getString("password");
                            String role2 = payload2.getString("role");
                            if (DataProcessing.updateUser(name2, password2, role2)) {
                                response.put("status", "success");
                            } else {
                                response.put("status", "error");
                                response.put("message", "Failed to update user");
                            }
                            break;
                            case "DELETE_USER":
                            String name3 = request.getJSONObject("payload").getString("name");
                            if (DataProcessing.deleteUser(name3)) {
                                response.put("status", "success");
                            } else {
                                response.put("status", "error");
                                response.put("message", "Failed to delete user");
                            }
                            break;
                        default:
                            response.put("status", "error");
                            response.put("message", "Invalid request type: " + type);
                            break;
                    }

                    // 发送响应
                    out.write(response.toJSONString() + "\n");
                    out.flush();
                    System.out.println("Response sent: " + response.toJSONString());

                } catch (Exception e) {
                    // 捕获处理请求时的异常，向客户端发送错误信息
                    JSONObject errorResponse = new JSONObject();
                    errorResponse.put("status", "error");
                    errorResponse.put("message", "Failed to process request: " + e.getMessage());
                    out.write(errorResponse.toJSONString() + "\n");
                    out.flush();
                    System.err.println("Error processing request: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
    }


    private static JSONArray listDocs() throws SQLException {
        String query = "SELECT * FROM doc_info";
        JSONArray docList = new JSONArray();
        try (Statement stmt = DataProcessing.connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                JSONObject doc = new JSONObject();
                doc.put("id", rs.getString("id"));
                doc.put("creator", rs.getString("creator"));
                doc.put("timestamp", rs.getTimestamp("timestamp"));
                doc.put("description", rs.getString("description"));
                doc.put("filename", rs.getString("filename"));
                docList.add(doc);
            }
        }
        return docList;
    }
}
