package src;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

public class Image {
    public int width;
    public int height;
    public int type;
    private BufferedImage image;

    public static final int IMAGE_TYPE_INT_RGB = BufferedImage.TYPE_INT_RGB;
    // TODO: Gray-scale image type not working need to check
    public static final int IMAGE_TYPE_BYTE_GRAY = BufferedImage.TYPE_BYTE_GRAY;
    public static final int IMAGE_TYPE_INT_ARGB = BufferedImage.TYPE_INT_ARGB;

    public Image() {
    }

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, IMAGE_TYPE_INT_RGB);
    }

    public Image(int width, int height, int type) {
        this.width = width;
        this.height = height;
        this.type = type;
        this.image = new BufferedImage(width, height, type);
    }

    public boolean readTgaFile(String filename) {
        File file = new File(filename);
        if (!file.exists())
            return false;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[18];
            fis.read(header);
            ByteBuffer bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);

            int idLength = bb.get(0) & 0xFF;
            int dataType = bb.get(2) & 0xFF; // 2 or 3 = Raw, 10 or 11 = RLE
            this.width = bb.getShort(12) & 0xFFFF;
            this.height = bb.getShort(14) & 0xFFFF;
            int bpp = bb.get(16) & 0xFF;
            int descriptor = bb.get(17) & 0xFF;

            if (idLength > 0)
                fis.skip(idLength);

            int bytesPerPixel = bpp / 8;
            int type = (bpp == 32) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
            this.image = new BufferedImage(this.width, this.height, type);

            boolean success;
            if (dataType == 2 || dataType == 3) {
                success = loadRawData(fis, bytesPerPixel);
            } else if (dataType == 10 || dataType == 11) {
                success = loadRleData(fis, bytesPerPixel);
            } else {
                System.err.println("Unknown TGA format: " + dataType);
                return false;
            }

            // NOTE: Handle TGA orientation (usually bottom-left, but check descriptor bit
            // 5)
            if ((descriptor & 0x20) == 0) {
                this.flip(false, true); // NOTE: Flip vertically if origin is bottom
            }
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadRawData(FileInputStream fis, int bytesPerPixel) throws IOException {
        int pixelCount = width * height;
        // TGA stores BGR or BGRA
        byte[] pixelBuffer = new byte[bytesPerPixel];

        for (int i = 0; i < pixelCount; i++) {
            int read = fis.read(pixelBuffer);
            if (read < bytesPerPixel)
                break;

            int b = pixelBuffer[0] & 0xFF;
            int g = pixelBuffer[1] & 0xFF;
            int r = pixelBuffer[2] & 0xFF;
            int a = (bytesPerPixel == 4) ? (pixelBuffer[3] & 0xFF) : 0xFF;

            int argb = (a << 24) | (r << 16) | (g << 8) | b;

            // Correct Coordinate Mapping: i % width is X (col), i / width is Y (row)
            this.image.setRGB(i % width, i / width, argb);
        }
        return true;
    }

    private boolean loadRleData(FileInputStream fis, int bytesPerPixel) throws IOException {
        int pixelCount = width * height;
        int currentPixel = 0;
        byte[] colorBuffer = new byte[bytesPerPixel];

        while (currentPixel < pixelCount) {
            int chunkHeader = fis.read();
            if (chunkHeader == -1)
                return false;

            if (chunkHeader < 128) {
                // Raw Chunk
                int count = chunkHeader + 1;
                for (int i = 0; i < count; i++) {
                    fis.read(colorBuffer);
                    setPixelFromBuffer(currentPixel++, colorBuffer, bytesPerPixel);
                }
            } else {
                // RLE Chunk
                int count = chunkHeader - 127;
                fis.read(colorBuffer);
                for (int i = 0; i < count; i++) {
                    setPixelFromBuffer(currentPixel++, colorBuffer, bytesPerPixel);
                }
            }
        }
        return true;
    }

    private void setPixelFromBuffer(int pixelIdx, byte[] buffer, int bytesPerPixel) {
        if (pixelIdx >= width * height)
            return;

        int x = pixelIdx % width;
        int y = pixelIdx / width;

        int r, g, b, a;

        if (bytesPerPixel == 1) {
            // Grayscale: R, G, and B are all the same value
            int gray = buffer[0] & 0xFF;
            r = g = b = gray;
            a = 0xFF;
        } else {
            // BGR or BGRA
            b = buffer[0] & 0xFF;
            g = buffer[1] & 0xFF;
            r = buffer[2] & 0xFF;
            a = (bytesPerPixel == 4) ? (buffer[3] & 0xFF) : 0xFF;
        }

        int argb = (a << 24) | (r << 16) | (g << 8) | b;
        this.image.setRGB(x, y, argb);
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