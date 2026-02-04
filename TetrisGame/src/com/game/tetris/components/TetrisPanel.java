package com.game.tetris.components;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class TetrisPanel extends JPanel implements Runnable, KeyListener {

    private Dimension _rectSz, _boardSz;
    private Thread _gameThr;
    private boolean _running = false;
    private boolean _paused = false;
    private boolean _gameOver = false;
    private int _speed = 500;
    private int _score = 0;
    private ScoreEvent _scoreEvent;

    private Color[][] _board;
    private Tetromino _currentPiece;

    private BufferedImage _img;
    private Graphics2D _g2;

    public TetrisPanel(Dimension rectSz, Dimension boardSz) {
        this._rectSz = rectSz;
        this._boardSz = boardSz;
        this._board = new Color[boardSz.height][boardSz.width];

        this.setPreferredSize(new Dimension(rectSz.width * boardSz.width + 1, rectSz.height * boardSz.height));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    public void setScoreEvent(ScoreEvent scoreEvent) {
        this._scoreEvent = scoreEvent;
    }

    public void start() {
        _score = 0;
        _running = true;
        _gameOver = false;
        _paused = false;
        _board = new Color[_boardSz.height][_boardSz.width]; // clear board
        spawnPiece();

        _img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        _g2 = _img.createGraphics();

        _gameThr = new Thread(this);
        _gameThr.start();
    }

    private void spawnPiece() {
        _currentPiece = Tetromino.random();
        // Center the piece
        int x = (_boardSz.width - _currentPiece.getWidth()) / 2;
        _currentPiece.setPosition(x, 0);

        if (!isValid(_currentPiece, x, 0)) {
            _gameOver = true;
        }
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        long timer = 0;

        while (_running) {
            long now = System.currentTimeMillis();
            long dt = now - lastTime;
            lastTime = now;

            if (!_paused && !_gameOver) {
                timer += dt;
                if (timer > _speed) {
                    update();
                    timer = 0;
                }
            }

            try {
                render();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            repaint();

            try {
                Thread.sleep(16); // ~60fps cap
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // Move down
        if (isValid(_currentPiece, _currentPiece.getPosition().x, _currentPiece.getPosition().y + 1)) {
            _currentPiece.move(0, 1);// Move the piece down in one position per tick
        } else {
            // Lock piece
            lockPiece();
            checkLines();
            spawnPiece();
        }
    }

    private void lockPiece() {
        // 1. Get the current piece's data
        int[][] shape = _currentPiece.getShape(); // The 2D array representing the shape (e.g., T, L, I)
        Point pos = _currentPiece.getPosition(); // The piece's current (x, y) on the board

        // 2. Iterate through every block in the piece's shape
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {

                // 3. Only process the actual blocks (non-zero values)
                // The shape array contains 0s for empty space and 1s (or others) for blocks
                if (shape[i][j] != 0) {

                    // 4. Calculate the absolute board coordinates
                    // pos.x/y is the top-left of the piece
                    // j/i is the offset inside the 4x4 or 3x3 shape grid
                    int x = pos.x + j;
                    int y = pos.y + i;

                    // 5. Safety Check: Ensure we are within board boundaries
                    if (x >= 0 && x < _boardSz.width && y >= 0 && y < _boardSz.height) {

                        // 6. "Lock" the block into the board
                        // Assign the piece's color to the specific grid cell on the board
                        _board[y][x] = _currentPiece.getColor();
                    }
                }
            }
        }
    }

    private void checkLines() {
        for (int y = _boardSz.height - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < _boardSz.width; x++) {
                if (_board[y][x] == null) {
                    full = false;
                    break;
                }
            }

            if (full) {
                // Clear line and move down
                for (int ky = y; ky > 0; ky--) {
                    System.arraycopy(_board[ky - 1], 0, _board[ky], 0, _boardSz.width);// Moves the line down from y-1
                                                                                       // to y
                }
                _board[0] = new Color[_boardSz.width]; // Empty top line
                y++; // Increment the y counter to check the same line again (it's now the one from
                     // above)

                _score += 10;
                if (_scoreEvent != null) {
                    _scoreEvent.onScoreChange(_score);
                }
            }
        }
    }

    private boolean isValid(Tetromino piece, int newX, int newY) {
        int[][] shape = piece.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = newX + j;
                    int y = newY + i;

                    if (x < 0 || x >= _boardSz.width || y >= _boardSz.height)
                        return false;
                    if (y >= 0 && _board[y][x] != null)
                        return false;
                }
            }
        }
        return true;
    }

    private void render() throws InterruptedException {
        // Se Background color
        _g2.setColor(Color.BLACK);
        _g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw Board Grid
        for (int y = 0; y < _boardSz.height; y++) {
            for (int x = 0; x < _boardSz.width; x++) {
                if (_board[y][x] != null) {
                    drawBlock(_g2, x, y, _board[y][x]); // Draw the blocks
                } else {
                    // Grid lines (optional)
                    _g2.setColor(Color.WHITE);
                    _g2.drawRect(x * _rectSz.width, y * _rectSz.height, _rectSz.width, _rectSz.height);// Draw an empty
                                                                                                       // block
                }
            }
        }

        // Draw Current Piece new position
        if (_currentPiece != null) {
            int[][] shape = _currentPiece.getShape();
            Point pos = _currentPiece.getPosition();
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] != 0) {
                        drawBlock(_g2, pos.x + j, pos.y + i, _currentPiece.getColor());
                    }
                }
            }
        }

        if (_gameOver) {// Draw Game Over in the screen
            _g2.setColor(new Color(0, 0, 0, 150));
            _g2.fillRect(0, 0, getWidth(), getHeight());
            _g2.setColor(Color.RED);
            _g2.drawString("GAME OVER", getWidth() / 2 - 40, getHeight() / 2);
        }
    }

    private void drawBlock(Graphics2D g, int x, int y, Color c) {
        g.setColor(c);
        g.fillRect(x * _rectSz.width, y * _rectSz.height, _rectSz.width, _rectSz.height);
        g.setColor(Color.WHITE);
        g.drawRect(x * _rectSz.width, y * _rectSz.height, _rectSz.width, _rectSz.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (_img != null) {
            g.drawImage(_img, 0, 0, null);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (_gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                start();
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            _paused = !_paused;
            return;
        }

        if (_paused)
            return;

        int kc = e.getKeyCode();
        Point pos = _currentPiece.getPosition();

        if (kc == KeyEvent.VK_LEFT) {
            if (isValid(_currentPiece, pos.x - 1, pos.y)) {
                _currentPiece.move(-1, 0);
            }
        } else if (kc == KeyEvent.VK_RIGHT) {
            if (isValid(_currentPiece, pos.x + 1, pos.y)) {
                _currentPiece.move(1, 0);
            }
        } else if (kc == KeyEvent.VK_DOWN) {
            if (isValid(_currentPiece, pos.x, pos.y + 1)) {
                _currentPiece.move(0, 1);
            }
        } else if (kc == KeyEvent.VK_UP) {
            _currentPiece.rotate();
            if (!isValid(_currentPiece, pos.x, pos.y)) {
                _currentPiece.rotateBack();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
