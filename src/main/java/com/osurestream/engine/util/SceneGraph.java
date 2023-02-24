package com.osurestream.engine.util;

import com.osurestream.engine.AppBase;
import com.osurestream.engine.primitives.Renderable;
import com.osurestream.engine.render.Camera;
import com.osurestream.engine.render.OrthographicCamera;
import com.osurestream.engine.render.RenderBatch;
import org.joml.Matrix4f;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class SceneGraph {

    public static int LAYERS_COUNT = 64; // performance hit zone
    private ArrayList<ArrayList<Renderable>> layers;
    public static Camera camera = new OrthographicCamera();

    public SceneGraph() {
        this.layers = new ArrayList<>();
        for (int i = 0; i < LAYERS_COUNT; i++) {
            layers.add(new ArrayList<>());
        }
    }

    public void render(RenderBatch batch) {
        for (ArrayList<Renderable> renderables : layers) {
            for (Renderable renderable : renderables) {
                batch.draw(renderable);
            }
        }
        batch.render();
    }

    public void update() {
        for (ArrayList<Renderable> renderables : layers) {
            for (Renderable renderable : renderables) {
                renderable.update();
            }
        }
    }

    public void add(Renderable renderable) {
        add(0, renderable);
    }

    public void add(int layer, Renderable renderable) {
        layers.get(layer).add(renderable);
    }

    public void clear() {
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).clear();
        }
    }
}
