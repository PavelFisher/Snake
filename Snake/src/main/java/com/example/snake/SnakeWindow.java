package com.example.snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;


public class SnakeWindow extends JFrame {
    private final GameField game;
    private Image img;

    public SnakeWindow() {
        setTitle("Змейка");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(360, 378);
        game = new GameField();
        add(game);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // Отступы от границ
        getRootPane().setBackground(Color.black);
        setLocationRelativeTo(null); // Центрирование на экране
        setVisible(true);

        try {
            setIconImage(ImageIO.read(new File("C:\\Users\\also1\\IdeaProjects\\SnakeFX\\target\\classes\\com\\example\\snakefx\\icon.jpeg")));
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        getContentPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension dimension = getSize();
                game.onChangeSize(dimension);
                setSize(dimension.width, dimension.height);

            }
        });
    }

    public static void main(String[] args) {
        SnakeWindow snake = new SnakeWindow();
    }
}