package src.vec;

// NOTE: Vector 3D (float) implementation
public class Vec3f {
    public float x, y, z;

    public Vec3f() {
        this(0, 0, 0);
    }

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f(float x, float y) {
        this(x, y, 0);
    }

    public static void swapVectors(Vec3f a, Vec3f b) {
        Vec3f temp = a.copy();
        a.x = b.x;
        a.y = b.y;
        a.z = b.z;

        b.x = temp.x;
        b.y = temp.y;
        b.z = temp.z;
    }

    public Vec3f add(Vec3f v) {
        return new Vec3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vec3f subtract(Vec3f v) {
        return new Vec3f(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public Vec3f multiplyByScalar(float t) {
        return new Vec3f(this.x * t, this.y * t, this.z * t);
    }

    public Vec3f divideByScalar(float t) {
        return new Vec3f(this.x / t, this.y / t, this.z / t);
    }

    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vec3f normalize() {
        float mag = this.magnitude();
        return mag == 0 ? new Vec3f(0, 0, 0) : this.divideByScalar(mag);
    }

    public float dot(Vec3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vec3f cross(Vec3f v) {
        return new Vec3f(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x);
    }

    public Vec3f copy() {
        return new Vec3f(this.x, this.y, this.z);
    }

    public void swapXY() {
        float temp = this.x;
        this.x = this.y;
        this.y = temp;
    }

    public Vec3f project(int width, int height) {
        return new Vec3f(
                (this.x + 1.0f) * ((width - 1) / 2.0f),
                (this.y + 1.0f) * ((height - 1) / 2.0f),
                (this.z + 1.0f) * ((255.0f) / 2.0f));
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", x, y, z);
    }
}
