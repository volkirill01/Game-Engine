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

public class TranslateGizmo extends Gizmo {

    private boolean isTranslating = false;
    private float translation = 0.0f;
    private final float translateSpeed = 0.007f;
    private final float slowTranslateSpeed = 0.0015f;
    private float currentTranslationSpeed = translateSpeed;
    private Vector3f startTranslation;
    private Vector3f translationDirection;

    public TranslateGizmo() {
        this.startTranslation = new Vector3f(0.0f);
        this.translationDirection = new Vector3f(0.0f);
    }

    @Override
    public boolean update(boolean use) {
        if (use)
            return false;

        if (KeyListener.isKeyDoubleClick(InputManager.getShortcut("translate").firstKeyCode) && !KeyListener.isAnyKeyPressed(InputManager.getShortcut("translate").firstKeyCode)) {
            this.isTranslating = true;
            this.startTranslation = new Vector3f(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.position);
            this.translationDirection.x = 1.0f;
            this.translationDirection.y = 1.0f;
            this.translationDirection.z = 1.0f;
        }

        this.translation = (MouseListener.getDy() + (-MouseListener.getDx()));

        if (this.isTranslating) {
            Window.get().getImGuiLayer().getGameViewWindow().setWantCaptureMouse(false);
            Window.get().setLockControl(true);

            Window.get().getImGuiLayer().getInspectorWindow().setLocked(true);

            if (KeyListener.isKeyDown(KeyCode.Left_Shift) || KeyListener.isKeyDown(KeyCode.Right_Shift))
                this.currentTranslationSpeed = this.slowTranslateSpeed;
            else
                this.currentTranslationSpeed = this.translateSpeed;

            if (KeyListener.isKeyClick(KeyCode.X)) {
                resetTranslation();
                this.translationDirection.x = 1.0f;
                this.translationDirection.y = 0.0f;
                this.translationDirection.z = 0.0f;
                Window.get().getImGuiLayer().showModalPopup("Translate in X axis", ConsoleMessage.MessageType.Info);
            } else if (KeyListener.isKeyClick(KeyCode.Y)) {
                resetTranslation();
                this.translationDirection.x = 0.0f;
                this.translationDirection.y = 1.0f;
                this.translationDirection.z = 0.0f;
                Window.get().getImGuiLayer().showModalPopup("Translate in Y axis", ConsoleMessage.MessageType.Info);
            } else if (KeyListener.isKeyClick(KeyCode.Z)) {
                resetTranslation();
                this.translationDirection.x = 0.0f;
                this.translationDirection.y = 0.0f;
                this.translationDirection.z = 1.0f;
                Window.get().getImGuiLayer().showModalPopup("Translate in Z axis", ConsoleMessage.MessageType.Info);
            }

            if ((KeyListener.isKeyClick(KeyCode.Escape)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                Window.get().getImGuiLayer().showModalPopup("Cancel Translation", ConsoleMessage.MessageType.Info);
                resetTranslation();
            }

            if ((KeyListener.isKeyClick(KeyCode.Enter)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) || ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                Window.get().getImGuiLayer().showModalPopup("Apply Translation", ConsoleMessage.MessageType.Info);
            }

            Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.increasePosition(
                    this.translation * this.translationDirection.x * this.currentTranslationSpeed,
                    this.translation * this.translationDirection.y * this.currentTranslationSpeed,
                    this.translation * this.translationDirection.z * this.currentTranslationSpeed
            );
        }

        if (KeyListener.isKeyClick(KeyCode.Escape) ||
                KeyListener.isKeyClick(KeyCode.Enter) ||
                MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) ||
                MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) ||
                ImGui.isMouseClicked(ImGuiMouseButton.Right) ||
                ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            this.isTranslating = false;
            Window.get().getImGuiLayer().getInspectorWindow().setLocked(false);
            Window.get().setLockControl(false);
        }

        return this.isTranslating;
    }

    private void resetTranslation() { Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.position = new Vector3f(this.startTranslation); }
}
