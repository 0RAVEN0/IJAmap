/**
 * Author: Michal Vanka (xvanka00) Romana Dzubarova (xdzuba00)
 * Contents: Controller for map.fxml
 */
package main.java;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for displaying a map
 */
public class Controller implements Initializable {

    StreetReader stRead = new StreetReader();
    public ShapeLine lineC = new ShapeLine();
    private List<Street> streets = null;
    private Map<String, Street> streetMap = null;
    private List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private List<Text> textArray = new ArrayList<>();

    public File StreetFile = null;
    public File LinkFile = null;

    private boolean click = false;

    TextArea timeTable = new TextArea();
    ComboBox roadDegree = new ComboBox();
    CheckBox closeStreet = new CheckBox("Close street");
    AnchorPane anchorP;


    @FXML
    private Pane mapWindow;

    @FXML
    private Group groupP;

    @FXML
    private BorderPane borderP;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorP = new AnchorPane(roadDegree,closeStreet,timeTable);
        roadDegree.setPromptText("Road degree");
        roadDegree.getItems().addAll("1. degree","2. degree","3. degree");
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
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("YAML (*.yaml)", "*.yaml"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            fc.setTitle("Open your map: ");

            StreetFile = fc.showOpenDialog(null);

            if (StreetFile != null){
                streets = stRead.read(StreetFile);
                streetMap = streets.stream().collect(Collectors.toMap(Street::getId, street -> street));


                lineArray = lineC.drawLine(streets);
                textArray = lineC.drawText(streets);


                for (javafx.scene.shape.Line line : lineArray) {
                    mapWindow.getChildren().addAll(line);
                    line.addEventHandler(MouseEvent.MOUSE_CLICKED,
                            new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    if (!click) {
                                        visibleNode();
                                        borderP.setRight(anchorP);
                                        click = true;
                                        return;
                                    }

                                    if (click){
                                        borderP.setRight(null);
                                        click = false;
                                        return;
                                    }
                                }

                            });
                }

                for (Text text : textArray) {
                    mapWindow.getChildren().addAll(text);
                }

            }
            else{
                System.err.println("Not valid file");
            }
        }
        catch (IllegalArgumentException e){
            //JOptionPane.showMessageDialog(null,e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error in file, please check the file for mistakes");
            alert.showAndWait();
            //e.printStackTrace();
        }
        //catching generic exception with displaying stacktrace in the error window
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unexpected exception");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            String exceptionText = sw.toString();
            Label label = new Label("The exception stacktrace was:");
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
            alert.showAndWait();
        }

    }

    /**
     * Open Dialog window when click on LineOpen MenuItem
     * @param actionEvent
     */
    public void lineClick(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("YAML (*.yaml)", "*.yaml"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
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
            //JOptionPane.showMessageDialog(null,e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error in file, please check the file for mistakes");
            alert.showAndWait();
        }
    }

    private void visibleNode(){
        timeTable.setPrefWidth(200);
        timeTable.setPrefHeight(230);
        timeTable.setLayoutY(130);
        timeTable.setEditable(false);
        timeTable.setText("Tu bude ten jizdni rad");

        roadDegree.setLayoutX(25);
        roadDegree.setLayoutY(5);
        roadDegree.setPrefWidth(150);

        closeStreet.setLayoutX(25);
        closeStreet.setLayoutY(75);
        closeStreet.setPrefWidth(150);
        closeStreet.setSelected(false);
    }

}
