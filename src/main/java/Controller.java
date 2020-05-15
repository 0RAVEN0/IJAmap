/**
 * Author: Michal Vanka (xvanka00) Romana Dzubarova (xdzuba00)
 * Contents: Controller for map.fxml
 */
package main.java;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Used for displaying a map
 */
public class Controller implements Initializable {

    Reader stRead = new Reader();
    public ShapeLine lineC = new ShapeLine();
    private List<Street> streets = null;
    private List<Line> lines = null;
    private List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private List<Text> textArray = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
    private final String[] color = {"CYAN","CORAL","GOLD","FUCHSIA","DARKGREEN","DARKCYAN","BLUEVIOLET",
            "MAGENTA","MAROON","OLIVE","DARKBLUE","RED","BLUE","GREEN","YELLOW","PINK","ORANGE","BROWN",
            "PURPLE","GREY"};


    public File StreetFile = null;
    public File LinkFile = null;

    private String hours;
    private String minute;
    public String stringTime;

    private boolean click = false;
    private boolean newTimeSet = false;

    private double updateTime = 1.0;

    LocalTime lastTime = null;
    boolean linesBeingSet = false;


    //ComboBox roadDegree = new ComboBox();
    Label streetLabel;
    CheckBox closeStreet = new CheckBox("Close street");
    AnchorPane anchorP;


    private Timer programTime;
    private LocalTime currentTime = LocalTime.now();

    private Circle busCircle = null;

    @FXML
    private Pane mapWindow;

    @FXML
    private BorderPane borderP;

    @FXML
    private Label Clock;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField setHour;

    @FXML
    private TextField setMinute;

    @FXML
    private Label timeSpeed;

