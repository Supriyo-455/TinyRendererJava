package src;

import src.vec.Mat4f;
import src.vec.Vec3f;
import src.vec.Vec4f;

class RandomShader implements Shader {

    private final Model model;

    private final Mat4f MV;

    private final Mat4f P;

    private int colorRGB;

    // NOTE: Triangle in eye coordinates
    private Vec3f[] tri;

    RandomShader(final Model model, final Mat4f ModelView, final Mat4f Perspective) {
        this.model = model;
        this.tri = new Vec3f[3];
        this.MV = ModelView;
        this.P = Perspective;
    }

    public void setColor(int colorRGB) {
        this.colorRGB = colorRGB;
    }

    @Override
    public ShaderReturnType fragment(final Vec3f bar) {
        ShaderReturnType result = new ShaderReturnType();
        result.colorRGB = this.colorRGB;
        result.discard = false;
        return result;
    }

    @Override
    public Vec4f vertex(final int face, final int vert, final Mat4f T) {
        int normalIndex = this.model.facetNormal.get(face).get(vert);

        int vertexIndex = this.model.facetVertex.get(face).get(vert);
        Vec3f v = this.model.vertices.get(vertexIndex - 1);

        Vec4f gl_position = T.multiply(this.MV).multiply(new Vec4f(v.x, v.y, v.z, 1));
        tri[vert] = gl_position.swizzle(0, 1, 2);
        return this.P.multiply(gl_position);
    }
}