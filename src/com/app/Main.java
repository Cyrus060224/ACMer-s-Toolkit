package com.app;

import com.app.ui.MainFrame;
import com.app.utils.DBHelper;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // 初始化数据库（自动建表）
        if (!initDatabase()) {
            JOptionPane.showMessageDialog(null,
                    "数据库初始化失败，程序无法启动！\n请检查 SQLite JDBC 驱动是否已添加到 Build Path。",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在 EDT 线程中启动 GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "程序启动失败：" + e.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 初始化数据库：自动创建所需的表结构
     */
    private static boolean initDatabase() {
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement()) {

            // 用户表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    username TEXT NOT NULL UNIQUE," +
                "    password TEXT NOT NULL," +
                "    created_at TEXT DEFAULT (datetime('now'))" +
                ")"
            );

            // 模板表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS template (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    title TEXT NOT NULL," +
                "    category TEXT," +
                "    time_limit INTEGER DEFAULT 0," +
                "    code_content TEXT," +
                "    user_id INTEGER," +
                "    created_at TEXT DEFAULT (datetime('now'))," +
                "    FOREIGN KEY (user_id) REFERENCES user(id)" +
                ")"
            );

            // 题目表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS problem (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    title TEXT NOT NULL," +
                "    source TEXT," +
                "    difficulty INTEGER DEFAULT 1," +
                "    tags TEXT," +
                "    description TEXT," +
                "    notes TEXT," +
                "    solution_code TEXT," +
                "    user_id INTEGER," +
                "    created_at TEXT DEFAULT (datetime('now'))," +
                "    FOREIGN KEY (user_id) REFERENCES user(id)" +
                ")"
            );

            // 训练记录表
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS training_record (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    user_id INTEGER NOT NULL," +
                "    preset_name TEXT," +
                "    planned_seconds INTEGER DEFAULT 0," +
                "    actual_seconds INTEGER DEFAULT 0," +
                "    completed INTEGER DEFAULT 0," +
                "    created_at TEXT DEFAULT (datetime('now'))," +
                "    FOREIGN KEY (user_id) REFERENCES user(id)" +
                ")"
            );

            // 设置表（key-value 存储）
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS settings (" +
                "    key TEXT PRIMARY KEY," +
                "    value TEXT" +
                ")"
            );

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}