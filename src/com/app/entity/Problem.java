package com.app.entity;

public class Problem {
    private int id;
    private String title;
    private String source;
    private int difficulty; // 1-5
    private String tags;
    private String description;
    private String notes;
    private String solutionCode;
    private int userId;
    private String createdAt;

    public Problem() {
    }

    public Problem(String title, String source, int difficulty, String tags,
                   String description, String notes, String solutionCode, int userId) {
        this.title = title;
        this.source = source;
        this.difficulty = difficulty;
        this.tags = tags;
        this.description = description;
        this.notes = notes;
        this.solutionCode = solutionCode;
        this.userId = userId;
    }

    public Problem(int id, String title, String source, int difficulty, String tags,
                   String description, String notes, String solutionCode, int userId, String createdAt) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.difficulty = difficulty;
        this.tags = tags;
        this.description = description;
        this.notes = notes;
        this.solutionCode = solutionCode;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSolutionCode() {
        return solutionCode;
    }

    public void setSolutionCode(String solutionCode) {
        this.solutionCode = solutionCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static String difficultyStars(int difficulty) {
        if (difficulty < 1) difficulty = 1;
        if (difficulty > 5) difficulty = 5;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < difficulty ? "★" : "☆");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", difficulty=" + difficulty +
                ", tags='" + tags + '\'' +
                '}';
    }
}
