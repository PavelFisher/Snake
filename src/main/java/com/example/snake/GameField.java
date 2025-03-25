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

//
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;

/// /


public class GameField extends JPanel implements ActionListener {
    private final int COUNT = 20;
    private int sizeX = 20; // default count elements
    private int sizeY = 20;
    private final int DOT_SIZE = 16;
    private final int TECH_HEIGHT = 58;
    private final int TECH_WIDTH = 40;
    private int currentHeight = sizeY * DOT_SIZE + TECH_HEIGHT;
    private int currentWidth = sizeX * DOT_SIZE + TECH_WIDTH;
    private BufferedImage apple;
    private int appleX;
    private int appleY;
    private final ArrayList<Integer> x = new ArrayList<>();
    private final ArrayList<Integer> y = new ArrayList<>();
    private final ArrayList<int[]> rotateElement = new ArrayList<>();
    private final ArrayList<BufferedImage> images = new ArrayList<>();
    private int dots;
    private Timer timer;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean inGame = true;
    private boolean pause = false;
    private BufferedImage imageBack;
    private GameOver GameOver;

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
        images.clear();
        rotateElement.clear();
        loadImages();
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
        do {
            appleX = new Random().nextInt(sizeX) * DOT_SIZE;
        } while (x.contains(appleX));
        do {
            appleY = new Random().nextInt(sizeY) * DOT_SIZE;
        } while (y.contains(appleY));

    }

    public void loadImages() {
        try {
            imageBack = ImageIO.read(new File("src/assets/green_grass.jpg"));
            images.add(ImageIO.read(new File("src/assets/head.png")));
            images.add(ImageIO.read(new File("src/assets/body.png")));
            images.add(ImageIO.read(new File("src/assets/tail.png")));

            apple = ImageIO.read(new File("src/assets/apple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        int[] element;
        super.paintComponent(g);

        Image resultingImage = imageBack.getScaledInstance(currentWidth, currentHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        g.drawImage(outputImage, 0, 0, this);

        if (pause) {
            Font font = new Font("Arial", Font.BOLD, 22);
            g.setFont(font);
            g.drawString("Pause", currentWidth / 2 - 60, currentHeight / 2);
        } else if (inGame) {
            g.drawImage(apple, appleX, appleY, this);
            for (int i = 0; i < dots; i++) { //from head to tail
//                rotate picture
                g.drawImage(images.get(i), x.get(i), y.get(i), this);
                for (int[] ints : rotateElement) {
                    if (ints[0] == i)
                        images.set(i, rotateImage(images.get(i), ints[1]));
                }
            }
            for (int i = 0; i < rotateElement.size(); i++) {
                element = rotateElement.get(i);
                if (element[0] == dots) rotateElement.remove(i);
                else {
                    element[0] += 1;
                    rotateElement.set(i, element);
                }
            }
        }
    }

    public void move() {
        int prevValue = 0;
        for (int i = dots - 1; i > 0; i--) {
            if (x.size() <= i) {
                x.add(x.get(i - 1));
                images.add(1, images.get(1));
                for (int j = 0; j < rotateElement.size(); j++) {
                    int[] ints = rotateElement.get(j);
                    if (ints[0] == 1) continue;
                    ints[0] += 1;
                    if (ints[0] >= dots) rotateElement.remove(j);
                }
            } else x.set(i, x.get(i - 1));

            if (y.size() <= i) y.add(y.get(i - 1));
            else y.set(i, y.get(i - 1));
        }
        if (left) {
            x.set(0, x.getFirst() - DOT_SIZE);
        }
        if (right) {
            x.set(0, x.getFirst() + DOT_SIZE);
        }
        if (up) {
            y.set(0, y.getFirst() - DOT_SIZE);
        }
        if (down) {
            y.set(0, y.getFirst() + DOT_SIZE);
        }
    }

    public void checkApple() {
        if (x.getFirst() == appleX && y.getFirst() == appleY) {
            dots++;
            createApple();
        }
    }

    public void checkCollisions() {
        for (int i = dots - 1; i > 0; i--) {
            if (i > 4 && x.getFirst().equals(x.get(i - 1)) && y.getFirst().equals(y.get(i - 1))) {
                inGame = false;
            }
        }

        if (x.getFirst() > sizeX * DOT_SIZE) {
            inGame = false;
        }
        if (x.getFirst() < 0) {
            inGame = false;
        }
        if (y.getFirst() > sizeY * DOT_SIZE) {
            inGame = false;
        }
        if (y.getFirst() < 0) {
            inGame = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            if (!pause) {
                checkApple();
                checkCollisions();
                move();
                repaint();
            } else repaint();
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
            if (key == KeyEvent.VK_P) pause = !pause;
            if (key == KeyEvent.VK_LEFT && !right) {
                if (up) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                    rotateElement.addFirst(new int[]{1, -90});
                }
                if (down) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                    rotateElement.addFirst(new int[]{1, 90});
                }
                left = true;
                up = false;
                down = false;
            }
            if (key == KeyEvent.VK_RIGHT && !left) {
                if (down) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                    rotateElement.addFirst(new int[]{1, -90});
                }
                if (up) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                    rotateElement.addFirst(new int[]{1, 90});
                }
                right = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_UP && !down) {
                if (right) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                    rotateElement.addFirst(new int[]{1, -90});
                }
                if (left) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                    rotateElement.addFirst(new int[]{1, 90});
                }
                right = false;
                up = true;
                left = false;
            }
            if (key == KeyEvent.VK_DOWN && !up) {
                if (right) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                    rotateElement.addFirst(new int[]{1, 90});
                }
                if (left) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                    rotateElement.addFirst(new int[]{1, -90});
                }
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

    private static BufferedImage rotateImage(BufferedImage buffImage, double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));

        int width = buffImage.getWidth();
        int height = buffImage.getHeight();

        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);

        BufferedImage rotatedImage = new BufferedImage(
                nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = rotatedImage.createGraphics();

        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        // rotation around the center point
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
    }

}
