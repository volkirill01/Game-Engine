package engine.imGui;

import engine.TestFieldsWindow;
import engine.entities.GameObject;
import engine.renderEngine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
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

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        globalGameObjectIndex = 0;
        for (GameObject gObject : gameObjects) {
            if (!gObject.doSerialization() || gObject.transform.parent != null)
                continue;

            ImGui.setCursorPosX(ImGui.getCursorPosX() - 15.0f);

            gObject.transform.drawInSceneHierarchy(0);
            globalGameObjectIndex++;
        }
        ImGui.popStyleVar();

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
            ImGui.pushStyleColor(0,
                    ImGui.getStyle().getColor(50).x,
                    ImGui.getStyle().getColor(50).y,
                    ImGui.getStyle().getColor(50).z,
                    255);

            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 30);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 45);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 45);
        }
        ImVec2 selectablePos = ImGui.getCursorPos();
        ImGui.setCursorPosX(startX);
        ImGui.selectable("##background" + obj.getUid(), true, 0, ImGui.getContentRegionAvailX(), 27.0f);
        ImGui.setItemAllowOverlap();
        ImGui.popStyleColor(3);

        ImGui.setCursorPos((selectablePos.x * 0.856f) + 20.0f, selectablePos.y);
        if (obj.transform.childs.size() == 0 && obj.transform.parent != null) {
            ImGui.setCursorPosX(ImGui.getCursorPosX() + 5.4f);
            level++;
        }
        if (obj.transform.parent == null && obj.transform.childs.size() == 0)
            ImGui.setCursorPosX(ImGui.getCursorPosX() - 19.2f);
        else
            ImGui.setCursorPosX(ImGui.getCursorPosX() - 4.8f);

        ImGui.pushStyleColor(ImGuiCol.Header, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, ImGui.getStyle().getFramePaddingY());

        ImGui.pushID(obj.getUid());
        boolean treeNodeOpen = ImGui.treeNodeEx(
                obj.name, // (obj == Window.getImGuiLayer().getInspectorWindow().getActiveGameObject() ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.FramePadding) |
                        ImGuiTreeNodeFlags.Selected |
                        (obj.transform.getChildCount() > 0 ? ImGuiTreeNodeFlags.OpenOnArrow : ImGuiTreeNodeFlags.Leaf) |
                        ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth,
                "\uEEF7 " + obj.name
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
//                    System.out.println("OnDragEnd objectName1:'" + playerGameObj.name + "'");
//                    System.out.println("OnDragEnd objectName2:'" + obj.name + "'");
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
