package com.project;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleShape extends Shape {
    private double startX, startY, currentX, currentY;

    public RectangleShape(String name, double x, double y, Color strokeColor, Color fillColor, double width) {
        super(name, strokeColor, fillColor, width);
        this.startX = x;
        this.startY = y;
        this.currentX = x;
        this.currentY = y;
    }

    public void setEndPoint(double x, double y) {
        this.currentX = x;
        this.currentY = y;
    }

    private double getX() { return Math.min(startX, currentX); }
    private double getY() { return Math.min(startY, currentY); }
    private double getWidth() { return Math.abs(currentX - startX); }
    private double getHeight() { return Math.abs(currentY - startY); }

    @Override
    public void draw(GraphicsContext gc) {
        double x = getX();
        double y = getY();
        double w = getWidth();
        double h = getHeight();

        if (fillColor != null) {
            gc.setFill(fillColor);
            gc.fillRect(x, y, w, h);
        }

        if (strokeWidth > 0) {
            gc.setStroke(strokeColor);
            gc.setLineWidth(strokeWidth);
            gc.strokeRect(x, y, w, h);
        }
    }

    @Override
    public Bounds getBoundingBox() {
        double margin = strokeWidth / 2.0;
        return new BoundsBaseCustom(getX() - margin, getY() - margin, getWidth() + strokeWidth, getHeight() + strokeWidth);
    }

    @Override
    public void move(double deltaX, double deltaY) {
        startX += deltaX;
        startY += deltaY;
        currentX += deltaX;
        currentY += deltaY;
    }
    
    // Helper interior o usar BoundingBox estándar de JavaFX
    private static class BoundsBaseCustom extends BoundingBox {
        public BoundsBaseCustom(double minX, double minY, double width, double height) {
            super(minX, minY, width, height);
        }
    }
}