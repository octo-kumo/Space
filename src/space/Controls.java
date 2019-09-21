package space;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Controls extends VBox {
    public ArrayList<BodyInfo> infoArray;

    public Controls(Board board) {
        infoArray = new ArrayList<>();
        setPrefWidth(300);
        setPadding(new Insets(5));
        Slider gSlider = new Slider(0.0000001, 0.0001, 6.670000000000001E-5);
        Label gLabel = new Label("G = 0.00667");
        Slider pitch = new Slider(-90, 90, -20);
        Label pitchLabel = new Label("Camera Pitch (-20\u00B0)");
        Slider yaw = new Slider(0, 360, 0);
        Label yawLabel = new Label("Camera Yaw (0\u00B0)");
        Slider roll = new Slider(0, 360, 0);
        Label rollLabel = new Label("Camera Roll (0\u00B0)");
        Slider speed = new Slider(.1, 5, 1);
        Label fovLabel = new Label("Camera FOV (45)");
        Slider fov = new Slider(10, 120, 45);
        Label speedLabel = new Label("Speed x1");
        CheckBox autoSpin = new CheckBox("Auto Spin");
        autoSpin.setSelected(true);
        autoSpin.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) board.timeline.play();
            else board.timeline.pause();
        });
        CheckBox autoFollow = new CheckBox("Follow Center of Mass");
        autoFollow.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (board.autoFollow = newValue) {
                board.camera.translateXProperty().unbind();
                board.camera.translateYProperty().unbind();
                board.camera.translateZProperty().unbind();
            }
        });
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        pitch.valueProperty().addListener((observable, oldValue, newValue) -> pitchLabel.setText("Camera Pitch (" + String.format("%.2f", newValue) + "\u00B0)"));
        pitch.valueProperty().bindBidirectional(board.xRotate.angleProperty());
        yaw.valueProperty().addListener((observable, oldValue, newValue) -> {
            yawLabel.setText("Camera Yaw (" + String.format("%.2f", newValue) + "\u00B0)");
            if (!autoSpin.isSelected())
                board.timeline.jumpTo(Board.totalSpinPeriod.multiply(((double) newValue) / 360d));
        });
        gSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Engine.G = (double) newValue;
            gLabel.setText("G = " + newValue);
        });
        yaw.valueProperty().bindBidirectional(board.yRotate.angleProperty());
        roll.valueProperty().addListener((observable, oldValue, newValue) -> rollLabel.setText("Camera Roll (" + String.format("%.2f", newValue) + "\u00B0)"));
        roll.valueProperty().bindBidirectional(board.zRotate.angleProperty());
        fov.valueProperty().addListener((observable, oldValue, newValue) -> fovLabel.setText("Camera FOV (" + String.format("%.2f", newValue) + ")"));
        fov.valueProperty().bindBidirectional(board.camera.fieldOfViewProperty());
        speed.valueProperty().addListener((observable, oldValue, newValue) -> speedLabel.setText("Speed x" + String.format("%.2f", newValue)));
        speed.valueProperty().bindBidirectional(board.engine.speed);

        GridPane creationPane = new GridPane();
        TextField xField = new TextField();
        xField.setPromptText("X");
        TextField yField = new TextField();
        yField.setPromptText("Y");
        TextField zField = new TextField();
        zField.setPromptText("Z");
        creationPane.add(xField, 0, 0);
        creationPane.add(yField, 1, 0);
        creationPane.add(zField, 2, 0);
        TextField vxField = new TextField();
        vxField.setPromptText("VX");
        TextField vyField = new TextField();
        vyField.setPromptText("VY");
        TextField vzField = new TextField();
        vzField.setPromptText("VZ");
        creationPane.add(vxField, 0, 1);
        creationPane.add(vyField, 1, 1);
        creationPane.add(vzField, 2, 1);
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Moon", "Sun"));
        type.getSelectionModel().select(0);
        TextField massField = new TextField();
        massField.setPromptText("Mass");
        TextField radiusField = new TextField();
        radiusField.setPromptText("Radius");
        Button add = new Button("Add");
        creationPane.add(massField, 0, 2);
        creationPane.add(radiusField, 1, 2);
        creationPane.add(add, 2, 2);
        creationPane.add(type, 0, 3, 3, 1);
        add.setOnAction(e -> {
            try {
                board.engine.addBody(Double.parseDouble(xField.getText()), Double.parseDouble(yField.getText()), Double.parseDouble(zField.getText()), Double.parseDouble(vxField.getText()), Double.parseDouble(vyField.getText()), Double.parseDouble(vzField.getText()), Double.parseDouble(massField.getText()), Double.parseDouble(radiusField.getText()), type.getSelectionModel().getSelectedIndex());
            } catch (NumberFormatException e1) {
                Alert a1 = new Alert(Alert.AlertType.ERROR, "Please key in a number!", ButtonType.OK);
                a1.showAndWait();
            }
        });

        VBox list = new VBox();
        board.engine.bodies.forEach(body -> {
            BodyInfo info = new BodyInfo(body);
            infoArray.add(info);
            list.getChildren().add(info);
        });
        list.setPrefHeight(300);

        getChildren().addAll(gLabel, gSlider, pitchLabel, pitch, yawLabel, yaw, rollLabel, roll, fovLabel, fov, speedLabel, speed, autoSpin, autoFollow, creationPane, list);
    }

    public void updateInfo() {
        infoArray.forEach(BodyInfo::updateInfo);
    }
}
