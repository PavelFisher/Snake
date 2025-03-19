package com.example.snake;

import javax.swing.*;
import java.awt.event.*;

public class GameOver extends JFrame {

    private static final long serialVersionUID = 1L;
    private static boolean Continue = false;

    public GameOver() {
        super("Игра окончена");
    }

    public boolean askContinue() {
        // Выход из программы при закрытии
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Кнопки для создания диалоговых окон
        JButton button1 = new JButton("Начать заново");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Continue = true;
            }
        });
        JButton button2 = new JButton("Выход");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        // Создание панели содержимого с размещением кнопок
        JPanel contents = new JPanel();
        contents.add(button1);
        contents.add(button2);
        setContentPane(contents);
        // Определение размера и открытие окна
        setSize(350, 100);
        setLocationRelativeTo(null);
        setVisible(true);
        if (Continue) return true;
        else return false;
    }

    public boolean getContinue() {
        return Continue;
    }

    public void dispose() {
        Continue = false;
        super.dispose();
    }

    /**
     * Функция создания диалогового окна.
     *
     * @param title - заголовок окна
     * @param modal - флаг модальности
     */
    private JDialog createDialog(String title, boolean modal) {
        JDialog dialog = new JDialog(this, title, modal);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setSize(180, 90);
        return dialog;
    }

}