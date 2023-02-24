package com.osurestream.engine.util;

import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Texture {

    private int textureID;
    private String path;

    private int width, height, channels;

    public static HashMap<String, Texture> cache = new HashMap<>();

    public static Texture create(String path) {
        if (cache.containsKey(path)) return cache.get(path);
        cache.put(path, new Texture(path));
        return cache.get(path);
    }

    public Texture(int textureID, int width, int height, int channels) {
        this.textureID = textureID;
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.path = String.valueOf(textureID);
    }

    public Texture(ByteBuffer buffer, int width, int height, int channels) {
        this.width = width;
        this.height = height;
        this.channels = channels;

        this.textureID = GLES20.glGenTextures();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, getTypeByChannels(channels),
                width, height, 0, getTypeByChannels(channels),
                GLES20.GL_UNSIGNED_BYTE, buffer);

        this.path = String.valueOf(textureID);
    }

    public Texture(String path) {
        this.path = path;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.textureID = GLES20.glGenTextures();

            IntBuffer widthBuffer = stack.callocInt(1);
            IntBuffer heightBuffer = stack.callocInt(1);
            IntBuffer channelsCount = stack.callocInt(1);
            stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = stbi_load("assets/" + path, widthBuffer, heightBuffer, channelsCount, 0);

            this.width = widthBuffer.get(0);
            this.height = heightBuffer.get(0);
            this.channels = channelsCount.get(0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, getTypeByChannels(channels),
                    width, height, 0, getTypeByChannels(channels),
                    GLES20.GL_UNSIGNED_BYTE, data);
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }
    }

    public static int getTypeByChannels(int channels) {
        switch (channels) {
            case 3: return GLES20.GL_RGB;
            case 4: return GLES20.GL_RGBA;
        }
        return GLES20.GL_RGB;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "Texture{" +
                "path='" + path + '\'' +
                '}';
    }
}
