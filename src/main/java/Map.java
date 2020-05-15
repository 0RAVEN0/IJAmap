/**
 * Authors: Michal Vanka (xvanka00), Romana Džubarová (xdzuba00)
 * Contents: Entry point for GUI
 */
package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Map extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

        Parent root = FXMLLoader.load(getClass().getResource("/main/resources/map.fxml"));

        primaryStage.setTitle("Map");
        primaryStage.setScene(new Scene(root, 1180, 600));
        primaryStage.show();
    }

}
