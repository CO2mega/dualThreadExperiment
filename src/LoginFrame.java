import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class LoginFrame {
    public JPanel OuterPanel;
    private JPanel InnerPanel_1;
    private JPanel InnerPanel_2;
    private JPanel InnerPanel_3;
    private JTextField UserNameTextField;
    private JPasswordField UserPasswordField1;
    private JButton LoginButton;
    private JButton ExitButton;

    public LoginFrame() {
        LoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                performLogin();
            }
        });

        UserPasswordField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        LoginButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        ExitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //DataProcessing.serializeDocs();
                System.exit(0);
            }
        });
    }

    private void disposeLoginFrame() {
        SwingUtilities.getWindowAncestor(OuterPanel).dispose();
    }

    private void performLogin() {
        String account = UserNameTextField.getText();
        String password = String.valueOf(UserPasswordField1.getPassword());

        // 创建“正在连接数据库”的对话框
        JDialog connectingDialog = new JDialog();
        connectingDialog.setTitle("连接中");
        connectingDialog.setSize(300, 150);
        connectingDialog.setLocationRelativeTo(null);
        connectingDialog.setLayout(new BorderLayout());

        // 添加提示文字
        JLabel label = new JLabel("正在连接数据库，请稍候...", SwingConstants.CENTER);
        connectingDialog.add(label, BorderLayout.CENTER);

        // 添加滚动的进度条
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // 设置为不确定模式
        connectingDialog.add(progressBar, BorderLayout.SOUTH);

        connectingDialog.setModal(false);
        connectingDialog.setVisible(true);

        // 使用线程连接数据库
        Thread connectionThread = new Thread(() -> {
            DataProcessing.connectToDatabase(); // 调用数据库连接方法
            SwingUtilities.invokeLater(() -> {
                connectingDialog.dispose(); // 关闭对话框
                if (DataProcessing.connectToDB) {
                    // 数据库连接成功
                    try {
                        AbstractUser currentUser = DataProcessing.searchUser(account, password);
                        if (currentUser != null) {
                            JOptionPane.showMessageDialog(null, "登录成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            openMainFrame(currentUser);
                            disposeLoginFrame();
                        } else {
                            JOptionPane.showMessageDialog(null, "用户名或密码错误！", "提示", JOptionPane.ERROR_MESSAGE);
                            UserNameTextField.setText("");
                            UserPasswordField1.setText("");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "用户验证失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "数据库连接失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        // 设置超时处理
        Timer timeoutTimer = new Timer(5000, e -> {
            if (connectionThread.isAlive()) {
                connectionThread.interrupt(); // 中断线程
                connectingDialog.dispose(); // 关闭对话框
                JOptionPane.showMessageDialog(null, "连接超时，请重试！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        timeoutTimer.setRepeats(false); // 只运行一次
        timeoutTimer.start();

        connectionThread.start(); // 启动线程
    }

    private void openMainFrame(AbstractUser currentUser) {
        try {
            JFrame mainFrame = new MainFrame(currentUser);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
