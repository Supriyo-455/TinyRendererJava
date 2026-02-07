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

    public static Mat4f perspective(float f) {
        Mat4f p = identity();

        p.m[3][2] = -(1 / f);

        return p;
    }

    public static Mat4f viewport(int upperLeftX, int upperLeftY, int width, int height) {
        Mat4f v = identity();
        float w = (float) width / 2;
        float h = (float) height / 2;

        v.m[0][0] = w;
        v.m[1][1] = h;
        v.m[0][3] = upperLeftX + w;
        v.m[1][3] = upperLeftY + h;

        return v;
    }

    public static Mat4f lookAt(Vec3f eye, Vec3f center, Vec3f up) {
        Mat4f M = identity();
        Vec3f n = eye.subtract(center).normalize();
        Vec3f l = up.cross(n).normalize();
        Vec3f m = n.cross(l).normalize();

        M.m[0][0] = l.x;
        M.m[0][1] = l.y;
        M.m[0][2] = l.z;
        M.m[0][3] = 0;

        M.m[1][0] = m.x;
        M.m[1][1] = m.y;
        M.m[1][2] = m.z;
        M.m[1][3] = 0;

        M.m[2][0] = n.x;
        M.m[2][1] = n.y;
        M.m[2][2] = n.z;
        M.m[2][3] = 0;

        M.m[3][0] = 0;
        M.m[3][1] = 0;
        M.m[3][2] = 0;
        M.m[3][3] = 1;

        Mat4f V = identity();

        V.m[0][3] = -center.x;
        V.m[1][3] = -center.y;
        V.m[2][3] = -center.z;

        return M.multiply(V);
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
