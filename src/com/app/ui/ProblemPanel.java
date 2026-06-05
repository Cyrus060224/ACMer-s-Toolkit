package com.app.ui;

import com.app.dao.ProblemDAO;
import com.app.entity.Problem;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class ProblemPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbDifficulty;
    private JTextField txtSearch;
    private User currentUser;

    public ProblemPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 0));

        // 顶部筛选面板
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new TitledBorder(null, "题目筛选", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel lblDifficulty = new JLabel("难度：");
        lblDifficulty.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        cmbDifficulty = new JComboBox<>();
        cmbDifficulty.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cmbDifficulty.addItem("全部难度");
        cmbDifficulty.addItem("★☆☆☆☆ (1)");
        cmbDifficulty.addItem("★★☆☆☆ (2)");
        cmbDifficulty.addItem("★★★☆☆ (3)");
        cmbDifficulty.addItem("★★★★☆ (4)");
        cmbDifficulty.addItem("★★★★★ (5)");

        JLabel lblSearch = new JLabel("搜索：");
        lblSearch.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        txtSearch.setColumns(12);

        JButton btnSearch = new JButton("查  找");
        btnSearch.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JButton btnAdd = new JButton("添  加");
        btnAdd.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JButton btnRefresh = new JButton("刷  新");
        btnRefresh.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        GroupLayout gl_topPanel = new GroupLayout(topPanel);
        gl_topPanel.setHorizontalGroup(
            gl_topPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_topPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblDifficulty)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(cmbDifficulty, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGap(15)
                    .addComponent(lblSearch)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnSearch)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnAdd)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnRefresh)
                    .addContainerGap(80, Short.MAX_VALUE))
        );
        gl_topPanel.setVerticalGroup(
            gl_topPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_topPanel.createSequentialGroup()
                    .addGroup(gl_topPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblDifficulty)
                        .addComponent(cmbDifficulty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSearch)
                        .addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch)
                        .addComponent(btnAdd)
                        .addComponent(btnRefresh))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        topPanel.setLayout(gl_topPanel);
        add(topPanel, BorderLayout.NORTH);

        // 表格
        String[] columnNames = {"ID", "标题", "来源", "难度", "标签", "备注预览"};
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
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem popView = new JMenuItem("查看详情...");
        JMenuItem popEdit = new JMenuItem("编辑...");
        JMenuItem popDelete = new JMenuItem("删除");
        popupMenu.add(popView);
        popupMenu.add(popEdit);
        popupMenu.addSeparator();
        popupMenu.add(popDelete);
        table.setComponentPopupMenu(popupMenu);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    doViewProblem();
                }
            }
        });

        // 事件绑定
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });

        cmbDifficulty.addActionListener(new ActionListener() {
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

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doAddProblem();
            }
        });

        popView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doViewProblem();
            }
        });

        popEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doEditProblem();
            }
        });

        popDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doDeleteProblem();
            }
        });

        refreshData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        if (currentUser == null) return; // 未登录不显示数据

        ProblemDAO dao = new ProblemDAO();
        java.util.List<Problem> list;

        int selectedIdx = cmbDifficulty.getSelectedIndex();
        if (selectedIdx == 0) {
            list = dao.getAllProblems(currentUser.getId());
        } else {
            list = dao.getProblemsByDifficulty(currentUser.getId(), selectedIdx);
        }

        for (Problem p : list) {
            String notesPreview = p.getNotes();
            if (notesPreview != null && notesPreview.length() > 40) {
                notesPreview = notesPreview.substring(0, 40) + "...";
            }
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getTitle(),
                    p.getSource() != null ? p.getSource() : "",
                    Problem.difficultyStars(p.getDifficulty()),
                    p.getTags() != null ? p.getTags() : "",
                    notesPreview != null ? notesPreview : ""
            });
        }
    }

    private void doSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        if (currentUser == null) return;

        tableModel.setRowCount(0);
        ProblemDAO dao = new ProblemDAO();
        java.util.List<Problem> list = dao.searchProblems(currentUser.getId(), keyword);
        for (Problem p : list) {
            String notesPreview = p.getNotes();
            if (notesPreview != null && notesPreview.length() > 40) {
                notesPreview = notesPreview.substring(0, 40) + "...";
            }
            tableModel.addRow(new Object[]{
                    p.getId(), p.getTitle(), p.getSource() != null ? p.getSource() : "",
                    Problem.difficultyStars(p.getDifficulty()),
                    p.getTags() != null ? p.getTags() : "",
                    notesPreview != null ? notesPreview : ""
            });
        }
    }

    private void doViewProblem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个题目！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        ProblemDAO dao = new ProblemDAO();
        Problem p = dao.getProblemById(id);
        if (p != null) {
            ProblemViewDialog dialog = new ProblemViewDialog((Frame) getTopLevelAncestor(), p);
            dialog.setVisible(true);
        }
    }

    public void doAddProblem() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField txtTitle = new JTextField();
        JComboBox<String> cmbSource = new JComboBox<>(new String[]{
                "Codeforces", "LeetCode", "洛谷", "HDU", "POJ", "AtCoder", "其他"
        });
        JSpinner spnDifficulty = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        JTextField txtTags = new JTextField();
        JTextArea txtDesc = new JTextArea(3, 25);
        txtDesc.setLineWrap(true);
        JTextArea txtNotes = new JTextArea(2, 25);
        txtNotes.setLineWrap(true);
        JTextArea txtSolution = new JTextArea(5, 25);
        txtSolution.setFont(new Font("Consolas", Font.PLAIN, 12));

        panel.add(new JLabel("标题："));
        panel.add(txtTitle);
        panel.add(new JLabel("来源："));
        panel.add(cmbSource);
        panel.add(new JLabel("难度(1-5)："));
        panel.add(spnDifficulty);
        panel.add(new JLabel("标签(逗号分隔)："));
        panel.add(txtTags);
        panel.add(new JLabel("题目描述："));
        panel.add(new JScrollPane(txtDesc));
        panel.add(new JLabel("备注："));
        panel.add(new JScrollPane(txtNotes));
        panel.add(new JLabel("题解代码："));
        panel.add(new JScrollPane(txtSolution));

        int result = JOptionPane.showConfirmDialog(this, panel, "添加题目",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = txtTitle.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "标题不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Problem p = new Problem(
                    title,
                    (String) cmbSource.getSelectedItem(),
                    (int) spnDifficulty.getValue(),
                    txtTags.getText().trim(),
                    txtDesc.getText(),
                    txtNotes.getText(),
                    txtSolution.getText(),
                    currentUser != null ? currentUser.getId() : 0
            );

            ProblemDAO dao = new ProblemDAO();
            if (dao.addProblem(p)) {
                JOptionPane.showMessageDialog(this, "题目添加成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doEditProblem() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个题目！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        ProblemDAO dao = new ProblemDAO();
        Problem p = dao.getProblemById(id);
        if (p == null) return;

        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField txtTitle = new JTextField(p.getTitle());
        JComboBox<String> cmbSource = new JComboBox<>(new String[]{
                "Codeforces", "LeetCode", "洛谷", "HDU", "POJ", "AtCoder", "其他"
        });
        cmbSource.setSelectedItem(p.getSource());
        JSpinner spnDifficulty = new JSpinner(new SpinnerNumberModel(p.getDifficulty(), 1, 5, 1));
        JTextField txtTags = new JTextField(p.getTags() != null ? p.getTags() : "");
        JTextArea txtDesc = new JTextArea(p.getDescription() != null ? p.getDescription() : "", 3, 25);
        txtDesc.setLineWrap(true);
        JTextArea txtNotes = new JTextArea(p.getNotes() != null ? p.getNotes() : "", 2, 25);
        txtNotes.setLineWrap(true);
        JTextArea txtSolution = new JTextArea(p.getSolutionCode() != null ? p.getSolutionCode() : "", 5, 25);
        txtSolution.setFont(new Font("Consolas", Font.PLAIN, 12));

        panel.add(new JLabel("标题："));
        panel.add(txtTitle);
        panel.add(new JLabel("来源："));
        panel.add(cmbSource);
        panel.add(new JLabel("难度(1-5)："));
        panel.add(spnDifficulty);
        panel.add(new JLabel("标签(逗号分隔)："));
        panel.add(txtTags);
        panel.add(new JLabel("题目描述："));
        panel.add(new JScrollPane(txtDesc));
        panel.add(new JLabel("备注："));
        panel.add(new JScrollPane(txtNotes));
        panel.add(new JLabel("题解代码："));
        panel.add(new JScrollPane(txtSolution));

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑题目",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            p.setTitle(txtTitle.getText().trim());
            p.setSource((String) cmbSource.getSelectedItem());
            p.setDifficulty((int) spnDifficulty.getValue());
            p.setTags(txtTags.getText().trim());
            p.setDescription(txtDesc.getText());
            p.setNotes(txtNotes.getText());
            p.setSolutionCode(txtSolution.getText());

            if (dao.updateProblem(p)) {
                JOptionPane.showMessageDialog(this, "更新成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "更新失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doDeleteProblem() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一个题目！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        int opt = JOptionPane.showConfirmDialog(this,
                "确定删除题目 '" + title + "'？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            ProblemDAO dao = new ProblemDAO();
            if (dao.deleteProblem(id)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
