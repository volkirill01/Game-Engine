package engine.toolbox;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager { // TODO CHANGE THIS FIELDS IN CUSTOM INPUT MANAGER WINDOW
    public static Shortcut[] SHORTCUTS = {
            new Shortcut("cameraRotate", GLFW_MOUSE_BUTTON_RIGHT, "Right Mouse Button"),
            new Shortcut("cameraMove(front)", GLFW_KEY_W, "W"),
            new Shortcut("cameraMove(back)", GLFW_KEY_S, "S"),
            new Shortcut("cameraMove(left)", GLFW_KEY_A, "A"),
            new Shortcut("cameraMove(right)", GLFW_KEY_D, "D"),
            new Shortcut("cameraMove(up)", GLFW_KEY_SPACE, "Space"),
            new Shortcut("cameraMove(down)", GLFW_KEY_LEFT_SHIFT, "(L)Shift"),

            new Shortcut("saveScene", GLFW_KEY_LEFT_CONTROL, GLFW_KEY_S, "Ctrl+S"),
            new Shortcut("loadScene", GLFW_KEY_LEFT_CONTROL, GLFW_KEY_O, "Ctrl+O")
    };

    public static Shortcut getShortcut(String shortcutName) {
        for (Shortcut shortcut : SHORTCUTS) {
            if (Objects.equals(shortcut.shortcutName, shortcutName))
                return shortcut;
        }
        return null;
    }
}
