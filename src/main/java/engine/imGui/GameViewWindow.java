package engine.imGui;

import engine.entities.GameObject;
import engine.eventSystem.EventSystem;
import engine.eventSystem.Events.Event;
import engine.eventSystem.Events.EventType;
import engine.imGui.editorToolsWindows.FastMeshPlace;
import engine.renderEngine.Window;
import engine.scene.SceneManager;
import engine.toolbox.MouseListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GameViewWindow extends EditorImGuiWindow {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    private Vector2f windowPos = new Vector2f();
    private Vector2f topLeftCornerPosition = new Vector2f();
    private Vector2f topRightCornerPosition = new Vector2f();

    private FastMeshPlace fastMeshPlaceWindow;

    private boolean captureMouse = false;

    public GameViewWindow() {
        this.fastMeshPlaceWindow = new FastMeshPlace();
    }

    @Override
    public void imgui() {
        ImGui.begin(" \uEC5C Game Viewport ", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);

        topLeftCornerPosition = new Vector2f(ImGui.getCursorStartPosX() + ImGui.getWindowPosX(), ImGui.getCursorStartPosY() + ImGui.getWindowPosY());

        ImGui.setCursorPosX(ImGui.getCursorPosX() - 6.0f);
        ImGui.beginMenuBar();

        ImGui.columns(2, "", false);
        ImVec2 drawGridTmp = new ImVec2();
        ImGui.calcTextSize(drawGridTmp, "Show grid");
        ImVec2 drawDebugTmp = new ImVec2();
        ImGui.calcTextSize(drawDebugTmp, "Show debug");

        ImGui.setCursorPos(ImGui.getContentRegionAvailX() / 2.0f + 97.0f, ImGui.getCursorPosY() - 0.4f);
        ImGui.setColumnWidth(0, ImGui.getWindowWidth() - ((drawGridTmp.x + drawDebugTmp.x) * 1.55f));
        if (ImGui.menuItem("\uEC74", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("\uEFFC", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.nextColumn();
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 1f, 1f);
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() + 4.0f);
        ImGui.text("Show grid");
        if (ImGui.isItemClicked(0))
            Window.get().drawGrid = !Window.get().drawGrid;
        ImGui.sameLine();
        if (ImGui.checkbox("##showGrid", Window.get().drawGrid))
            Window.get().drawGrid = !Window.get().drawGrid;

        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("Show debug");
        if (ImGui.isItemClicked(0))
            Window.get().drawDebug = !Window.get().drawDebug;
        ImGui.sameLine();
        if (ImGui.checkbox("##showDebug", Window.get().drawDebug))
            Window.get().drawDebug = !Window.get().drawDebug;
        ImGui.popStyleVar();

        ImGui.columns(1);
        ImGui.nextColumn();
        ImGui.endMenuBar();

        ImVec2 windowSize = getLargestSizeForViewport();
        windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();

        leftX = topLeft.x;
        bottomY = topLeft.y - 16;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y - 16;

        int textureId = Window.getScreenImage();

        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);
//        if (ImGui.beginDragDropTarget() && ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null) {
//            String[] payload = ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD");
//
//            Sprite sprite = new Sprite();
//            sprite.setTexture(AssetPool.getTexture(payload[1]));
//
//            GameObject object = Prefabs.generateSpriteObject(payload[0], sprite, 0.25f, 0.25f);
//            // Attach this to the mouse cursor
//            Window.getLevelEditorStuff().getComponent(MouseControls.class).pickupObject(object);
//
//            if (ImGui.acceptDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null)
//                MouseControls.placeS();
//
//            ImGui.endDragDropTarget();
//        }

        topRightCornerPosition = new Vector2f(ImGui.getCursorStartPosX() + ImGui.getWindowPosX() + ImGui.getWindowSizeX(), ImGui.getCursorStartPosY() + ImGui.getWindowPosY());

        captureMouse = !toolsWindowsImgui();

        if (ImGui.beginDragDropTarget() && ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null) {
            String[] payload = ImGui.getDragDropPayload("ASSETS_WINDOW_PAYLOAD");

            if (payload[0].equals("Scene") && ImGui.acceptDragDropPayload("ASSETS_WINDOW_PAYLOAD") != null)
                SceneManager.loadScene(payload[1]);

            ImGui.endDragDropTarget();
        }

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y - 16));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y - 16));

        super.imgui();
        ImGui.end();
    }

    public boolean getWantCaptureMouse() {
        if (!captureMouse || GameObject.showAddTag)
            return false;

        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    private boolean toolsWindowsImgui() {
        this.fastMeshPlaceWindow.imgui();

        if (ImGui.isWindowHovered())
            return false;

        return true;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private Vector2f getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new Vector2f(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    public Vector2f getTopLeftCorner() { return this.topLeftCornerPosition; }

    public Vector2f getTopRightCorner() { return this.topRightCornerPosition; }

    public boolean isPlaying() { return this.isPlaying; }

    public void setNotPlaying() { this.isPlaying = false; }
}
