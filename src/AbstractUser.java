import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Scanner;

public abstract class AbstractUser implements Serializable {
    protected String name;
    protected String password;
    protected String role;
    protected static String uploadpath = "uploadfile/";
    protected static String downloadpath = "downloadfile/";

    public AbstractUser(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public abstract void showMenu();

    public boolean changeSelfInfo(String password) throws SQLException {
        if (DataProcessing.updateUser(name, password, role)) {
            this.password = password;
            System.out.println("修改成功");
            return true;
        } else {
            return false;
        }
    }

    public static boolean downloadFile(String id) throws SQLException, IOException {
        byte[] buffer = new byte[1024];
        Doc doc = DataProcessing.searchDoc(id);

        if (doc == null) {
            return false;
        }

        File tempFile = new File(uploadpath + doc.getFilename());
        String filename = tempFile.getName();

        try (BufferedInputStream infile = new BufferedInputStream(new FileInputStream(tempFile));
             BufferedOutputStream targetfile = new BufferedOutputStream(new FileOutputStream(downloadpath + filename))) {
            int byteRead;
            while ((byteRead = infile.read(buffer)) != -1) {
                targetfile.write(buffer, 0, byteRead);
            }
        }
        return true;
    }
    public static void showFileList() throws SQLException {
        Enumeration<Doc> e = DataProcessing.listDoc();
        Doc doc;
        while (e.hasMoreElements()) {
            doc = e.nextElement();
            System.out.println("Id:" + doc.getId() + "\t Creator:" + doc.getCreator() + "\t Time:" + doc.getTimestamp() + "\t Filename:" + doc.getFilename());
            System.out.println("Description:" + doc.getDescription());
        }

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static void uploadFile(Scanner scanner) {
        System.out.println("********上传文件********");
        System.out.print("输入文件ID: ");
        String ID = scanner.next();
        System.out.print("输入文件路径: ");
        String dir = scanner.next();
        System.out.print("输入文件描述: ");
        String description = scanner.next();

        byte[] buffer = new byte[1024];
        File temp_file = new File(dir);
        String filename = temp_file.getName();

        if (!temp_file.exists() || !temp_file.isFile()) {
            System.out.println("上传失败：文件不存在或不是一个文件");
            return;
        }

        try {
            if (DataProcessing.searchDoc(ID) != null) {
                System.out.println("上传失败：文件ID重复");
                return;
            }
        } catch (SQLException e) {
            System.out.println("数据库查询错误：" + e.getMessage());
            return;
        }

        if (uploadpath == null || !(new File(uploadpath).isDirectory())) {
            System.out.println("上传失败：目标路径不存在");
            return;
        }

        try (BufferedInputStream infile = new BufferedInputStream(new FileInputStream(temp_file));
             BufferedOutputStream targetfile = new BufferedOutputStream(new FileOutputStream(uploadpath + filename))) {
            int byteRead;
            while ((byteRead = infile.read(buffer)) != -1) {
                targetfile.write(buffer, 0, byteRead);
            }
            System.out.println("上传成功");
        } catch (IOException e) {
            System.out.println("上传失败：" + e.getMessage());
        }
    }

    public Object toJSON() {
        return new JSONObject() {{
            put("name", name);
            put("password", password);
            put("role", role);
        }};
    }
}