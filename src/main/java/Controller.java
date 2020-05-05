package main.java;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private int mapLink;
    private int lineLink;

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
        JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fc.setDialogTitle("Open your map: ");

        mapLink = fc.showOpenDialog(null);
        File selectedFile = fc.getSelectedFile();

        /*if (mapLink == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        }*/

        Image image = new Image(selectedFile.toURI().toString(),1000,1000,true,true);
        Imagemap.setImage(image);
    }

    public void lineClick(ActionEvent actionEvent) {
        JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fc.setDialogTitle("Open your line: ");

        lineLink = fc.showOpenDialog(null);

    }
}
