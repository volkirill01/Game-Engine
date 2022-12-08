package engine.imGui;

import engine.TestFieldsWindow;
import engine.renderEngine.Window;
import engine.toolbox.SystemClipboard;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AssetsStructureWindow extends EditorImGuiWindow {

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
            boolean treeNodeOpen = doTreeNode(arr[index].getPath(), "", level, isEmpty, false, isEven, itemSpacing);

            if (treeNodeOpen) {
                // recursion for sub-directories
                if (arr[index].listFiles() != null)
                    DrawFiles(arr[index].listFiles(), 0, level + 1, itemSpacing);
                ImGui.treePop();
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
            startX = ImGui.getCursorStartPosX() + 7.9f;
            ImGui.setCursorPosX(ImGui.getCursorPosX() - 12.0f);
            boolean treeNodeOpen = doTreeNode(Window.getImGuiLayer().getAssetsWindow().assetsDirectory, "", 0, isEmpty, true, false, itemSpacing);

            if (treeNodeOpen) {
                a = 0;
                DrawFiles(arr, 0, 1, itemSpacing);
                ImGui.treePop();
            }
        }
        ImGui.popStyleVar();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
        if (ImGui.beginPopupContextWindow("Create Asset", ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Create New Folder"))
                Window.getImGuiLayer().getAssetsWindow().createNewFolder();
            if (ImGui.menuItem("Create New Scene"))
                Window.getImGuiLayer().getAssetsWindow().createNewScene();

            ImGui.separator();
            if (ImGui.menuItem("Paste")) {
                SystemClipboard.paste();
                File file = new File(Objects.requireNonNull(SystemClipboard.get()));
                String _fileName = Window.getImGuiLayer().getAssetsWindow().getCurrentDirectory() + "\\" + file.getPath().replace("\\", "/").split("/")[file.getPath().replace("\\", "/").split("/").length - 1];

//                System.out.println(file);
//                System.out.println(_fileName);

                Window.getImGuiLayer().getAssetsWindow().copy(file, new File(_fileName));
            }

            ImGui.endPopup();
        }
        ImGui.popStyleVar();

        super.imgui();
        ImGui.end();
    }

    public boolean doTreeNode(String filePath, String prefix, int level, boolean isEmpty, boolean defaultOpen, boolean isEven, ImVec2 itemSpacing) {
        String fileName = filePath.replace("\\", "/").split("/")[filePath.replace("\\", "/").split("/").length - 1];

        ImGui.pushID(filePath);

        if (isEven) {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 0);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 11);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 11);
        } else {
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 255, 5);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 255, 255, 15);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 255, 255, 255, 15);
        }
        ImVec2 selectablePos = ImGui.getCursorPos();
        ImGui.setCursorPosX(startX);
        ImGui.selectable("##background" + filePath, true, 0, ImGui.getContentRegionAvailX(), 27.0f);
        ImGui.setItemAllowOverlap();
        ImGui.popStyleColor(3);

        ImGui.setCursorPos((selectablePos.x * 0.856f) + 22.5f, selectablePos.y);
        if (isEmpty) {
            ImGui.setCursorPosX(ImGui.getCursorPosX() - 3.0f);
            level++;
        }

        ImGui.pushStyleColor(ImGuiCol.Header, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 0);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                prefix + fileName, //                         (Window.getImGuiLayer().getAssetsWindow().getCurrentDirectory().equals(filePath) ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.FramePadding) |
                        (isEmpty ? ImGuiTreeNodeFlags.Leaf : ImGuiTreeNodeFlags.OpenOnArrow) |
                        ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth |
                        (defaultOpen ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.FramePadding),
                prefix + fileName
        );
        ImGui.popStyleColor(3);
        ImGui.popID();

        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
            Window.getImGuiLayer().getAssetsWindow().refreshAssets(filePath);

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDragDropType, filePath); // Tooltip
            ImGui.text(filePath); // Some thin in tooltip(text, image)
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
//                    System.out.println("OnDragEnd to:'" + filePath + "'");
                    String _fileName = file.replace("\\", "/").split("/")[file.replace("\\", "/").split("/").length - 1];
                    Window.getImGuiLayer().getAssetsWindow().move(new File(file), new File(filePath + "\\" + _fileName));
                    Window.getImGuiLayer().getAssetsWindow().refreshAssets();
                }
            }
            ImGui.endDragDropTarget();
        }

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, itemSpacing.x, itemSpacing.y);
        if (ImGui.beginPopupContextItem("File Context Menu" + filePath)) {
            if (ImGui.menuItem("Copy")) {
                File file = new File(filePath);
                SystemClipboard.copy(file.getAbsolutePath());
            }

            if (ImGui.menuItem("Paste")) {
                SystemClipboard.paste();
                File file = new File(Objects.requireNonNull(SystemClipboard.get()));
                String _fileName = Window.getImGuiLayer().getAssetsWindow().getCurrentDirectory() + "\\" + file.getPath().replace("\\", "/").split("/")[file.getPath().replace("\\", "/").split("/").length - 1];

//                System.out.println(file);
//                System.out.println(_fileName);

                Window.getImGuiLayer().getAssetsWindow().copy(file, new File(_fileName));
            }

            ImGui.separator();
            if (ImGui.menuItem("Delete")) {
                try {
                    File file1 = new File(filePath);
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