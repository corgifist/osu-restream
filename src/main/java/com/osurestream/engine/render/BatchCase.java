package com.osurestream.engine.render;

import com.osurestream.engine.util.Texture;
import org.lwjgl.openal.AL10;

import java.util.ArrayList;

public class BatchCase {

    private Texture texture;
    private ArrayList<Float> vertices;
    private ArrayList<Float> colors;
    private ArrayList<Float> texcoords;
    private ArrayList<Integer> indices;

    private int size;

    public BatchCase(Texture texture) {
        this.texture = texture;
        this.vertices = new ArrayList<>();
        this.colors = new ArrayList<>();
        this.texcoords = new ArrayList<>();
        this.indices = new ArrayList<>();
    }

    public void clear() {
        vertices.clear();
        colors.clear();
        texcoords.clear();
        indices.clear();
        size = 0;
    }

    public void addTexcoord(float s, float t) {
        texcoords.add(s);
        texcoords.add(t);
    }

    public void addVertex(float x, float y, float z) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
    }

    public void addColor(float r, float g, float b, float a) {
        colors.add(r);
        colors.add(g);
        colors.add(b);
        colors.add(g);
    }

    public void addIndex(int x, int y, int z) {
        indices.add(x);
        indices.add(y);
        indices.add(z);
    }

    public float[] getVertices() {
        float[] result = new float[vertices.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = vertices.get(i);
        }
        return result;
    }

    public float[] getColors() {
        float[] result = new float[colors.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = colors.get(i);
        }
        return result;
    }

    public float[] getTexcoords() {
        float[] result = new float[texcoords.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = texcoords.get(i);
        }
        return result;
    }

    public int[] getIndices() {
        int[] result = new int[indices.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = indices.get(i);
        }
        return result;
    }

    public void setVertices(ArrayList<Float> vertices) {
        this.vertices = vertices;
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.indices = indices;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setColors(ArrayList<Float> colors) {
        this.colors = colors;
    }

    public void setTexcoords(ArrayList<Float> texcoords) {
        this.texcoords = texcoords;
    }

    @Override
    public String toString() {
        return "BatchCase{" +
                "texture=" + texture +
                ", vertices=" + vertices +
                ", colors=" + colors +
                ", texcoords=" + texcoords +
                ", indices=" + indices +
                ", size=" + size +
                '}';
    }
}
