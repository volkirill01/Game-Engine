package engine.gizmo;

import engine.Settings;
import engine.TestFieldsWindow;
import engine.entities.EditorCamera;
import engine.entities.GameObject;
import engine.imGui.ConsoleMessage;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.renderer.MasterRenderer;
import engine.toolbox.Maths;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.KeyCode;
import engine.toolbox.input.KeyListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GizmoSystem {

    private TranslateGizmo translateGizmo;
    private RotateGizmo rotateGizmo;
    private ScaleGizmo scaleGizmo;

    private boolean isTranslating = false;
    private boolean isRotating = false;
    private boolean isScaling = false;

    private Vector3f gizmoPosition = new Vector3f();
    private Vector3f gizmoRotation = new Vector3f();
    private Vector3f gizmoScale = new Vector3f();
    private boolean snapping = false;

    private static float[] cameraViewMatrixArray = new float[16];
    private static float[] projectionMatrixArray = new float[16];

    public enum GizmoType {
        Translate,
        Rotate,
        Scale,
        Select
    }

    private GizmoType gizmoType = GizmoType.Translate;

    public GizmoSystem() {
        this.translateGizmo = new TranslateGizmo();
        this.rotateGizmo = new RotateGizmo();
        this.scaleGizmo = new ScaleGizmo();

        MasterRenderer.getProjectionMatrix().get(projectionMatrixArray);
    }

    public void update() {
        if (KeyListener.isKeyClick(InputManager.getShortcut("translate").firstKeyCode))
            gizmoType = GizmoType.Translate;
        else if (KeyListener.isKeyClick(InputManager.getShortcut("rotate").firstKeyCode))
            gizmoType = GizmoType.Rotate;
        else if (KeyListener.isKeyClick(InputManager.getShortcut("scale").firstKeyCode))
            gizmoType = GizmoType.Scale;
        else if (KeyListener.isKeyClick(InputManager.getShortcut("select").firstKeyCode))
            gizmoType = GizmoType.Select;

        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == null)
            return;

        isTranslating = translateGizmo.update(isScaling | isRotating);
        isRotating = rotateGizmo.update(isScaling | isTranslating);
        isScaling = scaleGizmo.update(isRotating | isTranslating);
    }

    public void updateGizmo() {
        GameObject selectedObject = Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject();

        // Camera
        EditorCamera editorCamera = Window.get().getScene().camera();
        if (editorCamera != null)
            Maths.createViewMatrix(editorCamera).get(cameraViewMatrixArray);

        if (selectedObject == null)
            return;

        // GameObject Transform
        float[] transformationMatrixArray = new float[16];
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(selectedObject.transform.position, selectedObject.transform.rotation, selectedObject.transform.scale);
        transformationMatrix.get(transformationMatrixArray);

        // Snapping
        float snapValue = 0.0f;
        switch (gizmoType) {
            case Translate -> snapValue = Settings.MOVE_SNAP;
            case Rotate -> snapValue = Settings.ROTATE_SNAP;
            case Scale -> snapValue = Settings.SCALE_SNAP;
            case Select -> { }
        }

        float[] snapValues = new float[]{ snapValue, snapValue, snapValue };
        snapping = KeyListener.isKeyDown(KeyCode.Left_Control);

        switch (gizmoType) {
            case Translate -> {
                ImGuizmo.manipulate(cameraViewMatrixArray, projectionMatrixArray, transformationMatrixArray, Operation.TRANSLATE, Mode.LOCAL, snapping ? snapValues : new float[3]);
                Maths.decomposeTransformationMatrix(transformationMatrixArray, gizmoPosition, gizmoRotation, gizmoScale);
                if (ImGuizmo.isUsing())
                    selectedObject.transform.localPosition.set(gizmoPosition);
            }
            case Rotate -> {
                ImGuizmo.manipulate(cameraViewMatrixArray, projectionMatrixArray, transformationMatrixArray, Operation.ROTATE, Mode.LOCAL, snapping ? snapValues : new float[3]);
                if (ImGuizmo.isUsing())
                    Window.get().getImGuiLayer().showModalPopup("Rotation currently not working", ConsoleMessage.MessageType.Error);
//                    Maths.decomposeTransformationMatrix(transformationMatrixArray, gizmoPosition, gizmoRotation, gizmoScale);
//                    if (ImGuizmo.isUsing()) {
//                        Vector3f deltaRotation = gizmoRotation.sub(selectedObject.transform.localRotation);
//                        selectedObject.transform.localRotation.add(deltaRotation); // TODO FIX ROTATION CALCULATION
//                    }
            }
            case Scale -> {
                ImGuizmo.manipulate(cameraViewMatrixArray, projectionMatrixArray, transformationMatrixArray, Operation.SCALE, Mode.LOCAL, snapping ? snapValues : new float[3]);
                Maths.decomposeTransformationMatrix(transformationMatrixArray, gizmoPosition, gizmoRotation, gizmoScale);
                if (ImGuizmo.isUsing())
                    selectedObject.transform.localScale.set(gizmoScale);
            }
            case Select -> { }
        }
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
            gizmoType = GizmoType.Translate;
        }

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Rotate", InputManager.getShortcut("rotate").shortcutDisplayKeys, "Double press$to rotate object\nPress[X]to rotate align/X axis\nPress[Y]to rotate align/Y axis\nPress[Z]to rotate align/Z axis", true, "engineFiles/images/icons/tools/icon=rotate-tool(256x256).png")) {
            gizmoType = GizmoType.Rotate;
        }

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Scale", InputManager.getShortcut("scale").shortcutDisplayKeys, "Double press$to scale object\nPress[X]to scale align/X axis\nPress[Y]to scale align/Y axis\nPress[Z]to scale align/Z axis", true, "engineFiles/images/icons/tools/icon=scale-tool(256x256).png")) {
            gizmoType = GizmoType.Scale;
        }

        if (drawGizmoButton(startFrameRounding, startFramePadding, "Select", InputManager.getShortcut("select").shortcutDisplayKeys, "Press$to select object", false, "engineFiles/images/icons/tools/icon=select-tool(256x256).png")) {
            gizmoType = GizmoType.Select;
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

    public GizmoType getGizmoType() { return this.gizmoType; }
}
