package engine.toolbox;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {

    private static long lastFrameTime;
    private static float delta;

    public static void init() {
        lastFrameTime = getCurrentTime();
    }

    public static void update() {
        long currentFrameTime = getCurrentTime();

        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    private static long getCurrentTime() { return (long) (glfwGetTime() * 1000); }

    public static float deltaTime() { return delta; }
}
