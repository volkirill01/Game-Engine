package engine.gizmo;

import engine.TestFieldsWindow;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.toolbox.DefaultMeshes;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.Shortcut;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GizmoSystem {

    private TranslateGizmo translateGizmo;
    private RotateGizmo rotateGizmo;
    private ScaleGizmo scaleGizmo;

    private boolean isTranslating = false;
    private boolean isRotating = false;
    private boolean isScaling = false;

    public GizmoSystem() {
        this.translateGizmo = new TranslateGizmo();
        this.rotateGizmo = new RotateGizmo();
        this.scaleGizmo = new ScaleGizmo();
    }

    public void update() {
        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == null)
            return;

        isTranslating = translateGizmo.update(isScaling | isRotating);
        isRotating = rotateGizmo.update(isScaling | isTranslating);
        isScaling = scaleGizmo.update(isRotating | isTranslating);
    }

    public void imgui() {
        Vector2f windowPos = Window.get().getImGuiLayer().getGameViewWindow().getTopLeftCorner();
        Vector2f windowSize = new Vector2f(44.0f, 182.7f);

        ImGui.setNextWindowPos(windowPos.x - 6.3f, windowPos.y - 6.5f);
        ImGui.setNextWindowSize(windowSize.x, windowSize.y);

        float startFrameRounding = ImGui.getStyle().getFrameRounding();
        ImVec2 startFramePadding = ImGui.getStyle().getFramePadding();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 3.0f, 3.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 2.0f, 2.0f);
        ImGui.begin("Gizmo Tools", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5.0f, 5.0f);

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Move", InputManager.getShortcut("translate").shortcutDisplayKeys, "Double press$to move object\nPress[X]to move align/X axis\nPress[Y]to move align/Y axis\nPress[Z]to move align/Z axis", true, "engineFiles/images/icons/tools/icon=translate-tool(256x256).png")) {

        }

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Rotate", InputManager.getShortcut("rotate").shortcutDisplayKeys, "Double press$to rotate object\nPress[X]to rotate align/X axis\nPress[Y]to rotate align/Y axis\nPress[Z]to rotate align/Z axis", true, "engineFiles/images/icons/tools/icon=rotate-tool(256x256).png")) {

        }

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Scale", InputManager.getShortcut("scale").shortcutDisplayKeys, "Double press$to scale object\nPress[X]to scale align/X axis\nPress[Y]to scale align/Y axis\nPress[Z]to scale align/Z axis", true, "engineFiles/images/icons/tools/icon=scale-tool(256x256).png")) {

        }

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Select", InputManager.getShortcut("select").shortcutDisplayKeys, "Press$to select object", false, "engineFiles/images/icons/tools/icon=select-tool(256x256).png")) {

        }

        ImGui.popStyleVar();
        ImGui.end();
        ImGui.popStyleVar(3);
    }

    private boolean drawGizmoButton(float startFrameRounding, ImVec2 startFramePadding, String action, String shortcut, String tooltip, boolean displayXYZTooltips, String iconFilepath) {

        ImGui.setCursorPosX(ImGui.getCursorPosX() + 1.2f);
        boolean isClick = ImGui.imageButton(Loader.get().loadTexture(iconFilepath).getTextureID(), 26.0f, 26.0f, 0, 1, 1, 0);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, startFrameRounding);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, startFramePadding.x, startFramePadding.y);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();

            ImGui.text(action);
            ImGui.sameLine();
            ImGui.spacing();
            ImGui.sameLine();
            ImGui.spacing();
            ImGui.sameLine();
            ImGui.textDisabled(shortcut);

            if (displayXYZTooltips) {
                ImGui.separator();
                ImGui.textDisabled(tooltip.split("\n")[0].split("\\$")[0]);
                ImGui.sameLine();
                ImGui.text(shortcut);
                ImGui.sameLine();
                ImGui.textDisabled(tooltip.split("\n")[0].split("\\$")[1]);

                ImGui.separator();
                ImGui.textDisabled(tooltip.split("\n")[1].split("\\[")[0].split("]")[0]);
                ImGui.sameLine();
                ImGui.text(tooltip.split("\n")[1].split("\\[")[1].split("]")[0]);
                ImGui.sameLine();
                ImGui.textDisabled(tooltip.split("\n")[1].split("\\[")[1].split("]")[1].split("/")[0]);
                ImGui.sameLine();
                ImGui.textColored(221.0f / 255f, 71.0f / 255f, 51.0f / 255f, 1.0f, tooltip.split("\n")[1].split("\\[")[1].split("]")[1].split("/")[1]);

                ImGui.textDisabled(tooltip.split("\n")[2].split("\\[")[0].split("]")[0]);
                ImGui.sameLine();
                ImGui.text(tooltip.split("\n")[2].split("\\[")[1].split("]")[0]);
                ImGui.sameLine();
                ImGui.textDisabled(tooltip.split("\n")[2].split("\\[")[1].split("]")[1].split("/")[0]);
                ImGui.sameLine();
                ImGui.textColored(123.0f / 255f, 179.0f / 255f, 33.0f / 255f, 1.0f, tooltip.split("\n")[2].split("\\[")[1].split("]")[1].split("/")[1]);

                ImGui.textDisabled(tooltip.split("\n")[3].split("\\[")[0].split("]")[0]);
                ImGui.sameLine();
                ImGui.text(tooltip.split("\n")[3].split("\\[")[1].split("]")[0]);
                ImGui.sameLine();
                ImGui.textDisabled(tooltip.split("\n")[3].split("\\[")[1].split("]")[1].split("/")[0]);
                ImGui.sameLine();
                ImGui.textColored(22.0f / 255f, 126.0f / 255f, 213.0f / 255f, 1.0f, tooltip.split("\n")[3].split("\\[")[1].split("]")[1].split("/")[1]);
            }
            ImGui.endTooltip();
        }
        ImGui.popStyleVar(2);

        return isClick;
    }
}
