package com.osurestream.engine.primitives;

import com.osurestream.engine.AppBase;
import com.osurestream.engine.primitives.Sprite;
import com.osurestream.engine.render.Display;
import com.osurestream.engine.util.Rectangle;
import com.osurestream.engine.util.SceneGraph;
import com.osurestream.engine.util.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.swing.*;
import java.io.DataInput;
import java.util.Vector;

// just to test sprite batching and overall rendering :)
public class LaserRadiationObject extends Sprite {

    private Vector2f direction;
    private float speed;

    private Rectangle screenRect;

    public LaserRadiationObject() {
        super(Texture.create("laser_radiation.png"));
        this.screenRect = new Rectangle(0, 0, 1280, 720);
        randomizeDirection();
        position.x = AppBase.RANDOM.nextFloat(0, 900);
        position.y = AppBase.RANDOM.nextFloat(0, 500);

        setColor(AppBase.RANDOM.nextFloat(0, 1), AppBase.RANDOM.nextFloat(0, 1), AppBase.RANDOM.nextFloat(0, 1), 1);
    }

    @Override
    public void update() {
        Rectangle laserRect = getRectangle();
        if (!Rectangle.contains(screenRect, laserRect)) {
            direction = getNormal();
        }

        position.x += speed * direction.x;
        position.y += speed * direction.y;
    }

    public void randomizeDirection() {
        float x = AppBase.RANDOM.nextFloat();
        int scalar = AppBase.RANDOM.nextInt(0, 2);
        this.direction = new Vector2f(clamp12(scalar == 1 ? -x : x));
        this.speed = 5;
    }

    public Vector2f getNormal() {
        return new Vector2f(direction).negate().perpendicular();
    }

    public static float clamp12(float x) {
        return Math.min(Math.max(x, -2), 2);
    }

}
