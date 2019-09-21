package space;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public class Engine extends AnimationTimer implements EventHandler<KeyEvent> {
    ArrayList<Body> bodies;
    private Queue<Body> addQueue;
    public static double G = 6.67 * 0.001;
    private Board display;
    private long lastFrame = 0;

    SimpleDoubleProperty speed;

    private static final double SUN_MASS = 19890000;
    private static final double SUN_RADIUS = 7;
    private static final double[] PLANETARY_MASS = {0.330, 4.87, 5.97, 0.642, 1898, 568, 86.8, 102, 0.0146};
    private static final double[] PLANETARY_SIZE = {4879, 12104, 12756, 6792, 71492, 60268, 25559, 24764, 2370};
    private static final double[] PLANETARY_ROTATION = {140.76, -583.25, 2.39, 2.46, 0.99, 1.07, -1.72, 1.61, -15.33};
    private static final double[] PLANETARY_DISTANCE = {77.9, 118.2, 149.6, 227.9, 778.6, 1433.5, 2872.5, 4495.1, 5906.4};
    private static final double[] PLANETARY_PERIHELION = {59.0, 107.5, 147.1, 206.6, 740.5, 1352.6, 2741.3, 4444.5, 4436.8};
    private static final double[] PLANETARY_INCLINATION = {7.0, 3.4, 0, 1.9, 1.3, 2.5, 0.8, 1.8, 17.2};
    private static final double[] PLANETARY_TILT = {0.034, 177.4, 23.4, 25.2, 3.1, 26.7, 97.8, 28.3, 122.5};
    private static final String[] PLANETARY_NAMES = {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"};

    public Engine(Board display) {
        this.display = display;
        bodies = new ArrayList<>();
        addQueue = new LinkedList<>();
        speed = new SimpleDoubleProperty(1);
        //genSolarSystem();
        genRandom(30);
    }

    public void genSolarSystem() {
        Body sun = addBody(0, 0, 0, 0, 0, 0, SUN_MASS, SUN_RADIUS, 9);
        sun.name = "Sun";
        Body.generateSpin(sun.sphere, 24);
        double scaleDown = 10000;
        for (int a = 0; a < 9; a++) {
            double r = PLANETARY_PERIHELION[a] / 5;
            Body body = addBody(0, r * Math.sin(Math.toRadians(PLANETARY_INCLINATION[a])), r * Math.cos(Math.toRadians(PLANETARY_INCLINATION[a])), getOrbitalSpeed(SUN_MASS + PLANETARY_MASS[a], PLANETARY_PERIHELION[a] / 5, PLANETARY_DISTANCE[a] / 5), 0, 0, PLANETARY_MASS[a], PLANETARY_SIZE[a] / scaleDown, a);
            body.name = PLANETARY_NAMES[a];
            body.setTiltSpin(PLANETARY_TILT[a]);
            Body.generateSpin(body.sphere, PLANETARY_ROTATION[a]);
        }
    }

    public void genRandom(int amount) {
        for (int a = 0; a < amount; a++) {
            Body body = addBody(Math.random() * 200 - 100, Math.random() * 200 - 100, Math.random() * 200 - 100, Math.random() * 1000, Math.random() * 2 + .1, (int) (Math.random() * 10));
            body.setTiltSpin(Math.random() * 90);
            body.name = "Lol";
            Body.generateSpin(body.sphere, Math.random() * 2000 - 1000);
        }
    }


    public Body addBody(double x, double y, double z, double mass, double radius, int type) {
        return addBody(x, y, z, 0, 0, 0, mass, radius, type);
    }

    public Body addBody(double x, double y, double z, double vx, double vy, double vz, double mass, double radius, int type) {
        Body body = new Body(x, y, z, vx, vy, vz, mass, radius, type);
        addQueue.add(body);
        return body;
    }

    public void update(double rawDelta) {
        final double delta = speed.doubleValue() * rawDelta;
        if (bodies.size() > 1)
            for (int i = 0; i < bodies.size() - 1; i++) // Achieve a loop where every body is looped only once with any other body
                for (int j = i + 1; j < bodies.size(); j++) {
                    Body bodyA = bodies.get(i);
                    Body bodyB = bodies.get(j); // Easy access
                    if (!(bodyA.alive && bodyB.alive)) continue;
                    Vector AtoB = bodyB.position.minus(bodyA.position);
                    boolean intersecting = AtoB.magnitude() <= bodyA.radius + bodyB.radius;
                    if (!intersecting) {
                        Vector force = AtoB.nor();
                        double gMagnitude = G * bodyA.mass * bodyB.mass / AtoB.magnitude2();
                        force.scale(gMagnitude);
                        bodyA.velocity.incre(force.multiply(1 * delta / bodyA.mass));
                        bodyB.velocity.incre(force.multiply(-1 * delta / bodyB.mass));
                    } else {
                        Vector centerOfMass = bodyA.position.multiply(bodyA.mass).add(bodyB.position.multiply(bodyB.mass)).scale(1 / (bodyA.mass + bodyB.mass));
                        double totalMass = bodyA.mass + bodyB.mass;
                        double totalVolume = bodyA.getVolume() + bodyB.getVolume();
                        double finalRadius = Math.cbrt(3 * totalVolume / 4 / Math.PI);
                        bodyA.alive = false;
                        bodyB.alive = false;
                        addQueue.add(new Body(centerOfMass.x, centerOfMass.y, centerOfMass.z, totalMass, finalRadius, 1));
                    }
                }
        bodies.forEach(body -> {
            body.update(delta);
            if (!body.alive) {
                display.getChildren().remove(body.sphere);
                display.getChildren().removeAll(body.trail);
                if (body.spinTimeLine != null) body.spinTimeLine.stop();
            }
        });
        bodies.removeIf(b -> !b.alive);
        while (addQueue.size() > 0) {
            Body body = addQueue.remove();
            display.getChildren().add(body.sphere);
            display.getChildren().addAll(body.trail);
            bodies.add(body);
        }
    }

    @Override
    public void handle(long now) {
        if (lastFrame == 0) {
            lastFrame = now;
            return;
        }
        long deltaNano = now - lastFrame;
        lastFrame = System.nanoTime();
        double elapsedSeconds = deltaNano / 1_000_000_000.0;
        update(elapsedSeconds);
        Space.getInstance().controls.updateInfo();
        if (display.autoFollow) {
            Vector total = new Vector();
            AtomicReference<Double> totalMass = new AtomicReference<>((double) 0);
            bodies.forEach(b -> {
                total.incre(b.position.multiply(b.mass));
                totalMass.updateAndGet(v -> v + b.mass);
            });
            total.scale(1 / totalMass.get());
            display.pivot.setX(total.x);
            display.pivot.setY(total.y);
            display.pivot.setZ(total.z);
        }
    }

    @Override
    public void handle(KeyEvent event) {
        KeyCode key = event.getCode();
        System.out.println("Lol");
    }

    public double getOrbitalSpeed(double mass, double r, double a) {
        return Math.sqrt(G * mass * (2 / r - 1 / a));
    }
}
