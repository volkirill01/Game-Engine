package engine.renderEngine;

import engine.entities.Camera;
import engine.entities.GameObject;
import engine.eventSystem.EventSystem;
import engine.eventSystem.Events.Event;
import engine.eventSystem.Observer;
import engine.imGui.ConsoleMessage;
import engine.imGui.ImGuiLayer;
import engine.scene.*;
import engine.toolbox.KeyListener;
import engine.toolbox.MouseListener;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import javax.swing.*;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static Window window = null;
    private long glfwWindow;

    private String originalTitle = "New Game Engine (0.4)";

    private static int WIDTH;
    private static int HEIGHT;

    private static long lastFrameTime;
    private static float delta;

    private static int screenImage;

    private int[] tmpWidth = new int[1];
    private int[] tmpHeight = new int[1];
    private int[] tmpWindowPosX = new int[1];
    private int[] tmpWindowPosY = new int[1];
    public Vector2f windowSize = new Vector2f();
    public Vector2f windowPosition = new Vector2f();

    private static ImGuiLayer imGuiLayer;
    public PickingTexture pickingTexture;
    public boolean runtimePlaying = false;

    private SceneManager sceneManager;

    public boolean drawGrid = true;
    public boolean drawDebug = true;

    private Scene currentScene;

    public Window() { addToEventSystem(); }

    public static Window get() {
        if (Window.window == null)
            Window.window = new Window();
        return Window.window;
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

//        glfwWindowHint(GLFW_SAMPLES, 8); // multi sampling(Antialiasing)

        Vector4f workArea = getWorkArea();
        WIDTH = (int) workArea.z;
        HEIGHT = (int) workArea.w;

        // Create the window
        glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, "3D Render Engine", NULL, NULL);
        if (glfwWindow == NULL)
            throw new IllegalStateException("Failed to create the GLFW window.");

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            WIDTH = newWidth;
            HEIGHT = newHeight;
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_MULTISAMPLE); // multi sampling(Antialiasing)
//        glEnable(GL_FRAMEBUFFER_SRGB); // gamma correction

        this.pickingTexture = new PickingTexture(WIDTH, HEIGHT);
        pickingTexture.readPixel(0, 0);
        glViewport(0, 0, WIDTH, HEIGHT);

        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);

        imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        imGuiLayer.initImGui();

        sceneManager = new SceneManager();
        SceneManager.loadScene("Assets/testScene.scene");
    }

    public Vector4f getWorkArea() {
        int[] windowPosX = {0};
        int[] windowPosY = {0};
        int[] windowWidth = {0};
        int[] windowHeight = {0};

        glfwGetMonitorWorkarea(glfwGetPrimaryMonitor(), windowPosX, windowPosY, windowWidth, windowHeight);
        System.out.println("Window work area:\n\t(windowPosX: " + windowPosX[0] + ", windowPosY: " + windowPosY[0] + ", windowWidth: " + windowWidth[0] + ", windowHeight: "+ windowHeight[0] + ")");

        return new Vector4f(windowPosX[0], windowPosY[0], windowWidth[0], windowHeight[0]);
    }

    public void changeScene(SceneInitializer sceneInitializer) {

        if (currentScene != null)
            currentScene.destroy();

        getImGuiLayer().getInspectorWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        glfwSetWindowTitle(Window.window.glfwWindow, Window.window.originalTitle + " /Scene: " + currentScene.sceneName);

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public Scene getScene() { return currentScene; }

    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }

    public static void setScreenImage(int _screenImage) { screenImage = _screenImage; }

    public static int getScreenImage() { return screenImage; }

    public ImGuiLayer getImGuiLayer() { return imGuiLayer; }

    private static boolean closed = false;

    public static boolean isClosed() {
        glfwSetWindowCloseCallback(get().glfwWindow, Window.get()::close);
        return closed;
    }

    private void close(long l) {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Save Scene before exit?"));

        int res = JOptionPane.showConfirmDialog(null, panel, "Save Scene",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.YES_OPTION) {
            get().getScene().save();
            closed = true;
        } else if (res == JOptionPane.NO_OPTION)
            closed = true;
        else if (res == JOptionPane.CANCEL_OPTION)
            closed = false;
    }

    public static void createDisplay() {
        get().init();
        lastFrameTime = getCurrentTime();
    }

    public void updateDisplay() {
        glfwGetWindowSize(get().glfwWindow, tmpWidth, tmpHeight);
        glfwGetWindowPos(get().glfwWindow, tmpWindowPosX, tmpWindowPosY);
        windowSize.x = tmpWidth[0];
        windowSize.y = tmpHeight[0];
        windowPosition.x = tmpWindowPosX[0];
        windowPosition.y = tmpWindowPosY[0];

        glfwSwapBuffers(get().glfwWindow);
        long currentFrameTime = getCurrentTime();

        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getDelta() { return delta;}

    public static void closeDisplay() {
        glfwDestroyWindow(get().glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static float getWidth() { return WIDTH; }

    public static float getHeight() { return HEIGHT; }

    private static long getCurrentTime() { return (long) (glfwGetTime() * 1000); }

    @Override
    public void addToEventSystem() { EventSystem.addObserver(this); }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngineStartPlay -> {
                this.runtimePlaying = true;
                get().getImGuiLayer().getInspectorWindow().clearSelected();
                currentScene.save();
                get().changeScene(new LevelSceneInitializer(SceneManager.getCurrentScene()));
            }
            case GameEngineStopPlay -> {
                this.runtimePlaying = false;
                get().getImGuiLayer().getGameViewWindow().setNotPlaying();
                get().changeScene(new LevelEditorSceneInitializer(SceneManager.getCurrentScene()));
            }
            case LoadLevel -> {
                get().getImGuiLayer().showModalPopup("Open Scene", ConsoleMessage.MessageType.Info);
                get().changeScene(new LevelEditorSceneInitializer(SceneManager.getCurrentScene()));
            }
            case SaveLevel -> {
                get().getImGuiLayer().showModalPopup("Scene Saved", ConsoleMessage.MessageType.Info);
                currentScene.save();
            }
        }
    }
}
