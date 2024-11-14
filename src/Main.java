import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        AbstractUser currentUser;
        while (!exit) {
            System.out.println("****欢迎进入档案系统****");
            System.out.println("       1.登录");
            System.out.println("       2.退出");
            System.out.println("*********************");
            System.out.println("请选择菜单：");

            int i = scanner.nextInt();

            switch (i) {
                case 1:
                    System.out.println("请输入姓名：");
                    String name = scanner.next();
                    System.out.println("请输入密码：");
                    String password = scanner.next();
                    try {
                        currentUser = DataProcessing.searchUser(name, password);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (currentUser != null) {
                        System.out.println("登录成功！");
                        currentUser.showMenu();
                    } else {
                        System.out.println("用户名或密码错误！");
                    }
                    break;
                case 2:
                    System.out.println("退出系统。");
                    exit = true;
                    break;
                default:
                    System.out.println("无效的选择！");
                    break;
            }
        }

        scanner.close();
        System.exit(0);
    }
}