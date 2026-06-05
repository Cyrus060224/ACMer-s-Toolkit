package com.app.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Background music player panel - plays WAV files using Java Clip API
 */
public class MusicPlayerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JButton btnPlay;
    private JButton btnStop;
    private JSlider volumeSlider;
    private JComboBox<String> cmbMusic;

    private Clip clip;
    private Thread playThread;
    private volatile boolean playing = false;
    private volatile boolean paused = false;

    private static final String[] MUSIC_NAMES = {"gone", "date", "rain"};
    private static final String[] MUSIC_FILES = {"music/gone.wav", "music/デート.wav", "music/雨声.wav"};

    public MusicPlayerPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 2));

        JLabel lblMusic = new JLabel(" BGM:");
        lblMusic.setFont(new Font("Arial", Font.PLAIN, 11));
        lblMusic.setForeground(new Color(100, 100, 100));

        cmbMusic = new JComboBox<>(MUSIC_NAMES);
        cmbMusic.setFont(new Font("Arial", Font.PLAIN, 10));
        cmbMusic.setPreferredSize(new Dimension(60, 22));

        btnPlay = new JButton("Play");
        btnPlay.setFont(new Font("Arial", Font.BOLD, 10));
        btnPlay.setPreferredSize(new Dimension(65, 22));
        btnPlay.setToolTipText("Play / Pause");
        btnPlay.setForeground(new Color(0, 128, 0));

        btnStop = new JButton("Stop");
        btnStop.setFont(new Font("Arial", Font.BOLD, 10));
        btnStop.setPreferredSize(new Dimension(65, 22));
        btnStop.setToolTipText("Stop");
        btnStop.setForeground(Color.RED);
        btnStop.setEnabled(false);

        JLabel lblVol = new JLabel("Vol");
        lblVol.setFont(new Font("Arial", Font.PLAIN, 10));
        lblVol.setForeground(new Color(100, 100, 100));

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setPreferredSize(new Dimension(60, 22));

        add(lblMusic);
        add(cmbMusic);
        add(btnPlay);
        add(btnStop);
        add(lblVol);
        add(volumeSlider);

        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (playing && !paused) {
                    doPause();
                } else if (playing && paused) {
                    doResume();
                } else {
                    doPlay();
                }
            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doStop();
            }
        });

        volumeSlider.addChangeListener(e -> applyVolume());
    }

    private void doPlay() {
        if (playing) return;

        int idx = cmbMusic.getSelectedIndex();
        if (idx < 0 || idx >= MUSIC_FILES.length) return;

        final String filePath = MUSIC_FILES[idx];
        if (!new File(filePath).exists()) {
            JOptionPane.showMessageDialog(this,
                    "File not found: " + filePath, "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        playing = true;
        paused = false;
        btnPlay.setText("Pause");
        btnStop.setEnabled(true);
        cmbMusic.setEnabled(false);

        playThread = new Thread(new Runnable() {
            public void run() {
                try {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
                    clip = AudioSystem.getClip();
                    clip.open(ais);
                    applyVolume();
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    clip.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(MusicPlayerPanel.this,
                                    "Playback error: " + ex.getMessage(),
                                    "Audio Error", JOptionPane.ERROR_MESSAGE);
                            resetUI();
                        }
                    });
                }
            }
        });
        playThread.setDaemon(true);
        playThread.start();
    }

    private void doPause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            paused = true;
            btnPlay.setText("Resume");
        }
    }

    private void doResume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            paused = false;
            btnPlay.setText("Pause");
        }
    }

    private void doStop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        playing = false;
        paused = false;
        resetUI();
    }

    private void applyVolume() {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float val = volumeSlider.getValue() / 100.0f;
            float dB = (float) (Math.log(Math.max(val, 0.001)) / Math.log(10.0) * 20.0);
            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
            gainControl.setValue(dB);
        }
    }

    private void resetUI() {
        playing = false;
        paused = false;
        btnPlay.setText("Play");
        btnStop.setEnabled(false);
        cmbMusic.setEnabled(true);
    }

    public void cleanup() {
        doStop();
    }
}