    /**
     * Run time and change time by adding time into text fields. Set this time into Label.
     * @param updateTime can change time speed
     */
    public void timeStart(double updateTime){
        programTime = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                currentTime = currentTime.plusSeconds(1);

                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                stringTime = currentTime.format(timeFormat);

                if (newTimeSet){
                    currentTime = LocalTime.of(Math.abs(currentTime.getHour()
                            + (Integer.parseInt(hours) - currentTime.getHour())),Math.abs(currentTime.getMinute()
                            + (Integer.parseInt(minute) - currentTime.getMinute())),0);
                    newTimeSet = false;
                    lastTime = null;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Clock.setText(stringTime);

                        if ( (lastTime == null || (lastTime.until(currentTime, ChronoUnit.SECONDS) == 5))
                                && (lines != null && !linesBeingSet) ) {
                            lastTime = currentTime;

                            for (Line line : lines) {
                                for (Journey journey : line.getJourneys()) {
                                    for (LocalTime start : journey.getStarts()) {
                                        //a unique ID for identifying the circle for this journey at this time
                                        String circleId = journey.getId()+start.toString();
                                        //check if the time is between start and end of the journey
                                        if (currentTime.compareTo(start) >= 0
                                                && currentTime.compareTo(start.plusMinutes(journey.getLastSchedule().getArrival())) <= 0) {
                                            //check between which 2 stops the bus should be
                                            for (int i = 0; i < journey.getSequence().size()-1; i++) {
                                                LocalTime stop1departure = start.plusMinutes(journey.getSequence().get(i).getDeparture());
                                                LocalTime stop2arrival = start.plusMinutes(journey.getSequence().get(i+1).getArrival());

                                                //if the time is between these two stops, start calculating position
                                                if (currentTime.compareTo(stop1departure) >= 0 && currentTime.compareTo(stop2arrival) <= 0) {
                                                    Stop stop1 = journey.getStops().get(i);
                                                    Stop stop2 = journey.getStops().get(i+1);
                                                    //number of seconds between stops
                                                    int timeDiff = (journey.getSequence().get(i+1).getArrival()-journey.getSequence().get(i).getDeparture())*60;
                                                    Coordinate position;
                                                    double x, y;

                                                    //if the stops are on the same street (which is just a line)
                                                    if (stop1.getStreet() == stop2.getStreet()) {
                                                        Coordinate distance = stop1.distance(stop2.getCoordinate());
                                                        x = (stop1.getCoordinate().getX()
                                                                + (distance.getX() / timeDiff) * (stop1departure.until(currentTime, ChronoUnit.SECONDS)));
                                                        y = (stop1.getCoordinate().getY()
                                                                + (distance.getY() / timeDiff) * (stop1departure.until(currentTime, ChronoUnit.SECONDS)));
                                                        position = new Coordinate(x, y);
                                                    }
                                                    else {
                                                        Coordinate streetEnd;
                                                        if (stop1.getStreet().follows(stop2.getStreet())) {
                                                            streetEnd = stop1.getStreet().closerEndTo(stop2.getCoordinate());
                                                        }
                                                        //if the street doesn't follow, the streets must intersect
                                                        else {
                                                            streetEnd = stop1.getStreet().findIntersectionWith(stop2.getStreet());
                                                            try {
                                                                if (streetEnd == null) {
                                                                    throw new ArithmeticException("Stops \""+stop1.getId()+"\" and \""+stop2.getId()+"\" are on non-following streets, but no intersection was found");
                                                                }
                                                            }
                                                            catch (ArithmeticException e) {
                                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                                alert.setTitle("Error");
                                                                alert.setHeaderText("Calculating bus position: "+e.getMessage());
                                                                alert.showAndWait();
                                                                continue;
                                                            }
                                                        }
                                                        //distance between stop1 and the street end
                                                        Coordinate distance1 = stop1.distance(streetEnd);
                                                        Coordinate distance2 = stop2.distance(streetEnd);
                                                        //distance between street end and stop2
                                                        distance2.negate();

                                                        //Pythagoras theorem used to calculate straight line distance between stop1 and street end
                                                        double totalDistance1 = Math.sqrt(streetEnd.diffX(stop1.getCoordinate()) * streetEnd.diffX(stop1.getCoordinate())
                                                                + streetEnd.diffY(stop1.getCoordinate()) *  streetEnd.diffY(stop1.getCoordinate()));
                                                        //Pythagoras theorem used to calculate straight line distance between street end and stop2
                                                        double totalDistance2 = Math.sqrt(stop2.getCoordinate().diffX(streetEnd) * stop2.getCoordinate().diffX(streetEnd)
                                                                + stop2.getCoordinate().diffY(streetEnd) *  stop2.getCoordinate().diffY(streetEnd));

                                                        //calculation of the time (in seconds) the bus spends between stop1 and street end
                                                        long timeDiff1 = (long) ((timeDiff * totalDistance1) / (totalDistance1+totalDistance2));
                                                        //calculation of the time (in seconds) the bus spends between street end and stop2
                                                        long timeDiff2 = (long) ((timeDiff * totalDistance2) / (totalDistance1+totalDistance2));

                                                        //if current time is before the time that bus passes the street end (line direction change)
                                                        if (currentTime.compareTo(stop1departure.plusSeconds(timeDiff1)) <= 0) {
                                                            x = stop1.getCoordinate().getX()
                                                                    + (distance1.getX() / timeDiff1) * (stop1departure.until(currentTime, ChronoUnit.SECONDS));
                                                            y = stop1.getCoordinate().getY()
                                                                    + (distance1.getY() / timeDiff1) * (stop1departure.until(currentTime, ChronoUnit.SECONDS));
                                                        }
                                                        else {
                                                            x = streetEnd.getX() + (distance2.getX() / timeDiff2)
                                                                    * (stop1departure.plusSeconds(timeDiff1).until(currentTime, ChronoUnit.SECONDS));
                                                            y = streetEnd.getY() + (distance2.getY() / timeDiff2)
                                                                    * (stop1departure.plusSeconds(timeDiff1).until(currentTime, ChronoUnit.SECONDS));
                                                        }
                                                        position = new Coordinate(x,y);
                                                    }
                                                    System.out.println("Position: "+position);
                                                    busCircle = ShapeCircle.drawCircle(position, circleId);
                                                    for (Circle circle : circles) {
                                                        if (circle.getId().equals(circleId)) {
                                                            mapWindow.getChildren().remove(circle);
                                                        }
                                                    }
                                                    busCircle.setFill(Color.LAVENDER);
                                                    busCircle.setStroke(Color.PURPLE);
                                                    circles.add(busCircle);
                                                    mapWindow.getChildren().add(busCircle);
                                                    busCircle.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                                            new EventHandler<MouseEvent>() {
                                                                @Override
                                                                public void handle(MouseEvent event) {
                                                                    if (!click) {
                                                                        visibleNode();
                                                                        itineraryPrint(line,journey);
                                                                        strokeLine(lineArray,line.getStreets());
                                                                        borderP.setRight(anchorP);
                                                                        click = true;
                                                                        return;
                                                                    }
                                                                    unstrokeLine(lineArray,line.getStreets());
                                                                    for (Node node : anchorP.getChildren()){
                                                                        node.setVisible(false);
                                                                    }

                                                                    borderP.setRight(null);
                                                                    click = false;
                                                                }
                                                            });
                                                } // end if time within two stop times
                                            } //end for journey.sequence
                                        } //end if time between first and last stop
                                        else {
                                            for (Circle circle : circles) {
                                                if (circle.getId().equals(circleId)) {
                                                    mapWindow.getChildren().remove(circle);
                                                }
                                            }
                                        }
                                    } //end for journey.starts
                                }// end for Journey journey
                            } //end for Line line
                        }//end if time to redraw
                    }
                });
            }
        };

        timeSpeed.setText(updateTime + "x");
        programTime.scheduleAtFixedRate(timerTask,0, (long) (1000 / updateTime));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeStart(updateTime);

        onlyNumber(setHour);
        onlyNumber(setMinute);

        //scale for Pane to be on the full screen
        mapWindow.setScaleX(0.43046721);
        mapWindow.setScaleY(0.43046721);

        setHour.setPromptText(String.valueOf(currentTime.getHour()));
        setMinute.setPromptText(String.valueOf(currentTime.getMinute()));

        anchorP = new AnchorPane(closeStreet);

        //roadDegree.setPromptText("Road degree");
        //roadDegree.getItems().addAll("1. degree","2. degree","3. degree");
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
     * @param actionEvent representing some type of action
     */
    @FXML
    public void mapClick(ActionEvent actionEvent) {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("YAML (*.yaml)", "*.yaml"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            fc.setTitle("Open your map: ");

            StreetFile = fc.showOpenDialog(null);

            if (StreetFile != null){
                streets = stRead.readStreets(StreetFile);

                lineArray = lineC.drawLine(streets);
                textArray = lineC.drawText(streets);


                for (javafx.scene.shape.Line line : lineArray) {
                    mapWindow.getChildren().addAll(line);

                }

                for (Text text : textArray) {
                    mapWindow.getChildren().addAll(text);
                }
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
     * @param actionEvent representing some type of action
     */
    @FXML
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
                linesBeingSet = true;
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
                linesBeingSet = false;
            } //end if
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

    public void itineraryPrint(Line lineBus, Journey journey){
        int stopC = 0;

        for (Stop stops : lineBus.getStops()){
            for (LocalTime start : journey.getStarts()) {
                if (start.getHour() == currentTime.getHour()) {
                    streetLabel = new Label();
                    streetLabel.setFont(new Font("Arial", 20));
                    streetLabel.setLayoutX(15);
                    streetLabel.setLayoutY(25 + stopC*30);
                    streetLabel.setText(stops.getId() + " : " + String.valueOf(start.plusMinutes(journey.getSequence().get(stopC).getArrival())));
                    anchorP.getChildren().add(streetLabel);
                }
            }
            stopC++;
            //anchorP.getChildren().add(streetLabel);
        }

    }

    public void strokeLine(List<javafx.scene.shape.Line> lineArray, List<Street> streetArray){
        for (Street street : streetArray){
            for (javafx.scene.shape.Line line : lineArray){
                if (street.getId().equals(line.getId())){
                    line.setStroke(Color.PURPLE);
                    line.setStrokeWidth(7);
                }
            }
        }
    }

    public void unstrokeLine(List<javafx.scene.shape.Line> lineArray, List<Street> streetArray){
        for (Street street : streetArray){
            for (javafx.scene.shape.Line line : lineArray){
                if (street.getId().equals(line.getId())){
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(3);
                }
            }
        }
    }

    /**
     * Visible all nodes in BorderPane right.
     */
    private void visibleNode(){
        /*timeTable.setPrefWidth(200);
        timeTable.setPrefHeight(230);
        timeTable.setLayoutY(130);
        timeTable.setEditable(false);
        timeTable.setText("Tu bude ten jizdni rad");*/

        /*roadDegree.setLayoutX(25);
        roadDegree.setLayoutY(5);
        roadDegree.setPrefWidth(150);*/


        closeStreet.setLayoutX(25);
        closeStreet.setLayoutY(5);
        closeStreet.setPrefWidth(150);
        closeStreet.setSelected(false);
        closeStreet.setVisible(true);
    }

    /**
     * When click on faster button, updateTime increase
     * @param actionEvent representing some type of action
     */
    @FXML
    public void fasterTime(ActionEvent actionEvent) {

        if (updateTime >= 1) {
            updateTime = updateTime + 1;
            programTime.cancel();
            timeStart(updateTime);
            return;
        }
        if (updateTime >= 0.1){
            updateTime = (int)((updateTime + 0.1) * 100 + 0.5) / 100.0;
            programTime.cancel();
            timeStart(updateTime);
        }
    }

    /**
     * When click on faster button, updateTime decrease
     * @param actionEvent representing some type of action
     */
    @FXML
    public void slowerTime(ActionEvent actionEvent) {

        if (updateTime > 1) {
            updateTime = updateTime - 1;
            programTime.cancel();
            timeStart(updateTime);
            return;
        }
        if (updateTime > 0.1){
            updateTime = (int)((updateTime - 0.1) * 100 + 0.5) / 100.0;
            programTime.cancel();
            timeStart(updateTime);
        }
    }

    /**
     * Gets value from text fields into variables.
     * @param actionEvent representing some type of action
     */
    @FXML
    public void setNewTime(ActionEvent actionEvent) {

        if (setHour.getText().isEmpty() || setMinute.getText().isEmpty()){
            errorLabel.setText("Wrong time format");
            return;
        }

        newTimeSet = true;

        if(Integer.parseInt(setHour.getText()) >= 0 && Integer.parseInt(setHour.getText()) <= 24 && Integer.parseInt(setMinute.getText()) >= 0 && Integer.parseInt(setMinute.getText()) <= 59){
            errorLabel.setText("");
            hours = setHour.getText();
            minute = setMinute.getText();
            setHour.setText(setHour.getText());
            setMinute.setText(setMinute.getText());
        }
        else {
            errorLabel.setText("Wrong hour or minute format");
            hours = String.valueOf(currentTime.getHour());
            setHour.setText(hours);
            minute = String.valueOf(currentTime.getMinute());
            setMinute.setText(minute);
        }
    }

    public void onlyNumber(TextField timeField){
        Pattern pattern = Pattern.compile(".{0,2}");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>)
                change -> pattern.matcher(change.getControlNewText()).matches() ? change : null);
        timeField.setTextFormatter(formatter);
        timeField.addEventHandler(KeyEvent.KEY_PRESSED,
                new EventHandler<KeyEvent>() {

                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()){
                            case NUMPAD0:
                            case NUMPAD1:
                            case NUMPAD2:
                            case NUMPAD3:
                            case NUMPAD4:
                            case NUMPAD5:
                            case NUMPAD6:
                            case NUMPAD7:
                            case NUMPAD8:
                            case NUMPAD9:
                            case DIGIT0:
                            case DIGIT1:
                            case DIGIT2:
                            case DIGIT3:
                            case DIGIT4:
                            case DIGIT5:
                            case DIGIT6:
                            case DIGIT7:
                            case DIGIT8:
                            case DIGIT9:
                            case BACK_SPACE:
                                timeField.setEditable(true);
                                break;
                            default:
                                timeField.setEditable(false);
                                break;
                        }
                    }
                });
    }
}
