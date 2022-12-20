package engine.imGui.editorToolsWindows;

import engine.entities.GameObject;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.toolbox.DefaultMeshes;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class FastMeshPlace {

    public void imgui() {
        Vector2f windowPos = Window.get().getImGuiLayer().getGameViewWindow().getTopRightCorner();
        Vector2f windowSize = new Vector2f(194.0f, 97.0f);

        ImGui.setNextWindowPos(windowPos.x - windowSize.x - 8.5f, windowPos.y - 6.5f);
        ImGui.setNextWindowSize(windowSize.x, windowSize.y);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 3.0f, 3.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 2.0f, 2.0f);
        ImGui.begin("Meshes", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysHorizontalScrollbar);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5.0f, 5.0f);

        if (ImGui.imageButton(Loader.get().loadTexture("engineFiles/defaultMeshes/icon=defaultCube(256x256).png").getTextureID(), 46.0f, 46.0f, 0, 1, 1, 0)) {
            GameObject cube = Window.get().getScene().createGameObject("Cube");
            cube.addComponent(new MeshRenderer(DefaultMeshes.DefaultCube()));
            Window.get().getScene().addGameObjectToScene(cube);
        }
        ImGui.sameLine();

        if (ImGui.imageButton(Loader.get().loadTexture("engineFiles/defaultMeshes/icon=defaultSphere(256x256).png").getTextureID(), 46.0f, 46.0f, 0, 1, 1, 0)) {
            GameObject cube = Window.get().getScene().createGameObject("Sphere");
            cube.addComponent(new MeshRenderer(DefaultMeshes.DefaultSphere()));
            Window.get().getScene().addGameObjectToScene(cube);
        }
        ImGui.sameLine();

        if (ImGui.imageButton(Loader.get().loadTexture("engineFiles/defaultMeshes/icon=defaultSphere(256x256).png").getTextureID(), 46.0f, 46.0f, 0, 1, 1, 0)) {
            GameObject cube = Window.get().getScene().createGameObject("Sphere");
            cube.addComponent(new MeshRenderer(DefaultMeshes.DefaultSphere()));
            Window.get().getScene().addGameObjectToScene(cube);
        }
        ImGui.sameLine();

        if (ImGui.imageButton(Loader.get().loadTexture("engineFiles/defaultMeshes/icon=defaultSphere(256x256).png").getTextureID(), 46.0f, 46.0f, 0, 1, 1, 0)) {
            GameObject cube = Window.get().getScene().createGameObject("Sphere");
            cube.addComponent(new MeshRenderer(DefaultMeshes.DefaultSphere()));
            Window.get().getScene().addGameObjectToScene(cube);
        }

        ImGui.popStyleVar();
        ImGui.end();
        ImGui.popStyleVar(3);
    }
}
