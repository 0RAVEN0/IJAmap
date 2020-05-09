/**
 * Author: Michal Vanka (xvanka00) Romana Dzubarova (xdzuba00)
 * Contents: Controller for map.fxml
 */
package main.java;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.shape.*;

import javax.swing.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;

/**
 * Used for display map
 */
public class Controller implements Initializable {

    streetReader stRead = new streetReader();
    public Line lineC = new Line("line");
    public List<Street> streets = null;
    private List<javafx.scene.shape.Line> lineArray;
    private List<Text> textArray;

    public File StreetFile = null;
    public File LinkFile = null;

    @FXML
    private Pane mapWindow;

    @FXML
    private ScrollPane scrollP;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * This function zooms map
     */
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

        mapWindow.setScaleX(mapZoom * mapWindow.getScaleX());
        mapWindow.setScaleY(mapZoom * mapWindow.getScaleY());

    }

    /**
     * Open Dialog window when click on MapOpen MenuItem
     * @param actionEvent
     */
    public void mapClick(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("YAML (*.yaml)", "*.yaml"));
            fc.setTitle("Open your map: ");

            StreetFile = fc.showOpenDialog(null);

            if (StreetFile != null){
                streets = stRead.read(StreetFile);
                lineArray = lineC.drawLine(streets);
                textArray = lineC.drawText(streets);

                for (int i = 0; i < lineArray.size(); i++){
                    mapWindow.getChildren().addAll(lineArray.get(i));
                }

                for (int i = 0; i < textArray.size(); i++){
                    mapWindow.getChildren().addAll(textArray.get(i));
                }


            }
            else{
                System.out.println("Not valid file");
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }

    }

    /**
     * Open Dialog window when click on LineOpen MenuItem
     * @param actionEvent
     */
    public void lineClick(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("YAML (*.yaml)", "*.yaml"));
            fc.setTitle("Open your line: ");

            LinkFile = fc.showOpenDialog(null);

            if (LinkFile != null){
                System.out.println(LinkFile.getAbsolutePath());
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
