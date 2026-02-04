package com.game.tetris.components;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;

public class TetrisBoard extends JFrame implements ScoreEvent {

    private TetrisPanel _gamePanel;
    private JLabel _scoreLabel;

    public TetrisBoard(String title) {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        // Initialize Score Label
        _scoreLabel = new JLabel("Score: 0");
        _scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        _scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        _scoreLabel.setOpaque(true);
        _scoreLabel.setBackground(Color.DARK_GRAY);
        _scoreLabel.setForeground(Color.WHITE);

        this.add(_scoreLabel, BorderLayout.NORTH);
    }

    public void setGamePanel(TetrisPanel panel) {
        this._gamePanel = panel;
        this.add(panel, BorderLayout.CENTER);
        this.pack(); // Size the window to fit the panel
        this.setLocationRelativeTo(null); // Center on screen
    }

    public TetrisPanel getGamePanel() {
        return _gamePanel;
    }

    public JLabel getScoreLabel() {
        return _scoreLabel;
    }

    @Override
    public void onScoreChange(int score) {
        _scoreLabel.setText("Score: " + score);

    }
}
