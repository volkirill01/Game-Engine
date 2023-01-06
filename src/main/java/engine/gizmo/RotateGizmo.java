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

public class RotateGizmo extends Gizmo {

    private boolean isRotating = false;
    private float rotation = 0.0f;
    private final float rotatingSpeed = 0.2f;
    private final float slowRotatingSpeed = 0.02f;
    private float currentRotatingSpeed = rotatingSpeed;
    private Vector3f startRotation;
    private Vector3f rotationDirection;

    public RotateGizmo() {
        this.startRotation = new Vector3f(0.0f);
        this.rotationDirection = new Vector3f(0.0f);
    }

    @Override
    public boolean update(boolean use) {
        if (use)
            return false;

        if (KeyListener.isKeyDoubleClick(InputManager.getShortcut("rotate").firstKeyCode) && !KeyListener.isAnyKeyPressed(InputManager.getShortcut("rotate").firstKeyCode)) {
            this.isRotating = true;
            this.startRotation = new Vector3f(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.localRotation);
            this.rotationDirection.x = 1.0f;
            this.rotationDirection.y = 1.0f;
            this.rotationDirection.z = 1.0f;
        }

        this.rotation = (MouseListener.getDy() + (-MouseListener.getDx()));

        if (this.isRotating) {
            Window.get().getImGuiLayer().getGameViewWindow().setWantCaptureMouse(false);
            Window.get().setLockControl(true);

            if (KeyListener.isKeyDown(KeyCode.Left_Shift) || KeyListener.isKeyDown(KeyCode.Right_Shift))
                this.currentRotatingSpeed = this.slowRotatingSpeed;
            else
                this.currentRotatingSpeed = this.rotatingSpeed;

            if (KeyListener.isKeyClick(KeyCode.X)) {
                resetRotation();
                this.rotationDirection.x = 1.0f;
                this.rotationDirection.y = 0.0f;
                this.rotationDirection.z = 0.0f;
                Window.get().getImGuiLayer().showModalPopup("Rotate in X axis", ConsoleMessage.MessageType.Info);
            } else if (KeyListener.isKeyClick(KeyCode.Y)) {
                resetRotation();
                this.rotationDirection.x = 0.0f;
                this.rotationDirection.y = 1.0f;
                this.rotationDirection.z = 0.0f;
                Window.get().getImGuiLayer().showModalPopup("Rotate in Y axis", ConsoleMessage.MessageType.Info);
            } else if (KeyListener.isKeyClick(KeyCode.Z)) {
                resetRotation();
                this.rotationDirection.x = 0.0f;
                this.rotationDirection.y = 0.0f;
                this.rotationDirection.z = 1.0f;
                Window.get().getImGuiLayer().showModalPopup("Rotate in Z axis", ConsoleMessage.MessageType.Info);
            }

            if ((KeyListener.isKeyClick(KeyCode.Escape)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                Window.get().getImGuiLayer().showModalPopup("Cancel Rotation", ConsoleMessage.MessageType.Info);
                resetRotation();
            }

            if ((KeyListener.isKeyClick(KeyCode.Enter)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) || ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                Window.get().getImGuiLayer().showModalPopup("Apply Rotation", ConsoleMessage.MessageType.Info);
            }

            Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.increaseRotation(
                    this.rotation * this.rotationDirection.x * this.currentRotatingSpeed,
                    this.rotation * this.rotationDirection.y * this.currentRotatingSpeed,
                    this.rotation * this.rotationDirection.z * this.currentRotatingSpeed
            );
        }

        if (KeyListener.isKeyClick(KeyCode.Escape) ||
                KeyListener.isKeyClick(KeyCode.Enter) ||
                MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) ||
                MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) ||
                ImGui.isMouseClicked(ImGuiMouseButton.Right) ||
                ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            this.isRotating = false;
            Window.get().setLockControl(false);
        }

        return this.isRotating;
    }

    private void resetRotation() { Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.localRotation = new Vector3f(this.startRotation); }
}
