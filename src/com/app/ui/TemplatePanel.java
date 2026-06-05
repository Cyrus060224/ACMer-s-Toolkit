package com.app.ui;

import com.app.dao.TemplateDAO;
import com.app.entity.Template;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class TemplatePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbCategory;
    private JTextField txtSearch;
    private User currentUser;

    public TemplatePanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 0));

        // 顶部筛选面板
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new TitledBorder(null, "模板筛选", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel lblCategory = new JLabel("分类：");
        lblCategory.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        cmbCategory = new JComboBox<>();
        cmbCategory.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cmbCategory.addItem("全部分类");
        cmbCategory.addItem("动态规划");
        cmbCategory.addItem("贪心算法");
        cmbCategory.addItem("图论");
        cmbCategory.addItem("数据结构");
        cmbCategory.addItem("数论");
        cmbCategory.addItem("字符串");
        cmbCategory.addItem("搜索");
        cmbCategory.addItem("其他");

        JLabel lblSearchLabel = new JLabel("搜索：");
        lblSearchLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        txtSearch.setColumns(15);

        JButton btnSearch = new JButton("查  找");
        btnSearch.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JButton btnRefresh = new JButton("刷  新");
        btnRefresh.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        GroupLayout gl_topPanel = new GroupLayout(topPanel);
        gl_topPanel.setHorizontalGroup(
            gl_topPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_topPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblCategory)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(cmbCategory, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addGap(20)
                    .addComponent(lblSearchLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnSearch)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnRefresh)
                    .addContainerGap(150, Short.MAX_VALUE))
        );
        gl_topPanel.setVerticalGroup(
            gl_topPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_topPanel.createSequentialGroup()
                    .addGroup(gl_topPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblCategory)
                        .addComponent(cmbCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSearchLabel)
                        .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch)
                        .addComponent(btnRefresh))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        topPanel.setLayout(gl_topPanel);
        add(topPanel, BorderLayout.NORTH);

        // 表格
        String[] columnNames = {"ID", "分类", "标题", "时间限制(秒)", "代码预览"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 事件绑定
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });

        cmbCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSearch();
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doSearch();
                }
            }
        });

        // 右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem popView = new JMenuItem("查看详情...");
        JMenuItem popEdit = new JMenuItem("编辑...");
        JMenuItem popCopy = new JMenuItem("复制代码");
        JMenuItem popDelete = new JMenuItem("删除");
        popupMenu.add(popView);
        popupMenu.add(popEdit);
        popupMenu.add(popCopy);
        popupMenu.addSeparator();
        popupMenu.add(popDelete);

        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    doViewTemplate();
                }
            }
        });

        popView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doViewTemplate();
            }
        });

        popEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doEditTemplate();
            }
        });

        popCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doCopyCode();
            }
        });

        popDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doDeleteTemplate();
            }
        });

        // 初始加载
        refreshData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        if (currentUser == null) return; // 未登录不显示数据

        TemplateDAO dao = new TemplateDAO();
        java.util.List<Template> list;

        String selected = (String) cmbCategory.getSelectedItem();
        if (selected == null || "全部分类".equals(selected)) {
            list = dao.getAllTemplates(currentUser.getId());
        } else {
            list = dao.getTemplatesByCategory(currentUser.getId(), selected);
        }

        for (Template t : list) {
            String preview = t.getCodeContent();
            if (preview != null && preview.length() > 60) {
                preview = preview.substring(0, 60) + "...";
            }
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getCategory(),
                    t.getTitle(),
                    t.getTimeLimit(),
                    preview
            });
        }
    }

    public void doSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        if (currentUser == null) return;

        tableModel.setRowCount(0);
        TemplateDAO dao = new TemplateDAO();
        java.util.List<Template> list = dao.searchTemplates(currentUser.getId(), keyword);
        for (Template t : list) {
            String preview = t.getCodeContent();
            if (preview != null && preview.length() > 60) {
                preview = preview.substring(0, 60) + "...";
            }
            tableModel.addRow(new Object[]{
                    t.getId(), t.getCategory(), t.getTitle(), t.getTimeLimit(), preview
            });
        }
    }

    public void doViewTemplate() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个模板！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        TemplateDAO dao = new TemplateDAO();
        Template t = dao.getTemplateById(id);
        if (t != null) {
            TemplateViewDialog dialog = new TemplateViewDialog((Frame) getTopLevelAncestor(), t);
            dialog.setVisible(true);
        }
    }

    public void doEditTemplate() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个模板！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        TemplateDAO dao = new TemplateDAO();
        Template t = dao.getTemplateById(id);
        if (t != null) {
            TemplateEditDialog dialog = new TemplateEditDialog((Frame) getTopLevelAncestor(), t);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }

    public void doCopyCode() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个模板！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        TemplateDAO dao = new TemplateDAO();
        Template t = dao.getTemplateById(id);
        if (t != null) {
            StringSelection selection = new StringSelection(t.getCodeContent());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "代码已复制到剪贴板！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void doAddTemplate() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField txtTitle = new JTextField();
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{
                "动态规划", "贪心算法", "图论", "数据结构", "数论", "字符串", "搜索", "其他"
        });
        JTextField txtTime = new JTextField("1000");
        JTextArea txtCode = new JTextArea(5, 30);
        txtCode.setFont(new Font("Consolas", Font.PLAIN, 12));

        panel.add(new JLabel("标题："));
        panel.add(txtTitle);
        panel.add(new JLabel("分类："));
        panel.add(cmbCat);
        panel.add(new JLabel("时间限制(秒)："));
        panel.add(txtTime);
        panel.add(new JLabel("代码内容："));
        panel.add(new JScrollPane(txtCode));

        int result = JOptionPane.showConfirmDialog(this, panel, "添加新模板",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = txtTitle.getText().trim();
            String category = (String) cmbCat.getSelectedItem();
            String code = txtCode.getText();
            int timeLimit;
            try {
                timeLimit = Integer.parseInt(txtTime.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "时间限制必须是整数！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (title.isEmpty() || code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "标题和代码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Template t = new Template(category, title, code, timeLimit, currentUser.getId());
            TemplateDAO dao = new TemplateDAO();
            if (dao.addTemplate(t)) {
                JOptionPane.showMessageDialog(this, "模板添加成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void doDeleteTemplate() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的模板！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 2);
        int opt = JOptionPane.showConfirmDialog(this,
                "确定删除模板 '" + title + "'？", "确认删除", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            TemplateDAO dao = new TemplateDAO();
            if (dao.deleteTemplate(id)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
