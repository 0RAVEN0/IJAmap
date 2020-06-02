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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import main.java.map.Coordinate;
import main.java.map.Stop;
import main.java.map.Street;
import main.java.shapes.ShapeLine;
import main.java.transport.*;

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
 * The main controller used for displaying a map
 */
public class Controller implements Initializable {

    Reader stRead = new Reader();
    public ShapeLine lineC = new ShapeLine();
    private List<Street> streets = null;
    private List<Line> lines = null;
    private List<javafx.scene.shape.Line> lineArray = new ArrayList<>();
    private List<javafx.scene.shape.Line> shortlineArray = new ArrayList<>();
    protected final List<Circle> circles = new ArrayList<>();
    private List<Street> strokeStreet = null;
    private  List<Street> clickedStreet = new ArrayList<>();
    private  List<Street> shortStreet = new ArrayList<>();
    private  List<Stop> clickedStop = new ArrayList<>();
    private List<Stop> imaginaryStops = new ArrayList<>();
    private final List<String> lineID = new ArrayList<>();
    private final BusList busList = new BusList();

    public File StreetFile = null;
    public File LinkFile = null;

    private String hours;
    private String minute;
    public String stringTime;

    private Street firstClicked;

    private boolean click = false;
    private boolean newTimeSet = false;
    private boolean newTimeSet2 = false;

    private double updateTime = 1.0;

    private int Road_D = 1;
    private int busDelay = 0;

    LocalTime lastTime = null;
    boolean linesBeingSet = false;
    boolean timeStop = false;

    CheckBox closeStreetBtn2 = new CheckBox("Close street");
    Label streetLabel;
    Label headLabel = new Label();
    Label delayLabel = new Label();
    AnchorPane anchorP;
    javafx.scene.shape.Line Hline = new javafx.scene.shape.Line();
    Button closeLine = new Button();
    ComboBox<String> roadDegree = new ComboBox<String>();


    private Timer programTime;
    private LocalTime currentTime = LocalTime.now();
    private LocalTime halfCurrentTime = LocalTime.now();

    protected Circle busCircle = null;

    @FXML
    protected Pane mapWindow;

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

    @FXML
    private Button stopTimeBtn;


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

