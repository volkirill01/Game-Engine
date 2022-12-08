package engine.toolbox;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager { // TODO CHANGE THIS FIELDS IN CUSTOM INPUT MANAGER WINDOW
    public static Shortcut[] SHORTCUTS = {
            new Shortcut("cameraPan", GLFW_MOUSE_BUTTON_MIDDLE, "Middle Mouse Button"),
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
