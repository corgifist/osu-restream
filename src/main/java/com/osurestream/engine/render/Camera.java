package com.osurestream.engine.render;

import org.joml.Matrix4f;

public interface Camera {

    Matrix4f projection();
    Matrix4f view();
}
