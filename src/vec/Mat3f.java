package src.vec;

public class Mat3f {
    public float[][] m = new float[3][3];

    public Mat3f() {
    }

    public Mat3f(float[][] values) {
        for (int i = 0; i < 3; i++)
            System.arraycopy(values[i], 0, m[i], 0, 3);
    }

    public static Mat3f identity() {
        Mat3f I = new Mat3f();
        for (int i = 0; i < 3; i++)
            I.m[i][i] = 1;
        return I;
    }

    public static Mat3f rotateX(float angleRad) {
        Mat3f r = identity();
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);

        r.m[1][1] = c;
        r.m[1][2] = -s;
        r.m[2][1] = s;
        r.m[2][2] = c;

        return r;
    }

    public static Mat3f rotateY(float angleRad) {
        Mat3f r = identity();
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);

        r.m[0][0] = c;
        r.m[0][2] = s;
        r.m[2][0] = -s;
        r.m[2][2] = c;

        return r;
    }

    public static Mat3f rotateZ(float angleRad) {
        Mat3f r = identity();
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);

        r.m[0][0] = c;
        r.m[0][1] = -s;
        r.m[1][0] = s;
        r.m[1][1] = c;

        return r;
    }

    public Vec3f multiply(Vec3f v) {
        return new Vec3f(
                m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z,
                m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z,
                m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z);
    }

    public Mat3f multiply(Mat3f other) {
        Mat3f result = new Mat3f();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result.m[i][j] += m[i][k] * other.m[k][j];
                }
            }
        }
        return result;
    }

    public float determinant() {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1])
                - m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0])
                + m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }

    public Mat3f inverse() {
        float det = determinant();
        if (Math.abs(det) < 1e-8f)
            throw new RuntimeException("Mat3f not invertible");

        Mat3f inv = new Mat3f();
        inv.m[0][0] = (m[1][1] * m[2][2] - m[1][2] * m[2][1]) / det;
        inv.m[0][1] = -(m[0][1] * m[2][2] - m[0][2] * m[2][1]) / det;
        inv.m[0][2] = (m[0][1] * m[1][2] - m[0][2] * m[1][1]) / det;

        inv.m[1][0] = -(m[1][0] * m[2][2] - m[1][2] * m[2][0]) / det;
        inv.m[1][1] = (m[0][0] * m[2][2] - m[0][2] * m[2][0]) / det;
        inv.m[1][2] = -(m[0][0] * m[1][2] - m[0][2] * m[1][0]) / det;

        inv.m[2][0] = (m[1][0] * m[2][1] - m[1][1] * m[2][0]) / det;
        inv.m[2][1] = -(m[0][0] * m[2][1] - m[0][1] * m[2][0]) / det;
        inv.m[2][2] = (m[0][0] * m[1][1] - m[0][1] * m[1][0]) / det;

        return inv;
    }

    @Override
    public String toString() {
        return String.format(
                "[[%f, %f, %f], [%f, %f, %f], [%f, %f, %f]]",
                m[0][0], m[0][1], m[0][2],
                m[1][0], m[1][1], m[1][2],
                m[2][0], m[2][1], m[2][2]);
    }
}
