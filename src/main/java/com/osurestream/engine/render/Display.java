package com.osurestream.engine.render;

import com.osurestream.engine.util.RenderException;
import org.lwjgl.egl.EGL;
import org.lwjgl.egl.EGLCapabilities;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLESCapabilities;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.egl.EGL10.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWNativeEGL.glfwGetEGLDisplay;
import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Display {

    public static int VIRTUAL_WIDTH = 1280;
    public static int VIRTUAL_HEIGHT = 720;

    private String title;
    private int width, height;
    private boolean resizable;

    private long window, dpy;

    public static Display instance;
    public static EGLCapabilities egl;
    public static GLESCapabilities gles;

    private Display(String title, int width, int height, boolean resizable) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizable = resizable;

        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new RenderException("cannot initialize glfw to create window");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_EGL_CONTEXT_API);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);

        Configuration.OPENGLES_LIBRARY_NAME.set("libGLESv2.dll");
        this.window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RenderException("cannot create glfw window!");
        }

        this.dpy = glfwGetEGLDisplay();

        try (MemoryStack stack = stackPush()) {
            IntBuffer major = stack.mallocInt(1);
            IntBuffer minor = stack.mallocInt(1);

            if (!eglInitialize(dpy, major, minor)) {
                throw new RenderException(String.format("cannot create egl capabilities [0x%X]", eglGetError()));
            }

            Display.egl = EGL.createDisplayCapabilities(dpy, major.get(0), minor.get(0));
        }

        try {
            System.out.println("EGL Capabilities:");
            for (Field f : EGLCapabilities.class.getFields()) {
                if (f.getType() == boolean.class) {
                    if (f.get(egl).equals(Boolean.TRUE)) {
                        System.out.println("\t" + f.getName());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        glfwMakeContextCurrent(window);
        Display.gles = GLES.createCapabilities();
        glfwSwapInterval(1);
        try {
            System.out.println("OpenGL ES Capabilities:");
            for (Field f : GLESCapabilities.class.getFields()) {
                if (f.getType() == boolean.class) {
                    if (f.get(gles).equals(Boolean.TRUE)) {
                        System.out.println("\t" + f.getName());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        GLES20.glViewport(0, 0, width, height);
        System.out.println("GL_VENDOR: " + glGetString(GL_VENDOR));
        System.out.println("GL_VERSION: " + glGetString(GL_VERSION));
        System.out.println("GL_RENDERER: " + glGetString(GL_RENDERER));

        try (MemoryStack stack = stackPush()) {
            System.out.println("MAX_VERTEX_ATTRIBS: " + GLES20.glGetInteger(GL_MAX_VERTEX_ATTRIBS));
        }

        glfwShowWindow(window);
    }

    public boolean keyState(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public static void init(String title, int width, int height, boolean resizable) {
        Display.instance = new Display(title, width, height, resizable);
    }


    public void update() {
        if (glfwWindowShouldClose(window)) {
            terminate();
        }

        glfwPollEvents();
        glfwSwapBuffers(window);
    }

    public void terminate() {
        glfwDestroyWindow(window);
        glfwFreeCallbacks(window);
        eglTerminate(dpy);
        glfwTerminate();
    }

    public boolean isCloseRequested() {
        return glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        try (MemoryStack stack = stackPush()) {
            this.title = title;
            ByteBuffer titleBuffer = stack.UTF8(title);
            glfwSetWindowTitle(window, titleBuffer);
        }
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

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public static Display getInstance() {
        return instance;
    }

    public static void setInstance(Display instance) {
        Display.instance = instance;
    }
}
