package space;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Board extends Group {
    boolean autoFollow;
    Engine engine;
    Rotate xRotate;
    Rotate yRotate;
    Rotate zRotate;
    Timeline timeline;
    PerspectiveCamera camera;
    Translate pivot;

    public Board(PerspectiveCamera camera) {
        engine = new Engine(this);
        engine.update(0.001);
        this.camera = camera;
        setOnKeyPressed(engine);
        setOnKeyReleased(engine);
        prepareCamera(camera, true);
        getChildren().addAll(makeAxisGroup(), camera);
        engine.start();
    }

    public Group makeAxisGroup() {
        final Group axisGroup = new Group();
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(240.0, .5, .5);
        final Box yAxis = new Box(.5, 240.0, .5);
        final Box zAxis = new Box(.5, .5, 240.0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        return axisGroup;
    }

    static Duration totalSpinPeriod = Duration.seconds(30);

    public void prepareCamera(PerspectiveCamera camera, boolean spin) {
        camera.setFieldOfView(45);
        pivot = new Translate();
        xRotate = new Rotate(-20, Rotate.X_AXIS);
        yRotate = new Rotate(spin ? 0 : 45, Rotate.Y_AXIS);
        zRotate = new Rotate(0, Rotate.Z_AXIS);
        camera.getTransforms().addAll(
                pivot,
                yRotate,
                xRotate,
                zRotate,
                new Translate(0, 0, -100)
        );
        if (spin) {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(yRotate.angleProperty(), 0)),
                    new KeyFrame(totalSpinPeriod, new KeyValue(yRotate.angleProperty(), 360))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }
}
