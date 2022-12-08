package engine.toolbox;


import engine.renderEngine.Window;
import org.joml.Vector2f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY, worldX, worldY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean mouseButtonBeginPress[] = new boolean[9];
    private boolean[] isDragging = new boolean[9];

    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0f;
        this.scrollY = 0.0f;
        this.xPos = 0.0f;
        this.yPos = 0.0f;
    }

    public static void endFrame() {
        get().scrollY = 0.0f;
        get().scrollX = 0.0f;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().mouseButtonBeginPress = new boolean[9];
    }

    public static void clear() {
        get().scrollX = 0.0f;
        get().scrollY = 0.0f;
        get().xPos = 0.0f;
        get().yPos = 0.0f;
        get().lastX = 0.0f;
        get().lastY = 0.0f;
        get().lastWorldX = 0.0;
        get().lastWorldY = 0.0;
        get().mouseButtonDown = 0;
        Arrays.fill(get().isDragging, false);
        Arrays.fill(get().mouseButtonPressed, false);
        Arrays.fill(get().mouseButtonBeginPress, false);
    }

    public static MouseListener get() {
        if (MouseListener.instance == null)
            MouseListener.instance = new MouseListener();
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        if (get().mouseButtonDown > 0)
            for (int i = 0; i < get().mouseButtonPressed.length; i++)
                get().isDragging[i] = get().mouseButtonPressed[i];

        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().mouseButtonDown++;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
                get().mouseButtonBeginPress[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonDown--;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().mouseButtonBeginPress[button] = false;
                get().isDragging[button] = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static float getX() { return (float)get().xPos; }

    public static float getY() { return (float)get().yPos; }

    public static float getDx() { return (float) (get().lastX - get().xPos); }

    public static float getDy() { return (float) (get().lastY - get().yPos); }

    public static float getScrollX() { return (float)get().scrollX; }

    public static float getScrollY() { return (float)get().scrollY; }

    public static boolean isDragging(int button) { return get().isDragging[button]; }

    public static boolean mouseButtonDown(int button) { return get().mouseButtonPressed[button]; }

    public static boolean mouseButtonBeginPress(int button) { return get().mouseButtonBeginPress[button]; }

    public static float getScreenX() { return getScreen().x; }

    public static float getScreenY() { return getScreen().y; }

    public static Vector2f getScreen() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * Window.getWidth();
        float currentY = getY() - get().gameViewportPos.y;
        currentY = Window.getHeight() - ((currentY / get().gameViewportSize.y) * Window.getHeight());

        return new Vector2f(currentX, currentY);
    }

    public static float getWorldDx() { return (float)(get().lastWorldX - get().worldX); }

    public static float getWorldDy() { return (float)(get().lastWorldY - get().worldY); }

    public static void setGameViewportPos(Vector2f gameViewportPos) { get().gameViewportPos.set(gameViewportPos); }

    public static void setGameViewportSize(Vector2f gameViewportSize) { get().gameViewportSize.set(gameViewportSize); }
}
