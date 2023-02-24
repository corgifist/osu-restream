package com.osurestream.engine.primitives;

import com.osurestream.engine.AppBase;
import com.osurestream.engine.render.Display;
import com.osurestream.engine.util.Texture;
import org.joml.Vector3f;

import java.util.Arrays;

public class QuadPrimitive extends Renderable {

    public static Vector3f[] UNNORMALIZED_VERTICES = new Vector3f[] {
            new Vector3f(0.5f, 0.5f, 0),
            new Vector3f(0.5f, -0.5f, 0),
            new Vector3f(-0.5f, -0.5f, 0),
            new Vector3f(-0.5f, 0.5f, 0)
    };

    public static int[] QUAD_INDICES = new int[] {
            0, 1, 3,
            1, 2, 3
    };

    public static float[] QUAD_VERTICES = new float[] {
            960, 180, 0, // top left || bottom right
            960, 540, 0, // top right || top right
            320, 540, 0, // bottom right || top left
            320, 180, 0 // bottom left || bottom left
    };

    public static float[] QUAD_TEXCOORDS = new float[] {
            1, 1,
            0, 1,
            0, 0,
            1, 0
    };
    private Vector3f tl, tr, br, bl;
    private float[] texcoords;

    public QuadPrimitive(Vector3f tl, Vector3f tr, Vector3f br, Vector3f bl, float[] texcoords) {
        super();
        this.tl = vectorNDC(tl);
        this.tr = vectorNDC(tr);
        this.br = vectorNDC(br);
        this.bl = vectorNDC(bl);
        this.texcoords = texcoords;
        setupColors();
    }

    public QuadPrimitive(Vector3f tl, Vector3f tr, Vector3f br, Vector3f bl) {
        this(tl, tr, br, bl, QUAD_TEXCOORDS);
    }
    public QuadPrimitive() {
        this(new Vector3f(QUAD_VERTICES[0], QUAD_VERTICES[1], QUAD_VERTICES[2]),
             new Vector3f(QUAD_VERTICES[3], QUAD_VERTICES[4], QUAD_VERTICES[5]),
             new Vector3f(QUAD_VERTICES[6], QUAD_VERTICES[7], QUAD_VERTICES[8]),
             new Vector3f(QUAD_VERTICES[9], QUAD_VERTICES[10], QUAD_VERTICES[11]));
    }

    @Override
    public float[] vertices() {
        return transform(new float[] {
                tr.x, tr.y, tr.z,
                br.x, br.y, br.z,
                bl.x, bl.y, bl.z,
                tl.x, tl.y, tl.z
        });
    }

    @Override
    public float[] colors() {
        return colors;
    }

    @Override
    public int[] indices(int index) {
        int shift = 4;
        return new int[] {
                0 + shift * index, 1 + shift * index, 3 + shift * index,
                1 + shift * index, 2 + shift * index, 3 + shift * index
        };
    }

    @Override
    public float[] texcoords() {
        return flip(texcoords);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getTl() {
        return tl;
    }

    public void setTl(Vector3f tl) {
        this.tl = tl;
    }

    public Vector3f getTr() {
        return tr;
    }

    public void setTr(Vector3f tr) {
        this.tr = tr;
    }

    public Vector3f getBr() {
        return br;
    }

    public void setBr(Vector3f br) {
        this.br = br;
    }

    public Vector3f getBl() {
        return bl;
    }

    public void setBl(Vector3f bl) {
        this.bl = bl;
    }

    public float[] getTexcoords() {
        return texcoords;
    }

    public void setTexcoords(float[] texcoords) {
        this.texcoords = texcoords;
    }

    public static Vector3f vectorNDC(Vector3f xyz) {
        return new Vector3f(screenNDCX(xyz.x), screenNDCY(xyz.y), xyz.z);
    }

    public static Vector3f screenNDCVector(Vector3f xyz) {
        return new Vector3f(getNdcX(xyz.x), getNdcY(xyz.y), xyz.z);
    }

    public static float screenNDCX(float x) {
        return x / Display.VIRTUAL_WIDTH * 2 - 1;
    }

    public static float screenNDCY(float y) {
        return y / Display.VIRTUAL_HEIGHT * 2 - 1;
    }

    public static float getNdcX(float x) {
        return (x + 1f) * 0.5f * Display.VIRTUAL_WIDTH;
    }

    public static float getNdcY(float y) {
        return (1.0f - y) * 0.5f * Display.VIRTUAL_HEIGHT;
    }
}
