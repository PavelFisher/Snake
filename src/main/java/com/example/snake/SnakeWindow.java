package com.example.snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class SnakeWindow extends JFrame {
    private GameField game;
    private SnakeWindow snake;

    public SnakeWindow(GameField game) {
        snake = this;
        setTitle("Змейка");

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.setBackground(Color.GRAY);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        if (game == null) {
            this.game = new GameField();
            add(this.game);
        } else {
            add(game);
            this.game = game;
            this.game.loadGame();
        }
        setSize(this.game.getCurrentWidth(), this.game.getCurrentHeight());
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // Отступы от границ
        getRootPane().setBackground(Color.black);
        setLocationRelativeTo(null); // Центрирование на экране
        setVisible(true);

        try {
            setIconImage(ImageIO.read(new File("src/assets/icon.jpeg")));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        getContentPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension dimension = getSize();
                snake.game.onChangeSize(dimension);
                setSize(dimension.width, dimension.height);

            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                snake.game.setPause(true);
                Popup popup = new Popup();
                popup.saveGame(snake.game);
//                dispose();
//                System.exit(0);

            }
        });
    }

    public static void main(String[] args) {
        new SnakeWindow(null);
    }

    private JMenu createFileMenu() {
        // Создание выпадающего меню
        JMenu file = new JMenu("Action");
        JMenuItem newGame = new JMenuItem("New game", new ImageIcon("src/assets/new.png"));
        JMenuItem loadGame = new JMenuItem("Load game", new ImageIcon("src/assets/load.png"));
        newGame.setBackground(Color.GRAY);
        loadGame.setBackground(Color.GRAY);
        file.add(newGame);
        file.add(loadGame);

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                game.initGame();
            }
        });
        loadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
//                GameField game1 = Popup.loadGame();
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/saves/saves.dat"))) {
                    game.stop();
                    snake.dispose();
                    new SnakeWindow((GameField) ois.readObject());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        return file;
    }
}
