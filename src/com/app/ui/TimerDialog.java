package com.app.ui;

import com.app.dao.SettingsDAO;
import com.app.dao.TrainingRecordDAO;
import com.app.entity.TrainingRecord;
import com.app.entity.User;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class TimerDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // 预设选项（从数据库动态加载）
    private String[] PRESET_NAMES;
    private int[] PRESET_SECONDS;

    private JComboBox<String> cmbPreset;
    private JSpinner spnCustom;
    private JLabel lblDisplay;
    private JButton btnStart;
    private JButton btnPause;
    private JButton btnResume;
    private JButton btnStop;
    private JButton btnClose;

    private Thread timerThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private int remainingSeconds = 0;
    private int plannedSeconds = 0;
    private String currentPresetName = "";

    private User currentUser;

    public TimerDialog(Frame owner, User currentUser) {
        super(owner, "训练计时器", false);
        this.currentUser = currentUser;

        // 从数据库加载训练设置，动态生成预设
        loadPresets();

        setResizable(false);
        setBounds(100, 100, 420, 320);
        setLocationRelativeTo(owner);
        getContentPane().setLayout(new BorderLayout());

        // 顶部：预设选择
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(15, 15, 5, 15));

        JLabel lblPreset = new JLabel("预设计划：");
        lblPreset.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        cmbPreset = new JComboBox<>(PRESET_NAMES);
        cmbPreset.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        cmbPreset.setSelectedIndex(0);

        spnCustom = new JSpinner(new SpinnerNumberModel(30, 1, 300, 1));
        spnCustom.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        spnCustom.setVisible(false);

        JLabel lblMin = new JLabel("分钟");
        lblMin.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        lblMin.setVisible(false);

        GroupLayout gl_top = new GroupLayout(topPanel);
        gl_top.setHorizontalGroup(
            gl_top.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_top.createSequentialGroup()
                    .addComponent(lblPreset)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(cmbPreset, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(spnCustom, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblMin))
        );
        gl_top.setVerticalGroup(
            gl_top.createParallelGroup(Alignment.BASELINE)
                .addComponent(lblPreset)
                .addComponent(cmbPreset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(spnCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblMin)
        );
        topPanel.setLayout(gl_top);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // 中间：大字倒计时
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblDisplay = new JLabel("00:00:00", SwingConstants.CENTER);
        lblDisplay.setFont(new Font("微软雅黑", Font.BOLD, 48));
        lblDisplay.setForeground(new Color(0, 100, 0));
        centerPanel.add(lblDisplay, BorderLayout.CENTER);

        JLabel lblHint = new JLabel("选择预设后点击【开始】", SwingConstants.CENTER);
        lblHint.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblHint.setForeground(Color.GRAY);
        centerPanel.add(lblHint, BorderLayout.SOUTH);

        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // 底部：控制按钮
        JPanel btnPanel = new JPanel();
        FlowLayout fl_btnPanel = (FlowLayout) btnPanel.getLayout();
        fl_btnPanel.setVgap(10);
        fl_btnPanel.setHgap(8);

        btnStart = new JButton("▶ 开始");
        btnStart.setFont(new Font("微软雅黑", Font.BOLD, 12));
        btnStart.setForeground(new Color(0, 128, 0));

        btnPause = new JButton("⏸ 暂停");
        btnPause.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPause.setEnabled(false);

        btnResume = new JButton("▶ 继续");
        btnResume.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnResume.setEnabled(false);

        btnStop = new JButton("■ 停止");
        btnStop.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnStop.setEnabled(false);
        btnStop.setForeground(Color.RED);

        btnClose = new JButton("关  闭");
        btnClose.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        btnPanel.add(btnStart);
        btnPanel.add(btnPause);
        btnPanel.add(btnResume);
        btnPanel.add(btnStop);
        btnPanel.add(btnClose);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        // 事件绑定
        cmbPreset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isCustom = cmbPreset.getSelectedIndex() == PRESET_NAMES.length - 1;
                spnCustom.setVisible(isCustom);
                lblMin.setVisible(isCustom);
                topPanel.revalidate();
            }
        });

        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doStart();
            }
        });

        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doPause();
            }
        });

        btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doResume();
            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doStop();
            }
        });

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doClose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doClose();
            }
        });
    }

    /**
     * 从数据库加载训练设置，动态生成计时器预设
     */
    private void loadPresets() {
        SettingsDAO settingsDAO = new SettingsDAO();
        int workMin = 25;
        int restMin = 5;
        try {
            String w = settingsDAO.getSetting("training.work.minutes");
            if (w != null) workMin = Integer.parseInt(w);
            String r = settingsDAO.getSetting("training.rest.minutes");
            if (r != null) restMin = Integer.parseInt(r);
        } catch (NumberFormatException ignored) {}

        PRESET_NAMES = new String[]{
                "训练 (" + workMin + "分钟)",
                "短休息 (" + restMin + "分钟)",
                "标准赛制 (5小时)",
                "自定义"
        };
        PRESET_SECONDS = new int[]{
                workMin * 60,
                restMin * 60,
                5 * 3600,
                0
        };
    }

    private void doStart() {
        if (running) return;

        int presetIdx = cmbPreset.getSelectedIndex();
        if (presetIdx == PRESET_NAMES.length - 1) {
            // 自定义
            int minutes = (int) spnCustom.getValue();
            plannedSeconds = minutes * 60;
            currentPresetName = "自定义 (" + minutes + "分钟)";
        } else {
            plannedSeconds = PRESET_SECONDS[presetIdx];
            currentPresetName = PRESET_NAMES[presetIdx];
        }

        remainingSeconds = plannedSeconds;
        running = true;
        paused = false;

        updateButtonStates();
        lblDisplay.setForeground(new Color(0, 100, 0));

        timerThread = new Thread(new Runnable() {
            public void run() {
                while (running && remainingSeconds > 0) {
                    if (!paused) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        remainingSeconds--;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                updateDisplay();
                            }
                        });
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }

                if (running && remainingSeconds <= 0) {
                    // 计时完成
                    running = false;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            lblDisplay.setText("00:00:00");
                            lblDisplay.setForeground(Color.RED);
                            updateButtonStates();
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(TimerDialog.this,
                                    "训练时间到！\n计划：" + currentPresetName,
                                    "计时完成", JOptionPane.INFORMATION_MESSAGE);

                            // 保存训练记录
                            saveRecord(true, plannedSeconds);
                        }
                    });
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void doPause() {
        if (!running || paused) return;
        paused = true;
        lblDisplay.setForeground(new Color(200, 150, 0));
        updateButtonStates();
    }

    private void doResume() {
        if (!running || !paused) return;
        paused = false;
        lblDisplay.setForeground(new Color(0, 100, 0));
        updateButtonStates();
    }

    private void doStop() {
        if (!running) return;
        running = false;
        paused = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }

        int actualSeconds = plannedSeconds - remainingSeconds;
        saveRecord(false, actualSeconds);

        lblDisplay.setForeground(Color.GRAY);
        updateButtonStates();
        JOptionPane.showMessageDialog(this,
                "训练已停止。\n实际训练时长：" + TrainingRecord.formatSeconds(actualSeconds),
                "计时停止", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doClose() {
        if (running) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "计时器正在运行，确定关闭？\n（将保存当前进度）", "确认关闭",
                    JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) {
                return;
            }
            running = false;
            paused = false;
            if (timerThread != null) {
                timerThread.interrupt();
            }
            int actualSeconds = plannedSeconds - remainingSeconds;
            saveRecord(false, actualSeconds);
        }
        dispose();
    }

    private void saveRecord(boolean completed, int actualSeconds) {
        if (currentUser == null) return;
        TrainingRecord record = new TrainingRecord(
                currentUser.getId(), currentPresetName, plannedSeconds, actualSeconds, completed
        );
        TrainingRecordDAO dao = new TrainingRecordDAO();
        dao.addRecord(record);
    }

    private void updateDisplay() {
        lblDisplay.setText(TrainingRecord.formatSeconds(remainingSeconds));
    }

    private void updateButtonStates() {
        btnStart.setEnabled(!running);
        cmbPreset.setEnabled(!running);
        spnCustom.setEnabled(!running);
        btnPause.setEnabled(running && !paused);
        btnResume.setEnabled(running && paused);
        btnStop.setEnabled(running);
    }
}
