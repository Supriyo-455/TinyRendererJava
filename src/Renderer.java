package src;

import java.util.Arrays;
import java.util.stream.IntStream;

import src.vec.Mat3f;
import src.vec.Mat4f;
import src.vec.Vec2f;
import src.vec.Vec3f;
import src.vec.Vec4f;

public class Renderer {
    private Image frameBuffer;
    private float[] zBuffer;

    public Renderer(Image frameBuffer) {
        this.frameBuffer = frameBuffer;
        this.zBuffer = new float[this.frameBuffer.height * this.frameBuffer.width];
    }

    public Renderer(Image frameBuffer, float[] zBuffer) {
        this.frameBuffer = frameBuffer;
        this.zBuffer = zBuffer;
    }

    public void clearFrame() {
        Arrays.fill(this.zBuffer, Float.NEGATIVE_INFINITY);
        this.frameBuffer.fillColor(Color.BLACK);
    }

    public static Mat4f perspective(float f) {
        Mat4f p = Mat4f.identity();

        p.m[3][2] = -(1 / f);

        return p;
    }

    public static Mat4f viewport(int upperLeftX, int upperLeftY, int width, int height) {
        Mat4f v = Mat4f.identity();
        float w = (float) width / 2;
        float h = (float) height / 2;

        v.m[0][0] = w;
        v.m[1][1] = h;
        v.m[0][3] = upperLeftX + w;
        v.m[1][3] = upperLeftY + h;

        return v;
    }

    public static Mat4f lookAt(Vec3f eye, Vec3f center, Vec3f up) {
        Mat4f M = Mat4f.identity();
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

        Mat4f V = Mat4f.identity();

        V.m[0][3] = -center.x;
        V.m[1][3] = -center.y;
        V.m[2][3] = -center.z;

        return M.multiply(V);
    }

    // TODO: Reseach more on this topic.
    public void drawLine(Vec3f a, Vec3f b, int color) {
        int ax = (int) a.x;
        int bx = (int) b.x;

        int ay = (int) a.y;
        int by = (int) b.y;

        boolean steep = Math.abs(ax - bx) < Math.abs(ay - by);

        if (steep) {
            a.swapXY();
            b.swapXY();
        }

        if (ax > bx) {
            Vec3f.swapVectors(a, b);
        }

        int y = ay;
        int ierror = 0;

        for (int x = ax; x < bx; x++) {
            if (steep) {
                this.frameBuffer.setPixelColor(y, x, color);
            } else {
                this.frameBuffer.setPixelColor(x, y, color);
            }

            ierror += 2 * Math.abs(by - ay);
            if (ierror > bx - ax) {
                y += (by > ay) ? 1 : -1;
                ierror -= 2 * (bx - ax);
            }
        }
    }

    float areaOfTriangle(Vec3f a, Vec3f b, Vec3f c) {
        return 0.5f * ((b.y - a.y) * (b.x + a.x) + (c.y - b.y) * (c.x + b.x) + (a.y - c.y) * (a.x + c.x));
    }

    public void rasterize(Vec4f[] clipCoord, Mat4f viewPort, Shader shader) {

        Vec4f[] ndc = new Vec4f[3];
        for (int i = 0; i < 3; i++) {
            ndc[i] = clipCoord[i].divideByScalar(clipCoord[i].w);
        }

        Vec2f[] screenCoords = new Vec2f[3];
        for (int i = 0; i < 3; i++) {
            screenCoords[i] = viewPort.multiply(ndc[i]).swizzle(0, 1);
        }

        Mat3f ABC = new Mat3f();
        for (int i = 0; i < 3; i++) {
            ABC.m[i][0] = screenCoords[i].x;
            ABC.m[i][1] = screenCoords[i].y;
            ABC.m[i][2] = 1;
        }

        float totalArea = ABC.determinant();
        // NOTE: backface culling + discarding triangles that cover less than a pixel
        if (totalArea < 1)
            return;

        float bbminx = Math.min(screenCoords[0].x, Math.min(screenCoords[1].x, screenCoords[2].x));
        float bbmaxx = Math.max(screenCoords[0].x, Math.max(screenCoords[1].x, screenCoords[2].x));

        float bbminy = Math.min(screenCoords[0].y, Math.min(screenCoords[1].y, screenCoords[2].y));
        float bbmaxy = Math.max(screenCoords[0].y, Math.max(screenCoords[1].y, screenCoords[2].y));

        int width = this.frameBuffer.width;
        int height = this.frameBuffer.height;

        int xmin = Math.max((int) bbminx, 0);
        int xmax = Math.min((int) bbmaxx, width - 1);
        int ymin = Math.max((int) bbminy, 0);
        int ymax = Math.min((int) bbmaxy, height - 1);

        Mat3f invT = ABC.transpose().inverse();

        // NOTE: Parallelize outer loop over x
        IntStream.rangeClosed(xmin, xmax).parallel().forEach(x -> {
            for (int y = ymin; y <= ymax; y++) {

                Vec3f bc = invT.multiply(new Vec3f(x, y, 1));

                // NOTE: negative barycentric coordinate => the pixel is outside the triangle
                if (bc.x < 0 || bc.y < 0 || bc.z < 0)
                    continue;

                float z = bc.x * ndc[0].z +
                        bc.y * ndc[1].z +
                        bc.z * ndc[2].z;

                int index = x + y * width;

                // NOTE: Compute depth of a pixel
                if (z <= this.zBuffer[index])
                    continue;

                ShaderReturnType shaderReturn = shader.fragment(bc);
                if (shaderReturn.discard)
                    continue;

                this.zBuffer[index] = z;
                this.frameBuffer.setPixelColor(x, y, shaderReturn.colorRGB);
            }
        });
    }
}