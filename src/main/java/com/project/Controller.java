package com.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Controller implements Initializable {

    @FXML private Canvas canvas;
    @FXML private Canvas colorsCanvas;
    
    @FXML private RadioButton freehandRadio;
    @FXML private RadioButton lineRadio;
    @FXML private RadioButton rectRadio;
    @FXML private ToggleGroup toolGroup;

    @FXML private ColorPicker strokeColorPicker;
    @FXML private ColorPicker fillColorPicker;
    @FXML private Slider widthSlider;

    @FXML private ListView<Shape> objectsListView;
    @FXML private Button deleteButton;

    private GraphicsContext gc;
    private ColorPalette colorPalette;

    private final ObservableList<Shape> shapes = FXCollections.observableArrayList();
    private Shape currentDrawingShape = null;
    private Shape selectedShape = null;

    // Control de contadores para nombres (Traç lliure 1, Línia 2, etc.)
    private int freehandCount = 0;
    private int lineCount = 0;
    private int rectCount = 0;

    // Para el movimiento de objetos
    private double dragStartX = 0;
    private double dragStartY = 0;
    private boolean isMoving = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        colorPalette = new ColorPalette(colorsCanvas);
        colorPalette.setOnColorSelectedCallback(() -> {
            strokeColorPicker.setValue(colorPalette.getSelectedColor());
        });

        strokeColorPicker.setValue(Color.BLACK);
        fillColorPicker.setValue(Color.TRANSPARENT);

        // Eventos del Canvas de dibujo
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        // Sincronización de la lista de objetos
        objectsListView.setItems(shapes);
        objectsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedShape = newVal;
                deleteButton.setDisable(false);
            } else {
                selectedShape = null;
                deleteButton.setDisable(true);
            }
            redraw();
        });

        // Al cambiar de herramienta de dibujo, deseleccionamos el objeto actual
        toolGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                objectsListView.getSelectionModel().clearSelection();
            }
        });

        redraw();
    }

    private void onMousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;

        double x = event.getX();
        double y = event.getY();

        // Comprobar si hay un objeto seleccionado y si hacemos click dentro de su BoundingBox para moverlo
        if (selectedShape != null && selectedShape.getBoundingBox().contains(x, y)) {
            isMoving = true;
            dragStartX = x;
            dragStartY = y;
            return;
        }

        // Si no estamos moviendo, deseleccionamos para crear uno nuevo
        objectsListView.getSelectionModel().clearSelection();
        isMoving = false;

        Color strokeColor = strokeColorPicker.getValue();
        Color fillColor = fillColorPicker.getValue();
        if (fillColor != null && fillColor.equals(Color.TRANSPARENT)) {
            fillColor = null;
        }
        double width = widthSlider.getValue();

        if (freehandRadio.isSelected()) {
            freehandCount++;
            FreehandStroke stroke = new FreehandStroke("Traç lliure " + freehandCount, strokeColor, width);
            stroke.addPoint(x, y);
            currentDrawingShape = stroke;
        } else if (lineRadio.isSelected()) {
            lineCount++;
            currentDrawingShape = new LineShape("Línia " + lineCount, x, y, x, y, strokeColor, width);
        } else if (rectRadio.isSelected()) {
            rectCount++;
            currentDrawingShape = new RectangleShape("Rectangle " + rectCount, x, y, strokeColor, fillColor, width);
        }

        redraw();
    }

    private void onMouseDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (isMoving && selectedShape != null) {
            double deltaX = x - dragStartX;
            double deltaY = y - dragStartY;
            selectedShape.move(deltaX, deltaY);
            dragStartX = x;
            dragStartY = y;
            redraw();
            return;
        }

        if (currentDrawingShape == null) return;

        if (currentDrawingShape instanceof FreehandStroke) {
            ((FreehandStroke) currentDrawingShape).addPoint(x, y);
        } else if (currentDrawingShape instanceof LineShape) {
            ((LineShape) currentDrawingShape).setEndPoint(x, y);
        } else if (currentDrawingShape instanceof RectangleShape) {
            ((RectangleShape) currentDrawingShape).setEndPoint(x, y);
        }

        redraw();
    }

    private void onMouseReleased(MouseEvent event) {
        if (isMoving) {
            isMoving = false;
            return;
        }

        if (currentDrawingShape == null) return;

        // Añadir a la colección de formas y a la lista
        shapes.add(currentDrawingShape);
        objectsListView.getSelectionModel().select(currentDrawingShape);
        currentDrawingShape = null;
        redraw();
    }

    @FXML
    private void handleDeleteObject() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            objectsListView.getSelectionModel().clearSelection();
            redraw();
        }
    }

    private void redraw() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 1. Dibujar todas las formas guardadas en orden de creación
        for (Shape s : shapes) {
            s.draw(gc);
        }

        // 2. Dibujar la previsualización del trazo actual si se está creando
        if (currentDrawingShape != null) {
            currentDrawingShape.draw(gc);
        }

        // 3. Dibujar el recuadro discontinuo (BoundingBox) del objeto seleccionado
        if (selectedShape != null) {
            Bounds bounds = selectedShape.getBoundingBox();
            gc.setStroke(Color.DODGERBLUE);
            gc.setLineWidth(1.5);
            // Simular línea discontinua en JavaFX Canvas
            gc.setLineDashes(5.0);
            gc.strokeRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
            gc.setLineDashes(null); // Restaurar línea continua
        }
    }
}