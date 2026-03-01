package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import src.vec.Vec2f;
import src.vec.Vec3f;
import src.vec.Vec4f;

public class Model {
    public List<int[]> facetVertex = new ArrayList<>();
    public List<int[]> facetTexture = new ArrayList<>();
    public List<int[]> facetNormal = new ArrayList<>();

    public List<Vec3f> vertices = new ArrayList<>();
    public List<Vec3f> normals = new ArrayList<>();
    public List<Vec2f> texCoords = new ArrayList<>();

    public Image diffuseMap = new Image();
    public Image normalMap = new Image();
    public Image specularMap = new Image();

    public Model(String filename) throws Exception {
        loadObj(filename);
        loadTexture(filename, "_diffuse.tga", diffuseMap);
        loadTexture(filename, "_nm.tga", normalMap);
        loadTexture(filename, "_spec.tga", specularMap);
    }

    private void loadObj(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] words = line.split("\\s+");
                if (words.length < 2)
                    continue;

                switch (words[0]) {
                    case "v":
                        vertices.add(new Vec3f(
                                Float.parseFloat(words[1]),
                                Float.parseFloat(words[2]),
                                Float.parseFloat(words[3])));
                        break;
                    case "vn":
                        normals.add(new Vec3f(
                                Float.parseFloat(words[1]),
                                Float.parseFloat(words[2]),
                                Float.parseFloat(words[3])));
                        break;
                    case "vt":
                        // NOTE: UVs only need 2 components
                        texCoords.add(new Vec2f(
                                Float.parseFloat(words[1]),
                                Float.parseFloat(words[2])));
                        break;
                    case "f":
                        parseFace(words);
                        break;
                }
            }
            System.out.println("Model loaded: " + vertices.size() + " vertices, " + facetVertex.size() + " faces, "
                    + texCoords.size() + " texture coords.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading OBJ: " + e.getMessage());
        }
    }

    private void parseFace(String[] words) {
        int[] vIdx = new int[3];
        int[] tIdx = new int[3];
        int[] nIdx = new int[3];

        for (int i = 0; i < 3; i++) {
            String[] parts = words[i + 1].split("/");

            // NOTE: OBJ indices are 1-based, converting to 0-based
            vIdx[i] = Integer.parseInt(parts[0]);

            if (parts.length > 1 && !parts[1].isEmpty()) {
                tIdx[i] = Integer.parseInt(parts[1]);
            }
            if (parts.length > 2 && !parts[2].isEmpty()) {
                nIdx[i] = Integer.parseInt(parts[2]);
            }
        }
        facetVertex.add(vIdx);
        facetTexture.add(tIdx);
        facetNormal.add(nIdx);
    }

    private void loadTexture(String filename, String suffix, Image target) {
        int dot = filename.lastIndexOf(".");
        if (dot == -1)
            return;
        String texFile = filename.substring(0, dot) + suffix;

        target.readTgaFile(texFile);
        System.out.println("Texture loaded: " + texFile);
    }

    public Vec2f uv(int iface, int nthvert) {
        int[] texIndices = this.facetTexture.get(iface);
        int idx = texIndices[nthvert];
        return this.texCoords.get(idx);
    }

    public Vec3f getNormal(Vec2f uv) {
        // NOTE: Map [0,1] UV to pixel coordinates with V-flip
        int x = (int) (uv.x * (normalMap.width - 1));
        int y = (int) ((1 - uv.y) * (normalMap.height - 1));

        int color = normalMap.getPixelColor(clamp(x, 0, normalMap.width - 1),
                clamp(y, 0, normalMap.height - 1));

        // NOTE: Unpack and map [0, 255] to [-1, 1]
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        return new Vec3f(r * 2 - 1, g * 2 - 1, b * 2 - 1).normalize();
    }

    public int getDiffuse(Vec2f uv) {
        int x = (int) (uv.x * (diffuseMap.width - 1));
        int y = (int) ((1 - uv.y) * (diffuseMap.height - 1));
        return diffuseMap.getPixelColor(clamp(x, 0, diffuseMap.width - 1),
                clamp(y, 0, diffuseMap.height - 1));
    }

    public int getSpecular(Vec2f uv) {
        int x = (int) (uv.x * (specularMap.width));
        int y = (int) ((1 - uv.y) * (specularMap.height));
        return specularMap.getPixelColor(clamp(x, 0, specularMap.width - 1),
                clamp(y, 0, specularMap.height - 1));
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public int nFaces() {
        return facetVertex.size();
    }
}