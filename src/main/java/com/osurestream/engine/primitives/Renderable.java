package com.osurestream.engine.primitives;

import com.osurestream.engine.AppBase;
import com.osurestream.engine.util.SceneGraph;
import com.osurestream.engine.util.Texture;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;

import static com.osurestream.engine.util.SceneGraph.camera;

public abstract class Renderable {

    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public float angle;

    public float[] colors;

    public Matrix4f transformation;

    public Texture texture;

    public boolean visible;

    public boolean flipX, flipY;

    public Renderable() {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1);
        this.transformation = new Matrix4f().identity();
        this.visible = true;
    }

    public void update() {
        // to be implemented by parent classes
    }

    public float[] transform(float[] vertices) {
        updateShader();
        Vector4f[] vectorVertices = new Vector4f[vertices.length / 3];
        int vectorIndex = 0;
        for (int i = 0; i < vertices.length; i += 3) {
            Matrix4f mTransforms;
            Matrix2f m2D = new Matrix2f().identity().rotate((float) Math.toRadians(angle));
            if (camera != null) {
                mTransforms = new Matrix4f(transformation).mul(camera.view())
                                .mul(camera.projection());
            } else {
                mTransforms = new Matrix4f(transformation);
            }
            Vector2f transformedVertices2D = new Vector2f(
                    vertices[i + 0],
                    vertices[i + 1]
            );
            float angleRadians = (float) Math.toRadians(angle);
            vectorVertices[vectorIndex] = new Vector4f(
                    transformedVertices2D.x,
                    transformedVertices2D.y,
                    vertices[i + 2], 1.0f
            ).mul(mTransforms);
            vectorIndex++;
        }
        float[] result = new float[vectorVertices.length * 3];
        int resultIndex = 0;
        for (int i = 0; i < vectorVertices.length; i++) {
            result[resultIndex + 0] = vectorVertices[i].x;
            result[resultIndex + 1] = vectorVertices[i].y;
            result[resultIndex + 2] = vectorVertices[i].z;
            resultIndex += 3;
        }
        return result;
    }

    public void setupColors() {
        this.colors = new float[vertices().length / 3 * 4];
        Arrays.fill(colors, 1);
    }

    public void updateShader() {
        Vector3f ndcPosition = new Vector3f();
        ndcPosition.x = (QuadPrimitive.screenNDCX(position.x) * 0.5f) + 0.5f;
        ndcPosition.y = (QuadPrimitive.screenNDCY(position.y) * 0.5f) + 0.5f;
        ndcPosition.z = position.z;
        transformation.identity().
                translate(ndcPosition)
                .rotate((float) Math.toRadians(rotation.x), 1, 0, 0)
                .rotate((float) Math.toRadians(rotation.y), 0, 1, 0)
                .rotate((float) Math.toRadians(rotation.z), 0, 0, 1)
                .scale(scale);
    }

    public float[] flip(float[] texcoords) {
        float[] result = new float[texcoords.length];
        for (int i = 0; i < texcoords.length; i += 2) {
            float x = texcoords[i];
            float y = texcoords[i + 1];

            if (flipX) x = Math.max(0, 1 - x);
            if (flipY) y = Math.max(0, 1 - y);

            result[i + 0] = x;
            result[i + 1] = y;
        }
        return result;
    }

    public void setVertexColor(int index, float r, float g, float b, float a) {
        colors[index * 3 + 0] = r;
        colors[index * 3 + 1] = g;
        colors[index * 3 + 2] = b;
        colors[index * 3 + 3] = a;
    }

    public void setColor(float r, float g, float b, float a) {
        for (int i = 0; i < colors.length; i += 4) {
            colors[i + 0] = r;
            colors[i + 1] = g;
            colors[i + 2] = b;
            colors[i + 3] = a;
        }
    }

    public abstract float[] vertices();
    public abstract float[] colors();
    public abstract int[] indices(int index);
    public abstract float[] texcoords();

}
