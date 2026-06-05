package com.app.ui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // ========== 请替换为你的个人信息 ==========
    private static final String STUDENT_NAME = "王鑫";
    private static final String STUDENT_ID   = "244090126";
    private static final String CLASS_NAME   = "计算机1班";
    // ==========================================

    public AboutDialog(Frame owner) {
        super(owner, "关于 ACMer's Toolkit", true);
        setResizable(false);
        setBounds(100, 100, 420, 320);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblIcon = new JLabel("");
        lblIcon.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblAppName = new JLabel("ACMer's Toolkit");
        lblAppName.setFont(new Font("微软雅黑", Font.BOLD, 18));
        lblAppName.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblVersion = new JLabel("版本：V2.0.0");
        lblVersion.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        lblVersion.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("ACM 竞赛综合工具箱");
        lblDesc.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSep = new JLabel("─────────────────────────────");
        lblSep.setHorizontalAlignment(SwingConstants.CENTER);
        lblSep.setForeground(Color.GRAY);

        JLabel lblAuthor = new JLabel("作者：" + STUDENT_NAME + "  学号：" + STUDENT_ID);
        lblAuthor.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblAuthor.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblClass = new JLabel("班级：" + CLASS_NAME);
        lblClass.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblClass.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblCopyright = new JLabel("© 2026 All Rights Reserved");
        lblCopyright.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        lblCopyright.setForeground(Color.GRAY);
        lblCopyright.setHorizontalAlignment(SwingConstants.CENTER);

        GroupLayout gl_content = new GroupLayout(contentPanel);
        gl_content.setHorizontalGroup(
            gl_content.createParallelGroup(Alignment.CENTER)
                .addComponent(lblIcon, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblAppName, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblVersion, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblDesc, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblSep, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblAuthor, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblClass, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addComponent(lblCopyright, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
        );
        gl_content.setVerticalGroup(
            gl_content.createSequentialGroup()
                .addComponent(lblIcon, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(lblAppName, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblVersion, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblDesc, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblSep, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblAuthor, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblClass, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addGap(12)
                .addComponent(lblCopyright, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
        );
        contentPanel.setLayout(gl_content);

        // 确定按钮
        JPanel buttonPanel = new JPanel();
        FlowLayout fl_buttonPanel = (FlowLayout) buttonPanel.getLayout();
        fl_buttonPanel.setVgap(8);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton btnOk = new JButton("确  定");
        btnOk.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        buttonPanel.add(btnOk);

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnOk);
    }
}
