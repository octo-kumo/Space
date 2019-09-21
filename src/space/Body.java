package space;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point3D;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.InputStream;

public class Body {
    private final static int TRAIL_COUNT = 100;
    private final static double TRAIL_DELAY = .5;
    private final static PhongMaterial SUN_MATERIAL = new PhongMaterial();
    private final static PhongMaterial MERCURY_MATERIAL = new PhongMaterial();
    private final static PhongMaterial VENUS_MATERIAL = new PhongMaterial();
    private final static PhongMaterial EARTH_MATERIAL = new PhongMaterial();
    private final static PhongMaterial MARS_MATERIAL = new PhongMaterial();
    private final static PhongMaterial JUPITER_MATERIAL = new PhongMaterial();
    private final static PhongMaterial SATURN_MATERIAL = new PhongMaterial();
    private final static PhongMaterial SATURN_RING_MATERIAL = new PhongMaterial();
    private final static PhongMaterial URANUS_MATERIAL = new PhongMaterial();
    private final static PhongMaterial NEPTUNE_MATERIAL = new PhongMaterial();
    private final static PhongMaterial MOON_MATERIAL = new PhongMaterial();
    private final static PhongMaterial[] MATERIALS = {MERCURY_MATERIAL, VENUS_MATERIAL, EARTH_MATERIAL, MARS_MATERIAL, JUPITER_MATERIAL, SATURN_MATERIAL, URANUS_MATERIAL, NEPTUNE_MATERIAL, MOON_MATERIAL, SUN_MATERIAL};
    public boolean alive;
    public Sphere sphere;
    public Vector position;
    public Vector velocity;
    public double mass;
    public double radius;
    public Sphere[] trail = new Sphere[TRAIL_COUNT];
    public String name;
    private double trailTimer = 0;
    private int trailTarget = 0;
    public Timeline spinTimeLine;

    static {
        InputStream DIFFUSE_MAP = Body.class.getResourceAsStream("/space/material/2k_earth_daymap.jpg");
        InputStream NORMAL_MAP = Body.class.getResourceAsStream("/space/material/2k_earth_normal_map.tif");
        InputStream SPECULAR_MAP = Body.class.getResourceAsStream("/space/material/2k_earth_specular_map.tif");
        InputStream MOON_MAP = Body.class.getResourceAsStream("/space/material/2k_moon.jpg");
        InputStream SUN_MAP = Body.class.getResourceAsStream("/space/material/8k_sun.jpg");
        EARTH_MATERIAL.setDiffuseMap(new Image(DIFFUSE_MAP));
        EARTH_MATERIAL.setBumpMap(new Image(NORMAL_MAP));
        EARTH_MATERIAL.setSpecularMap(new Image(SPECULAR_MAP));
        SUN_MATERIAL.setDiffuseMap(new Image(SUN_MAP));
        SUN_MATERIAL.setSelfIlluminationMap(new Image(SUN_MAP));
        MOON_MATERIAL.setDiffuseMap(new Image(MOON_MAP));
        MERCURY_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/2k_mercury.jpg")));
        VENUS_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/2k_venus_surface.jpg")));
        MARS_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/2k_mars.jpg")));
        JUPITER_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/8k_jupiter.jpg")));
        SATURN_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/8k_saturn.jpg")));
        URANUS_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/2k_uranus.jpg")));
        NEPTUNE_MATERIAL.setDiffuseMap(new Image(Body.class.getResourceAsStream("/space/material/2k_neptune.jpg")));
    }

    public Body(double x, double y, double z) {
        this(x, y, z, 0, 0, 0, 10, 1, 0);
    }

    public Body(double x, double y, double z, double mass, double radius, int type) {
        this(x, y, z, 0, 0, 0, mass, radius, type);
    }

    public Body(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, int type) {
        sphere = new Sphere(radius);
        sphere.setMaterial(MATERIALS[type]);
        sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        sphere.setTranslateZ(z);
        sphere.setEffect(new Bloom());
        position = new Vector(x, y, z);
        velocity = new Vector(vx, vy, vz);
        this.mass = mass;
        this.radius = radius;
        alive = true;
        for (int a = 0; a < TRAIL_COUNT; a++) {
            trail[a] = new Sphere(.2);
            trail[a].setTranslateX(x);
            trail[a].setTranslateY(y);
            trail[a].setTranslateZ(z);
        }
        setSpinAxis(Rotate.Y_AXIS);
    }

    public void setSpinAxis(Point3D axis) {
        sphere.setRotationAxis(axis);
    }

    public void setTiltSpin(double degree) {
        sphere.setRotationAxis(Rotate.Z_AXIS);
        sphere.setRotate(degree);
        sphere.setRotationAxis(new Point3D(Math.sin(Math.toRadians(degree)), Math.cos(Math.toRadians(degree)), 0));
    }

    public void setSpin(double angle) {
        sphere.setRotate(angle);
    }

    public void update(double delta) {
        position.incre(velocity.multiply(delta));
        sphere.setTranslateX(position.x);
        sphere.setTranslateY(position.y);
        sphere.setTranslateZ(position.z);
        trailTimer += delta;
        if (trailTimer > TRAIL_DELAY) {
            trailTimer -= TRAIL_DELAY;
            trail[trailTarget].setTranslateX(position.x);
            trail[trailTarget].setTranslateY(position.y);
            trail[trailTarget].setTranslateZ(position.z);
            trailTarget++;
            trailTarget %= TRAIL_COUNT;
        }
    }

    public double getVolume() {
        return 4 * Math.PI * radius * radius * radius / 3;
    }

    public double getDensity() {
        return mass / getVolume();
    }

    public Vector getMomentum() {
        return velocity.multiply(mass);
    }

    public static Timeline generateSpin(Shape3D shape3D, double fullRevTime) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(shape3D.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(Math.abs(fullRevTime)), new KeyValue(shape3D.rotateProperty(), fullRevTime > 0 ? 360 : -360))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        return timeline;
    }
}
