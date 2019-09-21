package space;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class BodyInfo extends HBox {
    private final Label location;
    private Body body;

    public BodyInfo(Body body) {
        this.body = body;
        Label nameLabel = new Label(body.name);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        nameLabel.setAlignment(Pos.CENTER);
        location = new Label();
        Button follow = new Button("Follow");
        follow.setOnAction(e -> {
            Space.getInstance().board.pivot.xProperty().bind(body.sphere.translateXProperty());
            Space.getInstance().board.pivot.yProperty().bind(body.sphere.translateYProperty());
            Space.getInstance().board.pivot.zProperty().bind(body.sphere.translateZProperty());
        });
        getChildren().addAll(nameLabel, location, follow);
        setPrefHeight(20);
        setSpacing(5);
        updateInfo();
    }

    public void updateInfo() {
        if (body == null) return;
        if (!body.alive) {
            body = null;
            return;
        }
        location.setText(String.format("%.1f", body.position.x) + "," + String.format("%.1f", body.position.y) + "," + String.format("%.1f", body.position.z));
    }
}
