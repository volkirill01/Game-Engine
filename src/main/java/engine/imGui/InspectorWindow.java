package engine.imGui;

import engine.TestFieldsWindow;
import engine.assets.Asset;
import engine.entities.GameObject;
import engine.renderEngine.PickingTexture;
import engine.renderEngine.Window;
import engine.toolbox.customVariables.GameObjectTag;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.joml.Vector3f;
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

    public static boolean showAddTag = false;
    private transient Vector3f newTagColor = new Vector3f(216 / 255.0f, 162 / 255.0f, 77 / 255.0f);
    private final transient String newTagNameRef = "New tag";
    private transient String newTagName = newTagNameRef;

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
            drawTags();
            activeGameObject.imgui();
        } else {
            ImGui.begin(" \uEF4E Inspector ");

            ImGui.setCursorPosY(ImGui.getCursorPosY() + 20.0f);
            EditorImGui.horizontalCenteredText("Object not selected");
        }

        super.imgui();
        ImGui.end();
    }

    private void drawTags() {
        ImGui.beginMenuBar();
        ImGui.text("Tags");
        ImGui.sameLine();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        ImGui.text("\uF005");
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);

        List<GameObjectTag> tags = activeGameObject.getAllTags();

        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).tag.startsWith("##"))
                continue;

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, -4.0f + 2.5f);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 6.0f);

            ImVec2 tmp = new ImVec2();
            ImGui.calcTextSize(tmp, tags.get(i).tag);
            if (tags.get(i).tag.equals(""))
                ImGui.calcTextSize(tmp, "Tag");

            ImVec4 color = new ImVec4(tags.get(i).color.x / 255.0f, tags.get(i).color.y / 255.0f, tags.get(i).color.z / 255.0f, 1.0f);
            ImVec4 bgColor = new ImVec4((tags.get(i).color.x - 130) / 255.0f, (tags.get(i).color.y - 90) / 255.0f, (tags.get(i).color.z - 20) / 255.0f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.Button, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color.x + 0.07f, color.y + 0.07f, color.z + 0.07f, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, bgColor.x, bgColor.y, bgColor.z, bgColor.w);
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, color.w);

            ImGui.beginChildFrame(1919 + i, tmp.x + 35.0f, 26.0f, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

            ImGui.setCursorPosY(ImGui.getCursorPosY() + 2.8f);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 0);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 8.0f, 3.0f);
            ImGui.setNextItemWidth(tmp.x + 14.0f);
            tags.get(i).tag = drawTagInput("tag" + i, tags.get(i).tag);
            ImGui.popStyleVar();
            ImGui.popStyleColor();

            ImGui.sameLine();
            ImGui.setCursorPos(ImGui.getCursorPosX() - 5.0f + 2.1f, ImGui.getCursorPosY() + 6.1f - 1.4f);
            if (tags.get(i).tag.equals(""))
                ImGui.setCursorPos(ImGui.getCursorPosX() + 10.0f - 5.5f, ImGui.getCursorPosY());

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.4f - 0.3f, -2.4f + 0.6f);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
            if (ImGui.button("##deleteTag" + i)) {
                tags.remove(tags.get(i));
                i--;
            }
            ImGui.sameLine();
            ImGui.pushStyleColor(ImGuiCol.Text, bgColor.x, bgColor.y, bgColor.z, bgColor.w);
            ImGui.setCursorPos(ImGui.getCursorPosX() - 19.4f + 2.8f, ImGui.getCursorPosY() - 5.92f + 1.0f);
            ImGui.text("\uEEE4");
            ImGui.popStyleVar(2);

            ImGui.endChildFrame();
            ImGui.popStyleColor(7);
            ImGui.popStyleVar(2);
        }

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() - 2.0f);

        if (tags.size() > 0)
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
//            ImGui.setCursorPosY(ImGui.getCursorPosY() + TestFieldsWindow.getFloats[0]);

        if (ImGui.button("\uEFC2"))
            showAddTag = true;

        ImGui.popStyleVar();

        Window.get().getImGuiLayer().getInspectorWindow().drawIsLockedButton(0.0f);

        ImGui.endMenuBar();

        showCreateTagPopup();

        if (showAddTag)
            ImGui.openPopup("\t\t\t\t\t\t\t\t\t\t \uF005 Add new Tag");
    }

    private void showCreateTagPopup() {
        ImGui.setNextWindowSize(438.0f, 550.0f);
        ImGui.setNextWindowPos(Window.get().windowSize.x / 2.0f - ImGui.getWindowSizeX() / 2.0f + Window.get().windowPosition.x,
                Window.get().windowSize.y / 2.0f - ImGui.getWindowSizeY() / 3.0f + Window.get().windowPosition.y);

        if (showAddTag && ImGui.beginPopupModal("\t\t\t\t\t\t\t\t\t\t \uF005 Add new Tag", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImVec2 tmp = new ImVec2();
            ImGui.calcTextSize(tmp, "Choose tag color");
            ImGui.setCursorPosX((ImGui.getContentRegionAvailX() / 2.0f) - (tmp.x / 2.0f));
            ImGui.text("Choose tag color");

            float[] imColor = {newTagColor.x, newTagColor.y, newTagColor.z};
            if (ImGui.colorPicker3("##tagColor", imColor))
                newTagColor.set(imColor[0], imColor[1], imColor[2]);

            ImGui.separator();
            ImGui.spacing();

            tmp = new ImVec2();
            ImGui.calcTextSize(tmp, "Enter new tag");
            ImGui.setCursorPos((ImGui.getContentRegionAvailX() / 2.0f) - (tmp.x / 2.0f), ImGui.getCursorPosY() + 5.0f);
            ImGui.text("Enter new tag");

            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, ImGui.getStyle().getFramePaddingY());
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 5.0f);
            newTagName = drawTagInput("newTagInput", newTagName);
            ImGui.popStyleVar();

            ImGui.setCursorPos(ImGui.getContentRegionAvailX() / 6.0f, ImGui.getCursorPosY() + 10.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getContentRegionAvailX() / 6.0f, ImGui.getStyle().getFramePaddingY());
            if (ImGui.button("Close")) {
                newTagName = newTagNameRef;
                showAddTag = false;
            }

            ImGui.sameLine();
            if (ImGui.button("Ok")) {
                activeGameObject.addTag(new GameObjectTag(newTagName, new Vector3f(
                        newTagColor.x * 255.0f,
                        newTagColor.y * 255.0f,
                        newTagColor.z * 255.0f)));
                newTagName = newTagNameRef;
                showAddTag = false;
            }
            ImGui.popStyleVar();
            ImGui.endPopup();
        }
    }

    private String drawTagInput(String label, String text) {
        ImGui.pushID(label);

        float start = ImGui.getCursorPosX();
        ImString outString = new ImString(text, 20);

        if (ImGui.inputText("##" + label, outString)) {
            ImGui.popID();
            return outString.get();
        }
        if (text.equals("")) {
            ImGui.sameLine();
            ImVec4 color = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, color.w);
            ImGui.setCursorPosX(start + 10.0f);
            ImGui.text("Tag");
            ImGui.popStyleColor();
        }
        ImGui.popID();
        if (text.equals(newTagNameRef))
            ImGui.setKeyboardFocusHere(-1);

        return text;
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
