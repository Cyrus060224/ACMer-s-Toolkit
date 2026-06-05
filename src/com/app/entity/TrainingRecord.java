package com.app.entity;

import com.app.utils.TimeUtils;

public class TrainingRecord {
    private int id;
    private int userId;
    private String presetName;
    private int plannedSeconds;
    private int actualSeconds;
    private boolean completed;
    private String createdAt;

    public TrainingRecord() {
    }

    public TrainingRecord(int userId, String presetName, int plannedSeconds, int actualSeconds, boolean completed) {
        this.userId = userId;
        this.presetName = presetName;
        this.plannedSeconds = plannedSeconds;
        this.actualSeconds = actualSeconds;
        this.completed = completed;
    }

    public TrainingRecord(int id, int userId, String presetName, int plannedSeconds,
                          int actualSeconds, boolean completed, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.presetName = presetName;
        this.plannedSeconds = plannedSeconds;
        this.actualSeconds = actualSeconds;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public int getPlannedSeconds() {
        return plannedSeconds;
    }

    public void setPlannedSeconds(int plannedSeconds) {
        this.plannedSeconds = plannedSeconds;
    }

    public int getActualSeconds() {
        return actualSeconds;
    }

    public void setActualSeconds(int actualSeconds) {
        this.actualSeconds = actualSeconds;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 将秒数格式化为 HH:mm:ss（委托给 TimeUtils，保持向后兼容）
     */
    public static String formatSeconds(int totalSeconds) {
        return TimeUtils.formatSeconds(totalSeconds);
    }

    @Override
    public String toString() {
        return "TrainingRecord{" +
                "id=" + id +
                ", presetName='" + presetName + '\'' +
                ", plannedSeconds=" + plannedSeconds +
                ", actualSeconds=" + actualSeconds +
                ", completed=" + completed +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
