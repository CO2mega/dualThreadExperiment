import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * TODO 抽象用户类，为各用户子类提供模板
 *
 * @author gongjing
 * @date 2016/10/13
 */
public abstract class AbstractUser {
    static String uploadpath = "C:\\Users\\1\\Desktop\\dualExperiment\\uploadfile\\";
    String downloadpath = "C:\\Users\\1\\Desktop\\dualExperiment\\downloadfile\\";
    private String name;
    private String password;
    private String role;

    AbstractUser(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * TODO 展示档案文件列表
     *
     * @param
     * @return void
     * @throws SQLException
     */
    public static void showFileList() throws SQLException {
        Enumeration<Doc> e = DataProcessing.listDoc();
        Doc doc;
        while (e.hasMoreElements()) {
            doc = e.nextElement();
            System.out.println("Id:" + doc.getId() + "\t Creator:" + doc.getCreator() + "\t Time:" + doc.getTimestamp() + "\t Filename:" + doc.getFilename());
            System.out.println("Description:" + doc.getDescription());
        }

    }

    /**
     * TODO 修改用户自身信息
     *
     * @param password 口令
     * @return boolean 修改是否成功
     * @throws SQLException
     */
    public boolean changeSelfInfo(String password) throws SQLException {
        if (DataProcessing.updateUser(name, password, role)) {
            this.password = password;
            System.out.println("修改成功");
            return true;
        } else {
            return false;
        }
    }

    /**
     * TODO 下载档案文件
     *
     * @param id 档案编号
     * @return boolean 下载是否成功
     * @throws SQLException,IOException
     */
    public boolean downloadFile(String id) throws SQLException, IOException {
//boolean result=false;
        byte[] buffer = new byte[1024];
        Doc doc = DataProcessing.searchDoc(id);

        if (doc == null) {
            return false;
        }

        File tempFile = new File(uploadpath + doc.getFilename());
        String filename = tempFile.getName();

        BufferedInputStream infile = new BufferedInputStream(new FileInputStream(tempFile));
        BufferedOutputStream targetfile = new BufferedOutputStream(new FileOutputStream(downloadpath + filename));

        while (true) {
            int byteRead = infile.read(buffer);
            if (byteRead == -1) {
                break;
            }
            targetfile.write(buffer, 0, byteRead);
        }
        infile.close();
        targetfile.close();

        return true;
    }

    /**
     * TODO 展示菜单，需子类加以覆盖
     *
     * @param
     * @return void
     * @throws
     */
    public abstract void showMenu();

    /**
     * TODO 退出系统
     *
     * @param
     * @return void
     * @throws
     */
    public void exitSystem() {
        System.out.println("系统退出, 谢谢使用 ! ");
        System.exit(0);
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
        Scanner sc = new Scanner(System.in);
        System.out.print("输入文件ID: ");
        String ID = sc.next();
        System.out.print("输入文件路径: ");
        String dir = sc.next();
        System.out.print("输入文件描述: ");
        String description = sc.next();

        byte[] buffer = new byte[1024];
        File temp_file = new File(dir);
        String filename = temp_file.getName();
        try {
            if (DataProcessing.searchDoc(ID)!=null) {
                System.out.println("上传失败：文件ID重复");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            BufferedInputStream infile = new BufferedInputStream(new FileInputStream(temp_file));
            BufferedOutputStream targetfile = new BufferedOutputStream(new FileOutputStream(uploadpath + filename));
            while (true) {
                int byteRead = infile.read(buffer);
                if (byteRead == -1) break;
                targetfile.write(buffer, 0, byteRead);
            }
            infile.close();
            targetfile.close();
        } catch (IOException e) {
            System.out.println("上传失败");
            return;
        }
        System.out.println("上传成功");
    }

}