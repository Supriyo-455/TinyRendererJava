package src;

import java.util.Arrays;

import src.vec.Mat3f;
import src.vec.Mat4f;
import src.vec.Vec3f;
import src.vec.Vec4f;

public class Main {
        public static void main(String[] args) {
                long startTime = System.nanoTime();

                int width = 800;
                int height = 800;

                Image frameBuffer = new Image(width, height);
                frameBuffer.fillColor(Color.BLACK);

                float[] zBuffer = new float[width * height];

                Renderer renderer = new Renderer(frameBuffer, zBuffer);

                // Model model = new Model("models/diablo3_pose.obj");
                Model model = new Model("models/african_head.obj");

                Vec3f eye = new Vec3f(-1, 0, 5);
                Vec3f center = new Vec3f(0, 0, 0);
                Vec3f up = new Vec3f(0, 1, 0);

                Mat4f MV = Mat4f.lookAt(eye, center, up);
                Mat4f P = Mat4f.perspective(eye.subtract(center).magnitude());
                Mat4f viewPort = Mat4f.viewport(width / 16, height / 16, width * 7 / 8, height * 7 / 8);

                for (int frame = 0; frame < 144; ++frame) {
                        Arrays.fill(zBuffer, Float.NEGATIVE_INFINITY);

                        float angle = 0.1f * frame;
                        Mat4f rotY = Mat4f.rotationY(angle);
                        Mat4f rotX = Mat4f.rotationX(angle);

                        StringBuilder fileName = new StringBuilder("rendered/output-");
                        fileName.append(frame);
                        fileName.append(".png");

                        for (int i = 0; i < model.nFaces; i++) {

                                Vec4f[] clipCoords = new Vec4f[3];
                                for (int j = 0; j < 3; j++) {

                                        int vertexIndex = model.facets.get(i).get(j);
                                        Vec3f v = model.vertices.get(vertexIndex - 1);

                                        clipCoords[j] = P
                                                        .multiply(MV)
                                                        .multiply(rotY)
                                                        .multiply(rotX)
                                                        .multiply(new Vec4f(v.x, v.y, v.z, 1));
                                }

                                // TODO: Might want to view wireframe rendering in future
                                renderer.rasterize(
                                                clipCoords,
                                                viewPort,
                                                Color.generateRandomColorRGBValue());
                        }

                        frameBuffer.flip(false, true);
                        frameBuffer.save(fileName.toString());
                        frameBuffer.fillColor(Color.BLACK);
                }

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                System.out.println("Execution time: " + ((float) (duration / 1_000_000) / 1000) + " secs");
        }
}