package engine.toolbox;

import engine.renderEngine.Window;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.KeyListener;

public class Keyboard_EditorActions {

    public void update() {
        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() != null) {
            if (KeyListener.isKeyDown(InputManager.getShortcut("delete").firstKeyCode))
                GameObject_Manager.deleteGameObject(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject());

            if (KeyListener.isKeyDown(InputManager.getShortcut("duplicate").firstKeyCode) && KeyListener.isKeyClick(InputManager.getShortcut("duplicate").secondKeyCode))
                GameObject_Manager.duplicateGameObject(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject());

            if (KeyListener.isKeyDown(InputManager.getShortcut("copy").firstKeyCode) && KeyListener.isKeyClick(InputManager.getShortcut("copy").secondKeyCode))
                GameObject_Manager.copyGameObject(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject());

            if (KeyListener.isKeyDown(InputManager.getShortcut("paste").firstKeyCode) && KeyListener.isKeyClick(InputManager.getShortcut("paste").secondKeyCode))
                GameObject_Manager.pasteGameObject();
        }
    }
}
