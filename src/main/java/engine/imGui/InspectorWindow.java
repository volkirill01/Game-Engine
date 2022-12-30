package engine.imGui;

import engine.TestFieldsWindow;
import engine.assets.Asset;
import engine.entities.GameObject;
import engine.renderEngine.PickingTexture;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector4f;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class InspectorWindow extends EditorImGuiWindow {

    private Asset activeAsset;

    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeGameObjectsOgColor;
    private GameObject activeGameObject;
    private PickingTexture pickingTexture;

    private GameObject copyBuffer;

    private boolean isLocked = false;

    public InspectorWindow(PickingTexture pickingTexture) {
        this.activeGameObject = null;
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectsOgColor = new ArrayList<>();
    }

    @Override
    public void imgui() {
        if (activeAsset != null) {
            ImGui.begin(" \uEF4E Inspector ");
            activeAsset.mainImgui();

            activeAsset.imgui();
        } else if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            ImGui.begin(" \uEF4E Inspector ", ImGuiWindowFlags.MenuBar);

            activeGameObject = activeGameObjects.get(0);
            activeGameObject.imgui();
        } else {
            ImGui.begin(" \uEF4E Inspector ");

            ImGui.setCursorPosY(ImGui.getCursorPosY() + 20.0f);
            EditorImGui.horizontalCenteredText("Object not selected");
        }

        super.imgui();
        ImGui.end();
    }

    public void drawIsLockedButton(float xOffset) {
        ImVec2 buttonPos = new ImVec2(ImGui.getWindowContentRegionMaxX() - 21.0f + xOffset, ImGui.getCursorPosY());

        ImVec2 startPos = ImGui.getCursorPos();

        ImGui.setCursorPos(buttonPos.x, buttonPos.y);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() - 1.0f,  ImGui.getStyle().getFramePaddingY() - 3.0f);
        isLocked = EditorImGui.toggledButton(isLocked ? "\uF01A" : "\uF01B", isLocked);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text(!isLocked ? "Lock`s the Inspector\nNot allow to change Selection" : "Unlock the Inspector\nAllow to change Selection");
            ImGui.endTooltip();
        }
        ImGui.popStyleVar();

        ImGui.setCursorPos(startPos.x, startPos.y);
    }

    public void setActiveAsset(Asset asset) {
        if (!isLocked)
            this.activeAsset = asset;
    }

    public Asset getActiveAsset() { return this.activeAsset; }

    public GameObject getActiveGameObject() { return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null; }

    public void clearSelected() {
        if (!isLocked) {
            if (activeGameObjectsOgColor.size() > 0) {
                int i = 0;
                for (GameObject go : activeGameObjects) {
//                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
//                if (spr != null)
//                    spr.setColor(activeGameObjectsOgColor.get(i));
                    i++;
                }
            }
            this.activeGameObjects.clear();
            this.activeGameObjectsOgColor.clear();
        }
    }

    public List<GameObject> getActiveGameObjects() { return this.activeGameObjects; }

    public void setActiveGameObject(GameObject go) {
        if (!isLocked) {
            if (go != null) {
                clearSelected();
                this.activeGameObjects.add(go);
                this.activeAsset = null;
            }
        }
    }

    public void addActiveGameObject(GameObject go) {
//        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
//        if (spr != null) {
//            this.activeGameObjectsOgColor.add(new Vector4f(spr.getColor()));
//            spr.setColor(Settings.SELECTED_OBJECT_COLOR);
//        } else
        this.activeGameObjectsOgColor.add(new Vector4f());

        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() { return this.pickingTexture; }

    public boolean isLocked() { return this.isLocked; }

    public void setLocked(boolean locked) { this.isLocked = locked; }

    public GameObject getCopyBuffer() { return this.copyBuffer; }

    public void setCopyBuffer(GameObject copyBuffer) { this.copyBuffer = copyBuffer; }
}
