package engine.imGui;

import engine.assets.Asset;
import engine.assets.assetsTypes.*;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.scene.SceneManager;
import engine.toolbox.DefaultMeshes;
import engine.toolbox.SystemClipboard;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;

import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;

import java.util.*;
import java.util.List;

public class AssetsWindow extends EditorImGuiWindow {

    private static String payloadDragDropType = "ASSETS_WINDOW_PAYLOAD";
    private static String windowName = " \uEF36 Assets ";

    private List<Asset> assets = new ArrayList<>();
    private File[] oldContents;
    public String assetsDirectory = "Assets";
    private String currentDirectory = assetsDirectory;
    private final float defaultPadding = 4.0f;
    private float padding = defaultPadding;
    private final float defaultThumbnailSize = 45.0f;
    private float thumbnailSize = defaultThumbnailSize;

    private boolean refrashingFiles = true;

    public void refreshAssets() { refreshAssets(currentDirectory); }

    public void refreshAssets(String directory) {
        File directoryPath = new File(directory);
        File[] contents = directoryPath.listFiles();
        assert contents != null;

        if (Arrays.equals(oldContents, contents))
            return;

        assets = new ArrayList<>();

        for (File content : contents) {
            String filepath = content.getPath();
            String[] tmp = filepath.replace("\\", "/").split("/");
            String fileName = tmp[tmp.length - 1];

            if (content.isDirectory()) {
                if (content.list().length > 0)
                    assets.add(new Asset_Folder(filepath, fileName, loadFolderData(filepath), false, false));
                else
                    assets.add(new Asset_Folder(filepath, fileName, loadFolderData(filepath), true, false));
            } else if (content.isFile()) {
                if (filepath.endsWith(".scene"))
                    assets.add(Loader.get().loadAsset_Scene(filepath));
                else if (filepath.endsWith(".png"))
                    assets.add(Loader.get().loadAsset_Image(filepath, Loader.get().loadTexture(filepath)));
                else if (filepath.endsWith(".ogg"))
                    assets.add(Loader.get().loadAsset_Sound(filepath));
//                else if (filepath.endsWith(".ttf"))
//                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Font, "engineFiles/images/icons/icon=font-solid-(256x256).png"));
                else if (filepath.endsWith(".glsl"))
                    assets.add(Loader.get().loadAsset_Shader(filepath));
                else if (filepath.endsWith(".obj"))
                    assets.add(Loader.get().loadAsset_Model(filepath));
                else if (filepath.endsWith(".material"))
                    assets.add(Loader.get().loadAsset_Material(filepath));
                else if (filepath.endsWith(".mtl"))
                    continue;
                else if (filepath.endsWith(".meta"))
                    continue;
                else // Other
                    assets.add(Loader.get().loadAsset_Other(filepath));
            }
        }

        List<Asset> tmpArray1 = assets;
        List<Asset> tmpArray2 = new ArrayList<>();

        for (int i = 0; i < tmpArray1.size(); i++)
            if (tmpArray1.get(i).assetType == Asset.AssetType.Folder) {
                tmpArray2.add(tmpArray1.get(i));
                tmpArray1.remove(i);
                i--;
            }

        tmpArray2.addAll(tmpArray1);

        assets = tmpArray2;
        currentDirectory = directory;
        oldContents = contents;
    }

    private Map<String, Object> loadFolderData(String filepath) {
        Map<String, Object> data = new HashMap<>();
        File folder = new File(filepath);
        float size = 0.0f;

        try {
            size = getFolderSize(folder) / (1024 * 1024);
            String[] tmp = ("" + size).split("\\.");
            size = Float.parseFloat(tmp[0] + "." + tmp[1].charAt(0) + tmp[1].charAt(1));
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
//                throw new RuntimeException(e);
        }

        data.put("size", size);

        return data;
    }

