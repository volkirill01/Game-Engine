package engine.imGui.editorToolsWindows;

import engine.entities.GameObject;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.TexturedModel;
import engine.toolbox.DefaultMeshes;
import engine.toolbox.Maths;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class FastMeshPlace {

    public void imgui() {
        Vector2f windowPos = Window.get().getImGuiLayer().getGameViewWindow().getTopRightCorner();
        Vector2f windowSize = new Vector2f(194.0f, 97.0f);

        ImGui.setNextWindowPos(windowPos.x - windowSize.x - 8.5f, windowPos.y - 6.5f);
        ImGui.setNextWindowSize(windowSize.x, windowSize.y);

        float startFrameRounding = ImGui.getStyle().getFrameRounding();
        ImVec2 startFramePadding = ImGui.getStyle().getFramePadding();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 3.0f, 3.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 2.0f, 2.0f);
        ImGui.begin("Meshes", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysHorizontalScrollbar);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5.0f, 5.0f);

        // TODO CHANGE SCROLL FROM SHIFT + MOUSE_WHEEL TO MOUSE_WHEEL

        drawMeshButton(startFrameRounding, startFramePadding, DefaultMeshes.DefaultCube(), "Cube", "engineFiles/defaultMeshes/icon=defaultCube(256x256).png");

        drawMeshButton(startFrameRounding, startFramePadding, DefaultMeshes.DefaultSphere(), "Sphere", "engineFiles/defaultMeshes/icon=defaultSphere(256x256).png");

        drawMeshButton(startFrameRounding, startFramePadding, DefaultMeshes.DefaultCapsule(), "Capsule", "engineFiles/defaultMeshes/icon=defaultCapsule(256x256).png");

        drawMeshButton(startFrameRounding, startFramePadding, DefaultMeshes.DefaultPyramid(), "Pyramid", "engineFiles/defaultMeshes/icon=defaultPyramid(256x256).png");

        ImGui.popStyleVar();
        ImGui.end();
        ImGui.popStyleVar(3);
    }

    private void drawMeshButton(float startFrameRounding, ImVec2 startFramePadding, TexturedModel model, String name, String iconFont) {
        if (ImGui.imageButton(Loader.get().loadTexture(iconFont).getTextureID(), 46.0f, 46.0f, 0, 1, 1, 0)) {
            GameObject cube = Window.get().getScene().createGameObject(name);
            cube.addComponent(new MeshRenderer(model));
            Window.get().getScene().addGameObjectToScene(cube);
            Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(cube);
        }
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, startFrameRounding);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, startFramePadding.x, startFramePadding.y);
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.text("Create " + name + " Object\nOn Scene");
            ImGui.endTooltip();
        }
        ImGui.popStyleVar(2);
        ImGui.sameLine();
    }
}
