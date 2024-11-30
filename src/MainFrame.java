import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import javax.swing.*;
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
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JTabbedPane tabbedPane1;
    private JPanel ArchiveManage;
    private JTabbedPane ArchiveMngTbdp;
    private JPanel ListArchive;
    private JScrollPane scrollPane1;
    private JTable FileList1;
    private JPanel DownLoadArchive;
    private JScrollPane scrollPane2;
    private JTable FileList2;
    private JPanel panel7;
    private JButton choseAll;
    private JButton clearChoose;
    private JButton downloadButton;
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
    private JPanel UserManage;
    private JPanel MyProfile;
    private JTabbedPane MyPrrofileTbdP;
    private JPanel logOut;
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

    }

    public void uploadQueue() {
        DefaultTableModel model=(DefaultTableModel) UpLoadFileList.getModel();
        for (Docs doc : UploadQueuedDocs) {
            model.addRow(new Object[]{doc.id, new File(doc.dir).getName(), doc.timestamp, doc.creator, doc.description, new File(doc.dir).getParent()});
        }
        UploadQueuedDocs.clear();
    }
    private void exitSystemAction(ActionEvent e) {
        System.exit(0);
    }

    private void logoutAction(ActionEvent e) {
        this.dispose();
        try {
            JFrame loginFrame = new JFrame();
            loginFrame.setVisible(true);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void showFileListAction() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{

        }, new Object[]{"ID", "文件名", "时间", "上传者", "文件描述"});

        FileList1.setModel(model);
        Enumeration<Doc> e = null;
        try {
            e = DataProcessing.listDoc();

            while (e.hasMoreElements()) {
                Doc doc = e.nextElement();
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
    //
//    private void changeSelfPasswordAction() {
//
//    }
//


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


    private void menu1MouseEntered(MouseEvent e) {
        if (e.getSource() instanceof JMenu) {
            JMenu menu = (JMenu) e.getSource();
            menu.doClick();
        }
    }

    private void menu1MouseClicked(MouseEvent e) {
        // TODO add your code here
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
        DefaultTableModel model= (DefaultTableModel) UpLoadFileList.getModel();
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

            // 创建 Docs 对象
            Docs doc = new Docs(id, creator, timestamp,  description,dir);

            // 将 Docs 对象添加到集合中
            UploadQueuedDocs.add(doc);
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
                throw new RuntimeException(ex);
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
            CodeSource.setText("");
            CodeCMP.setText("");
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
        System.out.println("addinGUIMouseClicked");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        // 显示打开文件对话框
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            UploadQueuedDocs.add(new Docs(null, user.getName(), new Timestamp(System.currentTimeMillis()),null, selectedFile.getPath()));
            uploadQueue();
        }
    }

    //
    //
//    private void uploadFileAction() {
//
//    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        tabbedPane1 = new JTabbedPane();
        ArchiveManage = new JPanel();
        ArchiveMngTbdp = new JTabbedPane();
        ListArchive = new JPanel();
        scrollPane1 = new JScrollPane();
        FileList1 = new JTable();
        DownLoadArchive = new JPanel();
        scrollPane2 = new JScrollPane();
        FileList2 = new JTable();
        panel7 = new JPanel();
        choseAll = new JButton();
        clearChoose = new JButton();
        downloadButton = new JButton();
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
        UserManage = new JPanel();
        MyProfile = new JPanel();
        MyPrrofileTbdP = new JTabbedPane();
        logOut = new JPanel();
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

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

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
                    }
                    ArchiveMngTbdp.addTab("\u6863\u6848\u5217\u8868", ListArchive);

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
                            UpLoadFileList.setAutoCreateRowSorter(true);
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
                }
                ArchiveManage.add(ArchiveMngTbdp, BorderLayout.CENTER);
            }
            tabbedPane1.addTab("\u6863\u6848\u7ba1\u7406", ArchiveManage);

            //======== UserManage ========
            {
                UserManage.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                UserManage.setLayout(new BorderLayout());
            }
            tabbedPane1.addTab("\u7528\u6237\u7ba1\u7406", UserManage);

            //======== MyProfile ========
            {
                MyProfile.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 20));
                MyProfile.setLayout(new BorderLayout());

                //======== MyPrrofileTbdP ========
                {
                    MyPrrofileTbdP.setTabPlacement(SwingConstants.LEFT);

                    //======== logOut ========
                    {
                        logOut.setLayout(new BorderLayout());
                    }
                    MyPrrofileTbdP.addTab("\u6ce8\u9500\u767b\u5f55", logOut);

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
                                    .addContainerGap(17, Short.MAX_VALUE)
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

