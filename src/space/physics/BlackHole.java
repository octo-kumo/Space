package space.physics;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import space.Controller;

public class BlackHole extends Body {
    //Black holes? They are a mystery, it is not like i would know much about them, all i know is that they look black sooooo, black it is
    private final static PhongMaterial BLACK_HOLE_MATERIAL = new PhongMaterial();

    public BlackHole(double x, double y, double z, double vx, double vy, double vz, double mass, double radius) {
        super(x, y, z, vx, vy, vz, mass, radius, 0);
        sphere.setMaterial(BLACK_HOLE_MATERIAL);
        name = Controller.resources.getString("blackhole");
    }

    public static void load() {
        BLACK_HOLE_MATERIAL.setSpecularPower(0);
        BLACK_HOLE_MATERIAL.setDiffuseColor(Color.BLACK);
        BLACK_HOLE_MATERIAL.setSpecularColor(Color.BLACK);
    }
}
