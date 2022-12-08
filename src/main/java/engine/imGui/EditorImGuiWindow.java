package engine.imGui;

import engine.renderEngine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.joml.Vector2f;

public class EditorImGuiWindow {

    public Vector2f windowSize = new Vector2f();
    public Vector2f windowPos = new Vector2f();

    public void imgui() {
        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && ImGui.isWindowHovered())
            if (Window.getImGuiLayer().getWindowOnFullscreen() == null)
                setOnFullscreen(this);
            else
                setOnFullscreen(null);
    }

    public void setOnFullscreen(EditorImGuiWindow window) {
        this.windowSize = new Vector2f(ImGui.getWindowSizeX(), ImGui.getWindowSizeY());
        this.windowPos = new Vector2f(ImGui.getWindowPosX(), ImGui.getWindowPosY());
        Window.getImGuiLayer().setWindowOnFullscreen(window);
        System.out.println("Window full screen: '" + window + "' WindowSize: (" + windowSize.x + ", " + windowSize.y + ") WindowPos: (" + windowPos.x + ", " + windowPos.y + ")");
    }
}
