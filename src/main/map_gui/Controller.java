package main.map_gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ImageView Imagemap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Image image = new Image("file:src/main/map_gui/blueprint1.png");
        Image image = new Image("file:data/blueprint1.png",1000,1000,true,true);

        Imagemap.setImage(image);
    }
}
