package engine.toolbox.input;

import java.util.Objects;

public class InputManager { // TODO CHANGE THIS FIELDS IN CUSTOM INPUT MANAGER WINDOW
    public static Shortcut[] SHORTCUTS = {
            new Shortcut("copy", KeyCode.Left_Control, KeyCode.C, "Ctrl+C"),
            new Shortcut("paste", KeyCode.Left_Control, KeyCode.V, "Ctrl+V"),
            new Shortcut("duplicate", KeyCode.Left_Control, KeyCode.D, "Ctrl+D"),
            new Shortcut("delete", KeyCode.Delete, "Delete"),

            new Shortcut("cameraRotate", KeyCode.Mouse_Button_Right, "Right Mouse Button"),
            new Shortcut("cameraMove(front)", KeyCode.W, "W"),
            new Shortcut("cameraMove(back)", KeyCode.S, "S"),
            new Shortcut("cameraMove(left)", KeyCode.A, "A"),
            new Shortcut("cameraMove(right)", KeyCode.D, "D"),
            new Shortcut("cameraMove(up)", KeyCode.Space, "Space"),
            new Shortcut("cameraMove(down)", KeyCode.Left_Shift, "(L)Shift"),

            new Shortcut("saveScene", KeyCode.Left_Control, KeyCode.S, "Ctrl+S"),
            new Shortcut("openScene", KeyCode.Left_Control, KeyCode.O, "Ctrl+O"),

            new Shortcut("translate", KeyCode.G, "G"),
            new Shortcut("rotate", KeyCode.R, "R"),
            new Shortcut("scale", KeyCode.T, "T"),
            new Shortcut("select", KeyCode.V, "V")
    };

    public static Shortcut getShortcut(String shortcutName) {
        for (Shortcut shortcut : SHORTCUTS) {
            if (Objects.equals(shortcut.shortcutName, shortcutName))
                return shortcut;
        }
        return null;
    }
}
