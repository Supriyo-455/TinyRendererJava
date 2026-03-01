package src;

import java.util.ArrayList;
import java.util.List;

import src.vec.Mat4f;
import src.vec.Vec3f;
import src.vec.Vec4f;

public class Main {
        public static void main(String[] args) {

                try {
                        List<Model> models = new ArrayList<>();
                        models.add(new Model("models/african_head.obj"));
                        // models.add(new Model("models/african_head_eye_outer.obj"));
                        models.add(new Model("models/african_head_eye_inner.obj"));
                        models.add(new Model("models/floor.obj"));

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

                        Shader shader = new NormalMappingShader(models.get(0), MV, P, eye);

                        for (int frame = 0; frame < 100; ++frame) {

                                String fileName = String.format("rendered/output-%03d.png", frame);

                                float angle = 0.06f * frame;
                                Mat4f T = Mat4f.rotationY(angle);

                                renderer.clearFrame();

                                for (Model model : models) {
                                        shader.setModel(model);
                                        for (int i = 0; i < model.nFaces(); i++) {

                                                Vec4f[] clipCoords = new Vec4f[3];
                                                for (int j = 0; j < 3; j++) {
                                                        clipCoords[j] = shader.vertex(i, j, T);
                                                }

                                                // TODO: Might want to view wireframe rendering in future
                                                renderer.rasterize(
                                                                clipCoords,
                                                                viewPort,
                                                                shader);
                                        }
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