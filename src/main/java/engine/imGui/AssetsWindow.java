package engine.imGui;

import engine.renderEngine.Loader;
import engine.renderEngine.textures.Material;
import engine.toolbox.SystemClipboard;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;

import org.apache.commons.io.FileUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AssetsWindow extends EditorImGuiWindow {

    private static String payloadDragDropType = "ASSETS_WINDOW_PAYLOAD";
    private static String windowName = " \uEF36 Project ";

    private List<Asset> assets = new ArrayList<>();
    private File[] oldContents;
    public String assetsDirectory = "Assets";
    private String currentDirectory = assetsDirectory;
    private float padding = 8.0f;
    private float thumbnailSize = 58.5f;

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
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Folder, Loader.get().loadTexture("engineFiles/images/icons/icon=folder-solid-(256x256).png")));
                else
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Folder, Loader.get().loadTexture("engineFiles/images/icons/icon=folder-open-regular-(256x256).png")));
            } else if (content.isFile()) {
                if (filepath.endsWith(".scene"))
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Scene, Loader.get().loadTexture("engineFiles/images/icons/icon=scene-solid-(256x256).png")));
                else if (filepath.endsWith(".png"))
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Image, Loader.get().loadTexture(filepath)));
                else if (filepath.endsWith(".ogg"))
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Sound, Loader.get().loadTexture("engineFiles/images/icons/icon=volume-high-solid-(256x256).png")));
//                else if (filepath.endsWith(".ttf"))
//                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Font, "engineFiles/images/icons/icon=font-solid-(256x256).png"));
                else if (filepath.endsWith(".glsl"))
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Shader, Loader.get().loadTexture("engineFiles/images/icons/icon=shader-file-solid-(256x256).png")));
                else if (filepath.endsWith(".obj"))
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Model, Loader.get().loadTexture("engineFiles/images/icons/icon=cube-solid-(256x256).png")));
                else
                    assets.add(new Asset(filepath, fileName, Asset.AssetType.Other, Loader.get().loadTexture("engineFiles/images/icons/icon=file-circle-question-(256x256).png")));
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

    private void mkFile(String fileName) {
        File theDir = new File(currentDirectory + "/" + fileName);
        if (!theDir.exists())
            try {
                theDir.createNewFile();
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
                    FileUtils.copyDirectory(srcDir, new File(destDir.getAbsolutePath() + "-copy"), true);
                else
                    FileUtils.copyFile(srcDir, new File(destDir.getAbsolutePath().split("\\.")[0] + "-copy." + destDir.getAbsolutePath().split("\\.")[1]), true);
            }
        } catch (IOException e) {
            System.out.println("Error (AssetsWindow copy) '" + e + "'");
//            throw new RuntimeException(e);
        }
    }

    private void pasteButton() {
        if (ImGui.menuItem("Paste")) {
            SystemClipboard.paste();
            File file = new File(Objects.requireNonNull(SystemClipboard.get()));

            String fileName = currentDirectory + "\\" + file.getAbsolutePath().replace("\\", "/").split("/")[file.getAbsolutePath().replace("\\", "/").split("/").length - 1];
//            System.out.println(file);
//            System.out.println(fileName);

            copy(file, new File( fileName));
        }
    }

    public void createNewFolder() {
        refrashingFiles = false;
        ImGui.setWindowFocus(windowName);
        assets.add(new Asset(currentDirectory + currentNewFolderName, currentNewFolderName, Asset.AssetType.NewFolder, Loader.get().loadTexture("engineFiles/images/icons/icon=folder-solid-(256x256).png")));
    }

    public void createNewScene() {
        refrashingFiles = false;
        ImGui.setWindowFocus(windowName);
        assets.add(new Asset(currentDirectory + currentNewSceneName, currentNewSceneName, Asset.AssetType.NewScene, Loader.get().loadTexture("engineFiles/images/icons/icon=scene-solid-(256x256).png")));
    }

    private void popups(int i, ImVec4 borderColor) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 6.0f);
        ImGui.pushStyleColor(ImGuiCol.Border, borderColor.x, borderColor.y, borderColor.z, borderColor.w);

        if (ImGui.beginPopupContextWindow("Create Asset", ImGuiPopupFlags.NoOpenOverItems | ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Create New Folder"))
                createNewFolder();
            if (ImGui.menuItem("Create New Scene"))
                createNewScene();

            ImGui.separator();
            pasteButton();

            ImGui.endPopup();
        }

        if (ImGui.beginPopupContextItem("File Context Menu" + i)) {
            if (ImGui.menuItem("Copy")) {
                File file = new File(assets.get(i).assetPath);
                SystemClipboard.copy(file.getAbsolutePath());
            }

            pasteButton();

            ImGui.separator();
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

        if (ImGui.beginDragDropSource()) { // TODO пофиксить перемещение файлов
            ImGui.setDragDropPayload(payloadDragDropType, new String[]{assets.get(i).assetType.name(), assets.get(i).assetPath});
            ImGui.text(assets.get(i).assetName);
            ImGui.endDragDropSource();
        }

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

//        if (ImGui.button("\uEFD1 Refresh Assets"))
//            refreshAssets();

        ImGui.popStyleVar(3);
        ImGui.endMenuBar();

        ImVec4 menuBarColor = ImGui.getStyle().getColor(ImGuiCol.MenuBarBg);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() / 2.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, menuBarColor.x, menuBarColor.y, menuBarColor.z, menuBarColor.w);
        ImGui.beginChildFrame(2929, ImGui.getWindowWidth(), 29.0f);

        float[] floatThumbnailSize = { thumbnailSize };
        float[] floatPadding = { padding };
        ImVec2 tmpThumbnailSize = new ImVec2();
        ImVec2 tmpPadding = new ImVec2();

        ImGui.calcTextSize(tmpThumbnailSize, "Thumbnail Size");
        ImGui.calcTextSize(tmpPadding, "Padding");

        ImGui.setCursorPosX(ImGui.getCursorPosX() - 2.0f);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() / 2 - tmpThumbnailSize.x - (windowPaddingX / 2.0f));
        if (ImGui.sliderFloat("Thumbnail Size", floatThumbnailSize, 16, 256))
            thumbnailSize = floatThumbnailSize[0];
        ImGui.sameLine();
        float scrollbarSize = ImGui.getStyle().getScrollbarSize(); // TODO добавить проверку если есть скроллбар то это значение иначе 0
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - tmpPadding.x - (windowPaddingX / 2.0f) - scrollbarSize);
        if (ImGui.sliderFloat("Padding", floatPadding, 0, 32))
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

        ImVec4 color = new ImVec4(0, 0, 0, 0);
        ImVec4 hoverColor = new ImVec4(1.0f, 1.0f, 1.0f, 0.07f);
        ImGui.pushStyleColor(ImGuiCol.Border, color.x, color.y, color.z, color.w);
        ImGui.pushStyleColor(ImGuiCol.BorderShadow, color.x, color.y, color.z, color.w);
        ImGui.pushStyleColor(ImGuiCol.Button, color.x, color.y, color.z, color.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hoverColor.x, hoverColor.y, hoverColor.z, hoverColor.w);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, hoverColor.x, hoverColor.y, hoverColor.z, hoverColor.w);

        for (int i = 0; i < assets.size(); i++) {
            ImGui.setCursorPosY(ImGui.getCursorPosY() + (windowPaddingY / 2.0f));

            float spriteWidth = thumbnailSize;
            float spriteHeight = thumbnailSize;

            int id = assets.get(i).fileIcon.getTextureID();
            ImGui.pushID(i);

            switch (assets.get(i).assetType) {
                case Folder:
                    ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0);
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
                    ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0);
                    if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) {
//                        System.out.println("Open scene '" + assets.get(i).assetPath + "'");
//                        SceneManager.loadScene(assets.get(i).assetPath);
                    }
                    break;
                case Image:
                    float spriteW = 256;
                    float spriteH = 256;
                    float ratio = spriteW / spriteH;

                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
