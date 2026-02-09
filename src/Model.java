package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.vec.Vec3f;

public class Model {
    public List<List<Integer>> facetVertex;
    public List<List<Integer>> facetNormal;
    public List<Vec3f> normals;
    public List<Vec3f> vertices;
    public int nFaces, nVertices, nNormals;

    public Model(String filename) {
        this.facetVertex = new ArrayList<>();
        this.facetNormal = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.normals = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] words = line.split("\\s+");

                if (words[0].equals("v") && words.length >= 4) {
                    this.vertices.add(new Vec3f(
                            Float.parseFloat(words[1]),
                            Float.parseFloat(words[2]),
                            Float.parseFloat(words[3])));
                }

                if (words[0].equals("vn") && words.length >= 4) {
                    this.normals.add(new Vec3f(
                            Float.parseFloat(words[1]),
                            Float.parseFloat(words[2]),
                            Float.parseFloat(words[3])));
                }

                // TODO: Need to make use of other face values
                if (words[0].equals("f") && words.length >= 4) {
                    ArrayList<Integer> vertexIndex = new ArrayList<>(3);
                    ArrayList<Integer> vertexNormalIndex = new ArrayList<>(3);
                    for (int i = 1; i <= 3; i++) {
                        String[] parts = words[i].split("/");
                        if (!parts[0].isEmpty()) {
                            vertexIndex.add(Integer.parseInt(parts[0]));
                        }
                        if (!parts[2].isEmpty()) {
                            vertexNormalIndex.add(Integer.parseInt(parts[2]));
                        }
                    }
                    this.facetVertex.add(vertexIndex);
                    this.facetNormal.add(vertexNormalIndex);
                }
            }

            this.nFaces = this.facetVertex.size();
            this.nNormals = this.normals.size();
            this.nVertices = this.vertices.size();

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading OBJ file: " + e.getMessage());
        }
    }
}