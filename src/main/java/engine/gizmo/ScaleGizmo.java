package engine.gizmo;

import engine.imGui.ConsoleMessage;
import engine.renderEngine.Window;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.KeyCode;
import engine.toolbox.input.KeyListener;
import engine.toolbox.input.MouseListener;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.joml.Vector3f;

public class ScaleGizmo extends Gizmo {

    private boolean isScaling = false;
    private float scale = 0.0f;
    private final float scalingSpeed = 0.01f;
    private final float slowScalingSpeed = 0.0008f;
    private float currentScalingSpeed = scalingSpeed;
    private Vector3f startScale;
    private Vector3f scaleDirection;

    public ScaleGizmo() {
        this.startScale = new Vector3f(0.0f);
        this.scaleDirection = new Vector3f(0.0f);
    }

    @Override
    public boolean update(boolean use) {
        if (use)
            return false;

        if (KeyListener.isKeyDoubleClick(InputManager.getShortcut("scale").firstKeyCode) && !KeyListener.isAnyKeyPressed(InputManager.getShortcut("scale").firstKeyCode)) {
            this.isScaling = true;
            this.startScale = new Vector3f(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.scale);
            this.scaleDirection.x = 1.0f;
            this.scaleDirection.y = 1.0f;
            this.scaleDirection.z = 1.0f;
        }

        this.scale = (MouseListener.getDy() + (-MouseListener.getDx()));

        if (this.isScaling) {
            Window.get().getImGuiLayer().getGameViewWindow().setWantCaptureMouse(false);
            Window.get().setLockControl(true);

            Window.get().getImGuiLayer().getInspectorWindow().setLocked(true);

            if (KeyListener.isKeyDown(KeyCode.Left_Shift) || KeyListener.isKeyDown(KeyCode.Right_Shift))
                this.currentScalingSpeed = this.slowScalingSpeed;
            else
                this.currentScalingSpeed = this.scalingSpeed;

            if (KeyListener.isKeyClick(KeyCode.X)) {
                resetScale();
                this.scaleDirection.x = 1.0f;
                this.scaleDirection.y = 0.0f;
                this.scaleDirection.z = 0.0f;
                Window.get().getImGuiLayer().showModalPopup("Scaling in X axis", ConsoleMessage.MessageType.Info);
            } else if (KeyListener.isKeyClick(KeyCode.Y)) {
                resetScale();
                this.scaleDirection.x = 0.0f;
                this.scaleDirection.y = 1.0f;
                this.scaleDirection.z = 0.0f;
                Window.get().getImGuiLayer().showModalPopup("Scaling in Y axis", ConsoleMessage.MessageType.Info);
            } else if (KeyListener.isKeyClick(KeyCode.Z)) {
                resetScale();
                this.scaleDirection.x = 0.0f;
                this.scaleDirection.y = 0.0f;
                this.scaleDirection.z = 1.0f;
                Window.get().getImGuiLayer().showModalPopup("Scaling in Z axis", ConsoleMessage.MessageType.Info);
            }

            if ((KeyListener.isKeyClick(KeyCode.Escape)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                Window.get().getImGuiLayer().showModalPopup("Cancel Scaling", ConsoleMessage.MessageType.Info);
                resetScale();
            }

            if ((KeyListener.isKeyClick(KeyCode.Enter)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) || ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                Window.get().getImGuiLayer().showModalPopup("Apply Scale", ConsoleMessage.MessageType.Info);
            }

            Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.increaseScale(
                    this.scale * this.scaleDirection.x * this.currentScalingSpeed,
                    this.scale * this.scaleDirection.y * this.currentScalingSpeed,
                    this.scale * this.scaleDirection.z * this.currentScalingSpeed
            );
        }

        if (KeyListener.isKeyClick(KeyCode.Escape) ||
                KeyListener.isKeyClick(KeyCode.Enter) ||
                MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) ||
                MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) ||
                ImGui.isMouseClicked(ImGuiMouseButton.Right) ||
                ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            this.isScaling = false;
            Window.get().getImGuiLayer().getInspectorWindow().setLocked(false);
            Window.get().setLockControl(false);
        }

        return this.isScaling;
    }

    private void resetScale() { Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.scale = new Vector3f(this.startScale); }
}
