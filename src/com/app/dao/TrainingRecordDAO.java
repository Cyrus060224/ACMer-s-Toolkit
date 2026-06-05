package com.app.dao;

import com.app.entity.TrainingRecord;
import com.app.utils.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingRecordDAO {

    /**
     * 兼容方法 — UI 层调用的是 getRecordsByUser
     */
    public List<TrainingRecord> getRecordsByUser(int userId) {
        return getRecordsByUserId(userId);
    }

    public List<TrainingRecord> getRecordsByUserId(int userId) {
        List<TrainingRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM training_record WHERE user_id = ? ORDER BY id DESC";
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

    public boolean addRecord(TrainingRecord record) {
        String sql = "INSERT INTO training_record (user_id, preset_name, planned_seconds, actual_seconds, completed) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, record.getUserId());
            ps.setString(2, record.getPresetName());
            ps.setInt(3, record.getPlannedSeconds());
            ps.setInt(4, record.getActualSeconds());
            ps.setBoolean(5, record.isCompleted());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TrainingRecord> getAllRecords() {
        List<TrainingRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM training_record ORDER BY id DESC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取某个用户某个月份的训练总时长（秒）
     */
    public int getTotalSecondsByUserAndMonth(int userId, String yearMonth) {
        String sql = "SELECT COALESCE(SUM(actual_seconds), 0) FROM training_record WHERE user_id = ? AND substr(created_at, 1, 7) = ?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, yearMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCompletedCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM training_record WHERE user_id = ? AND completed = 1";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private TrainingRecord mapRow(ResultSet rs) throws SQLException {
        TrainingRecord r = new TrainingRecord();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setPresetName(rs.getString("preset_name"));
        r.setPlannedSeconds(rs.getInt("planned_seconds"));
        r.setActualSeconds(rs.getInt("actual_seconds"));
        r.setCompleted(rs.getBoolean("completed"));
        r.setCreatedAt(rs.getString("created_at"));
        return r;
    }
}
