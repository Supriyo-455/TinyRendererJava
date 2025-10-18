package src;

import java.util.stream.IntStream;

import src.vec.Vec3f;

// TODO: use only VecNf instead of VecNi := get rid of all integer value passing for coordinates and colors
public class Renderer {
    private Image frameBuffer;
    private Image zBuffer;

    public Renderer(Image frameBuffer, Image zBuffer) {
        this.frameBuffer = frameBuffer;
        this.zBuffer = zBuffer;
    }

    public void fillColor(int color) {
        for (int row = 0; row < this.frameBuffer.height; row++) {
            for (int col = 0; col < this.frameBuffer.width; col++) {
                this.frameBuffer.setPixelColor(row, col, color);
                this.zBuffer.setPixelColor(row, col, color);
            }
        }
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

    public void drawTriangle(Vec3f a, Vec3f b, Vec3f c, int color) {
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

        int ax = (int) a.x;
        int bx = (int) b.x;
        int cx = (int) c.x;

        int ay = (int) a.y;
        int by = (int) b.y;
        int cy = (int) c.y;

        int az = (int) a.z;
        int bz = (int) b.z;
        int cz = (int) c.z;

        int bbminx = (int) Math.min(Math.min(ax, bx), cx);
        int bbminy = (int) Math.min(Math.min(ay, by), cy);
        int bbmaxx = (int) Math.max(Math.max(ax, bx), cx);
        int bbmaxy = (int) Math.max(Math.max(ay, by), cy);

        float totalArea = this.areaOfTriangle(a, b, c);
        // NOTE: backface culling + discarding triangles that cover less than a pixel
        if (totalArea < 1)
            return;

        // NOTE: Parallelize outer loop over x
        IntStream.rangeClosed(bbminx, bbmaxx).parallel().forEach(x -> {
            for (int y = bbminy; y <= bbmaxy; y++) {
                float alpha = this.areaOfTriangle(new Vec3f(x, y, 1), b, c) / totalArea;
                float beta = this.areaOfTriangle(new Vec3f(x, y, 1), c, a) / totalArea;
                float gamma = this.areaOfTriangle(new Vec3f(x, y, 1), a, b) / totalArea;

                // NOTE: negative barycentric coordinate => the pixel is outside the triangle
                if (alpha < 0 || beta < 0 || gamma < 0)
                    continue;

                // NOTE: Compute the depth of a pixel
                int z = Color.interpolateThreeRGBvalues(az, bz, cz, alpha, beta, gamma);
                if (z <= this.zBuffer.getPixelColor(x, y))
                    continue;

                this.zBuffer.setPixelColor(x, y, z);
                this.frameBuffer.setPixelColor(x, y, color);
            }
        });
    }
}