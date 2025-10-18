package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.vec.Vec3f;

public class Model {
    public List<List<Integer>> facets;
    public List<Vec3f> vertices;
    public int nFaces, nVertices;

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

            this.nFaces = this.facets.size();
            this.nVertices = this.vertices.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}