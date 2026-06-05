package com.app.ui;

import com.app.dao.SettingsDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 训练难度设置对话框
 * 通过控制训练/休息时长实现难度分级
 */
public class TrainingSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // 难度预设：[训练分钟, 休息分钟]
    private static final int[][] DIFFICULTY_PRESETS = {
            {15, 10},  // 简单：短训练 + 长休息
            {25, 5},   // 中等：标准番茄钟
            {50, 3},   // 困难：长训练 + 短休息
    };
    private static final String[] DIFFICULTY_NAMES = {"简单", "中等", "困难"};
    private static final String[] DIFFICULTY_DESCS = {
            "短训练（15分钟）+ 长休息（10分钟），适合入门",
            "标准番茄钟（25分钟）+ 休息（5分钟），推荐日常使用",
            "高强度训练（50分钟）+ 短休息（3分钟），适合冲刺阶段"
    };

    private JComboBox<String> cmbDifficulty;
    private JSpinner spnWorkMinutes;
    private JSpinner spnRestMinutes;
    private JSpinner spnMaxRounds;
    private JCheckBox chkAutoStartRest;
    private JCheckBox chkSoundAlert;

    private SettingsDAO settingsDAO;
    private boolean saved = false;

    public TrainingSettingsDialog(Frame owner) {
        super(owner, "训练难度设置", true);
        this.settingsDAO = new SettingsDAO();
        setResizable(false);
        setBounds(100, 100, 450, 400);
        setLocationRelativeTo(owner);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        setContentPane(contentPanel);

        // === 上部：难度选择 ===
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));

        JPanel diffPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        diffPanel.setBorder(BorderFactory.createTitledBorder("难度等级"));
        ButtonGroup bg = new ButtonGroup();
        for (int i = 0; i < DIFFICULTY_NAMES.length; i++) {
            JRadioButton rb = new JRadioButton(DIFFICULTY_NAMES[i] + " — " + DIFFICULTY_DESCS[i]);
            rb.setActionCommand(String.valueOf(i));
            bg.add(rb);
            diffPanel.add(rb);
        }
        topPanel.add(diffPanel, BorderLayout.NORTH);

        // === 中部：自定义参数 ===
        JPanel customPanel = new JPanel(new GridLayout(4, 2, 10, 8));
        customPanel.setBorder(BorderFactory.createTitledBorder("自定义参数"));

        customPanel.add(new JLabel("训练时长（分钟）："));
        spnWorkMinutes = new JSpinner(new SpinnerNumberModel(25, 5, 120, 5));
        customPanel.add(spnWorkMinutes);

        customPanel.add(new JLabel("休息时长（分钟）："));
        spnRestMinutes = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        customPanel.add(spnRestMinutes);

        customPanel.add(new JLabel("最大轮数："));
        spnMaxRounds = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        customPanel.add(spnMaxRounds);

        chkAutoStartRest = new JCheckBox("训练结束后自动开始休息", true);
        chkSoundAlert = new JCheckBox("计时结束时播放提示音", true);
        customPanel.add(chkAutoStartRest);
        customPanel.add(chkSoundAlert);

        topPanel.add(customPanel, BorderLayout.CENTER);
        contentPanel.add(topPanel, BorderLayout.CENTER);

        // === 底部按钮 ===
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        JButton btnSave = new JButton("保存设置");
        btnSave.setFont(new Font("微软雅黑", Font.BOLD, 12));
        JButton btnCancel = new JButton("取消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        // 加载已有设置
        loadSettings();

        // 难度选择联动
        for (Component comp : diffPanel.getComponents()) {
            if (comp instanceof JRadioButton) {
                ((JRadioButton) comp).addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int idx = Integer.parseInt(e.getActionCommand());
                        spnWorkMinutes.setValue(DIFFICULTY_PRESETS[idx][0]);
                        spnRestMinutes.setValue(DIFFICULTY_PRESETS[idx][1]);
                    }
                });
            }
        }

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                saved = true;
                dispose();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnSave);
    }

    private void loadSettings() {
        String difficulty = settingsDAO.getSetting("training.difficulty");
        if (difficulty != null) {
            int idx = Integer.parseInt(difficulty);
            // 选中对应的 RadioButton
            Component[] comps = ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(0)).getComponents();
            if (idx >= 0 && idx < comps.length && comps[idx] instanceof JRadioButton) {
                ((JRadioButton) comps[idx]).setSelected(true);
                spnWorkMinutes.setValue(DIFFICULTY_PRESETS[idx][0]);
                spnRestMinutes.setValue(DIFFICULTY_PRESETS[idx][1]);
            }
        }

        String workMin = settingsDAO.getSetting("training.work.minutes");
        if (workMin != null) spnWorkMinutes.setValue(Integer.parseInt(workMin));

        String restMin = settingsDAO.getSetting("training.rest.minutes");
        if (restMin != null) spnRestMinutes.setValue(Integer.parseInt(restMin));

        String maxRounds = settingsDAO.getSetting("training.max.rounds");
        if (maxRounds != null) spnMaxRounds.setValue(Integer.parseInt(maxRounds));

        String autoRest = settingsDAO.getSetting("training.auto.rest");
        if (autoRest != null) chkAutoStartRest.setSelected(Boolean.parseBoolean(autoRest));

        String soundAlert = settingsDAO.getSetting("training.sound.alert");
        if (soundAlert != null) chkSoundAlert.setSelected(Boolean.parseBoolean(soundAlert));
    }

    private void saveSettings() {
        // 找到当前选中的难度
        String difficulty = "1"; // 默认中等
        Component[] comps = ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(0)).getComponents();
        for (Component comp : comps) {
            if (comp instanceof JRadioButton && ((JRadioButton) comp).isSelected()) {
                difficulty = ((JRadioButton) comp).getActionCommand();
                break;
            }
        }

        settingsDAO.setSetting("training.difficulty", difficulty);
        settingsDAO.setSetting("training.work.minutes", String.valueOf(spnWorkMinutes.getValue()));
        settingsDAO.setSetting("training.rest.minutes", String.valueOf(spnRestMinutes.getValue()));
        settingsDAO.setSetting("training.max.rounds", String.valueOf(spnMaxRounds.getValue()));
        settingsDAO.setSetting("training.auto.rest", String.valueOf(chkAutoStartRest.isSelected()));
        settingsDAO.setSetting("training.sound.alert", String.valueOf(chkSoundAlert.isSelected()));
    }

    public boolean isSaved() {
        return saved;
    }

    /**
     * 获取当前配置的训练时长（分钟）
     */
    public int getWorkMinutes() {
        return (int) spnWorkMinutes.getValue();
    }

    /**
     * 获取当前配置的休息时长（分钟）
     */
    public int getRestMinutes() {
        return (int) spnRestMinutes.getValue();
    }
}