    private float getFolderSize(File folder) {
        long length = 0;

        // listFiles() is used to list the
        // contents of the given folder
        File[] files = folder.listFiles();

        int count = files.length;

        // loop for traversing the directory
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            }
            else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }

    private void goBackFolder() {
        List<String> tmpList = new ArrayList<>(Arrays.stream(currentDirectory.replace("\\", "/").split("/")).toList());
        tmpList.remove(tmpList.size() - 1);

        StringBuilder tmpPath = new StringBuilder();
        for (String folder : tmpList)
            tmpPath.append("\\").append(folder);

        tmpPath.deleteCharAt(0);

        refreshAssets(String.valueOf(tmpPath));
    }

    String newFolderName = "newFolder";
    String currentNewFolderName = newFolderName;

    String newSceneName = "newScene";
    String currentNewSceneName = newSceneName;

    String newMaterialName = "newMaterial";
    String currentNewMaterialName = newMaterialName;

    private String[] inputText(String text) {
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, ImGui.getStyle().getFramePaddingY());
        ImString outString = new ImString(text, 256);
        String[] result = new String[2];

        if (ImGui.inputText("##newDir", outString, ImGuiInputTextFlags.AutoSelectAll)) {
            result[0] = outString.get();
            ImGui.popStyleVar();
            return result;
        }

        if (ImGui.isItemFocused() || !ImGui.isAnyItemActive()) {
            ImGui.setKeyboardFocusHere(-1);
            result[1] = "true";
        } else
            result[1] = "false";

        result[0] = text;
        ImGui.popStyleVar();
        return result;
    }

    private void mkDir(String folderName) {
        File theDir = new File(currentDirectory + "/" + folderName);
        if (!theDir.exists())
            theDir.mkdirs();
    }

    private void mkFile(String fileName, String data) {
        File theDir = new File(currentDirectory + "/" + fileName);
        if (!theDir.exists())
            try {
                FileUtils.writeStringToFile(theDir, data, "UTF-8");
//                theDir.createNewFile();
            } catch (IOException e) {
                System.out.println("Error (AssetsWindow mkFile) '" + e + "'");
//                throw new RuntimeException(e);
            }
    }

    public String getCurrentDirectory() { return currentDirectory; }

    public void move(File srcDir, File destDir) {
        try {
            if (srcDir.isDirectory()) {
                FileUtils.copyDirectory(srcDir, destDir, true);
                for (File file : Objects.requireNonNull(srcDir.listFiles()))
                    FileUtils.forceDelete(file);
            } else
                FileUtils.copyFile(srcDir, destDir, true);
            FileUtils.forceDelete(srcDir);
        } catch (IOException e) {
            System.out.println("Error (AssetsWindow move) '" + e + "'");
//            throw new RuntimeException(e);
        }
    }

    public void copy(File srcDir, File destDir) {
        try {
            if (!destDir.exists())
                if (srcDir.isDirectory())
                    FileUtils.copyDirectory(srcDir, destDir, true);
                else
                    FileUtils.copyFile(srcDir, destDir, true);
            else {
                if (srcDir.isDirectory())
                    FileUtils.copyDirectory(srcDir, new File(destDir.getAbsolutePath() + " (copy)"), true);
                else
                    FileUtils.copyFile(srcDir, new File(destDir.getAbsolutePath().split("\\.")[0] + " (copy)." + destDir.getAbsolutePath().split("\\.")[1]), true);
            }
        } catch (IOException e) {
            System.out.println("Error (AssetsWindow copy) '" + e + "'");
//            throw new RuntimeException(e);
        }
    }

    private void pasteButton() {
        if (ImGui.menuItem("Paste")) {
            if (Objects.requireNonNull(SystemClipboard.get()).split("% ")[0].equals("Asset")) {
                SystemClipboard.paste();
                File file = new File(Objects.requireNonNull(SystemClipboard.get()).split("% ")[1]);

                String fileName = currentDirectory + "\\" + file.getAbsolutePath().replace("\\", "/").split("/")[file.getAbsolutePath().replace("\\", "/").split("/").length - 1];
//                  System.out.println(file);
//                  System.out.println(fileName);

                copy(file, new File(fileName));
                Window.get().getImGuiLayer().showModalPopup("Paste", ConsoleMessage.MessageType.Simple);
            }
        }
    }

    public void createNewFolder() {
        refrashingFiles = false;
        ImGui.setWindowFocus(windowName);
        assets.add(new Asset_Folder(currentDirectory + "/" + currentNewFolderName, currentNewFolderName, loadFolderData(currentDirectory + currentNewFolderName), false, true));
    }

    public void createNewScene() {
        refrashingFiles = false;
        ImGui.setWindowFocus(windowName);
        assets.add(new Asset_Scene(currentDirectory + "/" + currentNewSceneName, currentNewSceneName, Loader.get().loadMeta("engineFiles/defaultAssets/defaultScene.meta", "engineFiles/defaultAssets/defaultScene.meta", true), true));
    }

    public void createMaterial() {
        refrashingFiles = false;
        ImGui.setWindowFocus(windowName);
        assets.add(new Asset_Material(currentDirectory + "/" + currentNewMaterialName, currentNewMaterialName, Loader.get().loadMeta(DefaultMeshes.getDefaultMaterialPath(), DefaultMeshes.getDefaultMaterialPath(), false), Loader.get().loadTexture("engineFiles/images/icons/icon=material-solid-(256x256).png"), true));
    }

    private void popups(int i, ImVec4 borderColor) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, borderColor.x, borderColor.y, borderColor.z, borderColor.w);

        ImVec4 buttonColor = ImGui.getStyle().getColor(ImGuiCol.Button);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing,
                ImGui.getStyle().getFramePaddingX() * 6.0f,
                ImGui.getStyle().getFramePaddingX());

        if (ImGui.beginPopupContextWindow("Create Asset", ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Create New Folder"))
                createNewFolder();
            if (ImGui.menuItem("Create New Scene"))
                createNewScene();
            if (ImGui.menuItem("Create Material"))
                createMaterial();

            ImGui.separator();
            if (ImGui.menuItem("Open In Explorer")) {
                try {
                    Desktop.getDesktop().open(new File(currentDirectory));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (ImGui.menuItem("Refresh Assets"))
                refreshAssets();
            ImGui.separator();

            pasteButton();

            ImGui.endPopup();
        }

        if (ImGui.beginPopupContextItem("File Context Menu" + i)) {
            if (ImGui.menuItem("Copy")) {
                File file = new File(assets.get(i).assetPath);
                SystemClipboard.copy("Asset% " + file.getAbsolutePath());
                Window.get().getImGuiLayer().showModalPopup("Copy", ConsoleMessage.MessageType.Simple);
            }

            pasteButton();

            ImGui.separator();
            if (ImGui.menuItem("Open File")) {
                try {
                    Desktop.getDesktop().open(new File(assets.get(i).assetPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ImGui.menuItem("Open In Explorer")) {
                try {
                    Desktop.getDesktop().open(new File(currentDirectory));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (ImGui.menuItem("Delete")) {
                try {
                    File file1 = new File(assets.get(i).assetPath);
                    if (file1.isDirectory())
                        for (File file : Objects.requireNonNull(file1.listFiles()))
                            FileUtils.forceDelete(file);
                    FileUtils.forceDelete(file1);
                } catch (IOException e) {
                    System.out.println("Error (AssetsWindow imgui: File Context Menu) '" + e + "'");
//                            throw new RuntimeException(e);
                }
            }

            ImGui.endPopup();
        }

        if (refrashingFiles) {
            if (ImGui.beginDragDropSource()) { // TODO пофиксить перемещение файлов
                ImGui.setDragDropPayload(payloadDragDropType, new String[]{assets.get(i).assetType.name(), assets.get(i).assetPath});
                ImGui.text(assets.get(i).assetName);
                ImGui.endDragDropSource();
            }
        }

        ImGui.popStyleColor(3);
        ImGui.popStyleVar();

        ImGui.popStyleColor();
        ImGui.popStyleVar();
    }

    @Override
    public void imgui() {
        if (refrashingFiles)
            refreshAssets();

        float windowPaddingX = ImGui.getStyle().getWindowPaddingX();
        float windowPaddingY = ImGui.getStyle().getWindowPaddingY();
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin(windowName, ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() + 2.0f, ImGui.getStyle().getFramePaddingY() - 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 2.0f, 2.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);
        ImGui.setCursorPosX(ImGui.getCursorPosX() - 2.5f);

        if (!currentDirectory.equals(assetsDirectory)) {
            if (ImGui.button("\uEA5C Back"))
                goBackFolder();
        } else {
            ImVec4 color = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
            ImGui.pushStyleColor(ImGuiCol.Button, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color.x, color.y, color.z, color.w);
            ImGui.button("\uEA5C Back");
            ImGui.popStyleColor(3);
        }

        List<String> filepathFolders = new ArrayList<>(List.of(currentDirectory.replace("\\", "/").split("/")));

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0.0f, ImGui.getStyle().getFramePaddingY());
        ImGui.pushStyleColor(ImGuiCol.Button, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.0f, 0.0f, 0.0f, 0.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);

        ImVec4 textDisabled = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
        ImGui.pushStyleColor(ImGuiCol.Text, textDisabled.x, textDisabled.y, textDisabled.z, textDisabled.w);

        ImGui.setCursorPosX(ImGui.getCursorPosX() + 5.0f);
        for (int i = 0; i < filepathFolders.size(); i++) {
            if (i < filepathFolders.size() - 1) {

                if (ImGui.button(filepathFolders.get(i))) {
                    StringBuilder directory = new StringBuilder(assetsDirectory);
                    for (int j = 1; j <= i; j++)
                        directory.append("/").append(filepathFolders.get(j));
                    refreshAssets(directory.toString());
                }

                ImGui.sameLine();
                ImGui.setCursorPosX(ImGui.getCursorPosX() - 2.0f);
                ImGui.textDisabled("\uEAB8");
                ImGui.sameLine();
                ImGui.setCursorPosX(ImGui.getCursorPosX() - 2.0f);
            } else {
                ImGui.popStyleColor();
                ImGui.pushFont(ImGuiLayer.boldText);

                ImGui.setCursorPos(ImGui.getCursorPosX() - 1.0f, ImGui.getCursorPosY() - 1.0f);
                ImGui.text(filepathFolders.get(i));

                ImGui.popFont();
            }
        }
        ImGui.popStyleColor(4);
        ImGui.popStyleVar(2);

        ImGui.popStyleVar(3);
        ImGui.endMenuBar();

        ImVec4 menuBarColor = ImGui.getStyle().getColor(ImGuiCol.MenuBarBg);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() / 2.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, menuBarColor.x, menuBarColor.y, menuBarColor.z, menuBarColor.w);
        ImGui.beginChildFrame(2929, ImGui.getWindowWidth(), 29.0f);

        float[] floatThumbnailSize = { thumbnailSize };
        float[] floatPadding = { padding };

        ImGui.setCursorPos(ImGui.getCursorPosX() + 2.0f, ImGui.getCursorPosY() + 3.6f);
        ImGui.text("Folders Size");
        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() + 2.0f, ImGui.getCursorPosY() - 3.6f + 1.3f);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() / 2.5f - (windowPaddingX / 2.0f));
        if (ImGui.sliderFloat("##FoldersSize", floatThumbnailSize, 16, 256))
            thumbnailSize = floatThumbnailSize[0];
        ImGui.sameLine();

        float scrollbarSize = ImGui.getStyle().getScrollbarSize(); // TODO добавить проверку если есть скроллбар то это значение иначе 0
        ImGui.setCursorPosX(ImGui.getCursorPosX() + 2.0f + 20.0f);
        ImGui.text("Padding");
        ImGui.sameLine();
        ImGui.setCursorPosX(ImGui.getCursorPosX() + 2.0f);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - (windowPaddingX / 2.0f) - scrollbarSize);
        if (ImGui.sliderFloat("##Padding", floatPadding, 0, 32))
            padding = floatPadding[0];

        ImGui.endChildFrame();
        ImGui.popStyleVar(2);
        ImGui.popStyleColor();

        ImVec4 borderColor = ImGui.getStyle().getColor(ImGuiCol.Border);

        if (assets.size() == 0) {
            popups(0, borderColor);

            ImGui.columns(1);
            ImGui.end();
            ImGui.popStyleVar();
            return;
        }

        float cellSize = thumbnailSize + padding + ImGui.getStyle().getFramePaddingX();

        float panelWidth = ImGui.getContentRegionAvailX() + ImGui.getStyle().getFramePaddingX();
        int columnsCount = (int)(panelWidth / cellSize);

        if (columnsCount < 1)
            columnsCount = 1;

        ImGui.columns(columnsCount, "", false);

        ImVec4 color;
        ImVec4 hoverColor;

        for (int i = 0; i < assets.size(); i++) {
            if (Window.get().getImGuiLayer().getInspectorWindow().getActiveAsset() == assets.get(i)) {
                color = new ImVec4(1.0f, 1.0f, 1.0f, 0.17f);
                hoverColor = new ImVec4(1.0f, 1.0f, 1.0f, 0.23f);
            } else {
                color = new ImVec4(0, 0, 0, 0);
                hoverColor = new ImVec4(1.0f, 1.0f, 1.0f, 0.07f);
            }

            ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.Button, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hoverColor.x, hoverColor.y, hoverColor.z, hoverColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, hoverColor.x, hoverColor.y, hoverColor.z, hoverColor.w);

            ImGui.setCursorPosY(ImGui.getCursorPosY() + (windowPaddingY / 2.0f));

            float spriteWidth = thumbnailSize;
            float spriteHeight = thumbnailSize;

            int id = assets.get(i).fileIcon.getTextureID();
            ImGui.pushID(i);

            switch (assets.get(i).assetType) {
                case Folder:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

                    if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                        try {
                            refreshAssets(assets.get(i).assetPath);
                        } catch (Exception e) {
                            System.out.println("Error (AssetsWindow imgui: case Folder) '" + e + "'");
//                            throw new RuntimeException(e);
                        }
                    }

                    if (ImGui.beginDragDropTarget()) {
                        if (ImGui.acceptDragDropPayload(payloadDragDropType) != null) {
                            String[] payload = ImGui.getDragDropPayload(payloadDragDropType);

                            File srcDir = new File(payload[1]);
                            File destDir = new File(assets.get(i).assetPath + "\\" + payload[1].replace("\\", "/").split("/")[payload[1].replace("\\", "/").split("/").length - 1]);

                            move(srcDir, destDir);

                            ImGui.endDragDropTarget();
                        }
                    }
                    break;
                case Scene:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

                    if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
                        System.out.println("Open scene '" + assets.get(i).assetPath + "'");
                        SceneManager.loadScene(assets.get(i).assetPath);
                    }
                    break;
                case Texture:
                    float spriteW = 256;
                    float spriteH = 256;
                    float ratio = spriteW / spriteH;

                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));


