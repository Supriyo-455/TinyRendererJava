package src;

import java.util.Arrays;

import src.vec.Mat3f;
import src.vec.Mat4f;
import src.vec.Vec3f;
import src.vec.Vec4f;

public class Main {
        public static void main(String[] args) {

                try {
                        Model model = new Model("models/african_head.obj");
                        // Model model = new Model("models/diablo3_pose.obj");
                        // Model model = new Model("models/tsunade.obj");

                        long startTime = System.nanoTime();

                        int width = 1000;
                        int height = 1000;

                        Image frameBuffer = new Image(width, height);
                        frameBuffer.fillColor(Color.BLACK);

                        Renderer renderer = new Renderer(frameBuffer);

                        Vec3f eye = new Vec3f(0, 0, 3);
                        Vec3f center = new Vec3f(0, 0, 0);
                        Vec3f up = new Vec3f(0, 1, 0);

                        Mat4f MV = Renderer.lookAt(eye, center, up);
                        Mat4f P = Renderer.perspective(eye.subtract(center).magnitude());
                        Mat4f viewPort = Renderer.viewport(width / 16, height / 16, width * 7 / 8, height * 7 / 8);

                        Shader shader = new NormalMappingShader(model, MV, P, eye);

                        for (int frame = 0; frame < 500; ++frame) {
                                renderer.clearFrame();

                                float angle = 0.06f * frame;
                                Mat4f rotY = Mat4f.rotationY(angle);
                                Mat4f rotX = Mat4f.rotationX(angle);
                                Mat4f rotZ = Mat4f.rotationZ(angle);

                                String fileName = String.format("rendered/output-%03d.png", frame);

                                for (int i = 0; i < model.nFaces(); i++) {

                                        Vec4f[] clipCoords = new Vec4f[3];
                                        for (int j = 0; j < 3; j++) {
                                                Mat4f T = rotY;
                                                // T = T.multiply(Mat4f.scale(2.5f, 2.5f, 2.5f));
                                                Vec4f v = shader.vertex(i, j, T);
                                                clipCoords[j] = v;
                                        }

                                        // TODO: Might want to view wireframe rendering in future
                                        renderer.rasterize(
                                                        clipCoords,
                                                        viewPort,
                                                        shader);
                                }

                                // TODO: Can we do this on a seperate thread?
                                frameBuffer.flip(false, true);
                                frameBuffer.save(fileName);
                        }

                        long endTime = System.nanoTime();
                        long duration = endTime - startTime;

                        System.out.println("Execution time: " + ((float) (duration / 1_000_000) / 1000) + " secs");
                } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                }
        }
}