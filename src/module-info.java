module Space {
    requires transitive javafx.controls;
    requires javafx.fxml;
//    requires kotlin.stdlib;
    requires javafx.web;
    requires javafx.media;
    requires org.json;
    exports space;
    exports space.physics;
    exports space.util;
}