package src;

// TODO: Research more on color
public class Color {

    public int r, g, b;
    private int rgb;

    public static final int RED = new Color(255, 0, 0).RGB();
    public static final int BLACK = new Color(0, 0, 0).RGB();
    public static final int GREEN = new Color(0, 255, 0).RGB();
    public static final int BLUE = new Color(0, 0, 255).RGB();
    public static final int WHITE = new Color(255, 255, 255).RGB();
    public static final int YELLOW = new Color(255, 255, 0).RGB();

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(float r, float g, float b) {
        int R = (int) (r * 255.0f + 0.5f);
        int G = (int) (g * 255.0f + 0.5f);
        int B = (int) (b * 255.0f + 0.5f);

        this(R, G, B);
    }

    public int RGB() {
        this.rgb = (0xFF << 24) | (this.r << 16) | (this.g << 8) | this.b;
        return this.rgb;
    }

    public static Color rgbToColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return new Color(r, g, b);
    }

    public static int intRGBToGray(int rgb) {
        Color rgbColor = rgbToColor(rgb);
        return (rgbColor.b + rgbColor.g + rgbColor.r) / 3;
    }

    // TODO: Use matrix operations for color?
    public static int interpolateTwoRGBvalues(int rgb1, int rgb2, float t) {
        float alpha = t;
        float beta = 1 - t;

        Color c1 = Color.rgbToColor(rgb1);
        Color c2 = Color.rgbToColor(rgb2);

        int r = (int) (c1.r * alpha + c2.r * beta);
        int g = (int) (c1.g * alpha + c2.g * beta);
        int b = (int) (c1.b * alpha + c2.b * beta);

        // NOTE: Clamp to valid range
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public static int interpolateThreeRGBvalues(int rgb1, int rgb2, int rgb3, float alpha, float beta, float gamma) {
        Color c1 = Color.rgbToColor(rgb1);
        Color c2 = Color.rgbToColor(rgb2);
        Color c3 = Color.rgbToColor(rgb3);

        int r = (int) (c1.r * alpha + c2.r * beta + c3.r * gamma);
        int g = (int) (c1.g * alpha + c2.g * beta + c3.g * gamma);
        int b = (int) (c1.b * alpha + c2.b * beta + c3.b * gamma);

        // NOTE: Clamp to valid range
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public static int generateRandomColorRGBValue() {
        int color = new Color(
                (float) Math.random(),
                (float) Math.random(),
                (float) Math.random()).RGB();

        return color;
    }
}
