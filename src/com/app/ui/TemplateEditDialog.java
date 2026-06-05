package com.app.ui;

import com.app.dao.TemplateDAO;
import com.app.entity.Template;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TemplateEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JTextField txtTitle;
    private JComboBox<String> cmbCategory;
    private JTextField txtTimeLimit;
    private JTextArea txtCode;
    private boolean saved = false;
    private Template template;

    public TemplateEditDialog(Frame owner, Template template) {
        super(owner, "编辑模板", true);
        this.template = template;
        setResizable(false);
        setBounds(100, 100, 500, 420);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(15, 15, 5, 15));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblTitleLabel = new JLabel("标题：");
        lblTitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JLabel lblCategoryLabel = new JLabel("分类：");
        lblCategoryLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JLabel lblTimeLabel = new JLabel("时间限制(秒)：");
        lblTimeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JLabel lblCodeLabel = new JLabel("代码内容：");
        lblCodeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        txtTitle = new JTextField(template.getTitle());
        txtTitle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        txtTitle.setColumns(20);

        cmbCategory = new JComboBox<>(new String[]{
                "动态规划", "贪心算法", "图论", "数据结构", "数论", "字符串", "搜索", "其他"
        });
        cmbCategory.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cmbCategory.setSelectedItem(template.getCategory());

        txtTimeLimit = new JTextField(String.valueOf(template.getTimeLimit()));
        txtTimeLimit.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        txtTimeLimit.setColumns(10);

        txtCode = new JTextArea(template.getCodeContent());
        txtCode.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtCode.setRows(8);
        JScrollPane codeScroll = new JScrollPane(txtCode);

        GroupLayout gl_content = new GroupLayout(contentPanel);
        gl_content.setHorizontalGroup(
            gl_content.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_content.createSequentialGroup()
                    .addGroup(gl_content.createParallelGroup(Alignment.TRAILING)
                        .addComponent(lblCodeLabel)
                        .addComponent(lblTimeLabel)
                        .addComponent(lblCategoryLabel)
                        .addComponent(lblTitleLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_content.createParallelGroup(Alignment.LEADING)
                        .addComponent(txtTitle, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(cmbCategory, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTimeLimit, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        .addComponent(codeScroll, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_content.setVerticalGroup(
            gl_content.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_content.createSequentialGroup()
                    .addGroup(gl_content.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblTitleLabel)
                        .addComponent(txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_content.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblCategoryLabel)
                        .addComponent(cmbCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_content.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblTimeLabel)
                        .addComponent(txtTimeLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_content.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblCodeLabel)
                        .addComponent(codeScroll, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPanel.setLayout(gl_content);

        // 按钮面板
        JPanel btnPanel = new JPanel();
        FlowLayout fl_btnPanel = (FlowLayout) btnPanel.getLayout();
        fl_btnPanel.setVgap(8);
        fl_btnPanel.setHgap(15);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        JButton btnSave = new JButton("保  存");
        btnSave.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnSave);

        JButton btnCancel = new JButton("取  消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnCancel);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSave();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnSave);
    }

    private void doSave() {
        String title = txtTitle.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        String code = txtCode.getText();
        int timeLimit;

        if (title.isEmpty() || code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "标题和代码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            timeLimit = Integer.parseInt(txtTimeLimit.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "时间限制必须是整数！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        template.setTitle(title);
        template.setCategory(category);
        template.setCodeContent(code);
        template.setTimeLimit(timeLimit);

        TemplateDAO dao = new TemplateDAO();
        if (dao.updateTemplate(template)) {
            saved = true;
            JOptionPane.showMessageDialog(this, "保存成功！");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
