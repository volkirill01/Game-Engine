package engine.assets;

import engine.renderEngine.Loader;
import engine.renderEngine.textures.Texture;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;

import java.util.Map;

public abstract class Asset {

    public String assetPath;
    public String assetName;
    public AssetType assetType;
    public Texture fileIcon;

    public Map<String, Object> data;

    public Asset(String assetPath, String assetName, Map<String, Object> data, AssetType assetType, Texture fileIcon) {
        this.assetPath = assetPath;
        this.assetName = assetName;
        this.data = data;
        this.assetType = assetType;
        this.fileIcon = fileIcon;
    }

    public enum AssetType {
        Folder,
        NewFolder,
        Scene,
        NewScene,
        Image,
        Model,
        Sound,
        Font,
        Shader,
        Other
    }

    public void mainImgui() {
        ImVec2 startCursorPos = ImGui.getCursorPos();

        ImGui.setCursorPos(3.0f, 30.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, 7.0f);
        ImGui.beginChildFrame(92921, ImGui.getContentRegionAvailX(), 60.0f + ImGui.getStyle().getFramePaddingY());

        ImGui.image(this.fileIcon.getTextureID(), 50.0f, 50.0f, 0, 1, 1, 0);

        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() + 8.0f, ImGui.getCursorPosY() + 3.0f);
        ImGui.text(this.assetName);

        ImGui.endChildFrame();
        ImGui.popStyleVar(2);

        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y + 60.0f + ImGui.getStyle().getFramePaddingY());
    }

    public abstract void imgui();

    public void saveMeta() { Loader.get().saveMeta(assetPath, this.data); }
}
