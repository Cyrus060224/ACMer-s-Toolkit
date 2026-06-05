package com.app.ui;

import com.app.dao.UserDAO;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;

    public RegisterDialog(Frame owner) {
        super(owner, "用户注册", true);
        setResizable(false);
        setBounds(100, 100, 400, 270);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(15, 15, 5, 15));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel("ACMer's Toolkit - 用户注册");
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblUsername = new JLabel("用户名：");
        JLabel lblPassword = new JLabel("密  码：");
        JLabel lblConfirm = new JLabel("确认密码：");

        txtUsername = new JTextField();
        txtUsername.setColumns(15);

        txtPassword = new JPasswordField();
        txtPassword.setColumns(15);

        txtConfirm = new JPasswordField();
        txtConfirm.setColumns(15);

        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(lblConfirm)
                        .addComponent(lblPassword)
                        .addComponent(lblUsername))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                        .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                        .addComponent(txtConfirm, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addGap(15)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUsername)
                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPassword)
                        .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblConfirm)
                        .addComponent(txtConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(20, Short.MAX_VALUE))
        );
        contentPanel.setLayout(gl_contentPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton btnRegister = new JButton("注  册");
        btnRegister.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        buttonPanel.add(btnRegister);

        JButton btnCancel = new JButton("取  消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        buttonPanel.add(btnCancel);

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnRegister);
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirm = new String(txtConfirm.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 3 || username.length() > 20) {
            JOptionPane.showMessageDialog(this, "用户名长度应为 3-20 个字符！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "密码长度不能少于 4 个字符！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "提示", JOptionPane.WARNING_MESSAGE);
            txtConfirm.setText("");
            txtConfirm.requestFocus();
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "用户名 '" + username + "' 已存在，请更换！", "注册失败", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        User newUser = new User(username, password);
        boolean success = userDAO.register(newUser);

        if (success) {
            JOptionPane.showMessageDialog(this, "注册成功！请返回登录。", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，请稍后重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
