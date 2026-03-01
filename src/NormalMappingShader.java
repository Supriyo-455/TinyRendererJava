package src;

import src.vec.Mat4f;
import src.vec.Vec2f;
import src.vec.Vec3f;
import src.vec.Vec4f;

public class NormalMappingShader implements Shader {
    private final Model model;

    private Mat4f MV;

    private final Mat4f P;

    private Vec3f cam;

    private final float e = 32f;

    // NOTE: Triangle in eye coordinates
    private Vec3f[] tri;

    private Vec2f[] varyingUV;

    private Mat4f normalMatrix;
    private final Vec3f lightDir = new Vec3f(0, 0, 1).normalize(); // Stationary Eye-Space Light

    NormalMappingShader(final Model model, final Mat4f ModelView, final Mat4f Perspective, Vec3f cam) {
        this.model = model;
        this.tri = new Vec3f[3];
        this.varyingUV = new Vec2f[3];
        this.MV = ModelView;
        this.P = Perspective;
        this.cam = cam.normalize();
    }

    @Override
    public ShaderReturnType fragment(final Vec3f bar) {

        float u = varyingUV[0].x * bar.x + varyingUV[1].x * bar.y + varyingUV[2].x * bar.z;
        float v = varyingUV[0].y * bar.x + varyingUV[1].y * bar.y + varyingUV[2].y * bar.z;

        Vec2f uv = new Vec2f(u, v);

        Vec3f objectNormal = model.getNormal(uv);
        Vec3f n = normalMatrix.multiply(new Vec4f(objectNormal, 0)).swizzle(0, 1, 2).normalize();

        float dotNL = Math.max(0, n.dot(this.lightDir));

        int diffusePixel = this.model.getDiffuse(uv);
        float rMap = ((diffusePixel >> 16) & 0xFF) / 255.0f;
        float gMap = ((diffusePixel >> 8) & 0xFF) / 255.0f;
        float bMap = (diffusePixel & 0xFF) / 255.0f;

        Vec3f reflectVector = n.multiplyByScalar(2 * dotNL).subtract(lightDir).normalize();
        int specPixel = this.model.getSpecular(uv);
        float specIntensity = ((specPixel >> 16) & 0xFF) / 255.0f;
        float specBase = Math.max(0, reflectVector.dot(cam));
        float specular = (3.0f * specIntensity) * (float) Math.pow(specBase, this.e);

        float ambient = 0.3f;
        float intensity = ambient + dotNL + specular;

        float finalR = Math.min(1.0f, rMap * intensity);
        float finalG = Math.min(1.0f, gMap * intensity);
        float finalB = Math.min(1.0f, bMap * intensity);

        ShaderReturnType result = new ShaderReturnType();
        result.colorRGB = new Color(finalR, finalG, finalB).RGB();
        return result;
    }

    @Override
    public Vec4f vertex(final int face, final int vert, final Mat4f T) {
        int vIdx = this.model.facetVertex.get(face)[vert] - 1;
        int tIdx = this.model.facetTexture.get(face)[vert] - 1;

        Mat4f modelView = T.multiply(this.MV);
        this.normalMatrix = modelView.inverse().transpose();

        Vec3f v = this.model.vertices.get(vIdx);
        this.varyingUV[vert] = this.model.texCoords.get(tIdx);

        Vec4f gl_position = modelView.multiply(new Vec4f(v.x, v.y, v.z, 1));
        this.tri[vert] = gl_position.swizzle(0, 1, 2);

        return this.P.multiply(gl_position);
    }
}