//                        System.out.println("Image file clicked '" + assets.get(i).assetPath + "'");
//                        GameObject object = Prefabs.generateSpriteObject(assets.get(i).assetName, sprite, ratio, 1.0f); // TODO MOVE THIS IN TO TILE PALETTE
                        // Attach this to the mouse cursor
//                        Window.getLevelEditorStuff().getComponent(MouseControls.class).pickupObject(object);
                    }
                    break;
                case Model:
//                    Mesh mesh = new Mesh();
//                    mesh.setTexture(AssetPool.getTexture(assets.get(i).fileIcon));
//                    mesh.setVertexCoords(new Vector3f[]{
//                            new Vector3f(0.0f, 1.0f, 0.0f),
//                            new Vector3f(-1.0f, -1.0f, 0.0f),
//                            new Vector3f(1.0f, -1.0f, 0.0f)
//                    });
//                    mesh.setTexCoords(new Vector2f[]{
//                            new Vector2f(1, 1),
//                            new Vector2f(1, 0),
//                            new Vector2f(0, 0)
//                    });
//                    mesh.setIndices(new int[]{
//                            2, 1, 0
//                    });
//                    id = mesh.getTextureId();
//
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
////                        System.out.println("Model file clicked '" + assets.get(i).assetPath + "'");
//
//                        GameObject object = Prefabs.generateMeshObject(assets.get(i).assetName, mesh); // TODO MOVE THIS IN TO TILE PALETTE
//                        // Attach this to the mouse cursor
//                        Window.getLevelEditorStuff().getComponent(MouseControls.class).pickupObject(object);
                    }
                    break;
                case Sound:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
//                        System.out.println("Sound file clicked '" + assets.get(i).assetPath + "'");
//                        Sound sound = AssetPool.getSound(assets.get(i).assetPath);
//
//                        if (!sound.isPlaying()) sound.play();
//                        else sound.stop();
                    }
                    break;
                case Font:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
//                        System.out.println("Font file clicked '" + assets.get(i).assetPath + "'");
                    }
                    break;
                case Shader:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
//                        System.out.println("Shader file clicked '" + assets.get(i).assetPath + "'");
                    }
                    break;
                case Other:
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, 0, 1, 1, 0)) {
//                        System.out.println("Other file clicked '" + assets.get(i).assetPath + "'");
                    }
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
                        mkFile(currentNewSceneName.split("\\.")[0] + ".scene");
                        refrashingFiles = true;

                        ImGui.nextColumn();
                        ImGui.popID();
                        ImGui.popStyleColor(5);
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
                        ImGui.popStyleColor(5);
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
        }
        ImGui.popStyleColor(5);
        ImGui.columns(1);

        super.imgui();
        ImGui.end();
        ImGui.popStyleVar();
    }
}
