package engine.entities;

import engine.toolbox.MouseListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private float distanceFromPlayer = 50; // zoom
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = 20;
    private float yaw;
    private float roll;

    private Player player;
    private Vector3f moveOffset = new Vector3f(10.0f, 2.0f, 0.0f);

    private float scrollSpeed = 2.0f;

    public Camera(Player player) { this.player = player; }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();

        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.transform.rotation.y + angleAroundPlayer);
//        System.out.println(position.x + " " + position.y + " " + position.z);

//        if (KeyListener.isKeyPressed(GLFW_KEY_W))
//            position.z -= moveSpeed * DisplayManager.getDelta();
//        else if (KeyListener.isKeyPressed(GLFW_KEY_S))
//            position.z += moveSpeed * DisplayManager.getDelta();
//
//        if (KeyListener.isKeyPressed(GLFW_KEY_A))
//            position.x -= moveSpeed * DisplayManager.getDelta();
//        else if (KeyListener.isKeyPressed(GLFW_KEY_D))
//            position.x += moveSpeed * DisplayManager.getDelta();
//
//        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE))
//            position.y += moveSpeed * DisplayManager.getDelta();
//        else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
//            position.y -= moveSpeed * DisplayManager.getDelta();
    }

    public Vector3f getPosition() { return this.position; }

    public float getPitch() { return this.pitch; }

    public float getYaw() { return this.yaw; }

    public float getRoll() { return this.roll; }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.transform.rotation.y + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.transform.position.x - offsetX;
        position.z = player.transform.position.z - offsetZ;
        position.y = player.transform.position.y + verticalDistance + moveOffset.y;
    }

    private float calculateHorizontalDistance() { return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch))); }

    private float calculateVerticalDistance() { return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch))); }

    private void calculateZoom() {
        float zoomLevel = MouseListener.getScrollY() * scrollSpeed;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            float pitchChange = MouseListener.getDy();
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            float angleChange = MouseListener.getDx();
            angleAroundPlayer += angleChange;
        }
    }

//    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
}
