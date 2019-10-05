package space.physics;

/**
 * Represents a object that can be collided with
 */
public interface Collidable {
    boolean isColliding(Collidable other);
}
