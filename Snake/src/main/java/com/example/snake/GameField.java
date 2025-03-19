package com.example.snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

//
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
////


public class GameField extends JPanel implements ActionListener {
    private final int COUNT = 20;
    private int sizeX = 20;
    private int sizeY = 20;
    private final int DOT_SIZE = 16;
    private final int TECH_HEIGHT = 58;
    private final int TECH_WIDTH = 40;
    private int currentHeight = sizeY * DOT_SIZE + TECH_HEIGHT;
    private int currentWidth = sizeX * DOT_SIZE + TECH_WIDTH;
    private Image dot;
    private Image apple;
    private int appleX;
    private int appleY;
    private ArrayList<Integer> x = new ArrayList<>();
    private ArrayList<Integer> y = new ArrayList<>();
    private int dots;
    private Timer timer;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean inGame = true;
    BufferedImage image;
    GameOver GameOver;

    public void onChangeSize(Dimension dimension) {
        boolean exit = false;
        if (dimension.height < COUNT * DOT_SIZE + TECH_HEIGHT) {
            dimension.height = COUNT * DOT_SIZE + TECH_HEIGHT;
            exit = true;
        }
        if (dimension.width < COUNT * DOT_SIZE + TECH_WIDTH) {
            dimension.width = COUNT * DOT_SIZE + TECH_WIDTH;
            exit = true;
        }
        if (exit) return;
        currentHeight = dimension.height;
        currentWidth = dimension.width;
        sizeY = (currentHeight - TECH_HEIGHT) / DOT_SIZE;
        sizeX = (currentWidth - TECH_WIDTH) / DOT_SIZE;
        currentHeight = dimension.height = sizeY * DOT_SIZE + TECH_HEIGHT;
        currentWidth = dimension.width = sizeX * DOT_SIZE + TECH_WIDTH;
    }

    public GameField() {
        setForeground(Color.GRAY);
        setBackground(Color.black);
        loadImages();
        initGame();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
    }

    public void initGame() {
        dots = 3;
        inGame = true;
        x.clear();
        y.clear();
        left = false;
        right = true;
        up = false;
        down = false;
        for (int i = 0; i < dots; i++) {
            x.add(i, 48 - i * DOT_SIZE);
            y.add(i, 48);
        }
        if (timer == null) {
            timer = new Timer(350, this);
            timer.start();
        } else timer.restart();
        createApple();
    }

    public void createApple() {
        appleX = new Random().nextInt(sizeX) * DOT_SIZE;
        appleY = new Random().nextInt(sizeY) * DOT_SIZE;
    }

    public void loadImages() {
        ImageIcon iia = new ImageIcon("src/apple.png");
        apple = iia.getImage();
        ImageIcon iid = new ImageIcon("src/dot.png");
        dot = iid.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            image = ImageIO.read(new File("C:\\Users\\also1\\IdeaProjects\\SnakeFX\\target\\classes\\com\\example\\snakefx\\green_grass.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(image.getSubimage(0, 0, currentWidth, currentHeight), 0, 0, this);


        Dimension size = getSize();

        if (inGame) {
            g.drawImage(apple, appleX, appleY, this);
            for (int i = 0; i < dots; i++) {
                g.drawImage(dot, x.get(i), y.get(i), this);
            }
        } else {
//            GameOver GameOver = new GameOver();
//            if (GameOver.askContinue()) initGame();
//            String str = "Game Over";
//            g.setColor(Color.white);
//            g.drawString(str, currentWidth / 2 - 30, currentHeight / 2);
        }
    }

    public void move() {
        for (int i = dots; i > 0; i--) {
            if (x.size() <= i) x.add(x.get(i - 1));
            else x.set(i, x.get(i - 1));
            if (y.size() <= i) y.add(y.get(i - 1));
            else y.set(i, y.get(i - 1));
        }
        if (left) {
            x.set(0, x.get(0) - DOT_SIZE);
        }
        if (right) {
            x.set(0, x.get(0) + DOT_SIZE);
        }
        if (up) {
            y.set(0, y.get(0) - DOT_SIZE);
        }
        if (down) {
            y.set(0, y.get(0) + DOT_SIZE);
        }
    }

    public void checkApple() {
        if (x.get(0) == appleX && y.get(0) == appleY) {
            dots++;
            createApple();
        }
    }

    public void checkCollisions() {
        for (int i = dots; i > 0; i--) {
            if (i > 4 && x.get(0) == x.get(i - 1) && y.get(0) == y.get(i - 1)) {
                inGame = false;
            }
        }

        if (x.get(0) > sizeX * DOT_SIZE) {
            inGame = false;
        }
        if (x.get(0) < 0) {
            inGame = false;
        }
        if (y.get(0) > sizeY * DOT_SIZE) {
            inGame = false;
        }
        if (y.get(0) < 0) {
            inGame = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            checkApple();
            checkCollisions();
            move();
            repaint();
        } else {
            if (GameOver == null) {
                GameOver = new GameOver();
                GameOver.askContinue();
            }
            if (GameOver.getContinue()) {
                initGame();
                GameOver.dispose();
                GameOver = null;
            }
        }
    }

    class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT && !right) {
                left = true;
                up = false;
                down = false;
            }
            if (key == KeyEvent.VK_RIGHT && !left) {
                right = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_UP && !down) {
                right = false;
                up = true;
                left = false;
            }
            if (key == KeyEvent.VK_DOWN && !up) {
                right = false;
                down = true;
                left = false;
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
            super.keyTyped(e);
        }

        /**
         * Invoked when a key has been pressed.
         */

        /**
         * Invoked when a key has been released.
         */
        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
        }

    }


}
