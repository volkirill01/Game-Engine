package engine.imGui;

import engine.TestFieldsWindow;
import engine.assets.Asset;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.toolbox.SystemClipboard;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import org.apache.commons.io.FileUtils;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProjectStructureWindow extends EditorImGuiWindow {

    private static String payloadDragDropType = "AssetsStructure";
    public File mainDir;
    private float startX;
    private int a = 0;

    public void DrawFiles(File[] arr, int index, int level, ImVec2 itemSpacing) {
        // terminate condition
        if (index == arr.length)
            return;

        // for sub-directories
        if (arr[index].isDirectory()) {
            List<File> folders = new ArrayList<>(Arrays.stream(arr[index].listFiles()).toList());
            for (int i = 0; i < folders.size(); i++)
                if (folders.get(i).isFile()) {
                    folders.remove(i);
                    i--;
                }

            boolean isEmpty = folders.size() == 0;
            boolean isEven = a % 2 == 0;
            a++;
            Vector2f iconPos = new Vector2f(0.0f);
            boolean treeNodeOpen = doTreeNode(arr[index].getPath(), "", level, iconPos, isEmpty, false, isEven, itemSpacing);

            ImVec2 startCursorPos;

            if (treeNodeOpen) {
                // recursion for sub-directories
                if (arr[index].listFiles() != null)
                    DrawFiles(arr[index].listFiles(), 0, level + 1, itemSpacing);

                startCursorPos = ImGui.getCursorPos();
                ImGui.setCursorPos(iconPos.x, iconPos.y);
                ImGui.image(Loader.get().loadTexture("engineFiles/images/icons/icon=folder-open-regular-(256x256).png").getTextureID(), 16.0f, 16.0f, 0, 1, 1, 0);
                ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);

                ImGui.treePop();
            } else {
                startCursorPos = ImGui.getCursorPos();
                ImGui.setCursorPos(iconPos.x, iconPos.y);
                ImGui.image(Loader.get().loadTexture("engineFiles/images/icons/icon=folder-solid-(256x256).png").getTextureID(), 16.0f, 16.0f, 0, 1, 1, 0);
                ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);
            }
        }

        // recursion for main directory
        DrawFiles(arr, ++index, level, itemSpacing);
    }

    // Driver Method
    @Override
    public void imgui() {
        if (mainDir.exists() && mainDir.isDirectory()) {
            ImGui.begin(" \uEF36 Project Structure ");
            ImVec2 itemSpacing = ImGui.getStyle().getItemSpacing();
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

            // array for files and sub-directories
            // of directory pointed by maindir
            File[] arr = mainDir.listFiles();

            // Calling recursive method
            assert arr != null;

            List<File> folders = new ArrayList<>(Arrays.stream(arr).toList());
            for (int i = 0; i < folders.size(); i++)
                if (folders.get(i).isFile()) {
                    folders.remove(i);
                    i--;
                }

            boolean isEmpty = folders.size() == 0;
            startX = ImGui.getCursorStartPosX() - 0.1f + TestFieldsWindow.getFloats[2];
            ImGui.setCursorPosX(ImGui.getCursorPosX() + TestFieldsWindow.getFloats[3]);
            Vector2f iconPos = new Vector2f(0.0f);
            boolean treeNodeOpen = doTreeNode(Window.get().getImGuiLayer().getAssetsWindow().assetsDirectory, "", 0, iconPos, isEmpty, true, false, itemSpacing);

            ImVec2 startCursorPos = ImGui.getCursorPos();

            if (treeNodeOpen) {
                a = 0;
                DrawFiles(arr, 0, 1, itemSpacing);

                ImGui.setCursorPos(iconPos.x, iconPos.y);
                ImGui.image(Loader.get().loadTexture("engineFiles/images/icons/icon=folder-open-regular-(256x256).png").getTextureID(), 16.0f, 16.0f, 0, 1, 1, 0);
                ImGui.treePop();
            } else {
                ImGui.setCursorPos(iconPos.x, iconPos.y);
                ImGui.image(Loader.get().loadTexture("engineFiles/images/icons/icon=folder-solid-(256x256).png").getTextureID(), 16.0f, 16.0f, 0, 1, 1, 0);
            }

            ImGui.setCursorPos(startCursorPos.x, startCursorPos.y);
        }
        ImGui.popStyleVar();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
        if (ImGui.beginPopupContextWindow("Create Asset", ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Create New Folder"))
                Window.get().getImGuiLayer().getAssetsWindow().createNewFolder();
            if (ImGui.menuItem("Create New Scene"))
                Window.get().getImGuiLayer().getAssetsWindow().createNewScene();

            ImGui.separator();
            if (ImGui.menuItem("Paste")) {
                SystemClipboard.paste();
                File file = new File(Objects.requireNonNull(SystemClipboard.get()));
                String _fileName = Window.get().getImGuiLayer().getAssetsWindow().getCurrentDirectory() + "\\" + file.getPath().replace("\\", "/").split("/")[file.getPath().replace("\\", "/").split("/").length - 1];

//                System.out.println(file);
//                System.out.println(_fileName);

                Window.get().getImGuiLayer().getAssetsWindow().copy(file, new File(_fileName));
                Window.get().getImGuiLayer().showModalPopup("Paste", ConsoleMessage.MessageType.Simple);
            }

            ImGui.endPopup();
        }
        ImGui.popStyleVar();

        super.imgui();
        ImGui.end();
    }

    public boolean doTreeNode(String filepath, String prefix, int level, Vector2f iconPos, boolean isEmpty, boolean defaultOpen, boolean isEven, ImVec2 itemSpacing) {
        String fileName = filepath.replace("\\", "/").split("/")[filepath.replace("\\", "/").split("/").length - 1];

        ImGui.pushID(filepath);

        Asset asset = Window.get().getImGuiLayer().getInspectorWindow().getActiveAsset();

        if (asset != null && asset.assetPath.equals(filepath)) {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 23);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 33);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 33);
        } else if (isEven) {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 0);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 11);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 11);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 5);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 15);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 15);
        }
        ImVec2 selectablePos = new ImVec2(ImGui.getCursorPosX() + 5.0f, ImGui.getCursorPosY());
        ImGui.setCursorPosX(startX);
        ImGui.selectable("##background" + filepath, true, 0, ImGui.getContentRegionAvailX(), 27.0f);
        ImGui.setItemAllowOverlap();
        ImGui.popStyleColor(3);

