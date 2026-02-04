package com.game.tetris;

import java.awt.Dimension;

import com.game.tetris.components.ScoreEvent;
import com.game.tetris.components.TetrisBoard;
import com.game.tetris.components.TetrisPanel;

public class Launcher {

    public static void main(String[] args) {
        try {
            Dimension boardSz = new Dimension(10, 20); // Standard Tetris board size
            Dimension rectSz = new Dimension(30, 30); // Block size

            TetrisBoard gameBoard = new TetrisBoard("Tetris Game | Developed by MrAlex6204");
            TetrisPanel gamePanel = new TetrisPanel(rectSz, boardSz);
            ScoreEvent scoreEvent = (ScoreEvent) gameBoard;

            gameBoard.setGamePanel(gamePanel);
            gamePanel.setScoreEvent(scoreEvent);

            gameBoard.setVisible(true);// Set visible the Game window

            gamePanel.start();// Start the game loop

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
