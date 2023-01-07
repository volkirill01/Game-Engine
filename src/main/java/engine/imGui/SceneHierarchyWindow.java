package engine.imGui;

import engine.entities.GameObject;
import engine.components.Light;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.guis.UIImage;
import engine.renderEngine.particles.ParticleSystem;
import engine.toolbox.GameObject_Manager;
import engine.toolbox.input.InputManager;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow extends EditorImGuiWindow {

    private static String payloadDragDropType = "SceneHierarchy";

    public int globalGameObjectIndex = 0;

    @Override
    public void imgui() {
        ImGui.begin(" \uEF74 Hierarchy ");

        List<GameObject> gameObjects = Window.get().getScene().getGameObjects();

        ImVec2 itemSpacing = ImGui.getStyle().getItemSpacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        globalGameObjectIndex = 0;
        ImVec4 textDisabled = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
        for (GameObject gObject : gameObjects) {
            if (!gObject.doSerialization() || gObject.transform.parent != null)
                continue;

            ImGui.setCursorPosX(ImGui.getCursorPosX() - 15.0f);

            gObject.transform.drawInSceneHierarchy(0);
            globalGameObjectIndex++;

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemSpacing.x, itemSpacing.y);
            if (ImGui.beginPopupContextItem("SceneHierarchy_Item" + gObject)) { // TODO ADD CONTEXT AND ITEM POPUPS TO HIERARCHY
                if (ImGui.menuItem("Copy", InputManager.getShortcut("copy").shortcutDisplayKeys))
                    GameObject_Manager.copyGameObject(gObject);

                if (ImGui.menuItem("Paste", InputManager.getShortcut("paste").shortcutDisplayKeys))
                    GameObject_Manager.pasteGameObject();
                ImGui.separator();

                ImGui.pushStyleColor(ImGuiCol.Text, textDisabled.x, textDisabled.y, textDisabled.z, textDisabled.w);
                if (ImGui.menuItem("Rename")) {
                    System.out.println("Rename Game Object");
                }
                ImGui.popStyleColor();
                if (ImGui.menuItem("Duplicate", InputManager.getShortcut("duplicate").shortcutDisplayKeys))
                    GameObject_Manager.duplicateGameObject(gObject);

                if (ImGui.menuItem("Delete", InputManager.getShortcut("delete").shortcutDisplayKeys))
                    GameObject_Manager.deleteGameObject(gObject);

                ImGui.endPopup();
            }
            ImGui.popStyleVar();
        }
        ImGui.popStyleVar();

        if (ImGui.beginPopupContextWindow("SceneHierarchy_Context", ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Paste", InputManager.getShortcut("paste").shortcutDisplayKeys))
                GameObject_Manager.pasteGameObject();
            ImGui.separator();

            if (ImGui.menuItem("Create Empty"))
                GameObject_Manager.createEmpty();

            ImGui.endPopup();
        }

        super.imgui();
        ImGui.end();
    }

    public static boolean doTreeNode(GameObject obj, float startX, int level) {
        if (obj == null) {
//            System.out.println("Error");
            return false;
        }
        boolean isEven = Window.get().getImGuiLayer().getSceneHierarchy().globalGameObjectIndex % 2 != 0;

         if (isEven) {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 0);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 11);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 11);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 5);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 15);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 15);
        }

        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() != null && Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().equals(obj)) {
            ImGui.popStyleColor(3);
            ImGui.pushStyleColor(ImGuiCol.Text,
                    ImGui.getStyle().getColor(ImGuiCol.DragDropTarget).x,
                    ImGui.getStyle().getColor(ImGuiCol.DragDropTarget).y,
                    ImGui.getStyle().getColor(ImGuiCol.DragDropTarget).z,
                    1);

            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 30);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 45);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 45);
        }
        ImVec2 selectablePos = ImGui.getCursorPos();
        ImGui.setCursorPosX(startX);
        ImGui.selectable("##background" + obj.getUid(), true, 0, ImGui.getContentRegionAvailX(), 27.0f);
        ImGui.setItemAllowOverlap();
        ImGui.popStyleColor(3);

        ImGui.setCursorPos(startX + 3.0f, selectablePos.y + 3.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() - 3.0f, ImGui.getStyle().getFramePaddingY() - 5.0f);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);

        ImGui.pushID(obj.getUid());
        int eyeIcon = obj.isVisible() ?
                Loader.get().loadTexture("engineFiles/images/utils/icon=eye-solid(32x32).png").getTextureID() :
                Loader.get().loadTexture("engineFiles/images/utils/icon=eye-slash-solid(32x32).png").getTextureID();
        ImVec4 eyeIconColor = obj.isVisible() ? ImGui.getStyle().getColor(ImGuiCol.Text) : ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
        if (ImGui.imageButton(eyeIcon, 13.0f, 13.0f, 0, 1, 1, 0, 4, 0, 0, 0, 0, eyeIconColor.x, eyeIconColor.y, eyeIconColor.z, eyeIconColor.w))
            obj.setVisible(!obj.isVisible());
        ImGui.sameLine();

        int cursorIcon = obj.isPickable() ?
                Loader.get().loadTexture("engineFiles/images/utils/icon=arrow-pointer-slash-solid(32x32).png").getTextureID() :
                Loader.get().loadTexture("engineFiles/images/utils/icon=arrow-pointer-solid(32x32).png").getTextureID();
        ImVec4 cursorIconColor = !obj.isPickable() ? ImGui.getStyle().getColor(ImGuiCol.Text) : ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
        if (ImGui.imageButton(cursorIcon, 13.0f, 13.0f, 0, 1, 1, 0, 4, 0, 0, 0, 0, cursorIconColor.x, cursorIconColor.y, cursorIconColor.z, cursorIconColor.w))
            obj.setPickable(!obj.isPickable());
        ImGui.popStyleColor(3);
        ImGui.popStyleVar();

        ImVec2 treeNodePos = new ImVec2(selectablePos.x + 70.0f, selectablePos.y);
        if (obj.transform.parent != null)
            treeNodePos.x -= 14.0f;

        ImGui.pushStyleColor(ImGuiCol.Header, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, ImGui.getStyle().getFramePaddingY());

        String treeNodeIcon = "\uF017"; // Transparent icon

