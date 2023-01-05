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

        Shortcut saveSceneShortcut = InputManager.getShortcut("saveScene");
        Shortcut openSceneShortcut = InputManager.getShortcut("openScene");

        if (KeyListener.isKeyDown(saveSceneShortcut.firstKeyCode) && KeyListener.isKeyClick(saveSceneShortcut.secondKeyCode))
            EventSystem.notify(null, new Event(EventType.SaveLevel));

        if (KeyListener.isKeyDown(openSceneShortcut.firstKeyCode) && KeyListener.isKeyClick(openSceneShortcut.secondKeyCode))
            EventSystem.notify(null, new Event(EventType.LoadLevel));

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save Scene", saveSceneShortcut.shortcutDisplayKeys))
                EventSystem.notify(null, new Event(EventType.SaveLevel));

            if (ImGui.menuItem("Open Scene", openSceneShortcut.shortcutDisplayKeys))
                EventSystem.notify(null, new Event(EventType.LoadLevel));

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem("Test1", saveSceneShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", openSceneShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Assets")) {
            if (ImGui.menuItem("Test1", saveSceneShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", openSceneShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Window")) {
            if (ImGui.menuItem("Test1", saveSceneShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", openSceneShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Help")) {
            if (ImGui.menuItem("Test1", saveSceneShortcut.shortcutDisplayKeys)) {
            }

            if (ImGui.menuItem("Test2", openSceneShortcut.shortcutDisplayKeys)) {
            }

            ImGui.endMenu();
        }

        ImGui.popStyleVar();
        ImGui.endMenuBar();
    }
}
