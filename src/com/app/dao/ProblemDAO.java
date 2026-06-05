package com.app.dao;

import com.app.entity.Problem;
import com.app.utils.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemDAO {

    public List<Problem> getAllProblems(int userId) {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT * FROM problem WHERE user_id = ? ORDER BY id DESC";
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

    public List<Problem> getProblemsByDifficulty(int userId, int difficulty) {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT * FROM problem WHERE user_id = ? AND difficulty = ? ORDER BY id DESC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, difficulty);
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

    public Problem getProblemById(int id) {
        String sql = "SELECT * FROM problem WHERE id = ?";
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

    public List<Problem> searchProblems(int userId, String keyword) {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT * FROM problem WHERE user_id = ? AND (title LIKE ? OR source LIKE ? OR tags LIKE ?) ORDER BY id DESC";
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

    public boolean addProblem(Problem p) {
        String sql = "INSERT INTO problem (title, source, difficulty, tags, description, notes, solution_code, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getSource());
            ps.setInt(3, p.getDifficulty());
            ps.setString(4, p.getTags());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getNotes());
            ps.setString(7, p.getSolutionCode());
            ps.setInt(8, p.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProblem(Problem p) {
        String sql = "UPDATE problem SET title = ?, source = ?, difficulty = ?, tags = ?, description = ?, notes = ?, solution_code = ? WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getSource());
            ps.setInt(3, p.getDifficulty());
            ps.setString(4, p.getTags());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getNotes());
            ps.setString(7, p.getSolutionCode());
            ps.setInt(8, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProblem(int id) {
        String sql = "DELETE FROM problem WHERE id = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将 ResultSet 当前行映射为 Problem 对象
     */
    private Problem mapRow(ResultSet rs) throws SQLException {
        Problem p = new Problem();
        p.setId(rs.getInt("id"));
        p.setTitle(rs.getString("title"));
        p.setSource(rs.getString("source"));
        p.setDifficulty(rs.getInt("difficulty"));
        p.setTags(rs.getString("tags"));
        p.setDescription(rs.getString("description"));
        p.setNotes(rs.getString("notes"));
        p.setSolutionCode(rs.getString("solution_code"));
        p.setUserId(rs.getInt("user_id"));
        p.setCreatedAt(rs.getString("created_at"));
        return p;
    }
}
