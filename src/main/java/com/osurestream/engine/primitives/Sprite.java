package com.osurestream.engine.primitives;

import com.osurestream.engine.render.Display;
import com.osurestream.engine.util.Rectangle;
import com.osurestream.engine.util.Texture;
import org.joml.Vector3f;

import java.util.ArrayList;

// wrapper class for better quad manipulation
public class Sprite extends QuadPrimitive {

    public static float[] FIXED_SPRITE_TEXCOORDS = new float[] {
            1, 1,
            1, 0,
            0, 0,
            0, 1
    };

    public Sprite(Texture texture) {
        updateSprite(texture);
    }

    public Sprite(int width, int height) {
        updateSprite(width, height);
    }

    public Rectangle getRectangle() {
        float[] vertices = vertices();

        ArrayList<Float> uniqueX = new ArrayList<>();
        ArrayList<Float> uniqueY = new ArrayList<>();
        for (int i = 0; i < vertices.length; i += 3) {
            if (uniqueX.contains(vertices[i])) continue;
            uniqueX.add(getNdcX(vertices[i]));
        }

        for (int i = 1; i < vertices.length; i += 3) {
            if (uniqueY.contains(vertices[i])) continue;
            uniqueY.add(getNdcY(vertices[i]));
        }

        float xMin = getMinimumFloat(uniqueX);
        float xMax = getMaximumFloat(uniqueX);
        float yMin = getMinimumFloat(uniqueY);
        float yMax = getMaximumFloat(uniqueY);

        float width = xMax - xMin;
        float height = yMax - yMin;

        return new Rectangle(position.x, position.y, width, height);
    }

    private float getMinimumFloat(ArrayList<Float> array) {
        float candidate = 999999999;
        for (float x : array) {
            if (x < candidate) candidate = x;
        }
        return candidate;
    }

    private float getMaximumFloat(ArrayList<Float> array) {
        float candidate = -99999999;
        for (float x : array) {
            if (x > candidate) candidate = x;
        }
        return candidate;
    }

    public void center() {
        position.x = Display.VIRTUAL_WIDTH / 2f - texture.getWidth() / 2f;
        position.y = Display.VIRTUAL_HEIGHT / 2f - texture.getHeight() / 2f;
    }

    public void updateSprite(int width, int height, Texture texture) {
        this.texture = texture;
        Vector3f tl = new Vector3f(0, height, 0);
        Vector3f tr = new Vector3f(width, height, 0);
        Vector3f br = new Vector3f(width, 0, 0);
        Vector3f bl = new Vector3f(0, 0, 0);
        this.setTl(QuadPrimitive.vectorNDC(tl));
        this.setTr(QuadPrimitive.vectorNDC(tr));
        this.setBr(QuadPrimitive.vectorNDC(br));
        this.setBl(QuadPrimitive.vectorNDC(bl));
        this.setTexcoords(FIXED_SPRITE_TEXCOORDS);
    }

    public void updateSprite(Texture texture) {
        updateSprite(texture.getWidth(), texture.getHeight(), texture);

    }

    public void updateSprite(int width, int height) {
        updateSprite(width, height, null);
    }

}
