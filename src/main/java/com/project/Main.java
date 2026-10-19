package com.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Load the only view of the example
        Parent root = FXMLLoader.load(getClass().getResource("/assets/layout.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Exemple 0605 - Paint");
        stage.setResizable(false);

        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image(getClass().getResourceAsStream("/icons/icon.png"));
            stage.getIcons().add(icon);
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