//        "\uEC94" // Sound icon
//        "\uEECF" // Camera icon

        if (obj.hasComponent(MeshRenderer.class))
            treeNodeIcon = "\uEEF7"; // Cube icon
        else if (obj.hasComponent(Light.class))
            treeNodeIcon = "\uEF6B";  // Light icon
        else if (obj.hasComponent(ParticleSystem.class))
            treeNodeIcon = "\uEFBE"; // ParticleSystem icon
        else if (obj.hasComponent(UIImage.class))
            treeNodeIcon = "\uEF5D"; // UI icon

        ImGui.setCursorPos(selectablePos.x + 85.0f, selectablePos.y + 5.5f);
        if (obj.transform.parent != null)
            ImGui.setCursorPosX(ImGui.getCursorPosX() - 12.0f);
        ImGui.text(treeNodeIcon);

        ImGui.setCursorPos(treeNodePos.x - 1.0f, treeNodePos.y);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                obj.name, // (obj == Window.getImGuiLayer().getInspectorWindow().getActiveGameObject() ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.FramePadding) |
                        ImGuiTreeNodeFlags.Selected |
                        (obj.transform.getChildCount() > 0 ? ImGuiTreeNodeFlags.OpenOnArrow : ImGuiTreeNodeFlags.Leaf) |
                        ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth,
                "\t" + obj.name
        );
        ImGui.popStyleColor(3);
        ImGui.popStyleVar();
        ImGui.popID();
        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() != null)
            if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().equals(obj))
                ImGui.popStyleColor(1);

        if (ImGui.isItemClicked())
            Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(obj);

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDragDropType, obj); // Tooltip
            ImGui.text(obj.name); // Some thin in tooltip(text, image)
//            System.out.println("OnDrag objectName:'" + obj.name + "'");
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            if (ImGui.acceptDragDropPayload(payloadDragDropType) == null) {
                ImGui.endDragDropTarget();
                return treeNodeOpen;
            }
            Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
            if (payloadObj != null) {
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject playerGameObj = (GameObject)payloadObj;
                    obj.transform.addChild(playerGameObj);
                }
            }
            ImGui.endDragDropTarget();
        }

//        float endY = ImGui.getCursorPosY();
//        ImGui.setCursorPos(startX + 4.0f, ImGui.getCursorPosY() - 22.0f);
//        if (obj.transform.parent != null)
//            ImGui.textDisabled("---".repeat(level + 1));
//        ImGui.setCursorPosY(endY);
//
//        ImGui.setCursorPos(startX, ImGui.getCursorPosY() - 56.5f);
//        ImGui.textDisabled("|");
//        ImGui.setCursorPos(startX, ImGui.getCursorPosY() + 21.4f);
//        ImGui.textDisabled("|");
//        ImGui.setCursorPosY(endY);

        return treeNodeOpen;
    }
}
