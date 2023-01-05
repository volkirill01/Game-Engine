package engine.imGui;

import engine.TestFieldsWindow;
import engine.graphEditor.ExampleImGuiNodeEditor;
import engine.graphEditor.Graph;
import engine.renderEngine.PickingTexture;
import engine.renderEngine.Window;
import engine.renderEngine.postProcessing.PostProcessingGui;
import engine.scene.Scene;
import engine.toolbox.input.JoystickListener;
import engine.toolbox.input.KeyListener;
import engine.toolbox.input.MouseListener;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class ImGuiLayer {

    private long glfwWindow;

    // LWJGL3 MyEngine.renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private EditorImGuiWindow windowOnFullscreen = null;
    private MenuBar menuBar;

    private SceneViewWindow sceneViewWindow;
    private Console console;
    private InspectorWindow inspectorWindow;
    private SceneHierarchyWindow sceneHierarchyWindow;
    private AssetsWindow assetsWindow;
    private AssetsStructureWindow assetsStructureWindow;
    private PostProcessingGui postProcessingWindow;
//    private ScriptGraph scriptGraphWindow;

    private ExampleImGuiNodeEditor test;
    private Graph graph;

    public float fontSize = 0.89f;

    public static ImFont defaultText;
    public static ImFont boldText;

    public static ImFont modalPopupFont;
    public float modalPopupFontSize = 1.7f;
    private float showModalPopupTime = 10;
    private float showModalPopupSpeed = 0.1f;

    private String currentModalPopupText = "Pupop";
    private ConsoleMessage.MessageType currentModalPopupType = ConsoleMessage.MessageType.Simple;
    private float currentModalPopupShowTime = 0;


    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.glfwWindow = glfwWindow;
        this.menuBar = new MenuBar();
        this.sceneViewWindow = new SceneViewWindow();
        this.console = new Console();
        this.inspectorWindow = new InspectorWindow(pickingTexture);
        this.sceneHierarchyWindow = new SceneHierarchyWindow();
        this.assetsWindow = new AssetsWindow();
        this.assetsStructureWindow = new AssetsStructureWindow();
        this.assetsStructureWindow.mainDir = new File(this.assetsWindow.assetsDirectory);
        this.postProcessingWindow = new PostProcessingGui();
//        this.scriptGraphWindow = new ScriptGraph();

        this.test = new ExampleImGuiNodeEditor();
        this.graph = new Graph();
    }

    // Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini"); // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input
        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard())
                KeyListener.keyCallback(w, key, scancode, action, mods);
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1])
                ImGui.setWindowFocus(null);

            if (!io.getWantCaptureMouse()|| sceneViewWindow.getWantCaptureMouse())
                MouseListener.mouseButtonCallback(w, button, action, mods);
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
            if (!io.getWantCaptureMouse() || sceneViewWindow.getWantCaptureMouse())
                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
            else
                MouseListener.clear();
        });

        glfwSetJoystickCallback((joystickId, event) -> {
//            final boolean[] joystickButtons = new boolean[16];
//
//            joystickButtons[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
//            joystickButtons[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
//            joystickButtons[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
//            joystickButtons[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
//            joystickButtons[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

//            io.setMouseDown(mouseDown);
//
//            if (!io.getWantCaptureMouse() && mouseDown[1])
//                ImGui.setWindowFocus(null);

//            if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse())
//                MouseListener.mouseButtonCallback(w, button, action, mods);
//            if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse())
            JoystickListener.joystickCallback(joystickId, event);
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                return Objects.requireNonNullElse(clipboardString, "");
            }
        });

        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        ImFontConfig defaultFontConfig = new ImFontConfig();
        defaultFontConfig.setPixelSnapH(true);
        defaultFontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // Fonts merge example
        fontConfig.setPixelSnapH(true);
        defaultText = fontAtlas.addFontFromFileTTF("engineFiles/fonts/segueui/segoeui.ttf", 20.0f * fontSize, defaultFontConfig);
        fontConfig.setMergeMode(true);
        fontConfig.setGlyphMinAdvanceX(13.0f); // Use if you want to make the icon monospaced
        short[] icons_ranges = { (short)0xEA5C, (short)0xF02C, 0 }; // Min(0xEA5C), Max(0xF025) icons range // TODO -+-+- Change min and max
        fontAtlas.addFontFromFileTTF("engineFiles/fonts/icofont_all.ttf", 15.5f * fontSize, fontConfig, icons_ranges);

        boldText = fontAtlas.addFontFromFileTTF("engineFiles/fonts/segueui/seguisb.ttf", 20.0f * fontSize, defaultFontConfig);
        modalPopupFont = fontAtlas.addFontFromFileTTF("engineFiles/fonts/segueui/segoeui.ttf", 20.0f * modalPopupFontSize * fontSize, defaultFontConfig);

        fontAtlas.build();
        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // Method initializes LWJGL3 MyEngine.renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.

        imGuiGlfw.init(glfwWindow, false);
        imGuiGl3.init("#version 400 core");

        ImGui.getStyle().setTouchExtraPadding(2.0f, 2.0f);

        setStyles();
    }

    private void setStyles() {
        // TODO MAKE THEME SWITCH
//        ImGui.getIO().setFontGlobalScale(0.89f);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 5.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.PopupRounding, 5.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 5.0f);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 1.4f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 1.4f);

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 8.0f, 8.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 5.0f, 5.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 2.0f, 2.0f);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 3.0f, 3.0f);

        ImGui.pushStyleVar(ImGuiStyleVar.ScrollbarSize, 12.0f);

        ImGui.pushStyleVar(ImGuiStyleVar.GrabRounding, 7.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.TabRounding, 4.0f);
        ImGui.getStyle().setWindowMenuButtonPosition(1);

        ImGui.pushStyleColor(ImGuiCol.Text, 255, 255, 255, 255);
        ImGui.pushStyleColor(ImGuiCol.TextDisabled, 128, 128, 128, 255);

        ImGui.pushStyleColor(ImGuiCol.WindowBg, 44, 44, 44, 255);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 44, 44, 44, 255);
        ImGui.pushStyleColor(ImGuiCol.PopupBg, 44, 44, 44, 255);

        ImGui.pushStyleColor(ImGuiCol.Border, 255, 255, 255, 15);
        ImGui.pushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, 31, 31, 31, 255);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 34, 34, 34, 255);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 38, 38, 38, 255);

        ImGui.pushStyleColor(ImGuiCol.TitleBg, 15, 15, 15, 255);
        ImGui.pushStyleColor(ImGuiCol.TitleBgActive, 15, 15, 15, 255);
        ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, 15, 15, 15, 255);

        ImGui.pushStyleColor(ImGuiCol.MenuBarBg, 15, 15, 15, 255);

        ImGui.pushStyleColor(ImGuiCol.ScrollbarBg, 30, 30, 30, 255);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, 64, 64, 64, 255);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabHovered, 74, 74, 74, 255);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabActive, 54, 54, 54, 255);

        ImGui.pushStyleColor(ImGuiCol.CheckMark, 255, 255, 255, 255);
        ImGui.pushStyleColor(ImGuiCol.SliderGrab, 64, 64, 64, 255);
        ImGui.pushStyleColor(ImGuiCol.SliderGrabActive, 54, 54, 54, 255);

        ImGui.pushStyleColor(ImGuiCol.Button, 64, 64, 64, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 74, 74, 74, 255);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 54, 54, 54, 255);

        ImGui.pushStyleColor(ImGuiCol.Header, 64, 64, 64, 255);
        ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 74, 74, 74, 255);
        ImGui.pushStyleColor(ImGuiCol.HeaderActive, 54, 54, 54, 255);

        ImGui.pushStyleColor(ImGuiCol.Separator, 0, 0, 0, 100);
        ImGui.pushStyleColor(ImGuiCol.SeparatorHovered, 0, 0, 0, 100);
        ImGui.pushStyleColor(ImGuiCol.SeparatorActive, 0, 0, 0, 100);

        ImGui.pushStyleColor(ImGuiCol.ResizeGrip, 44, 44, 44, 255);
        ImGui.pushStyleColor(ImGuiCol.ResizeGripHovered, 54, 54, 54, 255);
        ImGui.pushStyleColor(ImGuiCol.ResizeGripActive, 34, 34, 34, 255);

        ImGui.pushStyleColor(ImGuiCol.Tab, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.TabHovered, 54, 54, 54, 255);
        ImGui.pushStyleColor(ImGuiCol.TabActive, 44, 44, 44, 255);
        ImGui.pushStyleColor(ImGuiCol.TabUnfocused, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.TabUnfocusedActive, 0, 0, 0, 0);

        ImGui.pushStyleColor(ImGuiCol.DockingPreview, 255, 255, 255, 50);
        ImGui.pushStyleColor(ImGuiCol.DockingEmptyBg, 51, 51, 51, 255);

        ImGui.pushStyleColor(ImGuiCol.PlotLines, 156, 156, 156, 255);
        ImGui.pushStyleColor(ImGuiCol.PlotLinesHovered, 255, 110, 89, 255);
        ImGui.pushStyleColor(ImGuiCol.PlotHistogram, 230, 179, 0, 255);
        ImGui.pushStyleColor(ImGuiCol.PlotHistogramHovered, 255, 153, 0, 255);

        ImGui.pushStyleColor(ImGuiCol.TableHeaderBg, 48, 48, 51, 255);
        ImGui.pushStyleColor(ImGuiCol.TableBorderStrong, 79, 79, 89, 255);
        ImGui.pushStyleColor(ImGuiCol.TableBorderLight, 59, 59, 64, 255);

        ImGui.pushStyleColor(ImGuiCol.TableRowBg, 255, 0, 0, 255);
        ImGui.pushStyleColor(ImGuiCol.TableRowBgAlt, 0, 255, 0, 255);

        ImGui.pushStyleColor(ImGuiCol.TextSelectedBg, 66, 150, 250, 89);

        ImGui.pushStyleColor(ImGuiCol.DragDropTarget, 255, 154, 36, 89);

        ImGui.pushStyleColor(ImGuiCol.NavHighlight, 0, 0, 255, 255);
        ImGui.pushStyleColor(ImGuiCol.NavWindowingHighlight, 255, 255, 255, 179);
        ImGui.pushStyleColor(ImGuiCol.NavWindowingDimBg, 204, 204, 204, 51);

        ImGui.pushStyleColor(ImGuiCol.ModalWindowDimBg, 204, 204, 204, 89);

        ImGui.pushStyleVar(ImGuiStyleVar.IndentSpacing, 12);
    }

    public void update(Scene currentScene) {
        startFrame();

        if (this.windowOnFullscreen == null) {
            setupDockspace();
            currentScene.imgui();
//            TestFieldsWindow.imgui();
//            ImGui.showDemoWindow();
            sceneViewWindow.imgui();
            console.imgui();
    //        scriptGraphWindow.imgui();
            sceneHierarchyWindow.imgui();
            assetsWindow.imgui();
            assetsStructureWindow.imgui();
            postProcessingWindow.imgui();
            inspectorWindow.imgui();

//            test.imgui(graph); // TODO MAKE GRAPH EDITOR

        } else {
            ImGuiViewport viewport = ImGui.getMainViewport();
            ImGui.setNextWindowPos(viewport.getWorkPos().x, viewport.getWorkPos().y);
            ImGui.setNextWindowSize(viewport.getWorkSize().x, viewport.getWorkSize().y);
            ImGui.setNextWindowViewport(viewport.getID());

            this.windowOnFullscreen.imgui();
        }

        // TODO make window full screen on double click on tab bar

        currentModalPopupShowTime -= showModalPopupSpeed;
        if (currentModalPopupShowTime > 0)
            drawModalPopup();

        endFrame();
    }

    public void setWindowOnFullscreen(EditorImGuiWindow windowOnFullscreen) {
//        if (windowOnFullscreen == null) {
//            ImGui.setNextWindowSize(this.windowOnFullscreen.windowSize.x, this.windowOnFullscreen.windowSize.y);
//            ImGui.setNextWindowPos(this.windowOnFullscreen.windowPos.x, this.windowOnFullscreen.windowPos.y);
//            this.windowOnFullscreen.imgui();
//        }
        this.windowOnFullscreen = SerializationUtils.clone(windowOnFullscreen);
    }

    public EditorImGuiWindow getWindowOnFullscreen() { return this.windowOnFullscreen; }

    private void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, (int) Window.getWidth(), (int) Window.getHeight());
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 MyEngine.renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        long backupWindowPtr = glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        glfwMakeContextCurrent(backupWindowPtr);
    }

    // If you want to clean a room after yourself - do it by yourself
    private void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void footer() {
//        ImGui.setNextWindowPos(Window.get().windowPosition.x, Window.get().windowPosition.y + Window.get().windowSize.y - 30.0f - TestFieldsWindow.getFloats[0]);
//        ImGui.setNextWindowSize(Window.get().windowSize.x, 30.0f);

//        ImGui.setCursorPosX(Window.get().windowPosition.x);
//        ImGui.setCursorPosY(Window.get().windowPosition.y + Window.get().windowSize.y);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImVec4 footerColor = ImGui.getStyle().getColor(ImGuiCol.MenuBarBg);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, footerColor.x, footerColor.y, footerColor.z, footerColor.w);

        ImGui.setCursorPosY(ImGui.getCursorPosY() - 3.4f);

//        ImGui.beginChildFrame(90901, Window.get().windowSize.x, 30.0f, ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
        ImGui.begin("##Footer", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoDocking |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus |
                ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.setCursorPosX(Window.get().windowSize.x - (Window.get().windowSize.x / 3.0f));
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0, 0, 0, 0);
        ImGui.beginChild("Messages Info", Window.get().windowSize.x / 3.0f, Window.get().windowSize.y);

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, ImGui.getStyle().getItemSpacingX() * 1.5f, 0);
        ImGui.beginChild("Messages Count", Window.get().windowSize.x / 2.0f, Window.get().windowSize.y);
        Map<ConsoleMessage.MessageType, Integer> messages = Console.getMessagesCount();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5.0f);
        for (ConsoleMessage.MessageType messageType : messages.keySet()) {
            ImVec4 messageColor = ConsoleMessage.getMessageColor(messageType);
            String messageIcon = ConsoleMessage.getMessageIcon(messageType);
            ImGui.textColored(
                    messageColor.x / 255.0f, messageColor.y / 255.0f, messageColor.z / 255.0f, messageColor.w / 255.0f,
                    messageIcon + " " + messages.get(messageType).toString());
            ImGui.sameLine();
        }
        ImGui.endChild();
        ImGui.popStyleVar();

        ImGui.sameLine();
        ImGui.beginChild("Last Message", Window.get().windowSize.x / 2.0f, Window.get().windowSize.y);
        ConsoleMessage lastMessage = Console.getLastMessage();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5.0f);
        if (!lastMessage.messageText.equals("")) {
            ImGui.textColored(
                    lastMessage.messageColor.x, lastMessage.messageColor.y,
                    lastMessage.messageColor.z, lastMessage.messageColor.w,
                    "[" + lastMessage.messageType + "] " + lastMessage.messageText);
        }
        ImGui.endChild();

        ImGui.endChild();
        ImGui.popStyleColor();

        ImGui.end();
        ImGui.popStyleVar(3);
        ImGui.popStyleColor();
    }

    private void setupDockspace() {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(3);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));

        menuBar.imgui();

        ImGui.end();

