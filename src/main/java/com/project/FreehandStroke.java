package com.project;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FreehandStroke extends Shape {
    private final List<Point2D> points = new ArrayList<>();

    public FreehandStroke(String name, Color color, double width) {
        super(name, color, null, width);
    }

    public void addPoint(double x, double y) {
        points.add(new Point2D(x, y));
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (points.isEmpty()) return;

        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);

        gc.beginPath();
        gc.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point2D p : points) {
            gc.lineTo(p.getX(), p.getY());
        }
        gc.stroke();
    }

    @Override
    public Bounds getBoundingBox() {
        if (points.isEmpty()) return new BoundingBox(0, 0, 0, 0);
        double minX = points.get(0).getX();
        double maxX = minX;
        double minY = points.get(0).getY();
        double maxY = minY;

        for (Point2D p : points) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }
        
        // Dejar un pequeño margen para el grosor de la línea
        double margin = strokeWidth / 2.0;
        return new BoundingBox(minX - margin, minY - margin, (maxX - minX) + strokeWidth, (maxY - minY) + strokeWidth);
    }

    @Override
    public void move(double deltaX, double deltaY) {
        for (int i = 0; i < points.size(); i++) {
            Point2D p = points.get(i);
            points.set(i, new Point2D(p.getX() + deltaX, p.getY() + deltaY));
        }
    }
}