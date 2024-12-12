

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainGUI {
    public static void main(String[] args) {
        //打开登录窗口
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FlatIntelliJLaf.setup();
                    FlatIntelliJLaf.isUseNativeWindowDecorations();
                    UIManager.put( "TabbedPane.selectedBackground", Color.white );
                    JFrame frame = new JFrame("LoginFrame");
                    frame.setContentPane(new LoginFrame().OuterPanel);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 退出应用程序
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                //在此处添加退出应用程序前需完成工作，如：关闭网络连接、关闭数据库连接等
               // DataProcessing.disconnectFromDataBase();
                System.out.println("应用程序退出！");
            }
        });
    }

    public static void showMessage(Component component, String msg, String title) {
        JOptionPane.showMessageDialog(component, msg, title, JOptionPane.YES_NO_OPTION);
    }

    public static int showConfirmMessage(Component component, String msg, String title) {
        return JOptionPane.showConfirmDialog(component, msg, title, JOptionPane.OK_CANCEL_OPTION);
    }
}