package engine.toolbox.input;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyDown[] = new boolean[350];
    private boolean keyClicked[] = new boolean[350];
    private boolean keyDoubleClicked[] = new boolean[350];

    private final float doubleClickTimer = 0.8f;
    private float currentDoubleClickTimer = 0.0f;

    public static void endFrame() {
        Arrays.fill(get().keyClicked, false);
        Arrays.fill(get().keyDoubleClicked, false);

        if (get().currentDoubleClickTimer > 0)
            get().currentDoubleClickTimer -= 0.1f;
    }

    private static KeyListener get() {
        if (KeyListener.instance == null)
            KeyListener.instance = new KeyListener();
        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyDown[key] = true;
            get().keyClicked[key] = true;

            if (get().currentDoubleClickTimer > 0)
                get().keyDoubleClicked[key] = true;

            get().currentDoubleClickTimer = get().doubleClickTimer;
        } else if (action == GLFW_RELEASE) {
            get().keyDown[key] = false;
            get().keyClicked[key] = false;
        }
    }

    public static boolean isKeyDown(int keyCode) { return get().keyDown[keyCode]; }

    public static boolean isKeyClick(int keyCode) { return get().keyClicked[keyCode]; }

    public static boolean isKeyDoubleClick(int keyCode) { return get().keyDoubleClicked[keyCode]; }

    public static boolean isAnyKeyPressed() { return isAnyKeyPressed(-1); }

    public static boolean isAnyKeyPressed(int ignoredKey) {
        for (int i = 0; i < get().keyDown.length; i++)
            if (get().keyDown[i] && i != ignoredKey)
                return true;

        return false;
    }

    public static boolean isAnyKeyPressed(List<Integer> ignoredKeys) {
        for (int i = 0; i < get().keyDown.length; i++)
            if (get().keyDown[i] && !ignoredKeys.contains(i))
                return true;

        return false;
    }
}
