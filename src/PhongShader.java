package src;

import src.vec.Mat4f;
import src.vec.Vec3f;
import src.vec.Vec4f;

class PhongShader implements Shader {

    private final Model model;

    private final Mat4f MV;

    private final Mat4f P;

    private Vec3f lightDir;

    private Vec3f cam;

    private final float e = 1f;

    // NOTE: Triangle in eye coordinates
    private Vec3f[] tri;

    PhongShader(final Model model, final Mat4f ModelView, final Mat4f Perspective, Vec3f cam) {
        this.model = model;
        this.tri = new Vec3f[3];
        this.MV = ModelView;
        this.P = Perspective;
        this.cam = cam.normalize();

        // TODO: Move this light initiation out of the renderer
        this.lightDir = new Vec3f(1.0f, 1.0f, 1.0f).normalize();
        this.lightDir = this.MV
                .multiply(new Vec4f(this.lightDir.x, this.lightDir.y, this.lightDir.z, 1))
                .swizzle(0, 1, 2).normalize();
    }

    public Vec3f computeNormal() {
        return (this.tri[0].subtract(this.tri[1])).cross((this.tri[0].subtract(this.tri[2]))).normalize();
    }

    public void setLightDir(Vec3f l) {
        this.lightDir = this.MV
                .multiply(new Vec4f(l.x, l.y, l.z, 1))
                .swizzle(0, 1, 2).normalize();
    }

    @Override
    public ShaderReturnType fragment(final Vec3f bar) {
        float ambient = 0.3f;

        Vec3f normal = this.computeNormal();

        float dotNL = normal.dot(this.lightDir);
        // TODO: Is computing normal for each pixel required?
        float diffuse = Math.max(0, dotNL);

        Vec3f reflectVector = normal.multiplyByScalar(2 * dotNL).subtract(lightDir).normalize();
        float specBase = Math.max(0, reflectVector.dot(cam));
        float specular = (float) Math.pow(specBase, this.e);

        float colorValue = ambient + (0.3f * diffuse) + (0.5f * specular);
        colorValue = Math.min(1.0f, colorValue);

        ShaderReturnType result = new ShaderReturnType();
        result.colorRGB = new Color(colorValue, colorValue, colorValue).RGB();
        return result;
    }

    @Override
    public Vec4f vertex(final int face, final int vert, final Mat4f T) {
        int vertexIndex = this.model.facets.get(face).get(vert);
        Vec3f v = this.model.vertices.get(vertexIndex - 1);
        Vec4f gl_position = T.multiply(this.MV).multiply(new Vec4f(v.x, v.y, v.z, 1));
        tri[vert] = gl_position.swizzle(0, 1, 2);
        return this.P.multiply(gl_position);
    }
}