package com.osurestream.game;

import com.osurestream.engine.AppBase;
import com.osurestream.engine.GameClock;
import com.osurestream.engine.primitives.LaserRadiationObject;
import com.osurestream.engine.primitives.Sprite;
import com.osurestream.engine.primitives.TextPrimitive;
import com.osurestream.engine.render.Display;
import com.osurestream.engine.render.ShaderProgram;
import com.osurestream.engine.util.Texture;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

public class AppDirector extends AppBase {

    public static Vector4f BLACK = new Vector4f(0, 0, 0, 1);
    public static String APPLICATION_NAME = "osu!restream";

    private TextPrimitive dialog;

    public static String[] dialogs = {
        "teamlead",
        "linus pipinus",
        "teamcorgi",
            "linus lead",
            "elisei system"
    };

    private int dialogPointer;

    private String dialogProgress;

    private Sprite corgiPrincess;

    public AppDirector() {
        super();
    }

    @Override
    public void create() {
        Display.init(APPLICATION_NAME, 1280, 720, false);
        setShaderProgram(new ShaderProgram("default.vert", "default.frag"));
        clear(BLACK);

        this.corgiPrincess = new Sprite(Texture.create("corgi_princess.jpg"));
        corgiPrincess.setColor(0.2f, 0.2f, 0.2f, 0.2f);
        corgiPrincess.center();

        this.dialogPointer = 0;
        this.dialog = new TextPrimitive("Futura-Medium.ttf", "", 48);
        dialog.position.x = 300;
        dialog.position.y = 400;
        this.dialogProgress = "";

        scene.add(corgiPrincess);
        scene.add(dialog);
    }

    @Override
    public void update() {
        String dialogTarget = dialogs[dialogPointer];

        if (Display.instance.keyState(GLFW_KEY_Z) && dialogProgress.length() >= dialogTarget.length()) {
            dialogPointer++;
            dialogProgress = "";
        }

        if (dialogProgress.length() < dialogTarget.length()) {
            dialogProgress = dialogTarget.substring(0, dialogProgress.length() + 1);
        }

        dialog.setText(dialogProgress);

    }
}
