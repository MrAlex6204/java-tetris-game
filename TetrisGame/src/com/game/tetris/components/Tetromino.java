package com.game.tetris.components;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class Tetromino {

    private int[][] _shape;
    private Color _color;
    private Point _position;
    private int _type;

    public static final int[][] I_SHAPE = { { 1, 1, 1, 1 } };
    public static final int[][] J_SHAPE = { { 1, 0, 0 }, { 1, 1, 1 } };
    public static final int[][] L_SHAPE = { { 0, 0, 1 }, { 1, 1, 1 } };
    public static final int[][] O_SHAPE = { { 1, 1 }, { 1, 1 } };
    public static final int[][] S_SHAPE = { { 0, 1, 1 }, { 1, 1, 0 } };
    public static final int[][] T_SHAPE = { { 0, 1, 0 }, { 1, 1, 1 } };
    public static final int[][] Z_SHAPE = { { 1, 1, 0 }, { 0, 1, 1 } };

    public static final Color[] COLORS = {
            Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED
    };

    public Tetromino(int[][] shape, Color color) {
        this._shape = shape;
        this._color = color;
        this._position = new Point(0, 0); // Default position
    }

    public static Tetromino random() {
        Random rand = new Random();
        int type = rand.nextInt(7);
        int[][] shape = null;
        Color color = COLORS[type];

        switch (type) {
            case 0:
                shape = I_SHAPE;
                break;
            case 1:
                shape = J_SHAPE;
                break;
            case 2:
                shape = L_SHAPE;
                break;
            case 3:
                shape = O_SHAPE;
                break;
            case 4:
                shape = S_SHAPE;
                break;
            case 5:
                shape = T_SHAPE;
                break;
            case 6:
                shape = Z_SHAPE;
                break;
        }
        return new Tetromino(shape, color);
    }

    public int[][] getShape() {
        return _shape;
    }

    public Color getColor() {
        return _color;
    }

    public Point getPosition() {
        return _position;
    }

    public void setPosition(Point p) {
        this._position = p;
    }

    public void setPosition(int x, int y) {
        this._position.setLocation(x, y);
    }

    public void move(int dx, int dy) {
        _position.translate(dx, dy);
    }

    public void rotate() {
        int rows = _shape.length;
        int cols = _shape[0].length;
        int[][] newShape = new int[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                newShape[c][rows - 1 - r] = _shape[r][c];
            }
        }
        _shape = newShape;
    }

    // Helper to rotate back if invalid
    public void rotateBack() {
        int rows = _shape.length;
        int cols = _shape[0].length;
        int[][] newShape = new int[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                newShape[c][r] = _shape[rows - 1 - r][c];
            }
        }
        _shape = newShape;
    }

    public int getWidth() {
        return _shape[0].length;
    }

    public int getHeight() {
        return _shape.length;
    }
}
