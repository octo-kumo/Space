package space;

import javafx.geometry.Point3D;

public class Vector {
    double x, y, z;

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(Vector vector) {
        this(vector.x, vector.y, vector.z);
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y, z + v.z);
    }

    public Vector minus(Vector v) {
        return new Vector(x - v.x, y - v.y, z - v.z);
    }

    public Vector incre(Vector v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    public Vector decre(Vector v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    public Vector add(double x, double y, double z) {
        return new Vector(x + this.x, y + this.y, z + this.z);
    }

    public Vector translate(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector multiply(double scale) {
        return new Vector(x * scale, y * scale, z * scale);
    }

    public Vector scale(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public double dst(Vector vector) {
        return minus(vector).magnitude();
    }

    public double dst2(Vector vector) {
        return minus(vector).magnitude2();
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double magnitude2() {
        return x * x + y * y + z * z;
    }

    public boolean equals(Object object) {
        return object instanceof Vector && Double.compare(x, ((Vector) object).x) == 0 && Double.compare(y, ((Vector) object).y) == 0 && Double.compare(z, ((Vector) object).z) == 0;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public Vector lerp(Vector target, double alpha) {
        return new Vector((target.x - x) * alpha + x, (target.y - y) * alpha + y, (target.z - z) * alpha + z);
    }

    public Vector set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Point3D toPoint3D() {
        return new Point3D(x, y, z);
    }

    public Vector nor() {
        double magnitude = magnitude();
        return new Vector(x / magnitude, y / magnitude, z / magnitude);
    }

    public Vector normalise() {
        double magnitude = magnitude();
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;
        return this;
    }

    public Vector neg() {
        return new Vector(-x, -y, -z);
    }

    public Vector negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public static Vector getTotal(Vector... vectors) {
        Vector result = new Vector(0, 0, 0);
        for (Vector vector : vectors) result.incre(vector);
        return result;
    }
}