                Platform.runLater(() -> {

                    if ( (lastTime == null || (lastTime.until(currentTime, ChronoUnit.SECONDS) == 2))
                            && (lines != null && !linesBeingSet) ) {
                        lastTime = currentTime;

                        for (Bus bus : busList.getBusList()) {
                            busCircle = bus.updatePosition(currentTime, circles, mapWindow);
                            if (busCircle != null) {
                                busCircle.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                        event -> {
                                            if (!click) {
                                                LocalTime startTime;
                                                for (int i = bus.getJourney().getStarts().size()-1; i >=0; i--) {
                                                    //find, which time from start times is actuall
                                                    if (bus.getJourney().getStarts().get(i).getHour() == currentTime.getHour() &&
                                                            currentTime.getMinute() >= bus.getJourney().getStarts().get(i).getMinute()) {
                                                        startTime = bus.getJourney().getStarts().get(i);

                                                        for (BusSchedule schedule : bus.getBusSchedules()){
                                                            //find original arrival time from schedule and set busDelay variable
                                                            if (schedule.getOriginalArrival().equals(startTime)){
                                                                if (schedule.getOriginalArrival().compareTo(schedule.getArrival()) == 0){
                                                                    busDelay = 0;
                                                                }
                                                                if (schedule.getOriginalArrival().compareTo(schedule.getArrival()) < 0){
                                                                    busDelay = schedule.getArrival().getMinute()-schedule.getOriginalArrival().getMinute();
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }

                                                visibleNode();
                                                itineraryPrint(bus.getLine(), bus.getJourney(),busDelay);
                                                strokeLine(lineArray, bus.getLine().getStreets());
                                                strokeStreet = bus.getLine().getStreets();
                                                borderP.setRight(anchorP);
                                                click=true;
                                            }
                                        });

                                closeLine.setOnAction(e -> {
                                    click = false;
                                    unstrokeLine(lineArray, strokeStreet);
                                    for (Node node : anchorP.getChildren()) {
                                        node.setVisible(false);
                                    }
                                    borderP.setRight(null);
                                });
                            }//end if busCircle != null
                        }//end for Bus bus
                    }//end if time to redraw
                });
            }
        };

        TimerTask halfTask = new TimerTask() {

            @Override
            public void run() {
                halfCurrentTime = halfCurrentTime.plusSeconds(1);
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

                if (newTimeSet2){
                    halfCurrentTime = LocalTime.of(Math.abs(halfCurrentTime.getHour()
                            + (Integer.parseInt(hours) - halfCurrentTime.getHour())),Math.abs(halfCurrentTime.getMinute()
                            + (Integer.parseInt(minute) - halfCurrentTime.getMinute())),0);
                    newTimeSet2 = false;
                }

                Platform.runLater(() -> Clock.setText(halfCurrentTime.format(timeFormat)));
            }
        };

        timeSpeed.setText(updateTime + "x");
        programTime.scheduleAtFixedRate(timerTask,0, (long) (1000 / updateTime));
        programTime.scheduleAtFixedRate(halfTask,0, 1000);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeStart(updateTime);

        roadDegree.getItems().addAll("normal","busy","traffic collapse");

        closeStreetBtn2.setOnAction(e -> {
            if (closeStreetBtn2.isSelected()) {
                programTime.cancel();
                if (clickedStreet.isEmpty()){
                    clickedStreet.add(firstClicked);
                }
            } else {
                timeStart(updateTime);
                if (!clickedStreet.isEmpty()) {
                    for(int s =0; s < clickedStreet.size()-1;s++) {
                        if (clickedStreet.get(s).findIntersectionWith(clickedStreet.get(s+1)) == null) {
                            System.out.println("Incoherent road");
                        } else {
                            Stop stop = new Stop(clickedStreet.get(s).getId(), clickedStreet.get(s).findIntersectionWith(clickedStreet.get(s+1)));
                            imaginaryStops.add(stop);
                        }
                    }
                }
                if(!clickedStreet.isEmpty() && !imaginaryStops.isEmpty()){
                    Coordinate startStreet = null;
                    Coordinate endStreet = null;
                    Street imaginaryStreet;
                    List<Coordinate> coordinateList;
                    for (int k = 0; k<clickedStreet.size();k++){
                        //set first Street
                        if (k == 0){
                            startStreet = new Coordinate(clickedStreet.get(k).begin().getX(),clickedStreet.get(k).begin().getY());
                            endStreet = new Coordinate(imaginaryStops.get(k).getCoordinate().getX(),imaginaryStops.get(k).getCoordinate().getY());
                        }
                        //set last Street
                        else if(k == clickedStreet.size()-1){
                            startStreet = new Coordinate(imaginaryStops.get(k-1).getCoordinate().getX(),imaginaryStops.get(k-1).getCoordinate().getY());
                            endStreet = new Coordinate(clickedStreet.get(k).end().getX(),clickedStreet.get(k).end().getY());

                        }
                        else{
                            startStreet = new Coordinate(imaginaryStops.get(k-1).getCoordinate().getX(),imaginaryStops.get(k-1).getCoordinate().getY());
                            endStreet = new Coordinate(imaginaryStops.get(k).getCoordinate().getX(),imaginaryStops.get(k).getCoordinate().getY());

                        }
                        coordinateList = new ArrayList<>();
                        coordinateList.add(0,startStreet);
                        coordinateList.add(1,endStreet);
                        imaginaryStreet = new Street(clickedStreet.get(k).getId(),coordinateList);
                        shortStreet.add(imaginaryStreet);
                    }
                    unstrokeLine(lineArray, clickedStreet);
                    lineC = new ShapeLine();
                    shortlineArray = lineC.drawLine(shortStreet,"#E5989B");

                    for (javafx.scene.shape.Line lineShort : shortlineArray) {
                        lineShort.setStrokeWidth(7);
                        mapWindow.getChildren().addAll(lineShort);
                    }
                }
            }
        });

        streetManipul(false);

        onlyNumber(setHour);
        onlyNumber(setMinute);

        setHour.setPromptText(String.valueOf(currentTime.getHour()));
        setMinute.setPromptText(String.valueOf(currentTime.getMinute()));

        closeLine.setVisible(false);
        anchorP = new AnchorPane(headLabel,Hline,closeLine,closeStreetBtn2,roadDegree,delayLabel);

    }

