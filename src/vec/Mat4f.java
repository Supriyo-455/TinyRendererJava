package src.vec;

public class Mat4f {
    public float[][] m = new float[4][4];

    public Mat4f() {
    }

    public static Mat4f identity() {
        Mat4f I = new Mat4f();
        for (int i = 0; i < 4; i++)
            I.m[i][i] = 1;
        return I;
    }

    public Vec4f multiply(Vec4f v) {
        return new Vec4f(
                m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3] * v.w,
                m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3] * v.w,
                m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3] * v.w,
                m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3] * v.w);
    }

    public Mat4f multiply(Mat4f other) {
        Mat4f result = new Mat4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result.m[i][j] += m[i][k] * other.m[k][j];
                }
            }
        }
        return result;
    }

    public static Mat4f translation(float x, float y, float z) {
        Mat4f t = identity();
        t.m[0][3] = x;
        t.m[1][3] = y;
        t.m[2][3] = z;
        return t;
    }

    public static Mat4f scale(float sx, float sy, float sz) {
        Mat4f s = identity();
        s.m[0][0] = sx;
        s.m[1][1] = sy;
        s.m[2][2] = sz;
        return s;
    }

    public static Mat4f rotationX(float angleRad) {
        Mat4f r = identity();
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);
        r.m[1][1] = c;
        r.m[1][2] = -s;
        r.m[2][1] = s;
        r.m[2][2] = c;
        return r;
    }

    public static Mat4f rotationY(float angleRad) {
        Mat4f r = identity();
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);
        r.m[0][0] = c;
        r.m[0][2] = s;
        r.m[2][0] = -s;
        r.m[2][2] = c;
        return r;
    }

    public static Mat4f rotationZ(float angleRad) {
        Mat4f r = identity();
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);
        r.m[0][0] = c;
        r.m[0][1] = -s;
        r.m[1][0] = s;
        r.m[1][1] = c;
        return r;
    }

    public Mat4f transpose() {
        Mat4f result = new Mat4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[j][i] = this.m[i][j];
            }
        }
        return result;
    }

    public float determinant() {
        return m[0][0] * det3x3(m[1][1], m[1][2], m[1][3], m[2][1], m[2][2], m[2][3], m[3][1], m[3][2], m[3][3])
                - m[0][1] * det3x3(m[1][0], m[1][2], m[1][3], m[2][0], m[2][2], m[2][3], m[3][0], m[3][2], m[3][3])
                + m[0][2] * det3x3(m[1][0], m[1][1], m[1][3], m[2][0], m[2][1], m[2][3], m[3][0], m[3][1], m[3][3])
                - m[0][3] * det3x3(m[1][0], m[1][1], m[1][2], m[2][0], m[2][1], m[2][2], m[3][0], m[3][1], m[3][2]);
    }

    private float det3x3(float a, float b, float c, float d, float e, float f, float g, float h, float i) {
        return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    }

    public Mat4f inverse() {
        float det = determinant();
        if (Math.abs(det) < 1e-6)
            throw new RuntimeException("Matrix is singular and cannot be inverted.");

        Mat4f adjugate = new Mat4f();
        // Simplified cofactor calculation for brevity
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Get minor, apply sign ((-1)^(i+j)), and transpose simultaneously
                adjugate.m[j][i] = cofactor(i, j) / det;
            }
        }
        return adjugate;
    }

    private float cofactor(int row, int col) {
        float[][] minor = new float[3][3];
        int mRow = 0;
        for (int i = 0; i < 4; i++) {
            if (i == row)
                continue;
            int mCol = 0;
            for (int j = 0; j < 4; j++) {
                if (j == col)
                    continue;
                minor[mRow][mCol] = m[i][j];
                mCol++;
            }
            mRow++;
        }
        float det = det3x3(minor[0][0], minor[0][1], minor[0][2],
                minor[1][0], minor[1][1], minor[1][2],
                minor[2][0], minor[2][1], minor[2][2]);
        return ((row + col) % 2 == 0) ? det : -det;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append("[ ");
            for (int j = 0; j < 4; j++)
                sb.append(String.format("%8.3f ", m[i][j]));
            sb.append("]\n");
        }
        return sb.toString();
    }
}
