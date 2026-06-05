package com.app.dao;

import com.app.entity.User;
import com.app.utils.DBHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * 使用 SHA-256 对密码进行哈希
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 用户注册
     */
    public boolean register(User user) {
        String sql = "INSERT INTO user (username, password, created_at) VALUES (?, ?, datetime('now'))";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, hashPassword(user.getPassword()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证登录
     * 先尝试哈希匹配（新用户），再尝试明文匹配（兼容旧数据库中的遗留用户）
     */
    public User validateLogin(String username, String password) {
        // 先尝试 SHA-256 哈希匹配
        User user = tryLogin(username, hashPassword(password));
        if (user != null) {
            return user;
        }
        // 如果哈希匹配失败，尝试明文匹配（兼容旧版数据库中的明文密码）
        // 匹配成功后，自动更新为哈希密码
        user = tryLogin(username, password);
        if (user != null) {
            upgradePasswordToHash(user.getId(), password);
        }
        return user;
    }

    /**
     * 执行登录查询
     */
    private User tryLogin(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword("");
                    user.setCreatedAt(rs.getString("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将旧用户的明文密码升级为 SHA-256 哈希密码
     */
    private void upgradePasswordToHash(int userId, String plainPassword) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashPassword(plainPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证登录（别名，保持与 UI 层兼容）
     */
    public User login(String username, String password) {
        return validateLogin(username, password);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword("");
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据用户名查找用户
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword("");
                    user.setCreatedAt(rs.getString("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断用户名是否已存在
     */
    public boolean usernameExists(String username) {
        return getUserByUsername(username) != null;
    }
}