package com.app.entity;

public class Template {
    private int id;
    private String category;
    private String title;
    private String codeContent;
    private int timeLimit; // 秒
    private int userId;
    private String createdAt;

    public Template() {
    }

    public Template(int id, String category, String title, String codeContent, int timeLimit, int userId) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.codeContent = codeContent;
        this.timeLimit = timeLimit;
        this.userId = userId;
    }

    // 用于插入时（无 id，数据库自增）
    public Template(String category, String title, String codeContent, int timeLimit, int userId) {
        this.category = category;
        this.title = title;
        this.codeContent = codeContent;
        this.timeLimit = timeLimit;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
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

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", codeContent='" + codeContent + '\'' +
                ", timeLimit=" + timeLimit +
                '}';
    }
}
