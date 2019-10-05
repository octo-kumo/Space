package space;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import space.physics.BlackHole;
import space.physics.Body;
import space.physics.Star;
import space.util.Config;
import space.util.Vector;

import java.security.InvalidKeyException;
import java.util.*;

public class Engine extends AnimationTimer {
    static double G = 6.67 * 0.000001;
    AmbientLight ambientLight;
    Group bodyTrails;
    ArrayList<Body> bodies;
    // Simple properties to be bound to
    SimpleDoubleProperty speed;
    SimpleBooleanProperty paused;
    SimpleBooleanProperty collisionEnabled;
    SimpleBooleanProperty tidalForceCounted;
    SimpleDoubleProperty comX;
    SimpleDoubleProperty comY;
    SimpleDoubleProperty comZ;
    double totalMass = 0;
    Vector centerOfMass = new Vector();
    private Queue<Body> addQueue;
    private Board display;
    private long lastFrameTime = 0;

    public Engine(Board display) {
        this.display = display;
        bodyTrails = new Group();
        bodies = new ArrayList<>();
        addQueue = new LinkedList<>();
        speed = new SimpleDoubleProperty(1);
        paused = new SimpleBooleanProperty(false);
        collisionEnabled = new SimpleBooleanProperty(true);
        tidalForceCounted = new SimpleBooleanProperty(false);
        comX = new SimpleDoubleProperty(0);
        comY = new SimpleDoubleProperty(0);
        comZ = new SimpleDoubleProperty(0);
        ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.setLightOn(false);
        display.getChildren().addAll(ambientLight, bodyTrails);
        try {
            loadBodies(Config.loadSystem("solar_system"));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void loadBodies(Collection<Body> bodies) {
        this.bodies.forEach(b -> b.alive = false);
        flushBodies();
        for (Body body : bodies) addBody(body);
    }

    public Body addBody(Body body) {
        addQueue.add(body);
        Controller.getInstance().bodyList.getItems().add(body);
        return body;
    }

    Body addBody(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, int type, double revTime) {
        return addBody(new Body(x, y, z, vx, vy, vz, mass, radius, type, revTime));
    }

    Star addStar(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, double revTime) {
        return (Star) addBody(new Star(x, y, z, vx, vy, vz, mass, radius, revTime));
    }

    BlackHole addBlackHole(double x, double y, double z, double vx, double vy, double vz, double mass, double radius) {
        return (BlackHole) addBody(new BlackHole(x, y, z, vx, vy, vz, mass, radius));
    }

    private void update(double rawDelta) {
        if (paused.get()) return;
        final double delta = speed.doubleValue() * rawDelta;
        if (bodies.size() > 1)
            for (int i = 0; i < bodies.size() - 1; i++) // Achieve a loop where every body is looped only once with any other body
                for (int j = i + 1; j < bodies.size(); j++) {
                    Body bodyA = bodies.get(i);
                    Body bodyB = bodies.get(j); // Easy access
                    if (!(bodyA.alive && bodyB.alive)) continue;
                    handleBody(delta, bodyA, bodyB);
                }
        bodies.forEach(body -> body.update(delta));
        flushBodies();
    }

    private void flushBodies() {
        for (Iterator<Body> iterator = bodies.iterator(); iterator.hasNext(); ) {
            Body body = iterator.next();
            if (!body.alive) {
                Controller.getInstance().bodyList.getItems().remove(body);
                display.getChildren().remove(body.sphere);
                bodyTrails.getChildren().removeAll(body.trail);
                if (body instanceof Star) display.getChildren().remove(((Star) body).pointLight);
                iterator.remove();
            }
        }
        while (addQueue.size() > 0) {
            Body body = addQueue.remove();
            display.getChildren().add(body.sphere);
            bodyTrails.getChildren().addAll(body.trail);
            if (body instanceof Star) display.getChildren().add(((Star) body).pointLight);
            bodies.add(body);
        }
    }

    private void handleBody(double delta, Body bodyA, Body bodyB) {
        Vector AtoB = bodyB.getPosition().minus(bodyA.getPosition());
        if (bodyA.isColliding(bodyB) && collisionEnabled.get()) {
            double totalMass = bodyA.getMass() + bodyB.getMass();
            Vector totalMomentum = bodyA.getMomentum().incre(bodyB.getMomentum()).scale(1 / totalMass);
            Vector centerOfMass = bodyA.getPosition().multiply(bodyA.getMass()).add(bodyB.getPosition().multiply(bodyB.getMass())).scale(1 / totalMass);
            double totalVolume = bodyA.getVolume() + bodyB.getVolume();
            double finalRadius = Math.cbrt(3 * totalVolume / 4 / Math.PI);
            bodyA.alive = false;
            bodyB.alive = false;
            Body finalBody;
            if (bodyA instanceof BlackHole || bodyB instanceof BlackHole)
                finalBody = addBlackHole(centerOfMass.x, centerOfMass.y, centerOfMass.z, totalMomentum.x, totalMomentum.y, totalMomentum.z, totalMass, finalRadius);
            else if (bodyA instanceof Star || bodyB instanceof Star)
                finalBody = addStar(centerOfMass.x, centerOfMass.y, centerOfMass.z, totalMomentum.x, totalMomentum.y, totalMomentum.z, totalMass, finalRadius, 0);
            else
                finalBody = addBody(centerOfMass.x, centerOfMass.y, centerOfMass.z, totalMomentum.x, totalMomentum.y, totalMomentum.z, totalMass, finalRadius, bodyA.getMass() > bodyB.getMass() ? bodyA.type : bodyB.type, 0);
            double angularVelocityA = 2 * Math.PI / bodyA.spinPeriod;
            double angularVelocityB = 2 * Math.PI / bodyB.spinPeriod;
            double momentOfInertiaA = 2 * bodyA.getMass() * bodyA.getRadius() * bodyA.getRadius() / 5;
            double momentOfInertiaB = 2 * bodyB.getMass() * bodyB.getRadius() * bodyB.getRadius() / 5;
            double finalMomentum = angularVelocityA * momentOfInertiaA + angularVelocityB * momentOfInertiaB;
            double finalMomentOfInertia = 2 * totalMass * finalRadius * finalRadius / 5;
            double finalAngularVelocity = finalMomentum / finalMomentOfInertia;
            finalBody.spinPeriod = 2 * Math.PI / finalAngularVelocity;
            System.out.println("Engine => Collision : FINAL MASS=" + totalMass + " | FINAL RADIUS=" + finalRadius + " | Spin Period {" + bodyA.spinPeriod + ", " + bodyB.spinPeriod + "} => " + finalBody.spinPeriod);
        } else {
            Vector force = AtoB.nor();
            double gMagnitude = G * bodyA.getMass() * bodyB.getMass() / AtoB.magnitude2();
            force.scale(gMagnitude);
            bodyA.getVelocity().incre(force.multiply(1 * delta / bodyA.getMass()));
            bodyB.getVelocity().incre(force.multiply(-1 * delta / bodyB.getMass()));
            if (tidalForceCounted.get()) {
                Body bodyToBeRipped = null;
                boolean ripA = underRocheLimit(bodyB, bodyA);
                boolean ripB = underRocheLimit(bodyA, bodyB);
                if (ripA) bodyToBeRipped = bodyA;
                else if (ripB) bodyToBeRipped = bodyB;
                if ((ripA || ripB) && Math.random() < 0.1) {
                    double mass = bodyToBeRipped.getMass() / 2;
                    double radius = Math.cbrt(3 * bodyToBeRipped.getVolume() / 8 / Math.PI);
                    double angle = Math.random() * 2 * Math.PI;
                    double z = Math.random() * 2 - 1;
                    Vector random = new Vector(Math.sqrt(1 - z * z) * Math.cos(angle), Math.sqrt(1 - z * z) * Math.sin(angle), z);
                    random.scale(bodyToBeRipped.getRadius() * 1.2);
                    Vector position1 = bodyToBeRipped.getPosition().add(random);
                    Vector position2 = bodyToBeRipped.getPosition().minus(random);
                    bodyToBeRipped.alive = false;
                    addBody(position1.x, position1.y, position1.z, bodyToBeRipped.getVelocity().x, bodyToBeRipped.getVelocity().y, bodyToBeRipped.getVelocity().z, radius, mass, bodyToBeRipped.type, bodyToBeRipped.spinPeriod);
                    addBody(position2.x, position2.y, position2.z, bodyToBeRipped.getVelocity().x, bodyToBeRipped.getVelocity().y, bodyToBeRipped.getVelocity().z, radius, mass, bodyToBeRipped.type, bodyToBeRipped.spinPeriod);
                }
            }
        }
    }

    @Override
    public void handle(long now) {
        try {
            if (lastFrameTime == 0) { // Skip First Frame
                lastFrameTime = now;
                return;
            }
            double elapsedSeconds = Math.min(now - lastFrameTime, 100000000) / 1_000_000_000.0; // limit 1 frame to be under 200 milliseconds
            lastFrameTime = System.nanoTime();
            update(elapsedSeconds);
            updateCOM();

            if (display.autoSpin.get())
                display.yRotate.setAngle((display.yRotate.getAngle() + elapsedSeconds * 3) % 360);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    double getOrbitalSpeed(double mass, double r, double a) {
        return Math.sqrt(G * mass * (2 / r - 1 / a));
    }

    private boolean underRocheLimit(Body primary, Body secondary) {
        if (primary == null || secondary == null || primary.getMass() < 0.1 || secondary.getMass() < 0.1)
            return false;
        return primary.getPosition().minus(secondary.getPosition()).magnitude() * Math.pow(10, 3) < primary.getRadius() * Math.cbrt(2 * primary.getMass() / secondary.getMass());
    }

    private void updateCOM() {
        totalMass = 0;
        centerOfMass.set(0, 0, 0);
        bodies.forEach(b -> {
            centerOfMass.incre(b.getPosition().multiply(b.getMass()));
            totalMass += b.getMass();
        });
        centerOfMass.scale(1 / totalMass);
        comX.set(centerOfMass.x);
        comY.set(centerOfMass.y);
        comZ.set(centerOfMass.z);
        if (display.autoFollow) {
            display.pivot.setX(centerOfMass.x);
            display.pivot.setY(centerOfMass.y);
            display.pivot.setZ(centerOfMass.z);
        }
    }
}
