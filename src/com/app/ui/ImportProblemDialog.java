package com.app.ui;

import com.app.dao.ProblemDAO;
import com.app.entity.Problem;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线导入题目对话框
 * 从 Codeforces 公开 API 获取题目，导入到本地数据库
 */
public class ImportProblemDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final String CODEFORCES_API = "https://codeforces.com/api/problemset.problems";

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnFetch;
    private JButton btnImport;
    private JButton btnCancel;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    private User currentUser;
    private List<String[]> problemData = new ArrayList<>();
    private int importedCount = 0;

    public ImportProblemDialog(Frame owner, User currentUser) {
        super(owner, "在线导入题目", true);
        this.currentUser = currentUser;
        setBounds(100, 100, 700, 450);
        setLocationRelativeTo(owner);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        // 顶部说明
        JLabel lblInfo = new JLabel("从 Codeforces 获取题目，选择后导入到本地题库");
        lblInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        contentPanel.add(lblInfo, BorderLayout.NORTH);

        // 中部表格
        tableModel = new DefaultTableModel(new String[]{"题号", "名称", "难度", "标签"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部控制
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnFetch = new JButton("获取题目列表");
        btnFetch.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblStatus = new JLabel("点击【获取题目列表】从 Codeforces 加载");
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        leftPanel.add(btnFetch);
        leftPanel.add(Box.createHorizontalStrut(15));
        leftPanel.add(lblStatus);

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 20));
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        leftPanel.add(progressBar);

        bottomPanel.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnImport = new JButton("导入选中题目");
        btnImport.setFont(new Font("微软雅黑", Font.BOLD, 12));
        btnImport.setEnabled(false);
        btnCancel = new JButton("取消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        rightPanel.add(btnImport);
        rightPanel.add(btnCancel);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 事件绑定
        btnFetch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doFetch();
            }
        });

        btnImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doImport();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void doFetch() {
        btnFetch.setEnabled(false);
        lblStatus.setText("正在从 Codeforces 获取题目...");
        lblStatus.setForeground(Color.BLUE);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(CODEFORCES_API);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(15000);
                    conn.setRequestProperty("User-Agent", "ACMer-Toolkit/2.0");

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        throw new Exception("HTTP " + responseCode);
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    conn.disconnect();

                    String json = sb.toString();
                    parseProblems(json);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            populateTable();
                            progressBar.setIndeterminate(false);
                            progressBar.setVisible(false);
                            lblStatus.setText("共获取 " + problemData.size() + " 道题目，请选择要导入的题目");
                            lblStatus.setForeground(new Color(0, 128, 0));
                            btnFetch.setEnabled(true);
                            btnImport.setEnabled(true);
                        }
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            progressBar.setVisible(false);
                            lblStatus.setText("获取失败：" + ex.getMessage());
                            lblStatus.setForeground(Color.RED);
                            btnFetch.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 简单解析 Codeforces API JSON（不依赖第三方库）
     * 解析 problemset.problems 数组中的对象
     */
    private void parseProblems(String json) {
        problemData.clear();

        // 找 problems 数组
        int problemsStart = json.indexOf("\"problems\":");
        if (problemsStart < 0) return;
        int arrStart = json.indexOf("[", problemsStart);
        if (arrStart < 0) return;

        // 简单的括号匹配找到数组结束位置
        int depth = 0;
        int arrEnd = -1;
        for (int i = arrStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) {
                    arrEnd = i;
                    break;
                }
            }
        }
        if (arrEnd < 0) return;

        String arr = json.substring(arrStart + 1, arrEnd);

        // 逐个解析 {...} 块
        int i = 0;
        while (i < arr.length() && problemData.size() < 200) { // 限制200条
            while (i < arr.length() && arr.charAt(i) != '{') i++;
            if (i >= arr.length()) break;

            int objStart = i;
            int objEnd = arr.indexOf('}', i);
            if (objEnd < 0) break;
            String obj = arr.substring(objStart, objEnd + 1);

            String contestId = extractField(obj, "contestId");
            String index = extractField(obj, "index");
            String name = extractField(obj, "name");
            int rating = extractIntField(obj, "rating");
            String tags = extractArrayField(obj, "tags");

            if (contestId != null && index != null && name != null) {
                String problemId = contestId + index;
                String difficulty = rating > 0 ? String.valueOf(rating) : "-";
                problemData.add(new String[]{problemId, name, difficulty, tags});
            }

            i = objEnd + 1;
        }
    }

    private String extractField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + key.length());
        if (colon < 0) return null;
        int quoteStart = json.indexOf('"', colon + 1);
        if (quoteStart < 0) return null;
        int quoteEnd = json.indexOf('"', quoteStart + 1);
        if (quoteEnd < 0) return null;
        return json.substring(quoteStart + 1, quoteEnd);
    }

    private int extractIntField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return -1;
        int colon = json.indexOf(':', idx + key.length());
        if (colon < 0) return -1;
        int start = colon + 1;
        while (start < json.length() && !Character.isDigit(json.charAt(start)) && json.charAt(start) != '-') start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        if (end > start) {
            try {
                return Integer.parseInt(json.substring(start, end));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private String extractArrayField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        int bracketStart = json.indexOf('[', idx + key.length());
        if (bracketStart < 0) return "";
        int bracketEnd = json.indexOf(']', bracketStart);
        if (bracketEnd < 0) return "";
        String arr = json.substring(bracketStart + 1, bracketEnd).replace("\"", "").trim();
        return arr.isEmpty() ? "" : arr;
    }

    /**
     * 从 Codeforces 网页抓取题目描述
     * @param problemId 格式如 "123A"
     */
    private String fetchProblemDescription(String problemId) {
        try {
            // 解析题号：contestId + index（如 "123A" → contestId=123, index=A）
            String contestId = problemId.replaceAll("[A-Za-z]+$", "");
            String index = problemId.replaceAll("^[0-9]+", "");
            if (contestId.isEmpty() || index.isEmpty()) return "";

            String urlStr = "https://codeforces.com/problemset/problem/" + contestId + "/" + index;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "(HTTP " + responseCode + " - 无法获取题目描述)";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            conn.disconnect();

            String html = sb.toString();
            return extractProblemStatement(html);

        } catch (Exception e) {
            return "(获取失败: " + e.getMessage() + ")";
        }
    }

    /**
     * 从 Codeforces HTML 中提取题目描述
     * 题目内容在 <div class="problem-statement"> 标签内
     */
    private String extractProblemStatement(String html) {
        // 找到 problem-statement div
        int start = html.indexOf("problem-statement");
        if (start < 0) return "(未找到题目描述)";

        // 找到这个 div 的内容开始位置（> 之后）
        int divStart = html.indexOf(">", start);
        if (divStart < 0) return "(解析失败)";

        // 找到对应的关闭 div（简单深度匹配）
        int depth = 1;
        int pos = divStart + 1;
        while (depth > 0 && pos < html.length()) {
            int nextOpen = html.indexOf("<div", pos);
            int nextClose = html.indexOf("</div>", pos);

            if (nextClose < 0) break;

            if (nextOpen >= 0 && nextOpen < nextClose) {
                depth++;
                pos = nextOpen + 4;
            } else {
                depth--;
                if (depth == 0) {
                    // 提取纯文本
                    String content = html.substring(divStart + 1, nextClose);
                    return htmlToPlainText(content);
                }
                pos = nextClose + 6;
            }
        }

        return "(解析失败)";
    }

    /**
     * 简单的 HTML → 纯文本转换
     */
    private String htmlToPlainText(String html) {
        StringBuilder sb = new StringBuilder();
        boolean inTag = false;
        boolean inEntity = false;
        for (int i = 0; i < html.length(); i++) {
            char c = html.charAt(i);
            if (c == '<') {
                inTag = true;
                // <br> 和 <br/> 换行
                String peek = html.substring(i, Math.min(i + 4, html.length())).toLowerCase();
                if (peek.startsWith("<br") || peek.startsWith("<p")) {
                    sb.append("\n");
                }
            } else if (c == '>') {
                inTag = false;
            } else if (c == '&') {
                inEntity = true;
                // 常见 HTML 实体
                int semi = html.indexOf(';', i);
                if (semi >= 0 && semi < i + 10) {
                    String entity = html.substring(i, semi + 1);
                    if (entity.equals("&lt;")) sb.append("<");
                    else if (entity.equals("&gt;")) sb.append(">");
                    else if (entity.equals("&amp;")) sb.append("&");
                    else if (entity.equals("&quot;")) sb.append("\"");
                    else if (entity.equals("&nbsp;")) sb.append(" ");
                    else if (entity.equals("&#10;") || entity.equals("&#xa;")) sb.append("\n");
                    else sb.append(" ");
                    i = semi;
                    inEntity = false;
                }
            } else if (!inTag && !inEntity) {
                sb.append(c);
            }
        }
        // 清理多余空行
        String result = sb.toString().replaceAll("\n{3,}", "\n\n").trim();
        return result;
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        for (String[] row : problemData) {
            tableModel.addRow(row);
        }
    }

    private void doImport() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请先选择要导入的题目！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnImport.setEnabled(false);
        progressBar.setVisible(false);

        ProblemDAO dao = new ProblemDAO();
        importedCount = 0;
        final int total = selectedRows.length;

        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < selectedRows.length; i++) {
                    final int idx = i;
                    final int row = selectedRows[i];
                    String id = (String) tableModel.getValueAt(row, 0);
                    String name = (String) tableModel.getValueAt(row, 1);
                    String diffStr = (String) tableModel.getValueAt(row, 2);
                    String tags = (String) tableModel.getValueAt(row, 3);

                    // 更新进度
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            lblStatus.setText("正在导入第 " + (idx + 1) + "/" + total + " 题...");
                            progressBar.setVisible(true);
                            progressBar.setIndeterminate(false);
                            progressBar.setMaximum(total);
                            progressBar.setValue(idx);
                        }
                    });

                    int difficulty = 1;
                    try {
                        int rating = Integer.parseInt(diffStr);
                        if (rating <= 1200) difficulty = 1;
                        else if (rating <= 1600) difficulty = 2;
                        else if (rating <= 1900) difficulty = 3;
                        else if (rating <= 2400) difficulty = 4;
                        else difficulty = 5;
                    } catch (NumberFormatException ignored) {}

                    // 从 Codeforces 网页抓取题目描述
                    String description = fetchProblemDescription(id);

                    Problem p = new Problem();
                    p.setTitle(id + " - " + name);
                    p.setSource("Codeforces");
                    p.setDifficulty(difficulty);
                    p.setTags(tags);
                    p.setDescription(description);
                    p.setUserId(currentUser.getId());

                    if (dao.addProblem(p)) {
                        importedCount++;
                    }
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        progressBar.setValue(total);
                        lblStatus.setText("导入完成！共 " + importedCount + " 题");
                        JOptionPane.showMessageDialog(ImportProblemDialog.this,
                                "成功导入 " + importedCount + " 道题目（含题目描述）！",
                                "导入完成", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                });
            }
        }).start();
    }
}
