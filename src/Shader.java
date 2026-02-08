package src;

import src.vec.Mat4f;
import src.vec.Vec3f;
import src.vec.Vec4f;

class ShaderReturnType {
    public boolean discard;
    public int colorRGB;
}

public interface Shader {
    Vec4f vertex(final int face, final int vert, final Mat4f T);

    ShaderReturnType fragment(final Vec3f bar);
}
