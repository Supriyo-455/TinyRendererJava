package src.vec;

// NOTE: 4D vector implementation
public class Vec4f {
    public float x, y, z, w;

    public Vec4f() {
    }

    public Vec4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    private float get(int i) {
        return switch (i) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            case 3 -> w;
            default -> throw new IllegalArgumentException("Index must be 0..3");
        };
    }

    public Vec2f swizzle(int a, int b) {
        return new Vec2f(get(a), get(b));
    }

    public Vec3f swizzle(int a, int b, int c) {
        return new Vec3f(get(a), get(b), get(c));
    }

    public Vec4f swizzle(int a, int b, int c, int d) {
        return new Vec4f(get(a), get(b), get(c), get(d));
    }

    public Vec4f add(Vec4f v) {
        Vec4f result = new Vec4f();
        result.x = this.x + v.x;
        result.y = this.y + v.y;
        result.z = this.z + v.z;
        result.w = this.w + v.w;
        return result;
    }

    public Vec4f subtract(Vec4f v) {
        Vec4f result = new Vec4f();
        result.x = this.x - v.x;
        result.y = this.y - v.y;
        result.z = this.z - v.z;
        result.w = this.w - v.w;
        return result;
    }

    public Vec4f multiplyByScalar(float t) {
        Vec4f result = new Vec4f();
        result.x = this.x * t;
        result.y = this.y * t;
        result.z = this.z * t;
        result.w = this.w * t;
        return result;
    }

    public Vec4f divideByScalar(float t) {
        Vec4f result = new Vec4f();
        result.x = this.x / t;
        result.y = this.y / t;
        result.z = this.z / t;
        result.w = this.w / t;
        return result;
    }

    public float dot(Vec4f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z + this.w * v.w;
    }

    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public Vec4f normalize() {
        float mag = this.magnitude();
        if (mag == 0)
            return new Vec4f(0, 0, 0, 0);
        return this.divideByScalar(mag);
    }

    public Vec4f copy() {
        return new Vec4f(this.x, this.y, this.z, this.w);
    }

    public void swapXY() {
        float temp = this.x;
        this.x = this.y;
        this.y = temp;
    }

    public static void swapVectors(Vec4f a, Vec4f b) {
        Vec4f temp = a.copy();
        a.x = b.x;
        a.y = b.y;
        a.z = b.z;
        a.w = b.w;

        b.x = temp.x;
        b.y = temp.y;
        b.z = temp.z;
        b.w = temp.w;
    }

    @Override
    public String toString() {
        return String.format("Vec4f(%.3f, %.3f, %.3f, %.3f)", x, y, z, w);
    }
}
