package com.osurestream.engine.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrthographicCamera implements Camera {

    private Matrix4f projection, view;

    public Vector3f position, rotation;

    public OrthographicCamera() {
        this.projection = new Matrix4f().identity().ortho2D(-1, 1, -1, 1);
        this.view = new Matrix4f().identity();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
    }


    @Override
    public Matrix4f projection() {
        return projection;
    }

    @Override
    public Matrix4f view() {
        view.identity().translate(position)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z);
        return view;
    }
}