//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Image file clicked '" + assets.get(i).assetPath + "'");
////                        GameObject object = Prefabs.generateSpriteObject(assets.get(i).assetName, sprite, ratio, 1.0f); // TODO MOVE THIS IN TO TILE PALETTE
//                        // Attach this to the mouse cursor
////                        Window.getLevelEditorStuff().getComponent(MouseControls.class).pickupObject(object);
//                    }
                    break;
                case Model:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
//////                        System.out.println("Model file clicked '" + assets.get(i).assetPath + "'");
////
////                        GameObject object = Prefabs.generateMeshObject(assets.get(i).assetName, mesh); // TODO MOVE THIS IN TO TILE PALETTE
////                        // Attach this to the mouse cursor
////                        Window.getLevelEditorStuff().getComponent(MouseControls.class).pickupObject(object);
//                    }
                    break;
                case Sound:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));


//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Sound file clicked '" + assets.get(i).assetPath + "'");
////                        Sound sound = AssetPool.getSound(assets.get(i).assetPath);
////
////                        if (!sound.isPlaying()) sound.play();
////                        else sound.stop();
//                    }
                    break;
                case Font:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Font file clicked '" + assets.get(i).assetPath + "'");
//                    }
                    break;
                case Shader:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Shader file clicked '" + assets.get(i).assetPath + "'");
