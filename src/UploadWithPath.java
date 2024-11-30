import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import java.io.File;
import java.sql.Timestamp;
import java.util.Vector;

public class UploadWithPath extends JFrame {
    String upload_path = ".\\uploadfile\\";
    String download_path = ".\\downloadfile\\";
    private final String name;
    private  Vector<Docs> docsVector;
    private MainFrame mainFrame;
    public UploadWithPath(MainFrame mainframe, String name, Vector<Docs> docsVector) {
        this.name = name;
        this.docsVector = docsVector;
        this.mainFrame = mainframe;
        initComponents();

    }

    private void StartAction() {
        String ID = IdInput.getText();
        String dir = DirInput.getText();
        String description = DiscriptionInput.getText();
        File file = new File(dir);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "文件不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }else  if (!file.isFile()) {
            JOptionPane.showMessageDialog(this, "该路径不是一个文件", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }else if (!file.canRead()) {
            JOptionPane.showMessageDialog(this, "文件不可读取", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        docsVector.add(new Docs(ID, name, new Timestamp(System.currentTimeMillis()), description, dir));
        mainFrame.uploadQueue();
        JOptionPane.showMessageDialog(this, "已添加到上传队列");
        IdInput.setText("");
        DirInput.setText("");
        DiscriptionInput.setText("");
    }

    private void CancelMouseClicked(MouseEvent e) {

        this.dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        InfoLable = new JLabel();
        IdInput = new JTextField();
        IdLable = new JLabel();
        Start = new JButton();
        WarningLable = new JLabel();
        DirLable = new JLabel();
        DirInput = new JTextField();
        DiscriptionLable = new JLabel();
        DiscriptionInput = new JTextField();
        Cancel = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();

        //---- InfoLable ----
        InfoLable.setText("\u4e0a\u4f20\u6587\u4ef6");

        //---- IdLable ----
        IdLable.setText("\u6587\u4ef6ID");

        //---- Start ----
        Start.setText("\u4e0a\u4f20");
        Start.addActionListener(e -> StartAction());

        //---- DirLable ----
        DirLable.setText("\u8def\u5f84");

        //---- DiscriptionLable ----
        DiscriptionLable.setText("\u63cf\u8ff0");

        //---- Cancel ----
        Cancel.setText("\u53d6\u6d88");
        Cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CancelMouseClicked(e);
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(InfoLable)
                    .addContainerGap(254, Short.MAX_VALUE))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(WarningLable, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Start)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Cancel)
                            .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
                                    .addComponent(IdLable)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(IdInput, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addGap(0, 13, Short.MAX_VALUE)
                                    .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addComponent(DirLable)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(DirInput, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                            .addComponent(DiscriptionLable)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(DiscriptionInput, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)))))
                            .addGap(24, 24, 24))))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(InfoLable)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(IdLable)
                        .addComponent(IdInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(DirInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(DirLable))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(DiscriptionInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(DiscriptionLable))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(Start)
                        .addComponent(WarningLable)
                        .addComponent(Cancel))
                    .addGap(16, 16, 16))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel InfoLable;
    private JTextField IdInput;
    private JLabel IdLable;
    private JButton Start;
    private JLabel WarningLable;
    private JLabel DirLable;
    private JTextField DirInput;
    private JLabel DiscriptionLable;
    private JTextField DiscriptionInput;
    private JButton Cancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}