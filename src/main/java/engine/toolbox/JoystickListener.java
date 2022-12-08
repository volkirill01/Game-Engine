package engine.toolbox;

import static org.lwjgl.glfw.GLFW.*;

public class JoystickListener {
    private static JoystickListener instance;
    private boolean buttonsPressed[];

    private JoystickListener() { }

    public static JoystickListener get() {
        if (JoystickListener.instance == null)
            JoystickListener.instance = new JoystickListener();
        return JoystickListener.instance;
    }

    public static void joystickCallback(int joystickId, int event) {
        if (event == GLFW_CONNECTED) {
            // The joystick was connected
//            Console.log(Message.MessageType.Info, "Joystick connected!");
            System.out.println("Joystick connected!");
        } else if (event == GLFW_DISCONNECTED) {
            // The joystick was disconnected
//            Console.log(Message.MessageType.Info, "Joystick disconnected!");
            System.out.println("Joystick disconnected!");
        }
    }

    public static boolean isJoystickButtonPressed(int keyCode) { return get().buttonsPressed[keyCode]; }
}
