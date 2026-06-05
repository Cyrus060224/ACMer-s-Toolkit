package com.app.ui;

import com.app.entity.Template;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TemplateViewDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public TemplateViewDialog(Frame owner, Template template) {
        super(owner, "查看模板详情", true);
        setBounds(100, 100, 650, 500);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // 顶部信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 15, 5, 15));
        getContentPane().add(infoPanel, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("标题：" + template.getTitle());
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JLabel lblCategory = new JLabel("分类：" + template.getCategory());
        lblCategory.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JLabel lblTime = new JLabel("时间限制：" + template.getTimeLimit() + " 秒");
        lblTime.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        GroupLayout gl_info = new GroupLayout(infoPanel);
        gl_info.setHorizontalGroup(
            gl_info.createParallelGroup(Alignment.LEADING)
                .addComponent(lblTitle)
                .addGroup(gl_info.createSequentialGroup()
                    .addComponent(lblCategory)
                    .addGap(20)
                    .addComponent(lblTime))
        );
        gl_info.setVerticalGroup(
            gl_info.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(5)
                .addGroup(gl_info.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(lblTime))
        );
        infoPanel.setLayout(gl_info);

        // 代码区域
        JTextArea txtCode = new JTextArea(template.getCodeContent());
        txtCode.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtCode.setEditable(false);
        txtCode.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(txtCode);
        scrollPane.setBorder(new TitledBorder("完整代码"));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel btnPanel = new JPanel();
        FlowLayout fl_btnPanel = (FlowLayout) btnPanel.getLayout();
        fl_btnPanel.setVgap(8);
        fl_btnPanel.setHgap(15);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        JButton btnCopy = new JButton("复制代码到剪贴板");
        btnCopy.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnCopy);

        JButton btnClose = new JButton("关  闭");
        btnClose.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnClose);

        btnCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringSelection selection = new StringSelection(template.getCodeContent());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                JOptionPane.showMessageDialog(TemplateViewDialog.this, "已复制到剪贴板！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnClose);
    }
}
