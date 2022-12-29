package engine.gizmo;

import engine.eventSystem.EventSystem;
import engine.eventSystem.Events.Event;
import engine.eventSystem.Events.EventType;
import engine.imGui.Console;
import engine.imGui.ConsoleMessage;
import engine.renderEngine.Window;
import engine.toolbox.input.InputManager;
import engine.toolbox.input.KeyCode;
import engine.toolbox.input.KeyListener;
import engine.toolbox.input.MouseListener;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.joml.Vector3f;

public class GizmoSystem {

    private boolean isScaling = false;
    private float scale = 0.0f;
    private float scalingSpeed = 0.01f;

    private Vector3f startScale;

    public void update() {
        if (this.startScale == null)
            this.startScale = new Vector3f(0.0f);

        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == null)
            return;

        if (KeyListener.isKeyDoubleClick(InputManager.getShortcut("scale").firstKeyCode)) {
            this.isScaling = true;
            this.startScale = new Vector3f(Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.scale);
            Console.log(ConsoleMessage.MessageType.Info, "Scaling");
        }

        this.scale = (MouseListener.getDy() + (-MouseListener.getDx()));

        if (this.isScaling) {
            Window.get().getImGuiLayer().getGameViewWindow().setWantCaptureMouse(false);
            Window.get().setLockControl(true);

            Window.get().getImGuiLayer().getInspectorWindow().setLocked(true);

            if ((KeyListener.isKeyClick(KeyCode.Escape)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Right) || ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                Console.log(ConsoleMessage.MessageType.Info, "Cancel");
                Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.scale = new Vector3f(this.startScale);
            }

            if ((KeyListener.isKeyClick(KeyCode.Enter)) || MouseListener.mouseButtonClick(KeyCode.Mouse_Button_Left) || ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                Console.log(ConsoleMessage.MessageType.Info, "Commit");
            }

            Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject().transform.increaseScale(this.scale * this.scalingSpeed);
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
    }
}
