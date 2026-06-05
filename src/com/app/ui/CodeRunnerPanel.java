package com.app.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class CodeRunnerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextArea txtCode;
    private JTextArea txtInput;
    private JTextArea txtOutput;
    private JButton btnRun;
    private JButton btnStop;
    private JButton btnClear;
    private JLabel lblStatus;
    private JLabel lblTime;
    private JComboBox<String> cmbLanguage;
    private TitledBorder codeBorder;

    private Thread runThread;
    private Process runningProcess;
    private volatile boolean running = false;
    private static final int TIMEOUT_SECONDS = 10;
    private static final String TEMP_DIR = "data/temp";

    // ========== 语言配置 ==========

    private static final String[] LANG_NAMES = {
            "C (GCC)", "C++ (G++)", "Java", "Python 3"
    };

    // 文件扩展名
    private static final String[] LANG_EXTENSIONS = {
            ".c", ".cpp", ".java", ".py"
    };

    // 源文件名（固定 Solution + 扩展名）
    private static final String[] LANG_FILENAMES = {
            "Solution.c", "Solution.cpp", "Solution.java", "Solution.py"
    };

    // 编译命令（null 表示无需编译）
    private static final String[][] LANG_COMPILE_CMD = {
            {"gcc", "-o", null, null, "-lm"},     // gcc -o Solution.exe Solution.c -lm
            {"g++", "-o", null, null, "-lm"},     // g++ -o Solution.exe Solution.cpp -lm
            null,                                  // Java 单独处理
            null                                   // Python 无需编译
    };

    // 运行命令
    private static final String[][] LANG_RUN_CMD = {
            {null},                                // Solution.exe
            {null},                                // Solution.exe
            null,                                  // Java 单独处理
            {"python", null}                       // python Solution.py
    };

    // 默认代码模板
    private static final String[] LANG_DEFAULT_CODE = {
            "#include <stdio.h>\n\nint main() {\n    // 在此编写你的代码\n    printf(\"Hello, ACMer!\\n\");\n    return 0;\n}",
            "#include <iostream>\nusing namespace std;\n\nint main() {\n    // 在此编写你的代码\n    cout << \"Hello, ACMer!\" << endl;\n    return 0;\n}",
            "public class Solution {\n    public static void main(String[] args) {\n        // 在此编写你的代码\n        System.out.println(\"Hello, ACMer!\");\n    }\n}",
            "# 在此编写你的代码\nprint(\"Hello, ACMer!\")"
    };

    // 临时文件清理列表（扩展名）
    private static final String[][] LANG_CLEAN_EXTENSIONS = {
            {".exe", ".o"},        // C
            {".exe", ".o"},        // C++
            {".class"},            // Java
            {}                     // Python
    };

    // ========== 工具路径 ==========

    private final String javacPath;
    private final String javaPath;

    private static String[] detectJavaTools() {
        String javaHome = System.getProperty("java.home");
        File jreBin = new File(javaHome, "bin");
        File jdkBin = new File(jreBin, "javac").exists()
                ? jreBin
                : new File(jreBin.getParentFile(), "bin");

        String javac = new File(jdkBin, "javac").getAbsolutePath();
        String java = new File(jdkBin, "java").getAbsolutePath();

        if (!new File(javac).exists()) javac = "javac";
        if (!new File(java).exists()) java = "java";
        return new String[]{javac, java};
    }

    // ========== 构造函数 ==========

    public CodeRunnerPanel() {
        String[] tools = detectJavaTools();
        this.javacPath = tools[0];
        this.javaPath = tools[1];
        setLayout(new BorderLayout(0, 0));

        // 顶部工具栏
        JPanel toolbar = new JPanel();
        FlowLayout fl_toolbar = (FlowLayout) toolbar.getLayout();
        fl_toolbar.setAlignment(FlowLayout.LEFT);
        fl_toolbar.setHgap(10);

        JLabel lblLang = new JLabel("语言：");
        lblLang.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        cmbLanguage = new JComboBox<>(LANG_NAMES);
        cmbLanguage.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cmbLanguage.setSelectedIndex(2); // 默认 Java

        btnRun = new JButton("▶ 运行");
        btnRun.setFont(new Font("微软雅黑", Font.BOLD, 12));
        btnRun.setForeground(new Color(0, 128, 0));

        btnStop = new JButton("■ 终止");
        btnStop.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnStop.setEnabled(false);
        btnStop.setForeground(Color.RED);

        btnClear = new JButton("清空");
        btnClear.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        lblStatus = new JLabel("就绪");
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        lblTime = new JLabel("耗时: --ms");
        lblTime.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        toolbar.add(lblLang);
        toolbar.add(cmbLanguage);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnRun);
        toolbar.add(btnStop);
        toolbar.add(btnClear);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(lblStatus);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(lblTime);
        add(toolbar, BorderLayout.NORTH);

        // 中间分割面板
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setResizeWeight(0.6);

        // 代码编辑区
        txtCode = new JTextArea(LANG_DEFAULT_CODE[2]); // 默认 Java
        txtCode.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        txtCode.setTabSize(4);
        JScrollPane codeScroll = new JScrollPane(txtCode);
        codeBorder = new TitledBorder("代码编辑器 (Java)");
        codeScroll.setBorder(codeBorder);

        // 下半部分：输入和输出
        JSplitPane ioSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        ioSplit.setResizeWeight(0.4);

        txtInput = new JTextArea(3, 40);
        txtInput.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        txtInput.setTabSize(4);
        JScrollPane inputScroll = new JScrollPane(txtInput);
        inputScroll.setBorder(new TitledBorder("标准输入 (stdin)"));

        txtOutput = new JTextArea(4, 40);
        txtOutput.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        txtOutput.setEditable(false);
        txtOutput.setBackground(new Color(248, 248, 248));
        JScrollPane outputScroll = new JScrollPane(txtOutput);
        outputScroll.setBorder(new TitledBorder("运行结果 (stdout/stderr)"));

        ioSplit.setTopComponent(inputScroll);
        ioSplit.setBottomComponent(outputScroll);

        mainSplit.setTopComponent(codeScroll);
        mainSplit.setBottomComponent(ioSplit);

        add(mainSplit, BorderLayout.CENTER);

        // 事件绑定
        cmbLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onLanguageChanged();
            }
        });

        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doRun();
            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doStop();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtCode.setText("");
                txtInput.setText("");
                txtOutput.setText("");
                lblStatus.setText("就绪");
                lblStatus.setForeground(Color.BLACK);
                lblTime.setText("耗时: --ms");
                txtCode.requestFocus();
            }
        });
    }

    // ========== 语言切换 ==========

    private void onLanguageChanged() {
        int idx = cmbLanguage.getSelectedIndex();
        codeBorder.setTitle("代码编辑器 (" + LANG_NAMES[idx] + ")");
        txtCode.getParent().repaint();

        // 如果代码区为空或是其他语言的默认模板，则替换
        String current = txtCode.getText();
        boolean isDefault = current.trim().isEmpty();
        if (!isDefault) {
            for (String tpl : LANG_DEFAULT_CODE) {
                if (current.trim().equals(tpl.trim())) {
                    isDefault = true;
                    break;
                }
            }
        }
        if (isDefault) {
            txtCode.setText(LANG_DEFAULT_CODE[idx]);
        }
    }

    // ========== 运行逻辑 ==========

    private void doRun() {
        if (running) return;

        String code = txtCode.getText();
        if (code.trim().isEmpty()) {
            txtOutput.setText("错误：代码不能为空！");
            return;
        }

        int langIdx = cmbLanguage.getSelectedIndex();

        // 确保 temp 目录存在
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // 重置 UI
        txtOutput.setText("");
        running = true;
        btnRun.setEnabled(false);
        btnStop.setEnabled(true);
        cmbLanguage.setEnabled(false);

        runThread = new Thread(new Runnable() {
            public void run() {
                long startTime = System.currentTimeMillis();
                try {
                    String fileName = LANG_FILENAMES[langIdx];
                    File sourceFile = new File(TEMP_DIR, fileName);

                    // 1. 写入源文件
                    try (FileWriter fw = new FileWriter(sourceFile, java.nio.charset.StandardCharsets.UTF_8)) {
                        fw.write(code);
                    }

                    // 2. 编译（如果需要）
                    if (langIdx == 2) {
                        // Java 编译
                        updateStatus("正在编译...", Color.BLUE);
                        String compileResult = doCompile(javacPath, "-encoding", "UTF-8", sourceFile.getAbsolutePath());
                        if (compileResult != null) {
                            updateOutput("【编译失败】\n" + compileResult);
                            updateStatus("编译失败", Color.RED);
                            return;
                        }
                    } else if (langIdx == 0 || langIdx == 1) {
                        // C / C++ 编译
                        updateStatus("正在编译...", Color.BLUE);
                        String compiler = (langIdx == 0) ? "gcc" : "g++";
                        String exeName = "Solution" + (isWindows() ? ".exe" : "");
                        File exeFile = new File(TEMP_DIR, exeName);
                        String compileResult = doCompile(compiler,
                                "-finput-charset=UTF-8", "-o", exeFile.getAbsolutePath(), sourceFile.getAbsolutePath(), "-lm");
                        if (compileResult != null) {
                            updateOutput("【编译失败】\n" + compileResult);
                            updateStatus("编译失败", Color.RED);
                            return;
                        }
                    }

                    // 3. 运行
                    updateStatus("正在运行...", new Color(0, 128, 0));
                    String[] runCmd = buildRunCommand(langIdx);
                    ProcessBuilder runPb = new ProcessBuilder(runCmd);
                    runPb.directory(new File(TEMP_DIR).getAbsoluteFile());
                    runPb.redirectErrorStream(false);

                    // Python 强制 UTF-8 输出
                    if (langIdx == 3) {
                        java.util.Map<String, String> env = runPb.environment();
                        env.put("PYTHONUTF8", "1");
                    }
                    runningProcess = runPb.start();

                    // 写入 stdin
                    String input = txtInput.getText();
                    if (input != null && !input.isEmpty()) {
                        try (OutputStream os = runningProcess.getOutputStream()) {
                            os.write(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            os.flush();
                        }
                    }

                    // 读取 stdout 和 stderr
                    final StringBuilder stdout = new StringBuilder();
                    final StringBuilder stderr = new StringBuilder();

                    Thread stdoutThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                stdout.append(readStream(runningProcess.getInputStream()));
                            } catch (Exception ignored) {}
                        }
                    });
                    Thread stderrThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                stderr.append(readStream(runningProcess.getErrorStream()));
                            } catch (Exception ignored) {}
                        }
                    });
                    stdoutThread.start();
                    stderrThread.start();

                    boolean finished = runningProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    stdoutThread.join(3000);
                    stderrThread.join(3000);

                    long elapsed = System.currentTimeMillis() - startTime;

                    if (!finished) {
                        runningProcess.destroyForcibly();
                        updateOutput("【超时终止】程序运行超过 " + TIMEOUT_SECONDS + " 秒\n\n" + stdout.toString());
                        updateStatus("超时终止", Color.RED);
                    } else {
                        int exitCode = runningProcess.exitValue();
                        StringBuilder result = new StringBuilder();
                        if (exitCode != 0) {
                            result.append("【异常退出】exit code: ").append(exitCode).append("\n\n");
                        }
                        result.append(stdout.toString());
                        if (stderr.length() > 0) {
                            result.append("\n--- stderr ---\n").append(stderr.toString());
                        }
                        updateOutput(result.toString());
                        updateStatus(exitCode == 0 ? "运行完成" : "异常退出",
                                exitCode == 0 ? new Color(0, 128, 0) : Color.RED);
                    }

                    updateTime("耗时: " + elapsed + "ms");

                } catch (Exception ex) {
                    updateOutput("【运行错误】\n" + ex.getMessage());
                    updateStatus("运行错误", Color.RED);
                } finally {
                    running = false;
                    runningProcess = null;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            btnRun.setEnabled(true);
                            btnStop.setEnabled(false);
                            cmbLanguage.setEnabled(true);
                        }
                    });

                    // 清理临时文件
                    cleanTempFiles(langIdx);
                }
            }
        });
        runThread.setDaemon(true);
        runThread.start();
    }

    /**
     * 编译，成功返回 null，失败返回错误信息
     */
    private String doCompile(String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(".").getAbsoluteFile());
        pb.redirectErrorStream(true);
        Process proc = pb.start();
        String output = readStream(proc.getInputStream());
        boolean done = proc.waitFor(10, TimeUnit.SECONDS);
        if (!done || proc.exitValue() != 0) {
            return output;
        }
        return null;
    }

    /**
     * 构建运行命令
     */
    private String[] buildRunCommand(int langIdx) {
        switch (langIdx) {
            case 0: // C
            case 1: // C++
                String exeName = "Solution" + (isWindows() ? ".exe" : "");
                return new String[]{new File(TEMP_DIR, exeName).getAbsolutePath()};
            case 2: // Java
                return new String[]{javaPath, "-Dfile.encoding=UTF-8", "-cp", new File(TEMP_DIR, ".").getAbsolutePath(), "Solution"};
            case 3: // Python
                return new String[]{"python", new File(TEMP_DIR, "Solution.py").getAbsolutePath()};
            default:
                return new String[]{};
        }
    }

    /**
     * 清理临时文件
     */
    private void cleanTempFiles(int langIdx) {
        // 清理源文件和编译产物
        String baseName = "Solution";
        for (String ext : LANG_CLEAN_EXTENSIONS[langIdx]) {
            File f = new File(TEMP_DIR, baseName + ext);
            if (f.exists()) f.delete();
        }
        // 始终清理源文件
        File src = new File(TEMP_DIR, LANG_FILENAMES[langIdx]);
        if (src.exists()) src.delete();
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    // ========== 终止 ==========

    private void doStop() {
        if (runningProcess != null) {
            runningProcess.destroyForcibly();
        }
        if (runThread != null) {
            runThread.interrupt();
        }
        running = false;
        btnRun.setEnabled(true);
        btnStop.setEnabled(false);
        cmbLanguage.setEnabled(true);
        lblStatus.setText("已终止");
        lblStatus.setForeground(Color.RED);
    }

    // ========== 工具方法 ==========

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private void updateStatus(final String text, final Color color) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                lblStatus.setText(text);
                lblStatus.setForeground(color);
            }
        });
    }

    private void updateOutput(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtOutput.setText(text);
                txtOutput.setCaretPosition(0);
            }
        });
    }

    private void updateTime(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                lblTime.setText(text);
            }
        });
    }
}