//        if (isEmpty) {
//            ImGui.setCursorPosX(ImGui.getCursorPosX() + TestFieldsWindow.getFloats[1]);
//            level++;
//        } else
//            ImGui.setCursorPosX(ImGui.getCursorPosX() + TestFieldsWindow.getFloats[2]);

        iconPos.x = selectablePos.x + 19.0f + TestFieldsWindow.getFloats[0];
        iconPos.y = selectablePos.y + 5.0f;

        ImGui.setCursorPos(selectablePos.x + 6.5f - 8.8f + TestFieldsWindow.getFloats[4], selectablePos.y);

        ImGui.pushStyleColor(ImGuiCol.Header, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 0);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                prefix + "\t " + fileName, //                         (Window.getImGuiLayer().getAssetsWindow().getCurrentDirectory().equals(filepath) ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.FramePadding) |
                        (isEmpty ? ImGuiTreeNodeFlags.Leaf : ImGuiTreeNodeFlags.OpenOnArrow) |
                        ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth |
                        (defaultOpen ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.FramePadding),
                prefix + "\t " + fileName
        );
        ImGui.popStyleColor(3);
        ImGui.popID();

        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
            Window.get().getImGuiLayer().getAssetsWindow().refreshAssets(filepath);
            Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(null);
        }

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDragDropType, filepath); // Tooltip
            ImGui.text(filepath); // Some thin in tooltip(text, image)
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
                if (payloadObj.getClass().isAssignableFrom(String.class)) {
                    String file = (String)payloadObj;
//                    System.out.println("OnDragEnd from:'" + file + "'");
//                    System.out.println("OnDragEnd to:'" + filepath + "'");
                    String _fileName = file.replace("\\", "/").split("/")[file.replace("\\", "/").split("/").length - 1];
                    Window.get().getImGuiLayer().getAssetsWindow().move(new File(file), new File(filepath + "\\" + _fileName));
                    Window.get().getImGuiLayer().getAssetsWindow().refreshAssets();
                }
            }
            ImGui.endDragDropTarget();
        }

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemSpacing.x, itemSpacing.y);
        if (ImGui.beginPopupContextItem("File Context Menu" + filepath)) {
            if (ImGui.menuItem("Copy")) {
                File file = new File(filepath);
                SystemClipboard.copy(file.getAbsolutePath());
                Window.get().getImGuiLayer().showModalPopup("Copy", ConsoleMessage.MessageType.Simple);
            }

            if (ImGui.menuItem("Paste")) {
                SystemClipboard.paste();
                File file = new File(Objects.requireNonNull(SystemClipboard.get()));
                String _fileName = Window.get().getImGuiLayer().getAssetsWindow().getCurrentDirectory() + "\\" + file.getPath().replace("\\", "/").split("/")[file.getPath().replace("\\", "/").split("/").length - 1];

//                System.out.println(file);
//                System.out.println(_fileName);

                Window.get().getImGuiLayer().getAssetsWindow().copy(file, new File(_fileName));
            }

            ImGui.separator();
            if (ImGui.menuItem("Delete")) {
                try {
                    File file1 = new File(filepath);
                    if (file1.isDirectory())
                        for (File file : Objects.requireNonNull(file1.listFiles()))
                            FileUtils.forceDelete(file);
                    FileUtils.forceDelete(file1);
                } catch (IOException e) {
                    System.out.println("Error (DirectoryStructure treeNodeOpen: Delete) '" + e + "'");
//                            throw new RuntimeException(e);
                }
            }
            ImGui.endPopup();
        }
        ImGui.popStyleVar(2);

//        float endY = ImGui.getCursorPosY();
//        ImGui.setCursorPos(startX + 4.0f, ImGui.getCursorPosY() - 22.0f);
//        if (level > 0)
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