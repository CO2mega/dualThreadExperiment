import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Browser extends AbstractUser {
    public Browser(String name, String password, String role) {
        super(name, password, role);
    }

    @Override
    public void showMenu() {
        int choice;
        boolean exit = false;
        do {
            System.out.println("****欢迎进入档案浏览人员菜单****");
            System.out.println("    1.下载文件");
            System.out.println("    2.文件列表");
            System.out.println("    3.修改密码");
            System.out.println("    4.退出");
            System.out.println("**************************");
            System.out.println("请选择菜单：");
            Scanner scanner = new Scanner(System.in);
            choice = scanner.nextInt();
            switch (choice) {
                case 1: System.out.println("请输入你要下载的文件id");
                    String fileName = scanner.next();
                    try {
                        boolean result = downloadFile(fileName);
                        if (result) {
                            System.out.println("文件下载成功！");
                        } else {
                            System.out.println("文件未找到或下载失败！");
                        }
                    }
                    // 捕获 SQLException
                    catch (SQLException e) {
                        System.err.println("数据库操作出错: " + e.getMessage());
                        e.printStackTrace();
                    }
                    // 捕获 IOException
                    catch (IOException e) {
                        System.err.println("文件操作过程中发生 IO 错误: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        AbstractUser.showFileList();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 3:
                    System.out.println("请输入你要修改的密码");
                    String newpass = scanner.next();
                    setPassword(newpass);
                    System.out.println("修改密码后请重新登录");
                    exit = true;
                    break;
                case 4:
                    exit = true;

                default:
                    System.out.println("输入错误，请重试");
            }
        } while (!exit);
        System.out.println("退出登录");
    }
}
