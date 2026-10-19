package com.project;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// A freehand stroke: an ordered list of points with its own style
public class Stroke {

    private final List<Point2D> points = new ArrayList<>();
    private final Color color;
    private final double width;

    public Stroke(Color color, double width) {
        this.color = color;
        this.width = width;
    }

    public void addPoint(double x, double y) {
        points.add(new Point2D(x, y));
    }

    public void draw(GraphicsContext gc) {
        if (points.isEmpty()) return;

        gc.setStroke(color);
        gc.setLineWidth(width);

        // Join each point with the next one
        gc.beginPath();
        gc.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point2D p : points) {
            gc.lineTo(p.getX(), p.getY());
        }
        gc.stroke();
    }
}
