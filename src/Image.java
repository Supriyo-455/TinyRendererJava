package src;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Image {
    public int width;
    public int height;
    private BufferedImage image;

    // TODO: Gray-scale image type not working need to check
    public static final int IMAGE_TYPE_INT_RGB = BufferedImage.TYPE_INT_RGB;
    public static final int IMAGE_TYPE_BYTE_GRAY = BufferedImage.TYPE_BYTE_GRAY;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, IMAGE_TYPE_INT_RGB);
    }

    public Image(int width, int height, int type) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, type);
    }

    public void fillColor(int color) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                this.setPixelColor(row, col, color);
            }
        }
    }

    public void setPixelColor(int x, int y, int color) {
        this.image.setRGB(x, y, color);
    }

    public int getPixelColor(int x, int y) {
        return this.image.getRGB(x, y);
    }

    // TODO: Research more on this topic
    public void flip(boolean horizontal, boolean vertical) {
        BufferedImage flipped = new BufferedImage(height, width, this.image.getType());

        Graphics2D g = flipped.createGraphics();
        AffineTransform transform = new AffineTransform();

        transform.scale(horizontal ? -1 : 1, vertical ? -1 : 1);
        transform.translate(horizontal ? -width : 0, vertical ? -height : 0);

        g.drawImage(this.image, transform, null);
        g.dispose();

        this.image = flipped;
    }

    public void save(String filename) {
        try {
            ImageIO.write(this.image, "png", new File(filename));
            System.out.println("image saved as: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image copy() {
        return new Image(this.width, this.height);
    }
}