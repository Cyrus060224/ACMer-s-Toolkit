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

public class LoginDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private User loggedInUser = null;
    private boolean loginSuccess = false;

    public LoginDialog(Frame owner) {
        super(owner, "用户登录", true);
        setResizable(false);
        setBounds(100, 100, 380, 220);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(15, 15, 5, 15));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel("ACMer's Toolkit - 用户登录");
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblUsername = new JLabel("用户名：");
        JLabel lblPassword = new JLabel("密  码：");

        txtUsername = new JTextField();
        txtUsername.setColumns(15);

        txtPassword = new JPasswordField();
        txtPassword.setColumns(15);

        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(lblPassword)
                        .addComponent(lblUsername))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                        .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
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
                    .addContainerGap(20, Short.MAX_VALUE))
        );
        contentPanel.setLayout(gl_contentPanel);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton btnLogin = new JButton("登  录");
        btnLogin.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        buttonPanel.add(btnLogin);

        JButton btnCancel = new JButton("取  消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        buttonPanel.add(btnCancel);

        JButton btnRegister = new JButton("注册新用户");
        btnRegister.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        buttonPanel.add(btnRegister);

        // 登录按钮事件
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        // 取消按钮事件
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 注册按钮事件
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                RegisterDialog regDialog = new RegisterDialog((Frame) LoginDialog.this.getOwner());
                regDialog.setVisible(true);
            }
        });

        // 回车键触发登录
        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);

        if (user != null) {
            loggedInUser = user;
            loginSuccess = true;
            JOptionPane.showMessageDialog(this, "登录成功，欢迎 " + username + "！", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
