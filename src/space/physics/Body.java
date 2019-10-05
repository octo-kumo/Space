package space.physics;

import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import space.util.Materials;
import space.util.Vector;

/**
 * Represents a space body with mass, and shaped like a sphere.
 */
public class Body implements Mass, SphereCollider {

    private final static int TRAIL_COUNT = 100;
    private final static double TRAIL_DELAY = .5;
    public final Rotate tilt;
    public final Rotate rotate;
    public final Sphere sphere;
    public final Sphere[] trail;
    /**
     * Attributes of body's shape
     */
    private double radius;
    /**
     * Fields for display
     */
    public String name;
    public int type;
    public boolean alive;
    public double spinPeriod = 0;
    /**
     * Attributes of body made of matter
     */
    private Vector position;
    private Vector velocity;
    private double mass;
    private double trailTimer = 0; // When this timer exceeds TRAIL_DELAY, update the trail
    private int trailTarget = 0; // The "trail" sphere to update this round [0, TRAIL_COUNT-1]
    private double spinET = 0;

    /**
     * @param x      the x position
     * @param y      the y position
     * @param z      the z position
     * @param vx     the x velocity
     * @param vy     the y velocity
     * @param vz     the z velocity
     * @param mass   mass
     * @param radius radius of the shape
     */
    public Body(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, int type) {
        this(x, y, z, vx, vy, vz, mass, radius, type, 0);
    }

    /**
     * @param x      the x position
     * @param y      the y position
     * @param z      the z position
     * @param vx     the x velocity
     * @param vy     the y velocity
     * @param vz     the z velocity
     * @param mass   mass
     * @param radius radius of the shape
     */
    public Body(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, int type, double revTime) {
        this.position = new Vector(x, y, z);
        this.velocity = new Vector(vx, vy, vz);
        this.mass = mass;
        this.radius = radius;
        this.spinPeriod = revTime;
        trail = new Sphere[TRAIL_COUNT];
        sphere = new Sphere(radius);
        sphere.setMaterial(Materials.MATERIALS[type]);
        sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        sphere.setTranslateZ(z);
        alive = true;
        for (int a = 0; a < TRAIL_COUNT; a++) {
            trail[a] = new Sphere(radius / 20);
            trail[a].setMaterial(Materials.TRAIL_MATERIAL);
            trail[a].setTranslateX(x);
            trail[a].setTranslateY(y);
            trail[a].setTranslateZ(z);
        }
        this.type = type;
        sphere.getTransforms().addAll(tilt = new Rotate(0, Rotate.Z_AXIS), rotate = new Rotate(0, Rotate.Y_AXIS));
        name = "Body";
    }

    /**
     * Updates the body
     *
     * @param delta delta time passed. In seconds.
     */
    public void update(double delta) {
        position.incre(velocity.multiply(delta));
        sphere.setTranslateX(position.x);
        sphere.setTranslateY(position.y);
        sphere.setTranslateZ(position.z);
        spinET += delta;
        if (spinPeriod != 0) rotate.setAngle((spinET / spinPeriod) % 1 * 360);
        trailTimer += Math.abs(delta);// Maybe I am supporting negative delta
        if (trailTimer > TRAIL_DELAY) {
            trailTimer -= TRAIL_DELAY;
            trailTimer = Math.min(trailTimer, TRAIL_DELAY * 1.5); // Prevent trail spam when delta is too big. Cap trailTimer at 1.5 max
            trail[trailTarget].setTranslateX(position.x);
            trail[trailTarget].setTranslateY(position.y);
            trail[trailTarget].setTranslateZ(position.z);
            trailTarget++;
            trailTarget %= TRAIL_COUNT;
        }
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    public double getDensity() {
        return getMass() / getVolume();
    }
}
