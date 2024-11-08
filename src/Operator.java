import java.util.Scanner;

public class Operator extends User {

    public Operator(String name, String password, String role) {
        super(name, password, role);
    }

    @Override
    public void showMenu() {
        int choice;
        boolean exit = false;
        do {
            System.out.println("****欢迎进入档案录入人员菜单****");
            System.out.println("    1.上传档案");
            System.out.println("    2.下载档案");
            System.out.println("    3.文件列表");
            System.out.println("    4.修改密码");
            System.out.println("    5.退出");
            System.out.println("***************************");
            System.out.println("请选择菜单：");
            Scanner scanner = new Scanner(System.in);
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    uploadFile(scanner);
                case 2:
                case 3:
                case 4:
                    System.out.println("请输入你要修改的密码");
                    String newpass = scanner.next();
                    setPassword(newpass);
                    System.out.println("修改密码后请重新登录");
                    exit = true;
                    break;

                case 5:
                    exit = true;
                default:
                    System.out.println("输入错误，请重试");
            }
        } while (!exit);
        System.out.println("退出登录");
    }

    private void uploadFile(Scanner scanner) {
        System.out.println("上传成功");
    }
}