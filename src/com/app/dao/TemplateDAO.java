package com.app.dao;

import com.app.entity.Template;
import com.app.utils.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateDAO {

    public List<Template> getAllTemplates(int userId) {
        List<Template> list = new ArrayList<>();
        String sql = "SELECT * FROM template WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Template> getTemplatesByCategory(int userId, String category) {
        List<Template> list = new ArrayList<>();
        String sql = "SELECT * FROM template WHERE user_id = ? AND category = ? ORDER BY id DESC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Template> searchTemplates(int userId, String keyword) {
        List<Template> list = new ArrayList<>();
        String sql = "SELECT * FROM template WHERE user_id = ? AND (title LIKE ? OR category LIKE ? OR code_content LIKE ?) ORDER BY id DESC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            String pattern = "%" + keyword + "%";
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Template getTemplateById(int id) {
        String sql = "SELECT * FROM template WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addTemplate(Template t) {
        String sql = "INSERT INTO template (title, category, time_limit, code_content, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getCategory());
            ps.setInt(3, t.getTimeLimit());
            ps.setString(4, t.getCodeContent());
            ps.setInt(5, t.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTemplate(Template t) {
        String sql = "UPDATE template SET title = ?, category = ?, time_limit = ?, code_content = ? WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTitle());
            ps.setString(2, t.getCategory());
            ps.setInt(3, t.getTimeLimit());
            ps.setString(4, t.getCodeContent());
            ps.setInt(5, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTemplate(int id) {
        String sql = "DELETE FROM template WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Template mapRow(ResultSet rs) throws SQLException {
        Template t = new Template();
        t.setId(rs.getInt("id"));
        t.setTitle(rs.getString("title"));
        t.setCategory(rs.getString("category"));
        t.setTimeLimit(rs.getInt("time_limit"));
        t.setCodeContent(rs.getString("code_content"));
        t.setUserId(rs.getInt("user_id"));
        t.setCreatedAt(rs.getString("created_at"));
        return t;
    }
}
