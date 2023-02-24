package com.osurestream.engine.primitives;

import com.osurestream.engine.render.StandaloneTextRenderingProvider;
import com.osurestream.engine.util.TextRenderingProvider;
import com.osurestream.engine.util.TextRepresentation;
import com.osurestream.engine.util.Texture;
import org.lwjgl.opengles.GLES20;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class TextPrimitive extends Sprite {

    private String font;
    private String text;
    private int size;
    public static TextRenderingProvider TEXT_PROVIDER =
            new StandaloneTextRenderingProvider();

    public static ArrayList<Texture> TEXTURE_CACHE = new ArrayList<>();
    public static ArrayList<ByteBuffer> BUFFER_CACHE = new ArrayList<>();

    public static HashMap<TextRepresentation, BufferedImage> TEXT_CACHE = new HashMap<>();

    public TextPrimitive(String font, String text, int size) {
        super(64, 64);
        this.font = font;
        this.text = text;
        this.size = size;

        this.flipY = true;
    }

    public void update() {
        BufferedImage image = TEXT_PROVIDER.render(this);

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

        for(int h = 0; h < image.getHeight(); h++) {
            for(int w = 0; w < image.getWidth(); w++) {
                int pixel = pixels[h * image.getWidth() + w];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        Texture glTexture = new Texture(buffer, image.getWidth(), image.getHeight(), 4);
        TEXTURE_CACHE.add(glTexture);
        BUFFER_CACHE.add(buffer);

        updateSprite(glTexture);

    }

    public static void clearCaches() {
        for (Texture texture : TEXTURE_CACHE) {
            GLES20.glDeleteTextures(texture.getTextureID());
        }
        TEXTURE_CACHE.clear();

        for (ByteBuffer buffer : BUFFER_CACHE) {
            buffer.clear();
        }
        BUFFER_CACHE.clear();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
