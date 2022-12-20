package engine.entities;

import engine.renderEngine.Window;
import engine.toolbox.KeyListener;
import engine.toolbox.MouseListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

//    private float distanceFromPlayer = 1; // zoom
    private float angleAroundPlayer = 0.0f;

    private Vector3f position = new Vector3f(0.0f);
    private Vector3f direction = new Vector3f(0.0f);
    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private float roll = 0.0f;

//    private GameObject parent;

    private float actualMoveSpeed = 1.0f;

    private float moveSpeed = 1.0f;
    private float sprintSpeed = 5.0f;

    private float sensitivity = 0.35f;

    private Vector3f startPosition = new Vector3f(-0.66f, 1.35f, -2.1f);
    private Vector3f startRotation = new Vector3f(20.64f, 0.0f, 160.0f);
//    private Vector3f startRotation = new Vector3f(23.8f, 0.0f, 160.0f);
//    private float scrollSpeed = 0.15f;

    public Camera() {
        this.position = startPosition;

        this.pitch = startRotation.x;
        this.roll = startRotation.y;
        this.yaw = startRotation.z;

        angleAroundPlayer = 180 - startRotation.z;
    }

//    public Camera() {
//        this.parent = Window.get().getScene().getGameObject("MainCamera");
//        if (this.parent == null) {
//            this.parent = Window.get().getScene().createGameObject("MainCamera");
//            Window.get().getScene().addGameObjectToScene(parent);
//        }
//    }

    public void move() { // TODO MAKE EDITOR CAMERA
//        calculateZoom();
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            calculatePitch();
            calculateAngleAroundPlayer();
        }

//        float horizontalDistance = calculateHorizontalDistance();
//        float verticalDistance = calculateVerticalDistance();

//        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - angleAroundPlayer;
//        System.out.println(position.x + " " + position.y + " " + position.z);

        direction.x = 0;
        direction.y = 0;
        direction.z = 0;

        checkInput();
        movePos(direction);

        position.x = position.x;
        position.y = position.y;
        position.z = position.z;
    }

    private void checkInput() {
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
            actualMoveSpeed = sprintSpeed;
        else
            actualMoveSpeed = moveSpeed;

        if (KeyListener.isKeyPressed(GLFW_KEY_W))
            direction.z += actualMoveSpeed * Window.getDelta();
        else if (KeyListener.isKeyPressed(GLFW_KEY_S))
            direction.z -= actualMoveSpeed * Window.getDelta();

        if (KeyListener.isKeyPressed(GLFW_KEY_A))
            direction.x += actualMoveSpeed * Window.getDelta();
        else if (KeyListener.isKeyPressed(GLFW_KEY_D))
            direction.x -= actualMoveSpeed * Window.getDelta();

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE))
            direction.y += actualMoveSpeed * Window.getDelta();
        else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            direction.y -= actualMoveSpeed * Window.getDelta();
    }

    private void movePos(Vector3f direction) {
        if (direction.z != 0 ) {
            position.x -= (float) Math.sin(Math.toRadians(yaw)) * -1.0f * direction.z;
            position.z -= (float) Math.cos(Math.toRadians(yaw)) * direction.z;
        }
        if (direction.x != 0) {
            position.x -= (float) Math.sin(Math.toRadians(yaw - 90)) * -1.0f * direction.x;
            position.z -= (float) Math.cos(Math.toRadians(yaw - 90)) * direction.x;
        }

        position.y += direction.y;
    }

    public Vector3f getPosition() { return this.position; }

    public float getPitch() { return this.pitch; }

    public float getYaw() { return this.yaw; }

    public float getRoll() { return this.roll; }

//    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
//        float theta = parent.transform.rotation.y + angleAroundPlayer;
//        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
//        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
//        position.x = parent.transform.position.x - offsetX;
//        position.z = parent.transform.position.z - offsetZ;
//        position.y = parent.transform.position.y + verticalDistance;
//    }

//    private float calculateHorizontalDistance() { return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch))); }
//
//    private float calculateVerticalDistance() { return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch))); }

//    private void calculateZoom() {
//        float zoomLevel = MouseListener.getScrollY() * scrollSpeed;
//        distanceFromPlayer -= zoomLevel;
//    }

    private void calculatePitch() {
        float pitchChange = MouseListener.getDy() * sensitivity;
        pitch -= pitchChange;
    }

    private void calculateAngleAroundPlayer() {
        float angleChange = MouseListener.getDx() * sensitivity;
        angleAroundPlayer += angleChange;
    }

//    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
}
