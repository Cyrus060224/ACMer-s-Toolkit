package com.app.ui;

import com.app.entity.Problem;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProblemViewDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public ProblemViewDialog(Frame owner, Problem problem) {
        super(owner, "查看题目详情", true);
        setBounds(100, 100, 600, 500);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout(0, 0));

        // 顶部信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 15, 5, 15));
        getContentPane().add(infoPanel, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel(problem.getTitle());
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JLabel lblSource = new JLabel("来源：" + (problem.getSource() != null ? problem.getSource() : "未知"));
        lblSource.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JLabel lblDifficulty = new JLabel("难度：" + Problem.difficultyStars(problem.getDifficulty()));
        lblDifficulty.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JLabel lblTags = new JLabel("标签：" + (problem.getTags() != null && !problem.getTags().isEmpty() ? problem.getTags() : "无"));
        lblTags.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        GroupLayout gl_info = new GroupLayout(infoPanel);
        gl_info.setHorizontalGroup(
            gl_info.createParallelGroup(Alignment.LEADING)
                .addComponent(lblTitle)
                .addGroup(gl_info.createSequentialGroup()
                    .addComponent(lblSource)
                    .addGap(15)
                    .addComponent(lblDifficulty)
                    .addGap(15)
                    .addComponent(lblTags))
        );
        gl_info.setVerticalGroup(
            gl_info.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(5)
                .addGroup(gl_info.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblSource)
                    .addComponent(lblDifficulty)
                    .addComponent(lblTags))
        );
        infoPanel.setLayout(gl_info);

        // 中间标签页
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 题目描述标签
        JTextArea txtDesc = new JTextArea(problem.getDescription() != null ? problem.getDescription() : "暂无描述");
        txtDesc.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        txtDesc.setEditable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        tabbedPane.addTab("题目描述", new JScrollPane(txtDesc));

        // 题解代码标签
        JTextArea txtSolution = new JTextArea(problem.getSolutionCode() != null ? problem.getSolutionCode() : "暂无题解");
        txtSolution.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtSolution.setEditable(false);
        tabbedPane.addTab("题解代码", new JScrollPane(txtSolution));

        // 备注标签
        JTextArea txtNotes = new JTextArea(problem.getNotes() != null ? problem.getNotes() : "暂无备注");
        txtNotes.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        txtNotes.setEditable(false);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        tabbedPane.addTab("备注", new JScrollPane(txtNotes));

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel btnPanel = new JPanel();
        FlowLayout fl_btnPanel = (FlowLayout) btnPanel.getLayout();
        fl_btnPanel.setVgap(8);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        JButton btnClose = new JButton("关  闭");
        btnClose.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnClose);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnClose);
    }
}
