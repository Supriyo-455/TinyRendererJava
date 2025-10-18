package src;

import java.util.ArrayList;

import src.vec.Mat3f;
import src.vec.Vec3f;

public class Main {
        public static void main(String[] args) {
                long startTime = System.nanoTime();

                int width = 1000;
                int height = 1000;

                Image frameBuffer = new Image(width, height);
                Image zBuffer = new Image(width, height);
                Renderer renderer = new Renderer(frameBuffer, zBuffer);

                int red = new Color(255, 0, 0).RGB();
                int black = new Color(0, 0, 0).RGB();
                int green = new Color(0, 255, 0).RGB();
                int blue = new Color(0, 0, 255).RGB();
                int white = new Color(255, 255, 255).RGB();
                int yellow = new Color(255, 255, 0).RGB();

                renderer.fillColor(black);

                // TODO: Learn about viewport transform in detail
                // link - https://www.songho.ca/opengl/gl_viewport.html
                // Model model = new Model("models/diablo3_pose.obj");
                Model model = new Model("models/african_head.obj");
                Mat3f rotateXMat3f = Mat3f.rotateY((float) Math.PI / 4);

                for (int i = 0; i < model.nFaces; i++) {

                        ArrayList<Vec3f> screenTriangleCoordinates = new ArrayList<>();
                        for (int j = 0; j < 3; j++) {

                                int vertexIndex = model.facets.get(i).get(j);
                                Vec3f vertex = model.vertices.get(vertexIndex - 1);
                                Vec3f screenCoord = rotateXMat3f
                                                .multiply(vertex)
                                                .perspective(10)
                                                .project(width, height);

                                // System.out.println(screenCoord);
                                screenTriangleCoordinates.add(screenCoord);
                        }

                        // TODO: need projection matrix to project the coordinate properly on the screen

                        // TODO: Might want to view wireframe rendering in future

                        renderer.drawTriangle(
                                        screenTriangleCoordinates.get(0),
                                        screenTriangleCoordinates.get(1),
                                        screenTriangleCoordinates.get(2), Color.generateRandomColorRGBValue());
                }

                frameBuffer.flip(false, true);
                frameBuffer.save("rendered/output.png");

                zBuffer.flip(false, true);
                zBuffer.save("rendered/zBuffer.png");

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                System.out.println("Execution time: " + (duration / 1_000_000) + "ms");
        }
}