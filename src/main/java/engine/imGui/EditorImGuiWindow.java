package engine.imGui;

import engine.renderEngine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class EditorImGuiWindow implements Serializable {

    public void imgui() {
        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left) && ImGui.isWindowHovered()) {
            if (Window.getImGuiLayer().getWindowOnFullscreen() == null)
                setOnFullscreen(this);
            else
                setOnFullscreen(null);
        }
    }

    public void setOnFullscreen(EditorImGuiWindow window) {
        Window.getImGuiLayer().setWindowOnFullscreen(window);
        System.out.println("Window full screen: '" + window + "'");
    }
}
