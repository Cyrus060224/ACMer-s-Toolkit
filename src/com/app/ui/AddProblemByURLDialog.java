package com.app.ui;

import com.app.dao.ProblemDAO;
import com.app.entity.Problem;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 通过网址添加题目 — 自动抓取题目描述，用户填写题解和备注
 */
public class AddProblemByURLDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtUrl;
    private JButton btnFetch;
    private JTextField txtTitle;
    private JComboBox<String> cmbSource;
    private JSpinner spnDifficulty;
    private JTextField txtTags;
    private JTextArea txtDescription;
    private JTextArea txtSolution;
    private JTextArea txtNotes;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    private User currentUser;
    private boolean saved = false;

    public AddProblemByURLDialog(Frame owner, User currentUser) {
        super(owner, "通过网址添加题目", true);
        this.currentUser = currentUser;
        setBounds(100, 100, 750, 650);
        setLocationRelativeTo(owner);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        // === 顶部：URL 输入 ===
        JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
        urlPanel.setBorder(BorderFactory.createTitledBorder("题目网址"));
        txtUrl = new JTextField();
        txtUrl.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtUrl.setToolTipText("粘贴题目网址，支持 Codeforces / LeetCode / 洛谷 等");
        btnFetch = new JButton("获取题目");
        btnFetch.setFont(new Font("微软雅黑", Font.BOLD, 12));
        urlPanel.add(txtUrl, BorderLayout.CENTER);
        urlPanel.add(btnFetch, BorderLayout.EAST);
        contentPanel.add(urlPanel, BorderLayout.NORTH);

        // === 中部：题目信息 + 内容 ===
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setResizeWeight(0.35);

        // 上半部分：基本信息
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 8, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("题目信息"));

        infoPanel.add(new JLabel("标题："));
        txtTitle = new JTextField();
        infoPanel.add(txtTitle);

        infoPanel.add(new JLabel("来源："));
        cmbSource = new JComboBox<>(new String[]{
                "Codeforces", "LeetCode", "洛谷", "HDU", "POJ", "AtCoder", "牛客", "其他"
        });
        infoPanel.add(cmbSource);

        infoPanel.add(new JLabel("难度(1-5)："));
        spnDifficulty = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        infoPanel.add(spnDifficulty);

        infoPanel.add(new JLabel("标签："));
        txtTags = new JTextField();
        infoPanel.add(txtTags);

        mainSplit.setTopComponent(infoPanel);

        // 下半部分：三个文本区（题目描述 / 题解 / 备注）
        JTabbedPane tabbedPane = new JTabbedPane();

        txtDescription = new JTextArea();
        txtDescription.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        tabbedPane.addTab("题目描述（自动获取）", new JScrollPane(txtDescription));

        txtSolution = new JTextArea();
        txtSolution.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtSolution.setTabSize(4);
        tabbedPane.addTab("题解代码（自己写）", new JScrollPane(txtSolution));

        txtNotes = new JTextArea();
        txtNotes.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        tabbedPane.addTab("备注（自己写）", new JScrollPane(txtNotes));

        mainSplit.setBottomComponent(tabbedPane);
        contentPanel.add(mainSplit, BorderLayout.CENTER);

        // === 底部：状态 + 按钮 ===
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblStatus = new JLabel("粘贴题目网址，点击【获取题目】");
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setVisible(false);
        statusPanel.add(lblStatus);
        statusPanel.add(progressBar);
        bottomPanel.add(statusPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnSave = new JButton("保存题目");
        btnSave.setFont(new Font("微软雅黑", Font.BOLD, 12));
        JButton btnCancel = new JButton("取消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // === 事件绑定 ===
        btnFetch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doFetch();
            }
        });

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSave();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 回车也能获取
        txtUrl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doFetch();
            }
        });

        getRootPane().setDefaultButton(btnSave);
    }

    private void doFetch() {
        String urlStr = txtUrl.getText().trim();
        if (urlStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入题目网址！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 自动识别来源
        autoDetectSource(urlStr);

        btnFetch.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        lblStatus.setText("正在获取题目...");
        lblStatus.setForeground(Color.BLUE);

        new Thread(new Runnable() {
            public void run() {
                try {
                    // 如果是 http，先转成 https
                    String finalUrl = urlStr;
                    if (finalUrl.startsWith("http://")) {
                        finalUrl = finalUrl.replace("http://", "https://");
                    }

                    URL url = new URL(finalUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(true);
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml");
                    conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        // 网站拒绝自动访问，提示用户手动操作
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                txtDescription.setText("(网站拒绝自动获取，请手动打开网址复制题目描述粘贴到这里)");
                                txtDescription.setForeground(Color.RED);
                                lblStatus.setText("网站返回 " + responseCode + "，请手动复制题目描述");
                                lblStatus.setForeground(Color.ORANGE);
                                progressBar.setVisible(false);
                                btnFetch.setEnabled(true);
                                txtTitle.requestFocus();
                            }
                        });
                        return;
                    }

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    conn.disconnect();

                    String html = sb.toString();
                    final String title = extractTitle(html, urlStr);
                    final String description = extractDescription(html, urlStr);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (!title.isEmpty() && txtTitle.getText().isEmpty()) {
                                txtTitle.setText(title);
                            }
                            txtDescription.setText(description);
                            lblStatus.setText("获取成功！请补充题解和备注后保存");
                            lblStatus.setForeground(new Color(0, 128, 0));
                            progressBar.setVisible(false);
                            btnFetch.setEnabled(true);
                        }
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            lblStatus.setText("获取失败：" + ex.getMessage());
                            lblStatus.setForeground(Color.RED);
                            progressBar.setVisible(false);
                            btnFetch.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 根据 URL 自动识别题目来源
     */
    private void autoDetectSource(String url) {
        String lower = url.toLowerCase();
        if (lower.contains("codeforces.com")) {
            cmbSource.setSelectedItem("Codeforces");
        } else if (lower.contains("leetcode.com") || lower.contains("leetcode.cn")) {
            cmbSource.setSelectedItem("LeetCode");
        } else if (lower.contains("luogu.com.cn") || lower.contains("luogu.com")) {
            cmbSource.setSelectedItem("洛谷");
        } else if (lower.contains("acm.hdu.edu.cn")) {
            cmbSource.setSelectedItem("HDU");
        } else if (lower.contains("poj.org")) {
            cmbSource.setSelectedItem("POJ");
        } else if (lower.contains("atcoder.jp")) {
            cmbSource.setSelectedItem("AtCoder");
        } else if (lower.contains("nowcoder.com")) {
            cmbSource.setSelectedItem("牛客");
        }
    }

    /**
     * 从 HTML 中提取标题
     */
    private String extractTitle(String html, String url) {
        // 通用：<title>xxx</title>
        int titleStart = html.indexOf("<title>");
        int titleEnd = html.indexOf("</title>");
        if (titleStart >= 0 && titleEnd > titleStart) {
            String title = html.substring(titleStart + 7, titleEnd).trim();
            // 清理常见后缀
            title = title.replace(" - Codeforces", "")
                         .replace(" - LeetCode", "")
                         .replace(" - 洛谷", "")
                         .replace(" | 牛客网", "");
            return title;
        }
        return "";
    }

    /**
     * 从 HTML 中提取题目描述（针对不同网站适配）
     */
    private String extractDescription(String html, String url) {
        String lower = url.toLowerCase();

        if (lower.contains("codeforces.com")) {
            return extractBetween(html, "problem-statement", "</div>", 3);
        } else if (lower.contains("leetcode.com") || lower.contains("leetcode.cn")) {
            return extractBetween(html, "question-content", "</div>", 2);
        } else if (lower.contains("luogu.com")) {
            return extractBetween(html, "problem-content", "</div>", 2);
        } else {
            // 通用：<body> 内的纯文本
            return htmlToPlainText(html);
        }
    }

    /**
     * 简单提取：从某个标记开始，匹配若干层 div 后的内容
     */
    private String extractBetween(String html, String startMarker, String endTag, int depth) {
        int start = html.indexOf(startMarker);
        if (start < 0) return "(无法自动解析题目描述，请手动复制粘贴)";

        int divStart = html.indexOf(">", start);
        if (divStart < 0) return "(解析失败)";

        int pos = divStart + 1;
        int divDepth = 1;
        int maxEnd = divStart;

        // 找到指定深度后的结束位置
        while (divDepth > 0 && pos < html.length() && pos < divStart + 50000) {
            int nextOpen = html.indexOf("<div", pos);
            int nextClose = html.indexOf(endTag, pos);

            if (nextClose < 0) break;

            if (nextOpen >= 0 && nextOpen < nextClose) {
                divDepth++;
                pos = nextOpen + 4;
            } else {
                divDepth--;
                maxEnd = nextClose + endTag.length();
                pos = nextClose + endTag.length();
            }
        }

        String content = html.substring(divStart + 1, maxEnd);
        return htmlToPlainText(content);
    }

    /**
     * HTML → 纯文本
     */
    private String htmlToPlainText(String html) {
        StringBuilder sb = new StringBuilder();
        boolean inTag = false;
        for (int i = 0; i < html.length(); i++) {
            char c = html.charAt(i);
            if (c == '<') {
                inTag = true;
                String peek = html.substring(i, Math.min(i + 5, html.length())).toLowerCase();
                if (peek.startsWith("<br") || peek.startsWith("<p")) {
                    sb.append("\n");
                }
            } else if (c == '>') {
                inTag = false;
            } else if (!inTag) {
                if (c == '&') {
                    int semi = html.indexOf(';', i);
                    if (semi >= 0 && semi < i + 10) {
                        String entity = html.substring(i, semi + 1);
                        if (entity.equals("&lt;")) sb.append("<");
                        else if (entity.equals("&gt;")) sb.append(">");
                        else if (entity.equals("&amp;")) sb.append("&");
                        else if (entity.equals("&quot;")) sb.append("\"");
                        else if (entity.equals("&nbsp;")) sb.append(" ");
                        else sb.append(" ");
                        i = semi;
                    }
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString().replaceAll("\n{3,}", "\n\n").trim();
    }

    private void doSave() {
        String title = txtTitle.getText().trim();
        String description = txtDescription.getText().trim();
        String solution = txtSolution.getText().trim();
        String notes = txtNotes.getText().trim();
        String tags = txtTags.getText().trim();
        String source = (String) cmbSource.getSelectedItem();
        int difficulty = (int) spnDifficulty.getValue();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "标题不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (description.isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "题目描述为空，确定保存？", "确认", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
        }

        Problem p = new Problem();
        p.setTitle(title);
        p.setSource(source);
        p.setDifficulty(difficulty);
        p.setTags(tags);
        p.setDescription(description);
        p.setSolutionCode(solution);
        p.setNotes(notes);
        p.setUserId(currentUser.getId());

        ProblemDAO dao = new ProblemDAO();
        if (dao.addProblem(p)) {
            saved = true;
            JOptionPane.showMessageDialog(this, "题目保存成功！");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
