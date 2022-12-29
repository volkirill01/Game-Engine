package engine.imGui;

import engine.eventSystem.EventSystem;
import engine.eventSystem.Events.Event;
import engine.eventSystem.Events.EventType;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.KeyListener;
import engine.toolbox.input.Shortcut;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

public class MenuBar {

    public void imgui() {
        ImGui.beginMenuBar();
//        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 4.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 15f, ImGui.getStyle().getItemSpacingY());

        Shortcut saveShortcut = InputManager.getShortcut("saveScene");
        Shortcut loadShortcut = InputManager.getShortcut("loadScene");

        if (KeyListener.isKeyDown(saveShortcut.firstKeyCode) && KeyListener.isKeyClick(saveShortcut.secondKeyCode))
            EventSystem.notify(null, new Event(EventType.SaveLevel));

        if (KeyListener.isKeyDown(loadShortcut.firstKeyCode) && KeyListener.isKeyClick(loadShortcut.secondKeyCode))
            EventSystem.notify(null, new Event(EventType.LoadLevel));

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", saveShortcut.shortcutDisplayKeys))
                EventSystem.notify(null, new Event(EventType.SaveLevel));

            if (ImGui.menuItem("Load", loadShortcut.shortcutDisplayKeys))
                EventSystem.notify(null, new Event(EventType.LoadLevel));

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
