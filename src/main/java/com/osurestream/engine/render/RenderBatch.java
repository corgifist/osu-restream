package com.osurestream.engine.render;

import com.osurestream.engine.AppBase;
import com.osurestream.engine.primitives.Renderable;
import com.osurestream.game.AppDirector;
import org.joml.Vector4f;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class RenderBatch {

    public static int BATCH_SIZE = 256;
    public static boolean BLENDING_ENABLED = true;
    public static int DRAW_CALL_COUNT = 0;

    private int vbo, cbo, tbo, ebo;

    private ArrayList<BatchCase> cases;

    public RenderBatch() {
        this.cases = new ArrayList<>();
    }

    public void draw(Renderable renderable) {
        if (BLENDING_ENABLED) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
        if (!renderable.visible) return;
        BatchCase batchCase = getBatchCase(renderable);
        if (batchCase.getSize() == BATCH_SIZE) {
            flush(batchCase);
        }
        float[] renderableVertices = renderable.vertices();
        float[] renderableColors = renderable.colors();
        float[] renderableTexcoords = renderable.texcoords();
        int[] renderableIndices = renderable.indices(batchCase.getSize());


        for (int i = 0; i < renderableVertices.length; i += 3) {
            Vector4f transformedVertex4f =
                    new Vector4f(renderableVertices[i + 0], renderableVertices[i + 1],
                                renderableVertices[i + 2], 1.0f);
            transformedVertex4f.mul(renderable.transformation);
            batchCase.addVertex(transformedVertex4f.x, transformedVertex4f.y, transformedVertex4f.z);
        }

        for (int i = 0; i < renderableColors.length; i += 4) {
            batchCase.addColor(renderableColors[i + 0],
                                renderableColors[i + 1],
                                renderableColors[i + 2],
                                renderableColors[i + 3]);
        }

        for (int i = 0; i < renderableTexcoords.length; i += 2) {
            batchCase.addTexcoord(renderableTexcoords[i + 0],
                                    renderableTexcoords[i + 1]);
        }

        for (int i = 0; i < renderableIndices.length; i += 3) {
            batchCase.addIndex(renderableIndices[i + 0],
                                renderableIndices[i + 1],
                                renderableIndices[i + 2]);
        }
        batchCase.setSize(batchCase.getSize() + 1);
    }

    public void render() {
        for (BatchCase batchCase : cases) {
            flush(batchCase);
        }
    }

    public void flush(BatchCase batchCase) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            float[] batchVertices = batchCase.getVertices();
            float[] batchColors = batchCase.getColors();
            float[] batchTexcoords = batchCase.getTexcoords();
            int[] batchIndices = batchCase.getIndices();

            FloatBuffer verticesBuffer = stack.floats(batchVertices);
            FloatBuffer colorsBuffer = stack.floats(batchColors);
            FloatBuffer texcoordsBuffer = stack.floats(batchTexcoords);
            IntBuffer indicesBuffer = stack.ints(batchIndices);


            this.vbo = GLES20.glGenBuffers();
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer, GLES20.GL_DYNAMIC_DRAW);

            this.cbo = GLES20.glGenBuffers();
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, cbo);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, colorsBuffer, GLES20.GL_DYNAMIC_DRAW);

            this.tbo = GLES20.glGenBuffers();
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tbo);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texcoordsBuffer, GLES20.GL_DYNAMIC_DRAW);

            this.ebo = GLES20.glGenBuffers();
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GLES20.GL_DYNAMIC_DRAW);

            // vertex attributes part
            int positionLocation = 0;
            int colorLocation = 1;
            int texcoordLocation = 2;

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
            GLES20.glVertexAttribPointer(positionLocation, 3, GLES20.GL_FLOAT, false, 0, MemoryUtil.NULL);
            GLES20.glEnableVertexAttribArray(positionLocation);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, cbo);
            GLES20.glVertexAttribPointer(colorLocation, 4, GLES20.GL_FLOAT, false, 0, MemoryUtil.NULL);
            GLES20.glEnableVertexAttribArray(colorLocation);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tbo);
            GLES20.glVertexAttribPointer(texcoordLocation, 2, GLES20.GL_FLOAT, false, 0, MemoryUtil.NULL);
            GLES20.glEnableVertexAttribArray(texcoordLocation);

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ebo);

            if (batchCase.getTexture() != null) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, batchCase.getTexture().getTextureID());
                AppBase.SHADER_PROGRAM.setUniform("uTexture", 0);
                AppBase.SHADER_PROGRAM.setUniform("texturesEnabled", 1f);
                AppBase.SHADER_PROGRAM.setUniform("uChannels", batchCase.getTexture().getChannels());
            } else {
                AppBase.SHADER_PROGRAM.setUniform("texturesEnabled", 0f);
                AppBase.SHADER_PROGRAM.setUniform("uChannels", 0);
            }

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, batchCase.getIndices().length, GLES20.GL_UNSIGNED_INT, 0);
            DRAW_CALL_COUNT++;

            GLES20.glDeleteBuffers(vbo);
            GLES20.glDeleteBuffers(cbo);
            GLES20.glDeleteBuffers(tbo);
            GLES20.glDeleteBuffers(ebo);
        }
        Display.instance.setTitle(AppDirector.APPLICATION_NAME + " | glDrawElements count: " + DRAW_CALL_COUNT);
        batchCase.clear();
    }

    public BatchCase getBatchCase(Renderable renderable) {
        if (cases.size() == 0) {
            cases.add(new BatchCase(renderable.texture));
            return cases.get(0);
        }
        for (BatchCase batchCase : cases) {
            if (batchCase.getTexture() == null && renderable.texture == null) {
                return batchCase;
            }
            if (batchCase.getTexture() != null) {
                if (batchCase.getTexture().getTextureID() == renderable.texture.getTextureID()) {
                    return batchCase;
                }
            }
        }
        cases.add(new BatchCase(renderable.texture));
        return cases.get(cases.size() - 1);
    }

    @Override
    public String toString() {
        return "RenderBatch{" +
                "cases=" + cases +
                '}';
    }
}
