package com.app.ui;

import com.app.dao.TemplateDAO;
import com.app.dao.ProblemDAO;
import com.app.dao.TrainingRecordDAO;
import com.app.entity.Template;
import com.app.entity.Problem;
import com.app.entity.TrainingRecord;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class StatsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private User currentUser;
    private DefaultTableModel templateStatsModel;
    private DefaultTableModel problemStatsModel;
    private DefaultTableModel trainingStatsModel;
    private JLabel lblSummary;

    public StatsPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 模板分类统计
        JPanel templatePanel = new JPanel(new BorderLayout());
        templatePanel.setBorder(new TitledBorder("模板分类统计"));
        templateStatsModel = new DefaultTableModel(new String[]{"分类", "数量", "占比"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable templateTable = new JTable(templateStatsModel);
        templateTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        templateTable.setRowHeight(22);
        templatePanel.add(new JScrollPane(templateTable), BorderLayout.CENTER);
        add(templatePanel);

        // 2. 题目难度分布
        JPanel problemPanel = new JPanel(new BorderLayout());
        problemPanel.setBorder(new TitledBorder("题目难度分布"));
        problemStatsModel = new DefaultTableModel(new String[]{"难度", "题目数", "占比"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable problemTable = new JTable(problemStatsModel);
        problemTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        problemTable.setRowHeight(22);
        problemPanel.add(new JScrollPane(problemTable), BorderLayout.CENTER);
        add(problemPanel);

        // 3. 训练历史
        JPanel trainingPanel = new JPanel(new BorderLayout());
        trainingPanel.setBorder(new TitledBorder("训练历史记录"));
        trainingStatsModel = new DefaultTableModel(new String[]{"日期", "计划时长", "实际时长", "是否完成"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable trainingTable = new JTable(trainingStatsModel);
        trainingTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        trainingTable.setRowHeight(22);
        trainingPanel.add(new JScrollPane(trainingTable), BorderLayout.CENTER);
        add(trainingPanel);

        // 4. 综合统计
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(new TitledBorder("综合统计"));
        lblSummary = new JLabel();
        lblSummary.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        lblSummary.setVerticalAlignment(SwingConstants.TOP);
        lblSummary.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        summaryPanel.add(lblSummary, BorderLayout.CENTER);
        add(summaryPanel);

        refreshData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshData();
    }

    public void refreshData() {
        if (currentUser == null) {
            templateStatsModel.setRowCount(0);
            problemStatsModel.setRowCount(0);
            trainingStatsModel.setRowCount(0);
            return;
        }
        refreshTemplateStats();
        refreshProblemStats();
        refreshTrainingStats();
        refreshSummary();
    }

    private void refreshTemplateStats() {
        templateStatsModel.setRowCount(0);
        TemplateDAO dao = new TemplateDAO();
        List<Template> allTemplates = dao.getAllTemplates(currentUser.getId());
        int total = allTemplates.size();

        // 使用 Map<String, Integer> 统计各分类数量
        Map<String, Integer> categoryCount = new LinkedHashMap<>();
        for (Template t : allTemplates) {
            String cat = t.getCategory();
            categoryCount.put(cat, categoryCount.containsKey(cat) ? categoryCount.get(cat) + 1 : 1);
        }

        // 排序：按数量降序
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(categoryCount.entrySet());
        Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        for (Map.Entry<String, Integer> entry : sorted) {
            String percent = total > 0 ? String.format("%.1f%%", entry.getValue() * 100.0 / total) : "0%";
            String bar = buildBar(entry.getValue(), total);
            templateStatsModel.addRow(new Object[]{
                    entry.getKey(), entry.getValue(), bar + " " + percent
            });
        }

        if (total > 0) {
            templateStatsModel.addRow(new Object[]{"合计", total, "100%"});
        }
    }

    private void refreshProblemStats() {
        problemStatsModel.setRowCount(0);
        ProblemDAO dao = new ProblemDAO();
        List<Problem> allProblems = dao.getAllProblems(currentUser.getId());
        int total = allProblems.size();

        // 使用 Map<Integer, Integer> 统计各难度数量
        Map<Integer, Integer> difficultyCount = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            difficultyCount.put(i, 0);
        }
        for (Problem p : allProblems) {
            int d = p.getDifficulty();
            if (d >= 1 && d <= 5) {
                difficultyCount.put(d, difficultyCount.get(d) + 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : difficultyCount.entrySet()) {
            int count = entry.getValue();
            String percent = total > 0 ? String.format("%.1f%%", count * 100.0 / total) : "0%";
            String bar = buildBar(count, total);
            problemStatsModel.addRow(new Object[]{
                    Problem.difficultyStars(entry.getKey()), count, bar + " " + percent
            });
        }

        if (total > 0) {
            problemStatsModel.addRow(new Object[]{"合计", total, "100%"});
        }
    }

    private void refreshTrainingStats() {
        trainingStatsModel.setRowCount(0);
        TrainingRecordDAO dao = new TrainingRecordDAO();
        List<TrainingRecord> records;
        if (currentUser != null) {
            records = dao.getRecordsByUser(currentUser.getId());
        } else {
            records = dao.getAllRecords();
        }

        for (TrainingRecord r : records) {
            String date = r.getCreatedAt();
            if (date != null && date.length() > 19) {
                date = date.substring(0, 19);
            }
            trainingStatsModel.addRow(new Object[]{
                    date != null ? date : "--",
                    TrainingRecord.formatSeconds(r.getPlannedSeconds()),
                    TrainingRecord.formatSeconds(r.getActualSeconds()),
                    r.isCompleted() ? "✓ 完成" : "✗ 未完成"
            });
        }
    }

    private void refreshSummary() {
        TemplateDAO templateDAO = new TemplateDAO();
        ProblemDAO problemDAO = new ProblemDAO();
        TrainingRecordDAO recordDAO = new TrainingRecordDAO();

        List<Template> templates = templateDAO.getAllTemplates(currentUser.getId());
        List<Problem> problems = problemDAO.getAllProblems(currentUser.getId());
        List<TrainingRecord> records = recordDAO.getRecordsByUser(currentUser.getId());

        // 统计总训练时长
        int totalTrainSeconds = 0;
        int completedCount = 0;
        for (TrainingRecord r : records) {
            totalTrainSeconds += r.getActualSeconds();
            if (r.isCompleted()) completedCount++;
        }

        // 统计模板分类数
        Set<String> categories = new HashSet<>();
        for (Template t : templates) {
            categories.add(t.getCategory());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='padding:10px;'>");
        sb.append("<b>📊 数据总览</b><br><br>");
        sb.append("模板总数：<b>").append(templates.size()).append("</b> 条<br>");
        sb.append("模板分类：<b>").append(categories.size()).append("</b> 个<br>");
        sb.append("题目总数：<b>").append(problems.size()).append("</b> 道<br>");
        sb.append("训练次数：<b>").append(records.size()).append("</b> 次<br>");
        sb.append("完成次数：<b>").append(completedCount).append("</b> 次<br>");
        sb.append("总训练时长：<b>").append(TrainingRecord.formatSeconds(totalTrainSeconds)).append("</b><br>");
        if (currentUser != null) {
            sb.append("<br>当前用户：<b>").append(currentUser.getUsername()).append("</b>");
        } else {
            sb.append("<br><i>登录后可查看个人统计</i>");
        }
        sb.append("</body></html>");
        lblSummary.setText(sb.toString());
    }

    private String buildBar(int count, int total) {
        if (total == 0) return "";
        int blocks = (int) Math.round(count * 10.0 / total);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            bar.append(i < blocks ? "█" : "░");
        }
        return bar.toString();
    }
}
