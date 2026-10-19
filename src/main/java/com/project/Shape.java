package com.project;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Shape {
    protected String name;
    protected Color strokeColor;
    protected Color fillColor; // Usado para rectángulos
    protected double strokeWidth;

    public Shape(String name, Color strokeColor, Color fillColor, double strokeWidth) {
        this.name = name;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.strokeWidth = strokeWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void draw(GraphicsContext gc);
    
    public abstract Bounds getBoundingBox();

    public abstract void move(double deltaX, double deltaY);

    @Override
    public String toString() {
        return name;
    }
}