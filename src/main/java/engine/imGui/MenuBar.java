package engine.imGui;

import engine.toolbox.InputManager;
import engine.toolbox.KeyListener;
import engine.toolbox.Shortcut;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

public class MenuBar {

    public void imgui() {
        ImGui.beginMenuBar();
//        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 4.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 15f, ImGui.getStyle().getItemSpacingY());

        Shortcut saveShortcut = InputManager.getShortcut("saveScene");
        Shortcut loadShortcut = InputManager.getShortcut("loadScene");

//        if (KeyListener.isKeyPressed(saveShortcut.firstKeyCode) && KeyListener.keyBeginPress(saveShortcut.secondKeyCode))
//            EventSystem.notify(null, new Event(EventType.SaveLevel));
//
//        if (KeyListener.isKeyPressed(loadShortcut.firstKeyCode) && KeyListener.keyBeginPress(loadShortcut.secondKeyCode))
//            EventSystem.notify(null, new Event(EventType.LoadLevel));

        if (ImGui.beginMenu("File")) {
//            if (ImGui.menuItem("Save", saveShortcut.shortcutDisplayKeys))
//                EventSystem.notify(null, new Event(EventType.SaveLevel));
//
//            if (ImGui.menuItem("Load", loadShortcut.shortcutDisplayKeys))
//                EventSystem.notify(null, new Event(EventType.LoadLevel));

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem("Test1", saveShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", loadShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Assets")) {
            if (ImGui.menuItem("Test1", saveShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", loadShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Window")) {
            if (ImGui.menuItem("Test1", saveShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", loadShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Help")) {
            if (ImGui.menuItem("Test1", saveShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", loadShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        ImGui.popStyleVar();
        ImGui.endMenuBar();
    }
}
