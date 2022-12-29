package engine.toolbox.input;

import java.util.Objects;

public class InputManager { // TODO CHANGE THIS FIELDS IN CUSTOM INPUT MANAGER WINDOW
    public static Shortcut[] SHORTCUTS = {
            new Shortcut("cameraRotate", KeyCode.Mouse_Button_Right, "Right Mouse Button"),
            new Shortcut("cameraMove(front)", KeyCode.Arrow_Up, "W"),
            new Shortcut("cameraMove(back)", KeyCode.Arrow_Down, "S"),
            new Shortcut("cameraMove(left)", KeyCode.Arrow_Left, "A"),
            new Shortcut("cameraMove(right)", KeyCode.Arrow_Right, "D"),
            new Shortcut("cameraMove(up)", KeyCode.Space, "Space"),
            new Shortcut("cameraMove(down)", KeyCode.Left_Shift, "(L)Shift"),

            new Shortcut("saveScene", KeyCode.Left_Control, KeyCode.S, "Ctrl+S"),
            new Shortcut("loadScene", KeyCode.Left_Control, KeyCode.O, "Ctrl+O"),

            new Shortcut("translate", KeyCode.G, "G"),
            new Shortcut("rotate", KeyCode.R, "R"),
            new Shortcut("scale", KeyCode.S, "S")
    };

    public static Shortcut getShortcut(String shortcutName) {
        for (Shortcut shortcut : SHORTCUTS) {
            if (Objects.equals(shortcut.shortcutName, shortcutName))
                return shortcut;
        }
        return null;
    }
}