    /**
     * This function zooms map
     */
    @FXML
    private void Zoom(ScrollEvent event){
        event.consume();

        double mapZoom;

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

                lineArray = lineC.drawLine(streets,"#000000");

                for (javafx.scene.shape.Line line : lineArray) {
                    mapWindow.getChildren().addAll(line);

                    line.setCursor(Cursor.HAND);
                    line.addEventHandler(MouseEvent.MOUSE_CLICKED,
                            event -> {
                                if (!lineID.contains(line.getId())) {
                                    line.setStroke(Color.valueOf("#333D79FF"));
                                    line.setStrokeWidth(7);
                                    lineID.add(line.getId());
                                    streetManipul(true);
                                    setRoadDegree();
                                    for (Street street : streets){
                                        if (street.getId().equals(line.getId())){
                                            street.setBusyness(Road_D);
                                            if (clickedStreet.isEmpty()) {
                                                firstClicked = street;
                                            }
                                            if (closeStreetBtn2.isSelected()) {
                                                clickedStreet.add(street);
                                            }
                                        }
                                    }
                                    borderP.setRight(anchorP);
                                }
                                else{
                                    line.setStroke(Color.BLACK);
                                    line.setStrokeWidth(3);
                                    lineID.remove(line.getId());
                                    if (lineID.isEmpty()){
                                        streetManipul(false);
                                        borderP.setRight(null);
                                    }
                                }
                            });

                }
            }
        }
        catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error in file, please check the file for mistakes");
            alert.showAndWait();
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
                busList.clear();
                lines = stRead.readLines(LinkFile);

                //check if all streets within all lines are valid and add them, same with stops
                for (Line line : lines) {
                    for (Stop stop : line.getStops()) {
                        Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 10);
                        circle.setFill(Color.valueOf("#76528BFF"));
                        mapWindow.getChildren().add(circle);
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

                    for (Journey journey : line.getJourneys()) {
                        for (LocalTime start : journey.getStarts()) {
                            String busId = journey.getId()+start.toString();
                            if (!busList.add(new Bus(busId, journey, line, start, start.plusMinutes(journey.getLastSchedule().getDeparture()) ) ) ) {
                                throw new NoSuchElementException("Bus \""+busId+"\" couldn't be added to the busList.");
                            }
                        }
                    } //emd for Journey journey
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

    /**
     * Print itinerary into Border Pane
     * @param lineBus represents click line
     * @param journey represents line journey
     * @param busDelay represents how long bus is delayed
     */
    public void itineraryPrint(Line lineBus, Journey journey, int busDelay){
        int stopC = 0;
        List<LocalTime> start;
        start = journey.getStarts();

        //add delayLabel into anchorP with information about delay
        if(busDelay == 0){
            delayLabel.setTextFill(Color.GREEN);
            delayLabel.setText("Bus is on time.");
        }
        else {
            delayLabel.setTextFill(Color.RED);
            delayLabel.setText("Bus has " + busDelay + "delay.");
        }

        for (Stop stops : lineBus.getStops()){
            for (int i = journey.getStarts().size()-1; i >=0; i--) {
                //add stops names and departure times into anchorP
                if (start.get(i).getHour() == currentTime.getHour() && currentTime.getMinute() >= start.get(i).getMinute()) {
                    streetLabel = new Label();
                    streetLabel.setFont(new Font("Arial", 18));
                    streetLabel.setLayoutX(10);
                    streetLabel.setLayoutY(60 + stopC*30);
                    streetLabel.setText(stops.getId() + "        | " + start.get(i).plusMinutes(journey.getSequence().get(stopC).getDeparture()));

                    //if bus is on stop set color of bus departure time on green
                    if (currentTime.getMinute() == start.get(i).plusMinutes(journey.getSequence().get(stopC).getDeparture()).getMinute()){
                        streetLabel.setTextFill(Color.BROWN);
                        Hline.setVisible(false);
                    }
                    //if bus is between two stops set green line between two departure times
                    else if (currentTime.getMinute() > start.get(i).plusMinutes(journey.getSequence().get(stopC).getDeparture()).getMinute()){
                        Hline.setStartY(85 + stopC*30);
                        Hline.setEndY(85 + stopC*30);
                    }

                    anchorP.getChildren().add(streetLabel);
                    break;
                }
            }
            stopC++;
        }

    }

    /**
     * Making line thick and colored
     * @param lineArray list of all lines
     * @param streetArray list of streets on concrete Line
     */
    public void strokeLine(List<javafx.scene.shape.Line> lineArray, List<Street> streetArray){
        for (Street street : streetArray){
            for (javafx.scene.shape.Line line : lineArray){
                if (street.getId().equals(line.getId())){
                    line.setStroke(Color.valueOf("#76528BFF"));
                    line.setStrokeWidth(7);
                }
            }
        }
    }

    /**
     * Sets line black and on normal thickness
     * @param lineArray list of all lines
     * @param streetArray list of streets on concrete Line
     */
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
        Hline.setStartX(10);
        Hline.setEndX(250);
        Hline.setStrokeWidth(3);
        Hline.setStrokeLineCap(StrokeLineCap.ROUND);
        Hline.setStroke(Color.BROWN);
        Hline.setVisible(true);

        headLabel.setFont(new Font("Arial", 20));
        headLabel.setText("Stop name | Departure time");
        headLabel.setLayoutX(10);
        headLabel.setLayoutY(35);
        headLabel.setVisible(true);

        delayLabel.setLayoutX(25);
        delayLabel.setLayoutY(150);
        delayLabel.setVisible(true);
        delayLabel.setFont(new Font("Arial",12));

        closeLine.setLayoutX(25);
        closeLine.setLayoutY(300);
        closeLine.setPrefWidth(150);
        closeLine.setText("Close itinerary");
        closeLine.setVisible(true);
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
        newTimeSet2 = true;

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

    /**
     * TextFields accept only numbers
     * @param timeField text fields for clock
     */
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
     * Pause/run time while click on button
     * @param actionEvent representing some type of action
     */
    @FXML
    public void stopTime(ActionEvent actionEvent) {

        if (!closeStreetBtn2.isSelected()) {
            if (!timeStop) {
                stopTimeBtn.setText("\u25B6");
                timeStop = true;
                programTime.cancel();
            } else {
                stopTimeBtn.setText("\u23F8");
                timeStop = false;
                timeStart(updateTime);
            }
        }
    }

    /**
     * Function which sets road degree on street
     */
    public void setRoadDegree(){
        roadDegree.setOnAction(e -> {
            if(roadDegree.getValue().equals("normal")){
                Road_D = 1;
            }else if(roadDegree.getValue().equals("busy")){
                Road_D = 2;
            }
            else {
                Road_D = 3;
            }
        });
    }

    /**
     * Setting CheckBox and ComboBox parameter
     * @param visible if ChceckBox and ComboBox should be visible
     */
    public void streetManipul(boolean visible){
        roadDegree.setPromptText("normal");
        roadDegree.setLayoutX(25);
        roadDegree.setLayoutY(50);
        roadDegree.setVisible(visible);

        closeStreetBtn2.setLayoutX(25);
        closeStreetBtn2.setLayoutY(25);
        closeStreetBtn2.setPrefWidth(150);
        closeStreetBtn2.setVisible(visible);

    }
}
