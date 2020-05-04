package main.map_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ImageView Imagemap;

    @FXML
    private AnchorPane mapWindow;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Image image = new Image("file:src/main/map_gui/blueprint1.png");
        Image image = new Image("file:data/blueprint1.png",1000,1000,true,true);

        Imagemap.setImage(image);
    }

    @FXML
    private void Zoom(ScrollEvent event){
        event.consume();

        double mapZoom = 0.0;

        if (event.getDeltaY() > 0){
            mapZoom = 1.1;
        }
        else{
            mapZoom = 0.9;
        }

        //to not go behind window
        if (mapZoom * Imagemap.getScaleX() > 1.15){
            return;
        }

        Imagemap.setScaleX(mapZoom * Imagemap.getScaleX());
        Imagemap.setScaleY(mapZoom * Imagemap.getScaleY());

    }
}
