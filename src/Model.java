package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.vec.Vec3f;

public class Model {
    List<List<Integer>> facets;
    List<Vec3f> vertices;

    public Model(String filename) {
        this.facets = new ArrayList<>();
        this.vertices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] words = line.split(" ");

                if (words[0].equals("v") && words.length == 4) {
                    Vec3f vertex = new Vec3f(
                            Float.parseFloat(words[1]),
                            Float.parseFloat(words[2]),
                            Float.parseFloat(words[3]));
                    this.vertices.add(vertex);
                }

                // TODO: Need to make use of other face values
                if (words[0].equals("f") && words.length == 4) {
                    ArrayList<Integer> trianglePoints = new ArrayList<>(3);
                    for (int i = 1; i <= 3; i++) {
                        String[] facesSplit = words[i].split("/");
                        trianglePoints.add(Integer.parseInt(facesSplit[0]));
                    }
                    this.facets.add(trianglePoints);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vec3f getVertex(int vertexIndex) {
        // Vec3f vecf =
        // float x = (vecf.x + 1.0f) * ((width - 1) / 2);
        // float y = (vecf.y + 1.0f) * ((height - 1) / 2);
        // float z = (vecf.z + 1.0f) * (255.0f / 2);

        // Vec3i veci = new Vec3i((int) x, (int) y, (int) z);
        // return veci;
        return this.vertices.get(vertexIndex - 1);
    }
}