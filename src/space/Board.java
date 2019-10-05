package space;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Board extends Group implements EventHandler<MouseEvent> {
    final Group AXIS;
    SimpleBooleanProperty autoSpin;
    boolean autoFollow;
    Engine engine;
    Rotate xRotate;
    Rotate yRotate;
    Rotate zRotate;
    PerspectiveCamera camera;
    Translate pivot;
    Translate extraTranslate;
    private double lastX;
    private double lastY;

    public Board() {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(.5);
        camera.setFarClip(10000);

        autoSpin = new SimpleBooleanProperty(true);
        engine = new Engine(this);
        this.camera = camera;
        prepareCamera(camera);
        getChildren().addAll(AXIS = makeAxisGroup(), camera);
        AXIS.setVisible(false);
    }

    private Group makeAxisGroup() {
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

    private void prepareCamera(PerspectiveCamera camera) {
        camera.setFieldOfView(45);
        camera.getTransforms().addAll(
                pivot = new Translate(),
                yRotate = new Rotate(0, Rotate.Y_AXIS),
                xRotate = new Rotate(-20, Rotate.X_AXIS),
                zRotate = new Rotate(0, Rotate.Z_AXIS),
                extraTranslate = new Translate(0, 0, -100)
        );
    }

    /**
     * Used to handle dragging. So user can just change the viewing angle by dragging
     *
     * @param event the event lol
     */
    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            lastX = event.getX();
            lastY = event.getY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            double dX = event.getX() - lastX;
            double dY = event.getY() - lastY;
            lastX = event.getX();
            lastY = event.getY();
            yRotate.setAngle((yRotate.getAngle() + dX / 3 + 360) % 360);
            xRotate.setAngle(Math.max(-90, Math.min(90, xRotate.getAngle() - dY / 3)));
        }
    }
}
