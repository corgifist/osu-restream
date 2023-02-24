package com.osurestream.engine;

import com.osurestream.engine.primitives.TextPrimitive;
import com.osurestream.engine.render.Display;
import com.osurestream.engine.render.RenderBatch;
import com.osurestream.engine.render.ShaderProgram;
import com.osurestream.engine.util.Rectangle;
import com.osurestream.engine.util.SceneGraph;
import org.joml.Vector4f;
import org.lwjgl.opengles.GLES20;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class AppBase {

    public static ShaderProgram SHADER_PROGRAM;
    public static Random RANDOM = new Random();

    public SceneGraph scene;

    public RenderBatch batch;

    public AppBase() {
        this.scene = new SceneGraph();
        this.batch = new RenderBatch();
    }

    public abstract void create();

    public abstract void update();

    public void abstractUpdate() {
        TextPrimitive.clearCaches();
        if (Display.instance.keyState(GLFW_KEY_ESCAPE)) {
            Display.instance.terminate();
            System.exit(0);
        }
        Display.instance.update();
        GameClock.previousTime = (float) glfwGetTime();
        GameClock.frameCount++;
        scene.update();
    }

    public void abstractRender() {
        scene.render(batch);
    }

    public void abstractEnd() {
        GameClock.currentTime = (float) glfwGetTime();
        GameClock.deltaTime = (GameClock.currentTime - GameClock.previousTime) * 30;
        GameClock.clock += GameClock.deltaTime * GameClock.deltaStep;
    }
    public void clear(Vector4f color) {
        GLES20.glClearColor(color.x, color.y, color.z, color.w);
    }

    public void setShaderProgram(ShaderProgram program) {
        AppBase.SHADER_PROGRAM = program;
        program.activate();
    }

    public static void run(AppBase base) {
        base.create();
        while (!Display.instance.isCloseRequested()) {
            RenderBatch.DRAW_CALL_COUNT = 0;
            base.abstractUpdate();
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            base.update();
            base.abstractRender();
            base.abstractEnd();
        }
    }

}
