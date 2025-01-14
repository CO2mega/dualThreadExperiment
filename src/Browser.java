import java.util.Scanner;

public class Browser extends User {
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
                case 1:
                case 2:
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
