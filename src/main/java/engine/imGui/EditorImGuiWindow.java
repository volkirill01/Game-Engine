package engine.imGui;

import engine.renderEngine.Window;

import java.io.Serializable;

public class EditorImGuiWindow implements Serializable {

    public void imgui() {
//        if (ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left)) { // TODO FIX FULL SCREEN
//            if (Window.get().getImGuiLayer().getWindowOnFullscreen() == null)
//                setOnFullscreen(this);
//            else
//                setOnFullscreen(null);
//        }
    }

    public void setOnFullscreen(EditorImGuiWindow window) {
        Window.get().getImGuiLayer().setWindowOnFullscreen(window);
        System.out.println("Window full screen: '" + window + "'");
    }
}
