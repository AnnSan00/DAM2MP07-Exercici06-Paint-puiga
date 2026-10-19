package com.project;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ColorPalette {

    private static final Color[] COLORS = { 
        Color.BLACK, Color.GRAY, Color.RED, Color.ORANGE, 
        Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE 
    };
    
    private static final double PENCIL_WIDTH = 14;
    private static final double PENCIL_HEIGHT = 34;
    private static final double COL_SPACING = 28; // Espacio horizontal entre columnas
    private static final double ROW_SPACING = 38; // Espacio vertical entre filas

    private final Canvas canvas;
    private final GraphicsContext gc;
    private Color selectedColor = COLORS[0];
    private Runnable onColorSelectedCallback;

    public ColorPalette(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(this::onMouseClicked);
        draw();
    }

    public void setOnColorSelectedCallback(Runnable callback) {
        this.onColorSelectedCallback = callback;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color color) {
        this.selectedColor = color;
        draw();
    }

    private double getX(int index) {
        int col = index % 4; // 4 lápices por fila
        return 12 + col * COL_SPACING;
    }

    private double getY(int index) {
        int row = index / 4; // Fila 0 o Fila 1
        return 8 + row * ROW_SPACING;
    }

    private void onMouseClicked(MouseEvent event) {
        for (int i = 0; i < COLORS.length; i++) {
            double x = getX(i);
            boolean isSelected = COLORS[i].equals(selectedColor);
            double y = getY(i) + (isSelected ? -5 : 0); // Si está seleccionado, se eleva 5 píxeles

            // Comprobar si el clic está dentro del área del lápiz
            if (event.getX() >= x - 2 && event.getX() <= x + PENCIL_WIDTH + 2 &&
                event.getY() >= y && event.getY() <= y + PENCIL_HEIGHT) {
                
                selectedColor = COLORS[i];
                draw();
                if (onColorSelectedCallback != null) {
                    onColorSelectedCallback.run();
                }
                return;
            }
        }
    }

    public void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < COLORS.length; i++) {
            double x = getX(i);
            boolean isSelected = COLORS[i].equals(selectedColor);

            // Si está seleccionado, se dibuja 5 píxeles más arriba (efecto levantado)
            double y = getY(i) + (isSelected ? -5 : 0);

            // 1. Punta del lápiz (triángulo superior)
            gc.setFill(Color.LIGHTGRAY);
            gc.fillPolygon(
                new double[]{x, x + PENCIL_WIDTH, x + PENCIL_WIDTH / 2.0},
                new double[]{y + 9, y + 9, y},
                3
            );

            // 2. Cuerpo principal del lápiz (rectángulo con el color)
            gc.setFill(COLORS[i]);
            gc.fillRect(x, y + 9, PENCIL_WIDTH, PENCIL_HEIGHT - 14);

            // 3. Goma superior del lápiz
            gc.setFill(Color.LIGHTPINK);
            gc.fillRect(x, y + PENCIL_HEIGHT - 5, PENCIL_WIDTH, 4);

            // Si está seleccionado, se le añade un borde azul para remarcarlo visualmente
            if (isSelected) {
                gc.setStroke(Color.DODGERBLUE);
                gc.setLineWidth(1.5);
                gc.strokeRect(x - 2, y - 2, PENCIL_WIDTH + 4, PENCIL_HEIGHT + 4);
            }
        }
    }
}