//                    }
                    break;
                case Material:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Material file clicked '" + assets.get(i).assetPath + "'");
//                    }
                    break;
                case Other:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0))
                        Window.get().getImGuiLayer().getInspectorWindow().setActiveAsset(assets.get(i));

//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Other file clicked '" + assets.get(i).assetPath + "'");
//                    }
                    break;
            }

            popups(i, borderColor);

            try {
                if (assets.get(i).assetType == Asset.AssetType.NewScene) {
                    refrashingFiles = false;
                    ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0);
                    ImGui.setNextItemWidth(spriteWidth + (ImGui.getStyle().getFramePaddingX() * 2.0f));
                    String[] result = inputText(currentNewSceneName);
                    currentNewSceneName = result[0];

                    if (result[1] != null && result[1].equals("false")) {
                        assets.remove(i);
                        mkFile(currentNewSceneName + ".scene", Loader.get().getFileString("engineFiles/defaultFiles/blankScene.scene"));
                        refrashingFiles = true;

                        ImGui.nextColumn();
                        ImGui.popID();
                        ImGui.popStyleColor(4);
                        ImGui.columns(1);
                        ImGui.end();
                        ImGui.popStyleVar();
                        return;
                    }
                } else if (assets.get(i).assetType == Asset.AssetType.NewFolder) {
                    refrashingFiles = false;
                    ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0);
                    ImGui.setNextItemWidth(spriteWidth + (ImGui.getStyle().getFramePaddingX() * 2.0f));
                    String[] result = inputText(currentNewFolderName);
                    currentNewFolderName = result[0];

                    if (result[1] != null && result[1].equals("false")) {
                        assets.remove(i);
                        mkDir(currentNewFolderName);
                        refrashingFiles = true;

                        ImGui.nextColumn();
                        ImGui.popID();
                        ImGui.popStyleColor(4);
                        ImGui.columns(1);
                        ImGui.end();
                        ImGui.popStyleVar();
                        return;
                    }
                } else if (assets.get(i).assetType == Asset.AssetType.NewMaterial) {
                    refrashingFiles = false;
                    ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0);
                    ImGui.setNextItemWidth(spriteWidth + (ImGui.getStyle().getFramePaddingX() * 2.0f));
                    String[] result = inputText(currentNewMaterialName);
                    currentNewMaterialName = result[0];

                    if (result[1] != null && result[1].equals("false")) {
                        assets.remove(i);
                        mkFile(currentNewMaterialName + ".material", Loader.get().getMetaString("engineFiles/defaultAssets/defaultMaterial.material", "", false));
                        refrashingFiles = true;

                        ImGui.nextColumn();
                        ImGui.popID();
                        ImGui.popStyleColor(4);
                        ImGui.columns(1);
                        ImGui.end();
                        ImGui.popStyleVar();
                        return;
                    }
                } else {
                    ImGui.textWrapped(assets.get(i).assetName);
                }
            } catch (Exception e) {
                System.out.println("Error (AssetsWindow imgui: Create New File)'" + e + "'");
//                throw new RuntimeException(e);
            }

            ImGui.nextColumn();
            ImGui.popID();

            ImGui.popStyleColor(4);
        }
        ImGui.columns(1);

        super.imgui();
        ImGui.end();
        ImGui.popStyleVar();
    }
}
