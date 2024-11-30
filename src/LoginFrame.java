import javax.swing.*;
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

// 为密码框添加KeyListener
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
                super.mouseClicked(e);
                DataProcessing.serializeDocs();
                System.exit(0);

            }
        });


    }

    private void disposeLoginFrame() {
        SwingUtilities.getWindowAncestor(OuterPanel).dispose();
    }

    private void performLogin() {
        String account, password;
        AbstractUser currentUser = null;
        account = UserNameTextField.getText();
        password = String.valueOf(UserPasswordField1.getPassword());
        try {
            currentUser = DataProcessing.searchUser(account, password);
        } catch (SQLException er) {
            throw new RuntimeException(er);
        }
        if (currentUser != null) {
            JOptionPane.showMessageDialog(null, "登录成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            try {
                JFrame mainFrame = new MainFrame(currentUser);
               // mainFrame.setTitle("档案管理系统");

                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            } catch (Exception f) {
                f.printStackTrace();
            }
            disposeLoginFrame();
        } else {
            JOptionPane.showMessageDialog(null, "用户名或密码错误！", "提示", JOptionPane.ERROR_MESSAGE);
            UserNameTextField.setText("");
            UserPasswordField1.setText("");
        }
    }

}
