package space;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import space.util.Config;
import space.util.Information;
import space.util.Materials;
import space.util.ResizeHelper;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Space extends Application {
    private static Space instance;
    Board board;
    Parent parent;
    Stage primaryStage;
    Rectangle clip;
    private MediaPlayer player;
    private MediaView mediaView;
    private Text skipButton;
    private Runnable loader;
    private Text loading;
    private Scene scene;
    private FadeTransition fade;

    public static void main(String[] args) {
        System.out.println("Main => Java Init");
        loadFonts();
        launch(args);
        System.out.println("Main => Launched");
    }

    public static Space getInstance() {
        return instance;
    }

    public static void loadFonts() {
        Font.loadFont(Space.class.getResource("/space/font/Raleway-Bold.ttf").toExternalForm(), 32);
        Font.loadFont(Space.class.getResource("/space/font/Raleway-Light.ttf").toExternalForm(), 32);
        Font.loadFont(Space.class.getResource("/space/font/Raleway-Regular.ttf").toExternalForm(), 32);
        Font.loadFont(Space.class.getResource("/space/font/Raleway-RegularItalic.ttf").toExternalForm(), 32);
        Font.loadFont(Space.class.getResource("/space/font/VarelaRound-Regular.ttf").toExternalForm(), 32);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Main => Program Started");
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        instance = this; // Basic Config
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("Main => Program STOPPING!!");
            Platform.exit();
            System.exit(0);
        });
        System.out.println("Main => Basic Stage Configuration Done");
        System.out.println("Main => Making Da Clips + Loading Label + Loading Group + Fade");
        Group loadingGroup = new Group();
        loadingGroup.getStyleClass().add("loading-screen");
        Circle circle1 = new Circle(640, 380, 50);
        loading = new Text(590, 300, "Loading...");
        loading.setTextAlignment(TextAlignment.CENTER);
        loadingGroup.getChildren().addAll(circle1, loading);
        loading.setFont(Font.font(28));
        fade = new FadeTransition(Duration.millis(750), loadingGroup);
        fade.setFromValue(1);
        fade.setToValue(.2);
        fade.setCycleCount(1000);
        fade.setAutoReverse(true);
        fade.play();
        clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        System.out.println("Main => Done");
        System.out.println("Main => Making the Scene");
        scene = new Scene(loadingGroup, 1280, 660);
        scene.getStylesheets().add(getClass().getResource("/space/main.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        clip.widthProperty().bind(scene.widthProperty());
        clip.heightProperty().bind(scene.heightProperty());
        System.out.println("Main => Done");
        primaryStage.setScene(scene);
        primaryStage.show();

        createRunnables();
        System.out.println("Main => Starting the Loading Task...");
        new Thread(loader).start();
        System.out.println("Main => Main Thread Comes to a Stop ( \u0361\u00b0 \u035c\u0296 \u0361\u00b0)");
    }

    public void createRunnables() {
        Runnable fxTasks = () -> { // Requires the JavaFX Thread
            System.out.println("--- FX Tasks Started");
            SubScene spaceScene = new SubScene(board, 0, 0, true, null);

            spaceScene.setOnMouseDragged(board);
            spaceScene.setOnMousePressed(board);
            spaceScene.setOnMouseReleased(board);
            spaceScene.setOnScroll(event -> Controller.getInstance().cameraZoom.valueProperty().set(Math.max(0, Controller.getInstance().cameraZoom.valueProperty().get() + event.getDeltaY() / 100)));
            spaceScene.setFill(Color.BLACK);
            spaceScene.setCamera(board.camera);
            System.out.println("--- Subscene Done");
            //FX Binding
            Controller.getInstance().contentPane.setCenter(spaceScene);
            spaceScene.widthProperty().bind(Controller.getInstance().contentPane.widthProperty().add(-300));
            spaceScene.heightProperty().bind(Controller.getInstance().contentPane.heightProperty());
            Controller.getInstance().bind();
            System.out.println("--- firstTime = " + Config.getBoolean("firstTime"));
            if (Config.getBoolean("firstTime")) loading.setText(Controller.resources.getString("firstTime"));
            else loading.setText(Controller.resources.getString("welcomeBack"));
            System.out.println("--- Updated 'loading' label to '" + loading.getText() + '\'');
            System.out.println("--- FX Tasks Ended");
        };
        Runnable finalTransition = () -> {
            System.out.println("==== Final Transition STARTED");
            if (!(mediaView == null)) mediaView.setClip(null);
            parent.setClip(clip);
            fade.stop();
            board.engine.start();
            scene.setRoot(parent);
            ResizeHelper.addResizeListener(primaryStage);
            System.out.println("==== Final Transition ENDED");
        };
        loader = () -> { // Multi threading used to load everything.
            System.out.println("Loader-> Loader Started...");
            try {
                //Heavy Loaders
                Information.load();
                Config.load();
                if (!Config.ROOT.has("firstTime")) Config.ROOT.put("firstTime", true);
                Controller.resources = ResourceBundle.getBundle("space.lang.messages", new Locale(Config.getString("lang"), Config.getString("country")));
                parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/space/gi.fxml")));
                board = new Board();
                Materials.load();
                System.out.println("Loader-> Loaded 'Information' 'Config' 'Resource Bundle' 'parent' and 'Materials'.");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("Loader-> Launched FX Tasks...");
            Platform.runLater(fxTasks);
            //UI Setup
            boolean showIntro = Config.ROOT.getBoolean("firstTime");
            if (showIntro) {
                System.out.println("Loader-> Cool Video is showing! ( ͡ʘ ͜ʖ ͡ʘ)");
                Media media = new Media(getClass().getResource("/space/media/space_intro.mp4").toExternalForm());
                player = new MediaPlayer(media);
                mediaView = new MediaView(player);
                skipButton = new Text(1050, 640, "Click To Skip \u27F9");
                skipButton.getStyleClass().addAll("hover-cyan", "skip-button");
                skipButton.setOnMouseClicked(e -> player.stop());
                player.setOnEndOfMedia(player::stop);
                player.setOnStopped(() -> {
                    System.out.println("Loader-> Cool Video Stopped.");
                    Config.ROOT.put("firstTime", false);
                    try {
                        Config.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Loader-> Video Shown. Starting up finalTransition...");
                    Platform.runLater(finalTransition);
                });
                System.out.println("Loader-> Cool Video will be Shown Soon!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Loader-> Cool Video STARTING!");
                Platform.runLater(() -> {
                    mediaView.setClip(clip);
                    fade.setNode(skipButton);
                    fade.playFromStart();
                    scene.setRoot(new Group(mediaView, skipButton));
                    player.play();
                });
            } else { //Ignores intro
                System.out.println("Loader-> Cool Video is not showing!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Loader-> Video Not Shown. Starting up finalTransition...");
                Platform.runLater(finalTransition);
            }
        };
    }
}
