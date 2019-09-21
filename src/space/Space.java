package space;

import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Space extends Application {

    Controls controls;
    Board board;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(.5);
        camera.setFarClip(10000);
        SubScene scene = new SubScene(board = new Board(camera), 1000, 600, true, null);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        BorderPane borderPane = new BorderPane(scene, null, controls = new Controls(board), null, null);
        Scene root = new Scene(borderPane, 1000, 600);
        scene.widthProperty().bind(borderPane.widthProperty().add(-300));
        scene.heightProperty().bind(borderPane.heightProperty());
        primaryStage.setScene(root);
        primaryStage.setTitle("Space Simulator");
        primaryStage.show();
    }

    private static Space instance;

    public static Space getInstance() {
        return instance;
    }
}
