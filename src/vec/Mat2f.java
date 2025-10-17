package src.vec;

public class Mat2f {
    public float[][] m = new float[2][2];

    public Mat2f() {
    }

    public Mat2f(float a, float b, float c, float d) {
        m[0][0] = a;
        m[0][1] = b;
        m[1][0] = c;
        m[1][1] = d;
    }

    public static Mat2f identity() {
        return new Mat2f(1, 0, 0, 1);
    }

    public Vec2f multiply(Vec2f v) {
        return new Vec2f(
                m[0][0] * v.x + m[0][1] * v.y,
                m[1][0] * v.x + m[1][1] * v.y);
    }

    public Mat2f multiply(Mat2f other) {
        Mat2f result = new Mat2f();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result.m[i][j] = m[i][0] * other.m[0][j] + m[i][1] * other.m[1][j];
            }
        }
        return result;
    }

    public float determinant() {
        return m[0][0] * m[1][1] - m[0][1] * m[1][0];
    }

    public Mat2f inverse() {
        float det = determinant();
        if (Math.abs(det) < 1e-8f)
            throw new RuntimeException("Mat2f not invertible");

        Mat2f inv = new Mat2f();
        inv.m[0][0] = m[1][1] / det;
        inv.m[0][1] = -m[0][1] / det;
        inv.m[1][0] = -m[1][0] / det;
        inv.m[1][1] = m[0][0] / det;
        return inv;
    }

    @Override
    public String toString() {
        return String.format("[[%f, %f], [%f, %f]]", m[0][0], m[0][1], m[1][0], m[1][1]);
    }
}
