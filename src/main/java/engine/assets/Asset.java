package engine.assets;

import engine.TestFieldsWindow;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
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
        Texture,
        Model,
        Sound,
        Font,
        Shader,
        Material,
        NewMaterial,
        Other
    }

    public void mainImgui() {
        ImVec2 startCursorPos = ImGui.getCursorPos();
        float startFrameRounding = ImGui.getStyle().getFrameRounding();
        ImVec2 startFramePadding = ImGui.getStyle().getFramePadding();

        ImGui.setCursorPos(3.0f, 30.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, 7.0f);
        ImGui.beginChildFrame(92921, ImGui.getContentRegionAvailX(), 60.0f + ImGui.getStyle().getFramePaddingY());

        ImGui.image(this.fileIcon.getTextureID(), 50.0f, 50.0f, 0, 1, 1, 0);

        ImGui.sameLine();
        ImGui.setCursorPos(ImGui.getCursorPosX() + 8.0f, ImGui.getCursorPosY() + 3.0f);
        ImGui.text(this.assetName);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, startFrameRounding);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, startFramePadding.x, startFramePadding.y);
        ImGui.setCursorPosY(startCursorPos.y - 34.0f + 7.0f);
        Window.get().getImGuiLayer().getInspectorWindow().drawIsLockedButton(0.0f);
        ImGui.popStyleVar(2);

        ImGui.endChildFrame();
        ImGui.popStyleVar(2);

        ImGui.setCursorPos(startCursorPos.x, startCursorPos.y + 60.0f + ImGui.getStyle().getFramePaddingY());
    }

    public abstract void imgui();

    public void saveMeta() { Loader.get().saveMeta(assetPath, this.data); }
}
