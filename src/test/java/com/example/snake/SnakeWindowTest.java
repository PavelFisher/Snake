package com.example.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SnakeWindowTest {
    private SnakeWindow snakeWindow;

    @BeforeEach
    void setUp() {
        // Создаем экземпляр SnakeWindow перед каждым тестом
        snakeWindow = new SnakeWindow();
    }

    @Test
    void testWindowTitle() {
        assertEquals("Змейка", snakeWindow.getTitle());
    }

    @Test
    void testWindowSize() {
        assertEquals(360, snakeWindow.getWidth());
        assertEquals(378, snakeWindow.getHeight());
    }

    @Test
    void testGameFieldComponent() {
        Component[] components = snakeWindow.getContentPane().getComponents();
        boolean hasGameField = Arrays.stream(components).anyMatch(component -> component instanceof GameField);

        assertTrue(hasGameField, "GameField should be added to the SnakeWindow");
    }

    @Test
    void testIconImage() {
        assertNotNull(snakeWindow.getIconImage(), "Icon image should not be null");
    }
}