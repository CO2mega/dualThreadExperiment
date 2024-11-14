import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Enumeration;
import java.util.Scanner;

public class Administrator extends AbstractUser {
    public Administrator(String name, String password, String role) {
        super(name, password, role);
    }

    @Override
    public void showMenu() {
        int choice;
        boolean exit = false;
        do {
            System.out.println("****欢迎进入系统管理员菜单****");
            System.out.println("    1.新增用户");//1
            System.out.println("    2.删除用户");//1
            System.out.println("    3.修改用户");//1
            System.out.println("    4.用户列表");
            System.out.println("    5.下载档案");
            System.out.println("    6.档案列表");
            System.out.println("    7.修改密码");//1
            System.out.println("    8.退出");//1
            System.out.println("*************************");
            System.out.println("请选择菜单：");
            Scanner scanner = new Scanner(System.in);
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("输入你要新建的用户名：");
                    String name = scanner.next();
                    System.out.println("输入新建账户的密码：");
                    String password = scanner.next();
                    System.out.println("输入新用户的角色：");
                    String role = scanner.next();
                    try {
                        DataProcessing.insertUser(name, password, role);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 2:
                    delUser(scanner);
                    break;
                case 3:
                    changeUserInfo(scanner);
                    break;

                case 4:
                    listUser();
                    break;
                case 5:
                    System.out.println("请输入你要下载的文件id");
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

                case 6:
                case 7:
                    System.out.println("请输入你要修改的密码");
                    String newPass = scanner.next();
                    setPassword(newPass);
                    System.out.println("修改密码后请重新登录");
                    exit = true;
                    break;
                case 8:
                    exit = true;
                default:
                    System.out.println("输入错误，请重试");
            }
        } while (!exit);
        this.exitSystem();
    }

    private void addUser(Scanner scanner) {
        AbstractUser newuser = null;
        System.out.println("请输入你要增加的用户名");
        newuser.setName(scanner.next());
        System.out.println("请输入该用户的密码");
        newuser.setPassword(scanner.next());
        System.out.println("请输入该用户的身份\n1.管理员\n2.录入人员\n3.浏览人员");
        int roleChoice = scanner.nextInt();
        if (roleChoice == 1) newuser.setRole("administrator");
        else if (roleChoice == 2) newuser.setRole("operator");
        else if (roleChoice == 3) newuser.setRole("browser");
        try {
            if (DataProcessing.insertUser(newuser.getName(), newuser.getPassword(), newuser.getRole()))
                System.out.println("用户已新增");
            else System.out.println("已存在该用户");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void delUser(Scanner scanner) {
        System.out.print("请输入你需要删除的用户：");
        String delUser = scanner.next();
        System.out.printf("你要删除的是：%s,确定请按1，按其他退出", delUser);
        if (scanner.next().equals("1")) {
            try {
                if (DataProcessing.deleteUser(delUser)) System.out.println("成功删除该用户");
                else System.out.println("删除失败，请确认是否存在该用户");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void changeUserInfo(Scanner scanner) {
        System.out.println("请输入你要修改的用户名称");
        AbstractUser changeuser = null;
        changeuser.setName(scanner.next());
        System.out.println("请输入你要修改的用户密码");
        changeuser.setPassword(scanner.next());
        System.out.println("请输入你要修改的用户角色");
        changeuser.setRole(scanner.next());
        try {
            if (DataProcessing.updateUser(changeuser.getName(), changeuser.getPassword(), changeuser.getRole()))
                System.out.println("修改完成");
            else System.out.println("不存在该用户");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void listUser() {
        System.out.println("以下为用户列表");
        Enumeration<AbstractUser> userEnumeration = null;
        try {
            userEnumeration = DataProcessing.listUser();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("姓名   密码   角色");
        while (userEnumeration.hasMoreElements()) {
            AbstractUser user = userEnumeration.nextElement();
            System.out.printf("%s,%s,%s\n", user.getName(), user.getPassword(), user.getRole());

        }
    }
}
