/**
 * Author: Michal Vanka (xvanka00) Romana Dzubarova (xdzuba00)
 * Contents: Controller for map.fxml
 */
package main.java;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.stream.Collectors;

/**
 * Used for displaying a map
 */
public class Controller implements Initializable {

    Reader stRead = new Reader();
    public ShapeLine lineC = new ShapeLine();
    public ShapeCircle circleC = new ShapeCircle();
    private List<Street> streets = null;
    private Map<String, Street> streetMap = null;
    private List<Line> lines = null;
    private List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private List<Text> textArray = new ArrayList<>();

    public File StreetFile = null;
    public File LinkFile = null;

    private String hours;
    private String minute;
    private String[] dayTime = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
    List<String> dayTimelist = Arrays.asList(dayTime);

    private boolean click = false;
    private boolean newTimeSet = false;
    public String formatDateTime;

    TextArea timeTable = new TextArea();
    ComboBox roadDegree = new ComboBox();
    CheckBox closeStreet = new CheckBox("Close street");
    AnchorPane anchorP;

    Timer programTime = new Timer();
    private LocalTime currentTime = LocalTime.now();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            currentTime = currentTime.plusSeconds(1);
            DateTimeFormatter format1 = DateTimeFormatter.ofPattern("HH:mm:ss");
            formatDateTime = currentTime.format(format1);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (newTimeSet){
                        System.out.println("si tu ");
                        System.out.println("hodiny =  " + Integer.valueOf(hours));
                        System.out.println("minuty = " + Integer.valueOf(minute));
                        currentTime.of(Integer.valueOf(hours),Integer.valueOf(minute),currentTime.getSecond());
                        Clock.setText(currentTime.format(format1));
                    }
                    else{
                        Clock.setText(formatDateTime);
                    }

                }
            });
        }
    };

    private Circle busCircle = null;

    @FXML
    private Pane mapWindow;

    @FXML
    private BorderPane borderP;

    @FXML
    private Label Clock;

    @FXML
    private Button plusBtn;

    @FXML
    private Button minusBtn;

    @FXML
    private AnchorPane timeAnchor;

    @FXML
    private TextField setHour;

    @FXML
    private TextField setMinute;

    @FXML
    private Button setBtn;

    public void start(){
        programTime.scheduleAtFixedRate(timerTask,0,1000);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        start();

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
                streets = stRead.readStreets(StreetFile);
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error in file, please check the file for mistakes");
            alert.showAndWait();
            //e.printStackTrace();
        }
        //catching generic exception with displaying stacktrace in the error window
        catch (Exception e) {
            StacktraceErrWindow.display(e);
        }

    }

    /**
     * Open Dialog window when click on LineOpen MenuItem
     * @param actionEvent
     */
    public void lineClick(ActionEvent actionEvent) {
        if (streets == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Map file needs to be loaded first");
            alert.showAndWait();
            return;
        }
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("YAML (*.yaml)", "*.yaml"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            fc.setTitle("Open your line: ");

            LinkFile = fc.showOpenDialog(null);

            if (LinkFile != null){
                lines = stRead.readLines(LinkFile);

                //check if all streets within all lines are valid and add them, same with stops
                for (Line line : lines) {
                    for (Stop stop : line.getStops()) {
                        boolean found = false;
                        for(Street street : streets) {
                            if(street.addStop(stop)) {
                                found = true;
                                if(!line.addStreet(street)) {
                                    throw new IllegalArgumentException("Street \"" + street.getId() + "\" does not follow the last street.");
                                }
                                break;
                            }
                        }
                        if (!found) {
                            throw new NoSuchElementException("Stop \"" + stop.getId() + "\" is not within any street.");
                        }
                    } //end for Stop stop
                } //end for Line line

                //TODO show buses on the map
                /**
                 * Do node busCircle si zavoláš metodu drawCircle, ktorá je v classe ShapeCircle.
                 * Ako argument funkcie použiješ ten lines.yaml subor, ktorý si zmenil na, ved ty vieš
                 * čo v tvojej classe Reader.
                 * Chod do ShapeCircle.java. --->
                 */
                busCircle = circleC.drawCircle(lines);

                /**
                 * ---> mapWindow je Pane do neho vykresluješ kruh pomocou týchto metod.
                 * KONEC.
                 */
                mapWindow.getChildren().add(busCircle);


            } //end if
            else{
                System.out.println("Not valid file");
            }
        }
        catch (NoSuchElementException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Line file: " + e.getMessage());
            alert.showAndWait();
        }
        catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Line file street error: " + e.getMessage());
            alert.showAndWait();
        }
        catch (Exception e){
            StacktraceErrWindow.display(e);
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

    public void fasterTime(ActionEvent actionEvent) {

    }

    public void slowerTime(ActionEvent actionEvent) {
        //Timer.setDelay(Duration.seconds(0.5));
    }

    public void setNewTime(ActionEvent actionEvent) {
        newTimeSet = true;

        if(Integer.valueOf(setHour.getText()) >= 0 && Integer.valueOf(setHour.getText()) <= 24){
            hours = setHour.getText();
        }
        if(Integer.valueOf(setMinute.getText()) >= 0 && Integer.valueOf(setMinute.getText()) <= 59){
            minute = setMinute.getText();
        }

        setHour.setText("");
        setMinute.setText("");

    }
}
