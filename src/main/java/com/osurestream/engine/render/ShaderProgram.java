package com.osurestream.engine.render;

import com.osurestream.engine.util.RenderException;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ShaderProgram {

    private int programID;

    private HashMap<String, Integer> uniforms;

    public ShaderProgram(String vertex, String fragment) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int vertexID = createShader(GLES20.GL_VERTEX_SHADER, "shaders/" + vertex);
            int fragmentID = createShader(GLES20.GL_FRAGMENT_SHADER, "shaders/" + fragment);

            this.programID = GLES20.glCreateProgram();
            GLES20.glAttachShader(programID, vertexID);
            GLES20.glAttachShader(programID, fragmentID);
            GLES20.glLinkProgram(programID);

            IntBuffer successBuffer = stack.callocInt(1);
            GLES20.glGetProgramiv(programID, GLES20.GL_LINK_STATUS, successBuffer);
            if (successBuffer.get(0) == GLES20.GL_FALSE) {
                String infoLog = GLES20.glGetProgramInfoLog(programID);
                GLES20.glDeleteProgram(programID);
                throw new RenderException(infoLog);
            }
        }
        this.uniforms = new HashMap<>();
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrix = stack.callocFloat(4 * 4);
            value.get(matrix);
            GLES20.glUniformMatrix4fv(getUniformLocation(name), false, matrix);
        }
    }

    public void setUniform(String name, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vector = stack.callocFloat(3);
            value.get(vector);
            GLES20.glUniform3fv(getUniformLocation(name), vector);
        }
    }

    public void setUniform(String name, int value) {
        GLES20.glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform(String name, float value) {
        GLES20.glUniform1f(getUniformLocation(name), value);
    }

    public int getUniformLocation(String name) {
        if (uniforms.containsKey(name)) return uniforms.get(name);
        uniforms.put(name, GLES20.glGetUniformLocation(programID, name));
        return uniforms.get(name);
    }

    public void activate() {
        GLES20.glUseProgram(programID);
    }

    public void deactivate() {
        GLES20.glUseProgram(0);
    }

    private int createShader(int shaderType, String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int shaderID = GLES20.glCreateShader(shaderType);
            GLES20.glShaderSource(shaderID, Files.readString(Path.of(path)));
            GLES20.glCompileShader(shaderID);

            IntBuffer successBuffer = stack.callocInt(1);
            GLES20.glGetShaderiv(shaderID, GLES20.GL_COMPILE_STATUS, successBuffer);
            if (successBuffer.get(0) == GLES20.GL_FALSE) {
                String infoLog = GLES20.glGetShaderInfoLog(shaderID);
                GLES20.glDeleteShader(shaderID);
                throw new RenderException(infoLog);
            }
            return shaderID;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getProgramID() {
        return programID;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    public HashMap<String, Integer> getUniforms() {
        return uniforms;
    }

    public void setUniforms(HashMap<String, Integer> uniforms) {
        this.uniforms = uniforms;
    }
}
