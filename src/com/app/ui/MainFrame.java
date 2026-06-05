package com.app.ui;

import com.app.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel statusBar;
    private JTabbedPane tabbedPane;

    // 子面板
    private TemplatePanel templatePanel;
    private ProblemPanel problemPanel;
    private CodeRunnerPanel codeRunnerPanel;
    private StatsPanel statsPanel;
    private MusicPlayerPanel musicPlayerPanel;

    // 当前登录用户
    private User currentUser = null;

    // 菜单项（需要登录后启用）
    private JMenuItem mntmAddTemplate;
    private JMenuItem mntmEditTemplate;
    private JMenuItem mViewTemplate;
    private JMenuItem mntmCopyCode;
    private JMenuItem mntmDeleteTemplate;
    private JMenuItem mntmLogout;

    public MainFrame() {
        setTitle("ACMer's Toolkit - ACM 竞赛综合工具箱");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 1000, 650);
        setLocationRelativeTo(null);

        // ============ 菜单栏 ============
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // 文件菜单
        JMenu mnFile = new JMenu("文件(F)");
        mnFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(mnFile);

        JMenuItem mntmLogin = new JMenuItem("登录...");
        mntmLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmLogin);

        JMenuItem mntmRegister = new JMenuItem("注册新用户...");
        mntmRegister.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmRegister);

        mntmLogout = new JMenuItem("注销");
        mntmLogout.setEnabled(false);
        mnFile.add(mntmLogout);

        mnFile.addSeparator();

        JMenuItem mntmExit = new JMenuItem("退出(X)");
        mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmExit);

        // 编辑菜单
        JMenu mnEdit = new JMenu("编辑(E)");
        mnEdit.setMnemonic(KeyEvent.VK_E);
        menuBar.add(mnEdit);

        mntmAddTemplate = new JMenuItem("添加模板...");
        mntmAddTemplate.setEnabled(false);
        mntmAddTemplate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmAddTemplate);

        mntmEditTemplate = new JMenuItem("编辑选中模板...");
        mntmEditTemplate.setEnabled(false);
        mnEdit.add(mntmEditTemplate);

        mViewTemplate = new JMenuItem("查看模板详情...");
        mViewTemplate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK));
        mnEdit.add(mViewTemplate);

        mntmCopyCode = new JMenuItem("复制代码到剪贴板");
        mntmCopyCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        mnEdit.add(mntmCopyCode);

        mntmDeleteTemplate = new JMenuItem("删除选中模板");
        mntmDeleteTemplate.setEnabled(false);
        mnEdit.add(mntmDeleteTemplate);

        mnEdit.addSeparator();

        JMenuItem mntmRefresh = new JMenuItem("刷新列表");
        mntmRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        mnEdit.add(mntmRefresh);

        mnEdit.addSeparator();

        JMenuItem mntmImportURL = new JMenuItem("通过网址添加题目...");
        mnEdit.add(mntmImportURL);

        // 工具菜单
        JMenu mnTools = new JMenu("工具(T)");
        mnTools.setMnemonic(KeyEvent.VK_T);
        menuBar.add(mnTools);

        JMenuItem mntmTimer = new JMenuItem("训练计时器...");
        mntmTimer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        mnTools.add(mntmTimer);

        JMenuItem mntmTrainingSettings = new JMenuItem("训练设置...");
        mnTools.add(mntmTrainingSettings);

        JMenuItem mntmCodeRunner = new JMenuItem("切换到代码运行");
        mnTools.add(mntmCodeRunner);

        JMenuItem mntmStats = new JMenuItem("切换到统计面板");
        mnTools.add(mntmStats);

        // 帮助菜单
        JMenu mnHelp = new JMenu("帮助(H)");
        mnHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(mnHelp);

        JMenuItem mntmAbout = new JMenuItem("关于(A)...");
        mnHelp.add(mntmAbout);

        // ============ 主面板 ============
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // 标签页面板
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        templatePanel = new TemplatePanel(currentUser);
        problemPanel = new ProblemPanel(currentUser);
        codeRunnerPanel = new CodeRunnerPanel();
        statsPanel = new StatsPanel(currentUser);

        tabbedPane.addTab("模板管理", null, templatePanel, "管理ACM算法模板");
        tabbedPane.addTab("题库", null, problemPanel, "题目收集与管理");
        tabbedPane.addTab("代码运行", null, codeRunnerPanel, "在线编译运行代码");
        tabbedPane.addTab("统计", null, statsPanel, "学习统计面板");

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // 底部状态栏
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        statusBar = new JLabel(" 就绪 - 请先登录");
        statusBar.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        bottomPanel.add(statusBar, BorderLayout.WEST);

        // 背景音乐播放面板（状态栏右侧）
        musicPlayerPanel = new MusicPlayerPanel();
        bottomPanel.add(musicPlayerPanel, BorderLayout.CENTER);

        JLabel lblVersion = new JLabel("V2.0.0  ");
        lblVersion.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        lblVersion.setForeground(Color.GRAY);
        bottomPanel.add(lblVersion, BorderLayout.EAST);

        // ============ 事件绑定 ============

        // 登录
        mntmLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        // 注册
        mntmRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegisterDialog regDialog = new RegisterDialog(MainFrame.this);
                regDialog.setVisible(true);
            }
        });

        // 注销
        mntmLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogout();
            }
        });

        // 退出
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doExit();
            }
        });

        // 添加模板
        mntmAddTemplate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templatePanel.doAddTemplate();
            }
        });

        // 编辑模板
        mntmEditTemplate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templatePanel.doEditTemplate();
            }
        });

        // 查看模板
        mViewTemplate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templatePanel.doViewTemplate();
            }
        });

        // 复制代码
        mntmCopyCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templatePanel.doCopyCode();
            }
        });

        // 删除模板
        mntmDeleteTemplate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templatePanel.doDeleteTemplate();
            }
        });

        // 刷新
        mntmRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templatePanel.refreshData();
                problemPanel.refreshData();
                statsPanel.refreshData();
            }
        });

        // 通过网址添加题目
        mntmImportURL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "请先登录后再添加题目！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                AddProblemByURLDialog dlg = new AddProblemByURLDialog(MainFrame.this, currentUser);
                dlg.setVisible(true);
                if (dlg.isSaved()) {
                    problemPanel.refreshData();
                }
            }
        });

        // 训练计时器
        mntmTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TimerDialog timerDialog = new TimerDialog(MainFrame.this, currentUser);
                timerDialog.setVisible(true);
            }
        });

        // 训练设置
        mntmTrainingSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TrainingSettingsDialog dlg = new TrainingSettingsDialog(MainFrame.this);
                dlg.setVisible(true);
            }
        });

        // 切换到代码运行
        mntmCodeRunner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(2);
            }
        });

        // 切换到统计
        mntmStats.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(3);
                statsPanel.refreshData();
            }
        });

        // 关于
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog aboutDialog = new AboutDialog(MainFrame.this);
                aboutDialog.setVisible(true);
            }
        });

        // 窗口关闭确认
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doExit();
            }
        });
    }

    // ============ 业务方法 ============

    private void doLogin() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.isLoginSuccess()) {
            currentUser = loginDialog.getLoggedInUser();
            setTitle("ACMer's Toolkit - " + currentUser.getUsername());
            statusBar.setText(" 当前用户：" + currentUser.getUsername());

            // 启用编辑功能
            mntmAddTemplate.setEnabled(true);
            mntmEditTemplate.setEnabled(true);
            mntmDeleteTemplate.setEnabled(true);
            mntmLogout.setEnabled(true);

            // 通知各面板
            templatePanel.setCurrentUser(currentUser);
            problemPanel.setCurrentUser(currentUser);
            statsPanel.setCurrentUser(currentUser);
        }
    }

    private void doLogout() {
        int opt = JOptionPane.showConfirmDialog(this,
                "确定注销当前用户？", "确认注销", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            currentUser = null;
            setTitle("ACMer's Toolkit - ACM 竞赛综合工具箱");
            statusBar.setText(" 就绪 - 请先登录");

            mntmAddTemplate.setEnabled(false);
            mntmEditTemplate.setEnabled(false);
            mntmDeleteTemplate.setEnabled(false);
            mntmLogout.setEnabled(false);

            templatePanel.setCurrentUser(null);
            problemPanel.setCurrentUser(null);
            statsPanel.setCurrentUser(null);
        }
    }

    private void doExit() {
        int opt = JOptionPane.showConfirmDialog(this,
                "确定退出程序？", "确认退出", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            musicPlayerPanel.cleanup();
            System.exit(0);
        }
    }
}
