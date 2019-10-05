package space;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.JSONException;
import space.physics.BlackHole;
import space.physics.Body;
import space.physics.Star;
import space.util.Config;
import space.util.Information;
import space.util.Materials;
import space.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class Controller {

    private static final String[] lang = {"en", "zh", "zh", "fr", "ja", "de", "ru"};
    private static final String[] country = {"", "CN", "TW", "", "", "", ""};
    public static ResourceBundle resources;
    private static Controller instance;
    @FXML
    public Label bigHeader;
    @FXML
    public StackPane rootPane;
    @FXML
    public BorderPane contentPane;
    @FXML
    public TitledPane cameraControllPane;
    @FXML
    public Label cameraRollLabel;
    @FXML
    public Slider cameraRoll;
    @FXML
    public Label cameraPitchLabel;
    @FXML
    public Slider cameraPitch;
    @FXML
    public Label cameraYawLabel;
    @FXML
    public Slider cameraYaw;
    @FXML
    public CheckBox autoSpin;
    @FXML
    public GridPane cameraPositionControl;
    @FXML
    public TextField cameraXField;
    @FXML
    public Slider cameraXSlider;
    @FXML
    public TextField cameraYField;
    @FXML
    public Slider cameraYSlider;
    @FXML
    public TextField cameraZField;
    @FXML
    public Slider cameraZSlider;
    @FXML
    public CheckBox cameraBound;
    @FXML
    public CheckBox followCOM;
    @FXML
    public Label cameraFOVLabel;
    @FXML
    public Slider cameraFOV;
    @FXML
    public Label cameraZoomLabel;
    @FXML
    public Slider cameraZoom;
    @FXML
    public TitledPane engineControlPane;
    @FXML
    public Label simGLabel;
    @FXML
    public Slider simG;
    @FXML
    public Label simSpeedLabel;
    @FXML
    public Slider simSpeed;
    @FXML
    public CheckBox trails;
    @FXML
    public CheckBox collision;
    @FXML
    public CheckBox tidal;
    @FXML
    public CheckBox axis;
    @FXML
    public CheckBox ambientLight;
    @FXML
    public CheckBox paused;
    @FXML
    public TitledPane trackingPane;
    @FXML
    public TableView<Body> bodyList;
    @FXML
    public TableColumn<Body, String> nameCol;
    @FXML
    public TableColumn<Body, Double> xCol;
    @FXML
    public TableColumn<Body, Double> yCol;
    @FXML
    public TableColumn<Body, Double> zCol;
    @FXML
    public TitledPane createPane;
    @FXML
    public TextField newBodyX;
    @FXML
    public Slider newBodyXSlider;
    @FXML
    public TextField newBodyY;
    @FXML
    public Slider newBodyYSlider;
    @FXML
    public TextField newBodyZ;
    @FXML
    public Slider newBodyZSlider;
    @FXML
    public TextField newBodyVX;
    @FXML
    public Slider newBodyVXSlider;
    @FXML
    public TextField newBodyVY;
    @FXML
    public Slider newBodyVYSlider;
    @FXML
    public TextField newBodyVZ;
    @FXML
    public Slider newBodyVZSlider;
    @FXML
    public CheckBox obitCOM;
    @FXML
    public Label nameLabel;
    @FXML
    public TextField newBodyName;
    @FXML
    public TextField newBodyTilt;
    @FXML
    public Slider newBodyTiltSlider;
    @FXML
    public TextField newBodyF;
    @FXML
    public Slider newBodyFSlider;
    @FXML
    public Label massLabel;
    @FXML
    public TextField newBodyMass;
    @FXML
    public Slider newBodyMassSlider;
    @FXML
    public Label radiusLabel;
    @FXML
    public TextField newBodyRadius;
    @FXML
    public Slider newBodyRadiusSlider;
    @FXML
    public ComboBox<String> newBodyType;
    @FXML
    public CheckBox newBodyStar;
    @FXML
    public Button newBodyCreate;
    @FXML
    public Hyperlink newBodyInfo;
    @FXML
    public CheckBox newBodyBH;
    @FXML
    public Label createMessage;
    @FXML
    public TitledPane settingsPane;
    @FXML
    public Label systemnameLabel;
    @FXML
    public TextField systemName;
    @FXML
    public Button saveButton;
    @FXML
    public Button loadButton;
    @FXML
    public Label ioMessage;
    @FXML
    public Label avaliableLabel;
    @FXML
    public Label a1Label;
    @FXML
    public Label a2Label;
    @FXML
    public Label langLabel;
    @FXML
    public ComboBox<String> language;
    @FXML
    public ScrollPane infoPane;
    @FXML
    public ImageView infoImage;
    @FXML
    public Label infoName;
    @FXML
    public Label infoFacts;
    @FXML
    public VBox infoBody;

    @FXML
    public Button importButton;
    @FXML
    public Button exportButton;

    @FXML
    public ImageView viewChanger;

    @FXML
    public Label cameraPosition;

    private double tempX, tempY;
    private Sphere preview;
    private boolean infoShown = false;
    private FileChooser fileChooser;

    public static Controller getInstance() {
        return instance;
    }

    @FXML
    public void save() {
        try {
            Config.saveSystem(systemName.getText(), Space.getInstance().board.engine.bodies);
            ioMessage.setText(resources.getString("saved"));
        } catch (InvalidKeyException e) {
            ioMessage.setText(e.getMessage());
        } catch (JSONException e) {
            ioMessage.setText(resources.getString("corrupted"));
        }
    }

    @FXML
    public void load() {
        try {
            Space.getInstance().board.engine.loadBodies(Config.loadSystem(systemName.getText()));
            ioMessage.setText(resources.getString("loaded"));
        } catch (InvalidKeyException e) {
            ioMessage.setText(e.getMessage());
        } catch (JSONException e) {
            ioMessage.setText(resources.getString("corrupted"));
        }
    }

    private Image moon, sun;

    public void initialize() {
        instance = this;
        fileChooser = new FileChooser();
        newBodyType.getItems().addAll("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Moon");
        newBodyType.getSelectionModel().select(0);
        language.getItems().addAll("English", "简体", "繁體", "Fran\u00e7aise", "日本語", "Deutsche", "P\u0443\u0441\u0441\u043a\u0438\u0439");
        String saveLang = Config.getString("lang");
        String saveCountry = Config.getString("country");
        OptionalInt first = IntStream.range(0, lang.length).filter(a -> lang[a].equals(saveLang) && country[a].equals(saveCountry)).findFirst();
        language.getSelectionModel().selectedIndexProperty().addListener((a, b, newValue) -> setLocale(lang[(int) newValue], country[(int) newValue]));
        language.getSelectionModel().select(first.isPresent() ? first.getAsInt() : 0);
        preview = new Sphere();
        preview.setMaterial(Materials.TRAIL_MATERIAL);

        moon = new Image(getClass().getResource("/space/media/moon-icon.png").toExternalForm());
        sun = new Image(getClass().getResource("/space/media/sun-icon.png").toExternalForm());

        if (!Config.ROOT.has("dark-mode") || Config.ROOT.getBoolean("dark-mode")) {
            rootPane.getStyleClass().add("dark-bg");
            Config.ROOT.put("dark-mode", true);
            viewChanger.setImage(moon);
        }

        nameCol.setCellValueFactory(p -> {
            if (p.getValue() != null) return new SimpleStringProperty(p.getValue().name);
            else return new SimpleStringProperty("BODY");
        });

        xCol.setCellValueFactory(p -> {
            if (p.getValue() != null) return p.getValue().sphere.translateXProperty().asObject();
            else return new SimpleDoubleProperty(0).asObject();
        });
        yCol.setCellValueFactory(p -> {
            if (p.getValue() != null) return p.getValue().sphere.translateYProperty().asObject();
            else return new SimpleDoubleProperty(0).asObject();
        });
        zCol.setCellValueFactory(p -> {
            if (p.getValue() != null) return p.getValue().sphere.translateZProperty().asObject();
            else return new SimpleDoubleProperty(0).asObject();
        });
        makeFormattedDoubleCol(xCol);
        makeFormattedDoubleCol(yCol);
        makeFormattedDoubleCol(zCol);

        bodyList.setRowFactory(bi -> {
            TableRow<Body> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    Body body = row.getItem();
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem follow = new MenuItem("Follow");
                    MenuItem remove = new MenuItem("Remove");
                    MenuItem edit = new MenuItem("Edit");
                    contextMenu.getItems().addAll(follow, remove, edit);
                    follow.setOnAction(event -> Controller.getInstance().bindCamera(body.sphere.translateXProperty(), body.sphere.translateYProperty(), body.sphere.translateZProperty()));
                    remove.setOnAction(event -> body.alive = false);
                    edit.setOnAction(event -> {
                        Controller instance = Controller.getInstance();
                        instance.newBodyName.setText(body.name);
                        instance.newBodyX.setText(String.valueOf(body.getPosition().x));
                        instance.newBodyY.setText(String.valueOf(body.getPosition().y));
                        instance.newBodyZ.setText(String.valueOf(body.getPosition().z));
                        instance.newBodyVX.setText(String.valueOf(body.getVelocity().x));
                        instance.newBodyVY.setText(String.valueOf(body.getVelocity().y));
                        instance.newBodyVZ.setText(String.valueOf(body.getVelocity().z));

                        instance.newBodyTilt.setText(String.valueOf(body.tilt.getAngle()));
                        instance.newBodyF.setText(String.valueOf(body.spinPeriod));

                        instance.newBodyMass.setText(String.valueOf(body.getMass()));
                        instance.newBodyRadius.setText(String.valueOf(body.getRadius()));

                        instance.newBodyType.getSelectionModel().select(body.type);
                        instance.newBodyStar.setSelected(body instanceof Star);
                        instance.newBodyBH.setSelected(body instanceof BlackHole);
                        instance.createPane.setExpanded(true);
                    });
                    contextMenu.show(row, e.getScreenX(), e.getScreenY());
                }
            });
            return row;
        });
    }

    private void makeFormattedDoubleCol(TableColumn<Body, Double> col) {
        col.setCellFactory(cell -> new TableCell<>() {
            @Override
            public void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) setText(null);
                else setText(String.format("%+8.2e", price));
            }
        });
    }

    private void setLocale(String lang, String country) {
        Config.ROOT.put("lang", lang);
        Config.ROOT.put("country", country);
        try {
            Config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resources = ResourceBundle.getBundle("space.lang.messages", new Locale(lang, country));
        bigHeader.setText(resources.getString("header"));
        avaliableLabel.setText(resources.getString("available"));
        langLabel.setText(resources.getString("language"));
        massLabel.setText(resources.getString("mass"));
        radiusLabel.setText(resources.getString("radius"));
        nameLabel.setText(resources.getString("name"));
        avaliableLabel.setText(resources.getString("available"));
        systemnameLabel.setText(resources.getString("systemName"));
        cameraControllPane.setText(resources.getString("camControlPane"));
        createPane.setText(resources.getString("createPane"));
        engineControlPane.setText(resources.getString("engineControlPane"));
        trackingPane.setText(resources.getString("trackingPane"));
        settingsPane.setText(resources.getString("settingsPane"));
        saveButton.setText(resources.getString("saveButton"));
        loadButton.setText(resources.getString("loadButton"));
        importButton.setText(resources.getString("import"));
        exportButton.setText(resources.getString("export"));
        cameraPosition.setText(resources.getString("cameraPosition"));

        cameraBound.setText(resources.getString("cameraBound"));
        followCOM.setText(resources.getString("followCOM"));
        autoSpin.setText(resources.getString("autoSpin"));
        trails.setText(resources.getString("showTrails"));
        tidal.setText(resources.getString("tidalForces"));
        collision.setText(resources.getString("collision"));
        ambientLight.setText(resources.getString("ambientLight"));
        paused.setText(resources.getString("paused"));
        axis.setText(resources.getString("showAxis"));
        obitCOM.setText(resources.getString("obitCOM"));
        newBodyInfo.setText(resources.getString("moreInfo"));
        newBodyStar.setText(resources.getString("star"));
        newBodyBH.setText(resources.getString("blackhole"));
        newBodyCreate.setText(resources.getString("createPane"));
        systemName.setPromptText(resources.getString("onlyLetterAndUnderscore"));

        updateLabelWithSlider("cameraRoll", cameraRoll, cameraRollLabel);
        updateLabelWithSlider("cameraPitch", cameraPitch, cameraPitchLabel);
        updateLabelWithSlider("cameraYaw", cameraYaw, cameraYawLabel);
        updateLabelWithSlider("cameraFOV", cameraFOV, cameraFOVLabel);
        updateLabelWithSlider("cameraDistance", cameraZoom, cameraZoomLabel);
        updateLabelWithSlider("gConstant", simG, simGLabel);
        updateLabelWithSlider("simSpeed", simSpeed, simSpeedLabel);
    }

    private void linkSliderWithLabel(String formatKey, Slider slider, Label label) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> label.setText(String.format(resources.getString(formatKey), newValue)));
    }

    private void updateLabelWithSlider(String formatKey, Slider slider, Label label) {
        label.setText(String.format(resources.getString(formatKey), slider.getValue()));
    }

    private void linkSliderWithTextField(Slider slider, TextField textField) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!textField.isFocused()) textField.setText(String.valueOf(newValue));
        });
        slider.disableProperty().bindBidirectional(textField.disableProperty());
        textField.setOnAction(e -> slider.setValue(Double.parseDouble(textField.getText())));
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!textField.isFocused()) slider.setValue(Double.parseDouble(textField.getText()));
        });
    }

    private void makeSliderWithInfiniteRange(Slider slider, double diff) {
        slider.setOnMouseReleased(e -> {
            slider.setMax(Math.round(slider.getValue() + diff));
            slider.setMin(Math.round(slider.getValue() - diff));
        });
    }

    void bind() {
        Board board = Space.getInstance().board;
        board.getChildren().add(preview);
        cameraRoll.valueProperty().bindBidirectional(board.zRotate.angleProperty());
        cameraPitch.valueProperty().bindBidirectional(board.xRotate.angleProperty());
        cameraYaw.valueProperty().bindBidirectional(board.yRotate.angleProperty());
        cameraFOV.valueProperty().bindBidirectional(board.camera.fieldOfViewProperty());
        cameraZoom.valueProperty().addListener((observable, oldValue, newValue) -> board.extraTranslate.zProperty().setValue(-Math.pow(2, (double) newValue)));
        cameraXSlider.valueProperty().bindBidirectional(board.pivot.xProperty());
        cameraYSlider.valueProperty().bindBidirectional(board.pivot.yProperty());
        cameraZSlider.valueProperty().bindBidirectional(board.pivot.zProperty());
        linkSliderWithLabel("cameraRoll", cameraRoll, cameraRollLabel);
        linkSliderWithLabel("cameraPitch", cameraPitch, cameraPitchLabel);
        linkSliderWithLabel("cameraYaw", cameraYaw, cameraYawLabel);
        linkSliderWithLabel("cameraFOV", cameraFOV, cameraFOVLabel);
        linkSliderWithLabel("cameraDistance", cameraZoom, cameraZoomLabel);
        linkSliderWithTextField(cameraXSlider, cameraXField);
        linkSliderWithTextField(cameraYSlider, cameraYField);
        linkSliderWithTextField(cameraZSlider, cameraZField);
        makeSliderWithInfiniteRange(cameraXSlider, 500);
        makeSliderWithInfiniteRange(cameraYSlider, 500);
        makeSliderWithInfiniteRange(cameraZSlider, 500);
        autoSpin.selectedProperty().bindBidirectional(board.autoSpin);
        cameraBound.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                board.pivot.xProperty().unbind();
                board.pivot.yProperty().unbind();
                board.pivot.zProperty().unbind();
            }
        });
        cameraPositionControl.disableProperty().bind(cameraBound.selectedProperty());
        followCOM.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) bindCamera(board.engine.comX, board.engine.comY, board.engine.comZ);
            else cameraBound.setSelected(false);
        });

        simG.valueProperty().addListener((observable, oldValue, newValue) -> Engine.G = 6.67 * Math.pow(10, (double) newValue));
        simSpeed.valueProperty().bindBidirectional(board.engine.speed);
        linkSliderWithLabel("gConstant", simG, simGLabel);
        linkSliderWithLabel("simSpeed", simSpeed, simSpeedLabel);
        makeSliderWithInfiniteRange(simSpeed, 8);
        trails.selectedProperty().bindBidirectional(board.engine.bodyTrails.visibleProperty());
        collision.selectedProperty().bindBidirectional(board.engine.collisionEnabled);
        tidal.selectedProperty().bindBidirectional(board.engine.tidalForceCounted);
        axis.selectedProperty().bindBidirectional(board.AXIS.visibleProperty());
        ambientLight.selectedProperty().bindBidirectional(board.engine.ambientLight.lightOnProperty());
        paused.selectedProperty().bindBidirectional(board.engine.paused);

        linkSliderWithTextField(newBodyXSlider, newBodyX);
        linkSliderWithTextField(newBodyYSlider, newBodyY);
        linkSliderWithTextField(newBodyZSlider, newBodyZ);
        linkSliderWithTextField(newBodyVXSlider, newBodyVX);
        linkSliderWithTextField(newBodyVYSlider, newBodyVY);
        linkSliderWithTextField(newBodyVZSlider, newBodyVZ);
        linkSliderWithTextField(newBodyMassSlider, newBodyMass);
        linkSliderWithTextField(newBodyRadiusSlider, newBodyRadius);
        linkSliderWithTextField(newBodyTiltSlider, newBodyTilt);
        linkSliderWithTextField(newBodyFSlider, newBodyF);
        newBodyVXSlider.disableProperty().bind(obitCOM.selectedProperty());
        newBodyVYSlider.disableProperty().bind(obitCOM.selectedProperty());
        newBodyVZSlider.disableProperty().bind(obitCOM.selectedProperty());
        newBodyType.disableProperty().bind(newBodyStar.selectedProperty().or(newBodyBH.selectedProperty()));

        preview.radiusProperty().bind(newBodyRadiusSlider.valueProperty());
        preview.translateXProperty().bind(newBodyXSlider.valueProperty());
        preview.translateYProperty().bind(newBodyYSlider.valueProperty());
        preview.translateZProperty().bind(newBodyZSlider.valueProperty());
    }

    public void bindCamera(DoubleProperty x, DoubleProperty y, DoubleProperty z) {
        cameraBound.setSelected(true);
        Translate pivot = Space.getInstance().board.pivot;
        pivot.xProperty().bind(x);
        pivot.yProperty().bind(y);
        pivot.zProperty().bind(z);
    }

    @FXML
    public void addBody() {
        try {
            double x = Double.parseDouble(newBodyX.getText());
            double y = Double.parseDouble(newBodyY.getText());
            double z = Double.parseDouble(newBodyZ.getText());
            double mass = Double.parseDouble(newBodyMass.getText());
            double radius = Double.parseDouble(newBodyRadius.getText());
            double tilt = Double.parseDouble(newBodyTilt.getText());
            double f = Double.parseDouble(newBodyF.getText());

            if (mass <= 0 || radius <= 0) {
                createMessage.setText(resources.getString("massRadiusAbove0"));
                return;
            }
            double vx = Double.parseDouble(newBodyVX.getText());
            double vy = Double.parseDouble(newBodyVX.getText());
            double vz = Double.parseDouble(newBodyVX.getText());
            if (obitCOM.isSelected()) {
                Vector diff = new Vector(x, y, z).minus(Space.getInstance().board.engine.centerOfMass);
                double r = diff.magnitude();
                vx = Space.getInstance().board.engine.getOrbitalSpeed(Space.getInstance().board.engine.totalMass, r, r);
                Vector velocity = diff.cross(new Vector(0, 1, 0)).normalise().multiply(vx);
                vx = velocity.x;
                vy = velocity.y;
                vz = velocity.z;
            }
            Body finalBody;
            if (newBodyBH.isSelected())
                finalBody = Space.getInstance().board.engine.addBlackHole(x, y, z, vx, vy, vz, mass, radius);
            else if (newBodyStar.isSelected())
                finalBody = Space.getInstance().board.engine.addStar(x, y, z, vx, vy, vz, mass, radius, f);
            else
                finalBody = Space.getInstance().board.engine.addBody(x, y, z, vx, vy, vz, mass, radius, newBodyType.getSelectionModel().getSelectedIndex(), Math.random() * 500 - 250);
            finalBody.tilt.setAngle(tilt);
            finalBody.spinPeriod = f;
            if (!newBodyName.getText().isEmpty()) finalBody.name = newBodyName.getText();
        } catch (NumberFormatException e) {
            createMessage.setText(resources.getString("enterNumber"));
        }
    }

    @FXML
    public void showInfo() {
        Information info = Information.INFORMATION[newBodyStar.isSelected() ? 9 : newBodyType.getSelectionModel().getSelectedIndex()];
        infoImage.setImage(info.image);
        infoName.setText(info.title);
        StringBuilder facts = new StringBuilder();
        for (String text : info.facts) facts.append('\u2022').append(' ').append(text).append("\n");
        infoFacts.setText(facts.toString());
        infoBody.getChildren().clear();
        for (String text : info.body) {
            Label label = new Label();
            label.setWrapText(true);
            if (text.startsWith("*")) {
                label.setFont(Font.font("Raleway", FontWeight.BOLD, 13));
                text = "\u2022 " + text.substring(1);
            } else label.setFont(Font.font("Raleway", FontWeight.LIGHT, 13));
            label.setText(text);
            infoBody.getChildren().add(label);
        }
        if (!infoShown) {
            KeyFrame frameStart = new KeyFrame(Duration.millis(0), new KeyValue(infoPane.translateXProperty(), -300, Interpolator.EASE_OUT));
            KeyFrame frameEnd = new KeyFrame(Duration.millis(200), new KeyValue(infoPane.translateXProperty(), 0, Interpolator.EASE_OUT));
            Timeline line = new Timeline(frameStart, frameEnd);
            line.play();
            infoShown = true;
        }
    }

    @FXML
    public void importFromFile() {
        fileChooser.setTitle(resources.getString("import"));
        File file = fileChooser.showOpenDialog(Space.getInstance().primaryStage);
        if (file == null) return;
        try {
            Space.getInstance().board.engine.loadBodies(Config.loadSystem(file));
            ioMessage.setText(resources.getString("imported") + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            ioMessage.setText(e.getMessage());
        }
    }

    @FXML
    public void exportToFile() {
        fileChooser.setTitle(resources.getString("export"));
        File file = fileChooser.showSaveDialog(Space.getInstance().primaryStage);
        if (file == null) return;
        try {
            Config.saveSystem(file, Space.getInstance().board.engine.bodies);
            ioMessage.setText(resources.getString("exported") + file.getAbsolutePath());
        } catch (IOException e) {
            ioMessage.setText(e.getMessage());
        }
    }

    @FXML
    public void hideInfo() {
        if (infoShown) {
            KeyFrame frameStart = new KeyFrame(Duration.millis(0), new KeyValue(infoPane.translateXProperty(), 0, Interpolator.EASE_OUT));
            KeyFrame frameEnd = new KeyFrame(Duration.millis(200), new KeyValue(infoPane.translateXProperty(), -300, Interpolator.EASE_OUT));
            Timeline line = new Timeline(frameStart, frameEnd);
            line.play();
            infoShown = false;
        }
    }

    @FXML
    public void close() {
        Space.getInstance().primaryStage.fireEvent(new WindowEvent(Space.getInstance().primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        System.exit(0);
    }

    @FXML
    public void fullScreen() {
        Space.getInstance().primaryStage.setFullScreen(!Space.getInstance().primaryStage.isFullScreen());
        if (Space.getInstance().primaryStage.isFullScreen()) Space.getInstance().parent.setClip(null);
        else Space.getInstance().parent.setClip(Space.getInstance().clip);
    }

    public void windowPressed(MouseEvent e) {
        Stage stage = Space.getInstance().primaryStage;
        tempX = stage.getX() - e.getScreenX();
        tempY = stage.getY() - e.getScreenY();
        ((Node) e.getSource()).setCursor(Cursor.CLOSED_HAND);
    }

    public void windowReleased(MouseEvent e) {
        ((Node) e.getSource()).setCursor(Cursor.DEFAULT);
    }

    public void windowDrag(MouseEvent e) {
        Stage stage = Space.getInstance().primaryStage;
        stage.setX(e.getScreenX() + tempX);
        stage.setY(e.getScreenY() + tempY);
    }

    public void switchTheme() {
        if (rootPane.getStyleClass().contains("dark-bg")) {
            rootPane.getStyleClass().remove("dark-bg");
            Config.ROOT.put("dark-mode", false);
            viewChanger.setImage(sun);
        } else {
            rootPane.getStyleClass().add("dark-bg");
            Config.ROOT.put("dark-mode", true);
            viewChanger.setImage(moon);
        }
        try {
            Config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
