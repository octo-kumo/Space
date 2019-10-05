package space.physics;

import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import space.Controller;

import java.io.InputStream;

// A star in my program is just a body that emits light
public class Star extends Body {
    private final static PhongMaterial SUN_MATERIAL = new PhongMaterial();
    public PointLight pointLight;

    public Star(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, double revTime) {
        super(x, y, z, vx, vy, vz, mass, radius, 0, revTime);
        sphere.setMaterial(SUN_MATERIAL);
        pointLight = new PointLight(Color.WHITE);
        pointLight.translateXProperty().bind(sphere.translateXProperty());
        pointLight.translateYProperty().bind(sphere.translateYProperty());
        pointLight.translateZProperty().bind(sphere.translateZProperty());
        name = Controller.resources.getString("star");
    }

    public static void load() {
        InputStream SUN_MAP = Star.class.getResourceAsStream("/space/material/8k_sun.jpg");
        InputStream SUN_LIGHT_MAP = Star.class.getResourceAsStream("/space/material/8k_sun_illumination.jpg");
        SUN_MATERIAL.setDiffuseMap(new Image(SUN_MAP));
        SUN_MATERIAL.setSelfIlluminationMap(new Image(SUN_LIGHT_MAP));
        SUN_MATERIAL.setSpecularPower(100);
    }
}
