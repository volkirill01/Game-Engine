package engine.imGui;

import engine.TestFieldsWindow;
import engine.renderEngine.Window;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Random;

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
