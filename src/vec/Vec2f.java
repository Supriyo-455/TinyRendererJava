package src.vec;

// NOTE: 2D vector implementation
public class Vec2f {
    public float x, y;

    public Vec2f() {
    }

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f add(Vec2f v) {
        Vec2f result = new Vec2f();
        result.x = this.x + v.x;
        result.y = this.y + v.y;
        return result;
    }

    public Vec2f subtract(Vec2f v) {
        Vec2f result = new Vec2f();
        result.x = this.x - v.x;
        result.y = this.y - v.y;
        return result;
    }

    public Vec2f multiplyByScalar(float t) {
        Vec2f result = new Vec2f();
        result.x = this.x * t;
        result.y = this.y * t;
        return result;
    }

    public Vec2f divideByScalar(float t) {
        Vec2f result = new Vec2f();
        result.x = this.x / t;
        result.y = this.y / t;
        return result;
    }

    public float dot(Vec2f v) {
        return this.x * v.x + this.y * v.y;
    }

    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vec2f normalize() {
        float mag = this.magnitude();
        if (mag == 0)
            return new Vec2f(0, 0);
        return this.divideByScalar(mag);
    }

    public Vec2f copy() {
        return new Vec2f(this.x, this.y);
    }

    public void swapXY() {
        float temp = this.x;
        this.x = this.y;
        this.y = temp;
    }

    public static void swapVectors(Vec2f a, Vec2f b) {
        Vec2f temp = a.copy();
        a.x = b.x;
        a.y = b.y;

        b.x = temp.x;
        b.y = temp.y;
    }

    @Override
    public String toString() {
        return String.format("Vec2f(%.3f, %.3f)", x, y);
    }
}