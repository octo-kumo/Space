package space.physics;

import space.util.Vector;

/**
 * Represents a collider that has the shape of a sphere
 */
public interface SphereCollider extends Collidable {
    double getRadius();

    Vector getPosition();

    default boolean isColliding(Collidable other) {
        if (other instanceof SphereCollider)
            return ((SphereCollider) other).getPosition().minus(getPosition()).magnitude() <= getRadius() + ((SphereCollider) other).getRadius();
        return false;
    }

    default double getVolume() {
        return 4 * Math.PI * getRadius() * getRadius() * getRadius() / 3;
    }
}