//        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY() + mainViewport.getWorkSizeY() - TestFieldsWindow.getFloats[4]);
//        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), 30.0f - TestFieldsWindow.getFloats[5]);
//        footer();
    }

    public void showModalPopup(String text, ConsoleMessage.MessageType type) {
        currentModalPopupShowTime = showModalPopupTime;
        currentModalPopupText = text;
        currentModalPopupType = type;
    }

    private void drawModalPopup() {
        ImVec2 tmp = new ImVec2();
        ImGui.calcTextSize(tmp, currentModalPopupText);

        ImVec2 popupSize = new ImVec2(tmp.x + ImGui.getStyle().getWindowPaddingX() * 2 + 114.0f, 49.0f);
        ImVec2 popupPosition = new ImVec2
                (Window.get().windowSize.x / 2.0f - popupSize.x / 2.0f + Window.get().windowPosition.x,
                Window.get().windowSize.y / 2.0f - popupSize.y / 2.0f + Window.get().windowPosition.y);

        ImGui.setNextWindowSize(popupSize.x, popupSize.y);
        ImGui.setNextWindowPos(popupPosition.x, popupPosition.y);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, ImGui.getStyle().getWindowPaddingX(), ImGui.getStyle().getWindowPaddingY() - 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 10.0f);
        ImVec4 color = ConsoleMessage.getMessageColor(currentModalPopupType);
        ImGui.pushStyleColor(ImGuiCol.WindowBg, Math.abs(color.x / 255.0f / 2.5f), Math.abs(color.y / 255.0f / 2.5f), Math.abs(color.z / 255.0f / 2.5f), color.w / 255.0f);
        ImGui.begin(currentModalPopupText, ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoFocusOnAppearing);

        ImGui.pushFont(modalPopupFont);
        ImGui.pushStyleColor(ImGuiCol.Text, color.x / 255 ,color.y / 255, color.z / 255, color.w / 255);
        EditorImGui.horizontalCenteredText(currentModalPopupText);
        ImGui.popStyleColor();

        ImGui.popFont();
        ImGui.end();
        ImGui.popStyleColor();
        ImGui.popStyleVar(2);
    }

    public InspectorWindow getInspectorWindow() { return this.inspectorWindow; }

    public SceneViewWindow getGameViewWindow() { return this.sceneViewWindow; }

    public Console getConsole() { return this.console; }

    public AssetsWindow getAssetsWindow() { return this.assetsWindow; }

    public SceneHierarchyWindow getSceneHierarchy() { return this.sceneHierarchyWindow; }
}
