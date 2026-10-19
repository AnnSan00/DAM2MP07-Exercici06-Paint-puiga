package com.project;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LineShape extends Shape {
    private double x1, y1, x2, y2;

    public LineShape(String name, double x1, double y1, double x2, double y2, Color color, double width) {
        super(name, color, null, width);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void setEndPoint(double x2, double y2) {
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokeLine(x1, y1, x2, y2);
    }

    @Override
    public Bounds getBoundingBox() {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double width = Math.abs(x2 - x1);
        double height = Math.abs(y2 - y1);

        double margin = strokeWidth / 2.0;
        return new BoundingBox(minX - margin, minY - margin, width + strokeWidth, height + strokeWidth);
    }

    @Override
    public void move(double deltaX, double deltaY) {
        x1 += deltaX;
        y1 += deltaY;
        x2 += deltaX;
        y2 += deltaY;
    }
}