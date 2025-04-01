package com.example.snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class GameField extends JPanel implements ActionListener, Serializable {
    private final int COUNT = 20;
    private int sizeX = 20; // default count elements
    private int sizeY = 20;
    private final int DOT_SIZE = 30;
    private final int TECH_HEIGHT = 92;
    private final int TECH_WIDTH = 54;
    private int currentHeight = sizeY * DOT_SIZE + TECH_HEIGHT;
    private int currentWidth = sizeX * DOT_SIZE + TECH_WIDTH;
    private transient BufferedImage apple;
    private int appleX;
    private int appleY;
    private final ArrayList<Integer> x = new ArrayList<>();
    private final ArrayList<Integer> y = new ArrayList<>();
    private transient ArrayList<BufferedImage> images = new ArrayList<>();
    private int dots;
    private Timer timer;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean inGame = true;
    private boolean pause = false;
    private transient BufferedImage imageBack;
    private transient BufferedImage head;
    private transient BufferedImage body;
    private transient BufferedImage tail;
    private transient Popup Popup;

    public GameField() {
        setForeground(Color.GRAY);
        setBackground(Color.black);
        initGame();
    }

    public void stop() {
        timer.stop();
    }

    public void setPause(boolean value) {
        this.pause = value;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public int getCurrentWidth() {
        return currentWidth;
    }

    public void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/saves/saves.dat"))) {
            oos.writeObject(this);
            System.out.println("File has been written");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


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


    public void initGame() {
        images = null;
        x.clear();
        y.clear();
        loadImages();
        dots = 3;
        inGame = true;
        left = false;
        right = true;
        up = false;
        down = false;
        for (int i = 0; i < dots; i++) {
            x.add(i, dots * DOT_SIZE - i * DOT_SIZE);
            y.add(i, dots * DOT_SIZE);
        }
        if (timer == null) {
            timer = new Timer(350, this);
            timer.start();
        } else timer.restart();
        createApple();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
    }

    public void loadGame() {
        loadImages();
        inGame = true;
        pause = false;
        if (timer == null) {
            timer = new Timer(350, this);
            timer.start();
        } else timer.restart();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
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
            head = ImageIO.read(new File("src/assets/head30v2.png"));
            body = ImageIO.read(new File("src/assets/body30.png"));
            tail = ImageIO.read(new File("src/assets/tail30.png"));
            apple = ImageIO.read(new File("src/assets/apple30.png"));
            if (images == null) images = new ArrayList<>();
//            images.add(head);
//            images.add(body);
//            images.add(tail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        int angle;
        BufferedImage image;
        Font font;
        super.paintComponent(g);

        Image resultingImage = imageBack.getScaledInstance(currentWidth, currentHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        g.drawImage(outputImage, 0, 0, this);

        font = new Font("Arial", Font.PLAIN, 36);
        g.setFont(font);
        int width = currentWidth - 50;
        g.drawString(String.valueOf(dots - 3), currentWidth - 50 - 20 * (int) (Math.log10(dots - 3)), 40);


        if (pause) {
            font = new Font("Arial", Font.BOLD, 22);
            g.setFont(font);
            g.drawString("Pause", currentWidth / 2 - 60, currentHeight / 2);
        } else if (inGame) {
            g.drawImage(apple, appleX, appleY, this);
////                rotate picture
            for (int i = 0; i < x.size(); i++) {
//                0 element already rotated
                if (i == x.size() - 1) image = tail;
                else if (i == 0) image = head;
                else image = body;
                if (i != 0) {
                    if (x.get(i) == x.get(i - 1)) {
                        if (y.get(i) > y.get(i - 1)) {
                            angle = -90;
                        } else {
                            angle = 90;
                        }
                    } else {
                        if (x.get(i) > x.get(i - 1)) angle = 180;
                        else angle = 0;
                    }
                } else {
                    if (x.get(i) == x.get(i + 1)) {
                        if (y.get(i) > y.get(i + 1)) {
                            angle = 90;
                        } else {
                            angle = -90;
                        }
                    } else {
                        if (x.get(i) > x.get(i + 1)) angle = 0;
                        else angle = 180;
                    }
                }
                if (images.size() - 1 < i) images.add(i, rotateImage(image, angle));
                else images.set(i, rotateImage(image, angle));
                g.drawImage(images.get(i), x.get(i), y.get(i), this);
            }
        }
    }

    public void move() {
        for (int i = dots - 1; i > 0; i--) {
            if (x.size() <= i) {
                x.add(x.get(i - 1));
                images.add(1, body);
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
            if (Popup == null) {
                Popup = new Popup();
                Popup.askContinue();
            }
            if (Popup.getContinue()) {
                initGame();
                Popup.dispose();
                Popup = null;
            }
        }
    }


    class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_P) {
                setPause(!pause);
            }
            if (key == KeyEvent.VK_LEFT && !right) {
                if (up) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                }
                if (down) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                }
                left = true;
                up = false;
                down = false;
            }
            if (key == KeyEvent.VK_RIGHT && !left) {
                if (down) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                }
                if (up) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                }
                right = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_UP && !down) {
                if (right) {
                    images.set(0, rotateImage(images.getFirst(), -90));
                }
                if (left) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                }
                right = false;
                up = true;
                left = false;
            }
            if (key == KeyEvent.VK_DOWN && !up) {
                if (right) {
                    images.set(0, rotateImage(images.getFirst(), 90));
                }
                if (left) {
                    images.set(0, rotateImage(images.getFirst(), -90));
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
        graphics.rotate(radian, (width / 2), (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();

        return rotatedImage;
    }

}
