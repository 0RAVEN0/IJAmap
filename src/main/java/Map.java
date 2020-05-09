/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Entry point for GUI
 */
package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Map extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../resources/map.fxml"));

        primaryStage.setTitle("Map");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }
}
