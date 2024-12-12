import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

class Docs implements Serializable {
    public String id;
    public String creator;
    public Timestamp timestamp;
    public String description;
    public String filename;
    public String dir;

    //dir为路径+文件名，仅显示是表示为父级文件夹
    public Docs(String id, String creator, Timestamp timestamp, String description, String dir) {
        this.id = id;
        this.creator = creator;
        this.timestamp = timestamp;
        this.description = description;
        this.dir = dir;
        this.filename = new File(dir).getName();
    }
}

public class MainFrame extends JFrame {
    private static Vector<Docs> UploadQueuedDocs = new Vector<>();
    private final AbstractUser user;
    private int CodeGen;
    private Vector<Doc> cachedDocs = new Vector<>();
    private Vector<AbstractUser> cachedUsers = new Vector<>();
    private int UserCount = 0;
    private String[] roles = {"administrator", "operator", "browser"};
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JMenuBar menuBar1;
    private JMenu UserName;
    private JMenuItem ROLE;
    private JMenuItem TimeNow;
    private JMenuItem LogoutBut;
    private JMenuItem ExitBut;
    private JTabbedPane tabbedPane1;
    private JPanel ArchiveManage;
    private JTabbedPane ArchiveMngTbdp;
    private JPanel ListArchive;
    private JScrollPane scrollPane1;
    private JTable FileList1;
    private JPanel panel2;
    private JTextField FileSearchField;
    private JPanel UploadArchive;
    private JPanel panel9;
    private JMenuBar menuBar2;
    private JMenu uploadMenu;
    private JMenuItem addinGUI;
    private JMenuItem addinPath;
    private JScrollPane scrollPane3;
    private JTable UpLoadFileList;
    private JPanel panel8;
    private JButton Delchosen;
    private JButton clearQueue;
    private JButton Uploadbutton;
    private JPanel DownLoadArchive;
    private JScrollPane scrollPane2;
    private JTable FileList2;
    private JPanel panel7;
    private JButton choseAll;
    private JButton clearChoose;
    private JButton downloadButton;
    private JPanel UserManage;
    private JPanel ListArchive2;
    private JScrollPane scrollPane4;
    private JTable UserList;
    private JPanel panel3;
    private JTextField UserSearchField;
    private JButton AddUser;
    private JButton DelUser;
    private JButton SaveUserList;
    private JPanel MyProfile;
    private JTabbedPane MyPrrofileTbdP;
    private JPanel exitSys;
    private JLabel label6;
    private JSlider slider1;
    private JPanel changePasswd;
    private JButton button1;
    private JButton button2;
    private JPasswordField OriginPasswd;
    private JPasswordField NewPasswd;
    private JLabel label3;
    private JLabel label4;
    private JTextField CodeCMP;
    private JTextField CodeSource;
    private JLabel label5;
    private JPanel exitSys2;
    private JLabel label7;
    private JSlider slider2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    public MainFrame(AbstractUser user) {
        this.user = user;
        initComponents();
        if (Objects.equals(user.getRole(), "browser")) {
            tabbedPane1.remove(UserManage);
            tabbedPane1.revalidate();
            tabbedPane1.repaint();
            ArchiveMngTbdp.remove(UploadArchive);
            ArchiveMngTbdp.revalidate();
            ArchiveMngTbdp.repaint();
        } else if (Objects.equals(user.getRole(), "operator")) {
            tabbedPane1.remove(UserManage);
            tabbedPane1.revalidate();
            tabbedPane1.repaint();

            //ArchiveManage.setVisible(false);并非档案管理不可见，改为不可见上传
        }
        showFileListAction();
        downloadFileListAction();
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new Object[]{"ID", "文件名", "时间", "上传者", "文件描述", "目录"});
        UpLoadFileList.setModel(model);
        UserName.setText(user.getName());
        ROLE.setText("角色：" + user.getRole());
        ROLE.setEnabled(false);
        TimeNow.setText("当前时间：" + new Timestamp(System.currentTimeMillis()));
        TimeNow.setEnabled(false);
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                TimeNow.setText("当前时间：" + sdf.format(new Date()));
            }
        });

        // 启动定时器
        timer.start();
        UserListAction();
        FileSearchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "搜索");
        FileSearchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());
    }

    public void uploadQueue() {
        DefaultTableModel model = (DefaultTableModel) UpLoadFileList.getModel();
        for (Docs doc : UploadQueuedDocs) {
            model.addRow(new Object[]{doc.id, new File(doc.dir).getName(), doc.timestamp, doc.creator, doc.description, new File(doc.dir).getParent()});
        }
        UploadQueuedDocs.clear();
    }

    private void UserListAction() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"姓名", "密码", "角色"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (row < UserCount && column == 0 ||user.getName().equals(getValueAt(row, 0).toString())) {
                    return false;
                }
                return true;
            }
        };

        UserList.setModel(model);
        UserList.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(roles)));
        try {
            Enumeration<AbstractUser> e = DataProcessing.listUser();
            cachedUsers.clear(); // Clear the cache before populating
            while (e.hasMoreElements()) {
                AbstractUser user = e.nextElement();
                cachedUsers.add(user); // Add to cache
                UserCount++;
                model.addRow(new Object[]{user.getName(), user.getPassword(), user.getRole()});
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void exitSystemAction(ActionEvent e) {
        System.exit(0);
    }

    private void logoutAction(ActionEvent e) {
        this.dispose();
        try {
            JOptionPane.showMessageDialog(null, "已登出", "提示", JOptionPane.INFORMATION_MESSAGE);
            MainGUI.main(null);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void showFileListAction() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new Object[]{"ID", "文件名", "时间", "上传者", "文件描述"});
        FileList1.setModel(model);
        try {
            Enumeration<Doc> e = DataProcessing.listDoc();
            cachedDocs.clear(); // Clear the cache before populating
            while (e.hasMoreElements()) {
                Doc doc = e.nextElement();
                cachedDocs.add(doc); // Add to cache
                model.addRow(new Object[]{doc.getId(), doc.getFilename(), doc.getTimestamp(), doc.getCreator(), doc.getDescription()});
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void downloadFileListAction() {
        // 初始化表格模型，并指定列名和数据类型
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new Object[]{"", "ID", "文件名", "时间", "上传者", "文件描述"}) {
            // 重写getColumnClass方法，指定第一列为Boolean类型
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }

            // 重写isCellEditable方法，确保第一列可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        // 设置表格模型
        FileList2.setModel(model);
        Enumeration<Doc> en = null;
        try {
            en = DataProcessing.listDoc();

            while (en.hasMoreElements()) {
                Doc doc = en.nextElement();
                model.addRow(new Object[]{false, doc.getId(), doc.getFilename(), doc.getTimestamp(), doc.getCreator(), doc.getDescription()});
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void choseAllMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) FileList2.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(true, i, 0);
        }
    }

    private void clearChooseMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) FileList2.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(false, i, 0);
        }
    }

    private void downloadButtonMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) FileList2.getModel();
        int rowCount = model.getRowCount();
        boolean hasSelectedFiles = false;

        // 遍历表格中的每一行，检查是否有选中的文件
        for (int row = 0; row < rowCount; row++) {
            Boolean isSelected = (Boolean) model.getValueAt(row, 0); // 假设第一列是选中列
            if (isSelected != null && isSelected) {
                hasSelectedFiles = true;
                break; // 找到至少一个选中的文件，无需继续检查
            }
        }

        // 如果没有选中的文件，显示提示信息
        if (!hasSelectedFiles) {
            JOptionPane.showMessageDialog(null, "没有选中任何文件！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return; // 结束方法执行
        }

        // 执行下载操作
        for (int row = 0; row < rowCount; row++) {
            Boolean isSelected = (Boolean) model.getValueAt(row, 0); // 假设第一列是选中列
            if (isSelected != null && isSelected) {
                // 获取每行对应的文件ID，假设ID位于第二列
                Object id = model.getValueAt(row, 1);
                if (id instanceof String) {
                    // 调用下载方法，传递ID
                    try {
                        boolean success = AbstractUser.downloadFile((String) id);
                        if (!success) {
                            JOptionPane.showMessageDialog(null, "文件未找到或下载失败！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "数据库错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "文件下载错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "对应id未找到，请检查数据库", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // 所有文件下载完成后显示一次提示
        JOptionPane.showMessageDialog(null, "所有选中的文件下载完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addinPathMousePressed(MouseEvent e) {
        UploadWithPath uploadFrame = new UploadWithPath(this, user.name, UploadQueuedDocs);

        uploadFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadFrame.pack();
        uploadFrame.setLocationRelativeTo(null);
        uploadFrame.setVisible(true);
    }

    private void clearQueueMouseClicked(MouseEvent e) {
        UploadQueuedDocs.clear();
        uploadQueue();
        JOptionPane.showMessageDialog(null, "上传队列已清空", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void UploadbuttonMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) UpLoadFileList.getModel();
        UploadQueuedDocs.clear();
        for (int i = 0; i < model.getRowCount(); i++) {
            // 从表格模型中获取数据
            String id = (String) model.getValueAt(i, 0); // ID 列
            String fileName = (String) model.getValueAt(i, 1); // 文件名列
            Timestamp timestamp = (Timestamp) model.getValueAt(i, 2); // 时间列
            String creator = (String) model.getValueAt(i, 3); // 上传者列
            String description = (String) model.getValueAt(i, 4); // 文件描述列
            String parentDir = (String) model.getValueAt(i, 5); // 目录列
            String dir = parentDir + File.separator + fileName; // 完整路径
            if (id == null || description == null) {
                JOptionPane.showMessageDialog(null, "请填写完整第" + (i + 1) + "条" + ((id == null) ? "id" : "文件描述"), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 创建 Docs 对象
            Docs doc = new Docs(id, creator, timestamp, description, dir);

            // 将 Docs 对象添加到集合中
            UploadQueuedDocs.add(doc);
        }
        if (UploadQueuedDocs.isEmpty()) {
            JOptionPane.showMessageDialog(null, "上传队列为空", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Enumeration<Docs> en = UploadQueuedDocs.elements();
        while (en.hasMoreElements()) {
            Docs doc = en.nextElement();
            byte[] buffer = new byte[1024];
            File temp_file = new File(doc.dir);
            String filename = temp_file.getName();
            try {
                if (!DataProcessing.insertDoc(doc.id, doc.creator, doc.timestamp, doc.description, filename)) {
                    JOptionPane.showMessageDialog(null, "上传失败：文件ID重复", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(null, "上传失败:\n"+ex, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                BufferedInputStream infile = new BufferedInputStream(new FileInputStream(temp_file));
                BufferedOutputStream targetfile = new BufferedOutputStream(new FileOutputStream(".\\uploadfile\\" + filename));
                while (true) {
                    int byteRead = infile.read(buffer);
                    if (byteRead == -1) break;
                    targetfile.write(buffer, 0, byteRead);
                }
                infile.close();
                targetfile.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "上传失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        UploadQueuedDocs.clear();
        DefaultTableModel model1 = new DefaultTableModel(new Object[][]{}, new Object[]{"ID", "文件名", "时间", "上传者", "文件描述", "目录"});
        UpLoadFileList.setModel(model1);
        uploadQueue();
        showFileListAction();
        downloadFileListAction();
        JOptionPane.showMessageDialog(null, "上传成功", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void CodeSourceMouseClicked(MouseEvent e) {
        CodeGen = new Random().nextInt(9001) + 1000;
        CodeSource.setText(String.valueOf(CodeGen));
    }

    private void button1MouseClicked(MouseEvent e) {
        OriginPasswd.setText("");
        NewPasswd.setText("");
        JOptionPane.showMessageDialog(null, "已清空", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void button2MouseClicked(MouseEvent e) {//确认修改密码
        String originPasswd = String.valueOf(OriginPasswd.getPassword());
        String newPasswd = String.valueOf(NewPasswd.getPassword());
        String code = String.valueOf(CodeCMP.getText());
        if (!code.equals(String.valueOf(CodeGen))) {
            JOptionPane.showMessageDialog(null, "验证码错误", "错误", JOptionPane.ERROR_MESSAGE);
            CodeSource.setText("点击获取验证码");
            CodeCMP.setText("");
            return;
        }
        if (originPasswd.equals("") || newPasswd.equals("")) {
            JOptionPane.showMessageDialog(null, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (originPasswd.equals(newPasswd)) {
            JOptionPane.showMessageDialog(null, "新密码不能与原密码相同", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (CodeCMP.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "验证码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (originPasswd.equals(user.getPassword())) {
            user.setPassword(newPasswd);
            try {
                if (DataProcessing.updateUser(user.getName(), newPasswd, user.getRole())) {
                    JOptionPane.showMessageDialog(null, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(null, "修改失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "原密码错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
        OriginPasswd.setText("");
        NewPasswd.setText("");
        CodeSource.setText("点击获取验证码");
        CodeCMP.setText("");
    }

    private void slider1StateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int value = (int) source.getValue();
            // 检查滑动条的值是否达到指定位置
            if (value == 100) {
                // 执行操作
                exitSystemAction(null);
            }
        }
    }

    private void addinGUIMousePressed(MouseEvent e) {
        // 创建一个文件选择器
        //System.out.println("addinGUIMouseClicked");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        // 显示打开文件对话框
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            UploadQueuedDocs.add(new Docs(null, user.getName(), new Timestamp(System.currentTimeMillis()), null, selectedFile.getPath()));
            uploadQueue();
        }
    }

    private void slider2StateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int value = (int) source.getValue();
            // 检查滑动条的值是否达到指定位置
            if (value == 100) {
                // 执行操作
                logoutAction(null);
            }
        }

    }

    private void LogoutButMousePressed(MouseEvent e) {
        logoutAction(null);
    }

    private void ExitButMousePressed(MouseEvent e) {
        exitSystemAction(null);
    }


    private void SearchFieldKeyReleased(KeyEvent e) {
        String keyword = FileSearchField.getText();
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new Object[]{"ID", "文件名", "时间", "上传者", "文件描述"});
        FileList1.setModel(model);
        for (Doc doc : cachedDocs) {
            if (doc.getId().contains(keyword) || doc.getFilename().contains(keyword) || doc.getDescription().contains(keyword) || new String(String.valueOf(doc.getTimestamp())).contains(keyword) || doc.getCreator().contains(keyword)) {
                model.addRow(new Object[]{doc.getId(), doc.getFilename(), doc.getTimestamp(), doc.getCreator(), doc.getDescription()});
            }
        }
    }

    private void UserSearchFieldKeyReleased(KeyEvent e) {
        String keyword = UserSearchField.getText();
        DefaultTableModel model1 = new DefaultTableModel(new Object[][]{}, new String[]{"姓名", "密码", "角色"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (row < UserCount && column == 0 ||user.getName().equals(getValueAt(row, 0).toString())) {
                    return false;
                }
                return true;
            }
        };
        UserList.setModel(model1);
        UserList.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(roles)));
        for (AbstractUser user : cachedUsers) {
            if (user.getName().contains(keyword) || user.getPassword().contains(keyword) || user.getRole().contains(keyword)) {
                model1.addRow(new Object[]{user.getName(), user.getPassword(), user.getRole()});
            }
        }
    }

    private void AddUserMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) UserList.getModel();
        model.addRow(new Object[]{"", "", ""});
        model.isCellEditable(model.getRowCount() - 1, 0);
    }

    private void DelUserMouseClicked(MouseEvent e) {
        DefaultTableModel model = (DefaultTableModel) UserList.getModel();
        int row = UserList.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "请选中一行", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = (String) model.getValueAt(row, 0);
        try {
            if (DataProcessing.deleteUser(name)) {
                model.removeRow(row);
                JOptionPane.showMessageDialog(null, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

private void SaveUserListMouseClicked(MouseEvent e) {
    DefaultTableModel model = (DefaultTableModel) UserList.getModel();
    for (int i = model.getRowCount() - 1; i >= 0; i--) { // 从后向前遍历，避免索引问题
        boolean isRowEmpty = true;
        for (int j = 0; j < model.getColumnCount(); j++) {
            Object cellValue = model.getValueAt(i, j);
            if (cellValue != null && !cellValue.toString().isEmpty()) {
                isRowEmpty = false;
                break;
            }
        }
        if (isRowEmpty) {
            model.removeRow(i); // 删除全空行
        } else {
            String name = (String) model.getValueAt(i, 0);
            String password = (String) model.getValueAt(i, 1);
            String role = (String) model.getValueAt(i, 2);
            if (name.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请填写完整第" + (i + 1) + "条" + ((name.isEmpty()) ? "姓名" : (password.isEmpty()) ? "密码" : "角色"), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!role.equals("administrator") && !role.equals("operator") && !role.equals("browser")) {
                JOptionPane.showMessageDialog(null, "角色只能为administrator/operator/browser", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (DataProcessing.updateUser(name, password, role)) {
                    JOptionPane.showMessageDialog(null, "修改" + name + "成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if (DataProcessing.insertUser(name, password, role)) {
                        JOptionPane.showMessageDialog(null, "新增" + name + "成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "新增失败", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    UserListAction();
}


    //
    //
//    private void uploadFileAction() {
//
//    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        menuBar1 = new JMenuBar();
        UserName = new JMenu();
        ROLE = new JMenuItem();
        TimeNow = new JMenuItem();
        LogoutBut = new JMenuItem();
        ExitBut = new JMenuItem();
        tabbedPane1 = new JTabbedPane();
        ArchiveManage = new JPanel();
        ArchiveMngTbdp = new JTabbedPane();
        ListArchive = new JPanel();
        scrollPane1 = new JScrollPane();
        FileList1 = new JTable();
        panel2 = new JPanel();
        FileSearchField = new JTextField();
        UploadArchive = new JPanel();
        panel9 = new JPanel();
        menuBar2 = new JMenuBar();
        uploadMenu = new JMenu();
        addinGUI = new JMenuItem();
        addinPath = new JMenuItem();
        scrollPane3 = new JScrollPane();
        UpLoadFileList = new JTable();
        panel8 = new JPanel();
        Delchosen = new JButton();
        clearQueue = new JButton();
        Uploadbutton = new JButton();
        DownLoadArchive = new JPanel();
        scrollPane2 = new JScrollPane();
        FileList2 = new JTable();
        panel7 = new JPanel();
        choseAll = new JButton();
        clearChoose = new JButton();
        downloadButton = new JButton();
        UserManage = new JPanel();
        ListArchive2 = new JPanel();
        scrollPane4 = new JScrollPane();
        UserList = new JTable();
        panel3 = new JPanel();
        UserSearchField = new JTextField();
        AddUser = new JButton();
        DelUser = new JButton();
        SaveUserList = new JButton();
        MyProfile = new JPanel();
        MyPrrofileTbdP = new JTabbedPane();
        exitSys = new JPanel();
        label6 = new JLabel();
        slider1 = new JSlider();
        changePasswd = new JPanel();
        button1 = new JButton();
        button2 = new JButton();
        OriginPasswd = new JPasswordField();
        NewPasswd = new JPasswordField();
        label3 = new JLabel();
        label4 = new JLabel();
        CodeCMP = new JTextField();
        CodeSource = new JTextField();
        label5 = new JLabel();
        exitSys2 = new JPanel();
        label7 = new JLabel();
        slider2 = new JSlider();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== UserName ========
            {
                UserName.setText("text");

                //---- ROLE ----
                ROLE.setText("text");
                UserName.add(ROLE);

                //---- TimeNow ----
                TimeNow.setText("text");
                UserName.add(TimeNow);

                //---- LogoutBut ----
                LogoutBut.setText("\u6ce8\u9500\u767b\u5f55");
                LogoutBut.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        LogoutButMousePressed(e);
                    }
                });
                UserName.add(LogoutBut);

                //---- ExitBut ----
                ExitBut.setText("\u9000\u51fa\u7cfb\u7edf");
                ExitBut.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        ExitButMousePressed(e);
                    }
                });
                UserName.add(ExitBut);
            }
            menuBar1.add(UserName);
        }
        setJMenuBar(menuBar1);

        //======== tabbedPane1 ========
        {
            tabbedPane1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));

            //======== ArchiveManage ========
            {
                ArchiveManage.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
                ArchiveManage.setLayout(new BorderLayout());

                //======== ArchiveMngTbdp ========
                {
                    ArchiveMngTbdp.setTabPlacement(SwingConstants.LEFT);
                    ArchiveMngTbdp.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));

                    //======== ListArchive ========
                    {
                        ListArchive.setLayout(new BorderLayout());

                        //======== scrollPane1 ========
                        {

                            //---- FileList1 ----
                            FileList1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                            FileList1.setAutoCreateRowSorter(true);
                            FileList1.setEnabled(false);
                            FileList1.setShowHorizontalLines(true);
                            FileList1.setShowVerticalLines(true);
                            FileList1.setModel(new DefaultTableModel(
                                new Object[][] {
                                },
                                new String[] {
                                    "ID", "\u6587\u4ef6\u540d", "\u65f6\u95f4", "\u521b\u5efa\u8005", "\u6587\u4ef6\u63cf\u8ff0"
                                }
                            ) {
                                boolean[] columnEditable = new boolean[] {
                                    false, true, true, true, true
                                };
                                @Override
                                public boolean isCellEditable(int rowIndex, int columnIndex) {
                                    return columnEditable[columnIndex];
                                }
                            });
                            FileList1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                            scrollPane1.setViewportView(FileList1);
                        }
                        ListArchive.add(scrollPane1, BorderLayout.CENTER);

                        //======== panel2 ========
                        {

                            //---- FileSearchField ----
                            FileSearchField.addKeyListener(new KeyAdapter() {
                                @Override
                                public void keyReleased(KeyEvent e) {
                                    SearchFieldKeyReleased(e);
                                }
                            });

                            GroupLayout panel2Layout = new GroupLayout(panel2);
                            panel2.setLayout(panel2Layout);
                            panel2Layout.setHorizontalGroup(
                                panel2Layout.createParallelGroup()
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addComponent(FileSearchField, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 359, Short.MAX_VALUE))
                            );
                            panel2Layout.setVerticalGroup(
                                panel2Layout.createParallelGroup()
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addComponent(FileSearchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                            );
                        }
                        ListArchive.add(panel2, BorderLayout.NORTH);
                    }
                    ArchiveMngTbdp.addTab("\u6863\u6848\u5217\u8868", ListArchive);

                    //======== UploadArchive ========
                    {
                        UploadArchive.setLayout(new BorderLayout());

                        //======== panel9 ========
                        {
                            panel9.setLayout(new BorderLayout());

                            //======== menuBar2 ========
                            {

                                //======== uploadMenu ========
                                {
                                    uploadMenu.setText("\u4e0a\u4f20\u6587\u4ef6");
                                    uploadMenu.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));

                                    //---- addinGUI ----
                                    addinGUI.setText("GUI\u9009\u62e9\u5668");
                                    addinGUI.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                                    addinGUI.addMouseListener(new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent e) {
                                            addinGUIMousePressed(e);
                                        }
                                    });
                                    uploadMenu.add(addinGUI);

                                    //---- addinPath ----
                                    addinPath.setText("\u8def\u5f84\u6dfb\u52a0");
                                    addinPath.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                                    addinPath.addMouseListener(new MouseAdapter() {
                                        @Override
                                        public void mousePressed(MouseEvent e) {
                                            addinPathMousePressed(e);
                                        }
                                    });
                                    uploadMenu.add(addinPath);
                                }
                                menuBar2.add(uploadMenu);
                            }
                            panel9.add(menuBar2, BorderLayout.CENTER);
                        }
                        UploadArchive.add(panel9, BorderLayout.NORTH);

                        //======== scrollPane3 ========
                        {

                            //---- UpLoadFileList ----
                            UpLoadFileList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                            UpLoadFileList.setShowHorizontalLines(true);
                            UpLoadFileList.setShowVerticalLines(true);
                            UpLoadFileList.setModel(new DefaultTableModel(
                                new Object[][] {
                                },
                                new String[] {
                                    "ID", "\u6587\u4ef6\u540d", "\u65f6\u95f4", "\u521b\u5efa\u8005", "\u6587\u4ef6\u63cf\u8ff0"
                                }
                            ) {
                                boolean[] columnEditable = new boolean[] {
                                    false, true, true, true, true
                                };
                                @Override
                                public boolean isCellEditable(int rowIndex, int columnIndex) {
                                    return columnEditable[columnIndex];
                                }
                            });
                            UpLoadFileList.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                            scrollPane3.setViewportView(UpLoadFileList);
                        }
                        UploadArchive.add(scrollPane3, BorderLayout.CENTER);

                        //======== panel8 ========
                        {
                            panel8.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            panel8.setLayout(new BoxLayout(panel8, BoxLayout.X_AXIS));

                            //---- Delchosen ----
                            Delchosen.setText("\u5220\u9664\u9009\u4e2d");
                            Delchosen.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                            panel8.add(Delchosen);

                            //---- clearQueue ----
                            clearQueue.setText("\u6e05\u7a7a\u961f\u5217");
                            clearQueue.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                            clearQueue.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    clearQueueMouseClicked(e);
                                }
                            });
                            panel8.add(clearQueue);

                            //---- Uploadbutton ----
                            Uploadbutton.setText("\u4e0a\u4f20");
                            Uploadbutton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                            Uploadbutton.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    UploadbuttonMouseClicked(e);
                                }
                            });
                            panel8.add(Uploadbutton);
                        }
                        UploadArchive.add(panel8, BorderLayout.SOUTH);
                    }
                    ArchiveMngTbdp.addTab("\u4e0a\u4f20\u6863\u6848", UploadArchive);

                    //======== DownLoadArchive ========
                    {
                        DownLoadArchive.setLayout(new BorderLayout());

                        //======== scrollPane2 ========
                        {

                            //---- FileList2 ----
                            FileList2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                            FileList2.setAutoCreateRowSorter(true);
                            FileList2.setShowHorizontalLines(true);
                            FileList2.setShowVerticalLines(true);
                            FileList2.setModel(new DefaultTableModel(
                                new Object[][] {
                                    {null, null, null},
                                },
                                new String[] {
                                    null, null, null
                                }
                            ) {
                                Class<?>[] columnTypes = new Class<?>[] {
                                    Object.class, Object.class, Boolean.class
                                };
                                @Override
                                public Class<?> getColumnClass(int columnIndex) {
                                    return columnTypes[columnIndex];
                                }
                            });
                            FileList2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                            scrollPane2.setViewportView(FileList2);
                        }
                        DownLoadArchive.add(scrollPane2, BorderLayout.CENTER);

                        //======== panel7 ========
                        {
                            panel7.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            panel7.setLayout(new BoxLayout(panel7, BoxLayout.X_AXIS));

                            //---- choseAll ----
                            choseAll.setText("\u5168\u9009");
                            choseAll.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    choseAllMouseClicked(e);
                                    choseAllMouseClicked(e);
                                }
                            });
                            panel7.add(choseAll);

                            //---- clearChoose ----
                            clearChoose.setText("\u6e05\u9664\u9009\u4e2d");
                            clearChoose.setHorizontalAlignment(SwingConstants.RIGHT);
                            clearChoose.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    clearChooseMouseClicked(e);
                                }
                            });
                            panel7.add(clearChoose);

                            //---- downloadButton ----
                            downloadButton.setText("\u4e0b\u8f7d");
                            downloadButton.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    downloadButtonMouseClicked(e);
                                }
                            });
                            panel7.add(downloadButton);
                        }
                        DownLoadArchive.add(panel7, BorderLayout.SOUTH);
                    }
                    ArchiveMngTbdp.addTab("\u4e0b\u8f7d\u6863\u6848", DownLoadArchive);
                }
                ArchiveManage.add(ArchiveMngTbdp, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u6863\u6848\u7ba1\u7406", ArchiveManage);

            //======== UserManage ========
            {
                UserManage.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                UserManage.setLayout(new BorderLayout());

                //======== ListArchive2 ========
                {
                    ListArchive2.setLayout(new BorderLayout());

                    //======== scrollPane4 ========
                    {

                        //---- UserList ----
                        UserList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                        UserList.setShowHorizontalLines(true);
                        UserList.setShowVerticalLines(true);
                        UserList.setModel(new DefaultTableModel(
                            new Object[][] {
                            },
                            new String[] {
                                "ID", "\u6587\u4ef6\u540d", "\u65f6\u95f4", "\u521b\u5efa\u8005", "\u6587\u4ef6\u63cf\u8ff0"
                            }
                        ) {
                            boolean[] columnEditable = new boolean[] {
                                false, true, true, true, true
                            };
                            @Override
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return columnEditable[columnIndex];
                            }
                        });
                        UserList.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 14));
                        scrollPane4.setViewportView(UserList);
                    }
                    ListArchive2.add(scrollPane4, BorderLayout.CENTER);

                    //======== panel3 ========
                    {

                        //---- UserSearchField ----
                        UserSearchField.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyReleased(KeyEvent e) {
                                UserSearchFieldKeyReleased(e);
                            }
                        });

                        //---- AddUser ----
                        AddUser.setText("\u6dfb\u52a0");
                        AddUser.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                AddUserMouseClicked(e);
                            }
                        });

                        //---- DelUser ----
                        DelUser.setText("\u5220\u9664");
                        DelUser.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                DelUserMouseClicked(e);
                            }
                        });

                        //---- SaveUserList ----
                        SaveUserList.setText("\u4fdd\u5b58");
                        SaveUserList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                SaveUserListMouseClicked(e);
                            }
                        });

                        GroupLayout panel3Layout = new GroupLayout(panel3);
                        panel3.setLayout(panel3Layout);
                        panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addComponent(UserSearchField, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(AddUser)
                                    .addGap(18, 18, 18)
                                    .addComponent(DelUser)
                                    .addGap(18, 18, 18)
                                    .addComponent(SaveUserList)
                                    .addGap(0, 172, Short.MAX_VALUE))
                        );
                        panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addGroup(panel3Layout.createParallelGroup()
                                        .addComponent(UserSearchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(AddUser)
                                        .addComponent(DelUser)
                                        .addComponent(SaveUserList))
                                    .addGap(0, 0, Short.MAX_VALUE))
                        );
                    }
                    ListArchive2.add(panel3, BorderLayout.NORTH);
                }
                UserManage.add(ListArchive2, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u7528\u6237\u7ba1\u7406", UserManage);

            //======== MyProfile ========
            {
                MyProfile.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 20));
                MyProfile.setLayout(new BorderLayout());

                //======== MyPrrofileTbdP ========
                {
                    MyPrrofileTbdP.setTabPlacement(SwingConstants.LEFT);

                    //======== exitSys ========
                    {

                        //---- label6 ----
                        label6.setText("\u786e\u5b9a\u9000\u51fa\u8bf7\u62d6\u52a8\u5230\u53f3\u4fa7");
                        label6.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));

                        //---- slider1 ----
                        slider1.setValue(0);
                        slider1.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));
                        slider1.setPaintTicks(true);
                        slider1.setPaintLabels(true);
                        slider1.addChangeListener(e -> slider1StateChanged(e));

                        GroupLayout exitSysLayout = new GroupLayout(exitSys);
                        exitSys.setLayout(exitSysLayout);
                        exitSysLayout.setHorizontalGroup(
                            exitSysLayout.createParallelGroup()
                                .addGroup(exitSysLayout.createSequentialGroup()
                                    .addContainerGap(110, Short.MAX_VALUE)
                                    .addGroup(exitSysLayout.createParallelGroup()
                                        .addComponent(slider1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(label6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap(135, Short.MAX_VALUE))
                        );
                        exitSysLayout.setVerticalGroup(
                            exitSysLayout.createParallelGroup()
                                .addGroup(exitSysLayout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(label6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(18, 18, Short.MAX_VALUE)
                                    .addComponent(slider1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(236, 236, 236))
                        );
                    }
                    MyPrrofileTbdP.addTab("\u9000\u51fa\u7cfb\u7edf", exitSys);

                    //======== changePasswd ========
                    {

                        //---- button1 ----
                        button1.setText("\u6e05\u9664");
                        button1.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                button1MouseClicked(e);
                            }
                        });

                        //---- button2 ----
                        button2.setText("\u786e\u5b9a");
                        button2.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                button2MouseClicked(e);
                            }
                        });

                        //---- label3 ----
                        label3.setText("\u539f\u5bc6\u7801");

                        //---- label4 ----
                        label4.setText("\u65b0\u5bc6\u7801");

                        //---- CodeSource ----
                        CodeSource.setEditable(false);
                        CodeSource.setText("\u70b9\u51fb\u83b7\u53d6\u9a8c\u8bc1\u7801");
                        CodeSource.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                CodeSourceMouseClicked(e);
                            }
                        });

                        //---- label5 ----
                        label5.setText("\u9a8c\u8bc1\u7801");

                        GroupLayout changePasswdLayout = new GroupLayout(changePasswd);
                        changePasswd.setLayout(changePasswdLayout);
                        changePasswdLayout.setHorizontalGroup(
                            changePasswdLayout.createParallelGroup()
                                .addGroup(changePasswdLayout.createSequentialGroup()
                                    .addGap(86, 86, 86)
                                    .addGroup(changePasswdLayout.createParallelGroup()
                                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                                        .addComponent(label4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(label5, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(changePasswdLayout.createParallelGroup()
                                        .addGroup(changePasswdLayout.createSequentialGroup()
                                            .addComponent(CodeCMP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(CodeSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(changePasswdLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(OriginPasswd)
                                            .addGroup(changePasswdLayout.createSequentialGroup()
                                                .addComponent(button1)
                                                .addGap(73, 73, 73)
                                                .addComponent(button2))
                                            .addComponent(NewPasswd, GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(161, 161, 161))
                        );
                        changePasswdLayout.setVerticalGroup(
                            changePasswdLayout.createParallelGroup()
                                .addGroup(changePasswdLayout.createSequentialGroup()
                                    .addGap(57, 57, 57)
                                    .addGroup(changePasswdLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(label3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(OriginPasswd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(changePasswdLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(NewPasswd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(changePasswdLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(CodeCMP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(CodeSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(87, 87, 87)
                                    .addGroup(changePasswdLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(button1)
                                        .addComponent(button2))
                                    .addGap(120, 120, 120))
                        );
                    }
                    MyPrrofileTbdP.addTab("\u4fee\u6539\u5bc6\u7801", changePasswd);

                    //======== exitSys2 ========
                    {

                        //---- label7 ----
                        label7.setText("\u6ce8\u9500\u767b\u5f55\u8bf7\u62d6\u52a8\u5230\u53f3\u4fa7");
                        label7.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));

                        //---- slider2 ----
                        slider2.setValue(0);
                        slider2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 28));
                        slider2.setPaintTicks(true);
                        slider2.setPaintLabels(true);
                        slider2.addChangeListener(e -> slider2StateChanged(e));

                        GroupLayout exitSys2Layout = new GroupLayout(exitSys2);
                        exitSys2.setLayout(exitSys2Layout);
                        exitSys2Layout.setHorizontalGroup(
                            exitSys2Layout.createParallelGroup()
                                .addGroup(exitSys2Layout.createSequentialGroup()
                                    .addContainerGap(110, Short.MAX_VALUE)
                                    .addGroup(exitSys2Layout.createParallelGroup()
                                        .addComponent(slider2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(label7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap(135, Short.MAX_VALUE))
                        );
                        exitSys2Layout.setVerticalGroup(
                            exitSys2Layout.createParallelGroup()
                                .addGroup(exitSys2Layout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(label7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(18, 18, Short.MAX_VALUE)
                                    .addComponent(slider2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(236, 236, 236))
                        );
                    }
                    MyPrrofileTbdP.addTab("\u6ce8\u9500\u767b\u5f55", exitSys2);
                }
                MyProfile.add(MyPrrofileTbdP, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u6211\u7684", MyProfile);
        }
        contentPane.add(tabbedPane1, BorderLayout.CENTER);
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

}

