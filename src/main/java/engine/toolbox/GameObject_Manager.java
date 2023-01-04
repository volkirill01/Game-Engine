package engine.toolbox;

import engine.entities.GameObject;
import engine.imGui.ConsoleMessage;
import engine.renderEngine.Window;

public class GameObject_Manager {

    public static void copyGameObject(GameObject original) {
        if (original == null)
            return;

        Window.get().getImGuiLayer().getInspectorWindow().setCopyBuffer(original.copy());
        Window.get().getImGuiLayer().showModalPopup("Copy", ConsoleMessage.MessageType.Simple);
    }

    public static void pasteGameObject() {
        if (Window.get().getImGuiLayer().getInspectorWindow().getCopyBuffer() == null)
            return;

        GameObject copy = Window.get().getImGuiLayer().getInspectorWindow().getCopyBuffer().copy();
        Window.get().getImGuiLayer().getInspectorWindow().setCopyBuffer(copy);
        copy.name = addCopySuffix(copy.name);

        Window.get().getScene().addGameObjectToScene(copy);
        Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(copy);
        Window.get().getImGuiLayer().showModalPopup("Paste", ConsoleMessage.MessageType.Simple);
    }

    public static void duplicateGameObject(GameObject original) {
        if (original == null)
            return;

        GameObject copy = original.copy();
        copy.name = addCopySuffix(copy.name);

        Window.get().getScene().addGameObjectToScene(copy);
        Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(copy);
        Window.get().getImGuiLayer().showModalPopup("Duplicate", ConsoleMessage.MessageType.Simple);
    }

    private static String addCopySuffix(String name) {
        if (name.contains("(copy")) {
            try {
                int copyIndex = Integer.parseInt(name.substring(name.length() - 2, name.length() - 1));
                copyIndex++;
                name = name.substring(0, name.length() - 2) + copyIndex + ")";
            } catch (NumberFormatException e) {
                name = name.substring(0, name.length() - 1) + " 0)";
            }
        } else
            name += " (copy)";

        return name;
    }

    public static void deleteGameObject(GameObject gameObject) {
        if (gameObject == null)
            return;

        gameObject.destroy();
        Window.get().getImGuiLayer().showModalPopup("Delete", ConsoleMessage.MessageType.Simple);
    }

    public static void createEmpty() { createEmpty("Empty"); }

    public static void createEmpty(String name) {
        GameObject go = Window.get().getScene().createGameObject(name);
        Window.get().getScene().addGameObjectToScene(go);
        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(null);
        Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(go);
    }
}
