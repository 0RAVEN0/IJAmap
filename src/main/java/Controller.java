package main.java;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ImageView Imagemap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        Imagemap.setScaleX(mapZoom * Imagemap.getScaleX());
        Imagemap.setScaleY(mapZoom * Imagemap.getScaleY());

    }

    public void mapClick(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Open your map: ");

            File selectedFile = fc.showOpenDialog(null);

            if (selectedFile != null){
                System.out.println(selectedFile.getAbsolutePath());
            }
            else{
                System.out.println("Not valid file");
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }

    }

    public void lineClick(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Open your line: ");

            File selectedFile = fc.showOpenDialog(null);

            if (selectedFile != null){
                System.out.println(selectedFile.getAbsolutePath());
            }
            else{
                System.out.println("Not valid file");
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
}
