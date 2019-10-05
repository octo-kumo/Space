package space.physics;

import space.util.Vector;

/**
 * Represents a object with mass
 */
public interface Mass {
    double getMass();

    Vector getVelocity();

    Vector getPosition();

    default Vector getMomentum() {
        return getVelocity().multiply(getMass());
    }

    default double getKE() {
        return 0.5 * getMass() * getVelocity().magnitude2();
    }
}
