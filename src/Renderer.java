package src;

import java.util.Arrays;
import java.util.stream.IntStream;

import src.vec.Mat3f;
import src.vec.Mat4f;
import src.vec.Vec2f;
import src.vec.Vec3f;
import src.vec.Vec4f;

// TODO: use only VecNf instead of VecNi := get rid of all integer value passing for coordinates and colors
public class Renderer {
    private Image frameBuffer;
    private float[] zBuffer;

    public Renderer(Image frameBuffer, float[] zBuffer) {
        this.frameBuffer = frameBuffer;
        this.zBuffer = zBuffer;
        Arrays.fill(zBuffer, Integer.MIN_VALUE);
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

    public void rasterize(Vec4f[] clipCoord, Mat4f viewPort, int color) {
        // // NOTE: Sort the vertices in ascending y order
        // if (a.y > b.y) {
        // Vector2D.swapVectors(a, b);
        // }
        // if (a.y > c.y) {
        // Vector2D.swapVectors(a, c);
        // }
        // if (b.y > c.y) {
        // Vector2D.swapVectors(b, c);
        // }

        // int totalHeight = c.y - a.y;

        // // NOTE: Check the triangle is not de-generate
        // if (a.y != b.y) {
        // int segment_height = b.y - a.y;
        // for (int y = a.y; y <= b.y; y++) {
        // int x1 = a.x + ((c.x - a.x) * (y - a.y)) / totalHeight;
        // int x2 = a.x + ((b.x - a.x) * (y - a.y)) / segment_height;

        // int maxX = x1 > x2 ? x1 : x2;
        // int minX = x1 < x2 ? x1 : x2;

        // for (int x = minX; x <= maxX; x++) {
        // this.frameBuffer.setPixelColor(x, y, color);
        // }
        // }
        // }

        // // NOTE: Check the triangle is not de-generate
        // if (c.y != b.y) {
        // int segment_height = c.y - b.y;
        // for (int y = b.y; y <= c.y; y++) {
        // int x1 = a.x + ((c.x - a.x) * (y - a.y)) / totalHeight;
        // int x2 = b.x + ((c.x - b.x) * (y - b.y)) / segment_height;

        // int maxX = x1 > x2 ? x1 : x2;
        // int minX = x1 < x2 ? x1 : x2;

        // for (int x = minX; x <= maxX; x++) {
        // this.frameBuffer.setPixelColor(x, y, color);
        // }
        // }
        // }

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

                this.zBuffer[index] = z;
                this.frameBuffer.setPixelColor(x, y, color);
            }
        });
    }
}