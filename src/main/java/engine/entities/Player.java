package engine.entities;

import engine.Settings;
import engine.components.Transform;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.TexturedModel;
import engine.terrain.Terrain;
import engine.toolbox.KeyListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {

    private float moveSpeed = 10;
    private float turnSpeed = 160;
    private float jumpPower = 30;

    private float currentMoveSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
        super("Player");
        transform = new Transform(position, rotation, scale);
        addComponent(new MeshRenderer(model));
    }

    public void move(Terrain terrain) {
        checkInputs();
        super.transform.increaseRotation(new Vector3f(0, currentTurnSpeed * Window.getDelta(), 0));
        float distance = currentMoveSpeed * Window.getDelta();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.transform.rotation.y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.transform.rotation.y)));
        super.transform.increasePosition(new Vector3f(dx, 0, dz));
        upwardsSpeed += Settings.GRAVITY * Window.getDelta();
        super.transform.increasePosition(new Vector3f(0, upwardsSpeed * Window.getDelta(), 0));
        float terrainHeight = terrain.getHeight(super.transform.position.x, super.transform.position.z);
        if (super.transform.position.y < terrainHeight) {
            upwardsSpeed = 0;
            isInAir = false;
            super.transform.position.y = terrainHeight;
        }
    }

    private void jump() {
        if (!isInAir) {
            upwardsSpeed = jumpPower;
            isInAir = true;
        }
    }

    private void checkInputs() {
        if (KeyListener.isKeyPressed(GLFW_KEY_W))
            currentMoveSpeed = moveSpeed;
        else if (KeyListener.isKeyPressed(GLFW_KEY_S))
            currentMoveSpeed = -moveSpeed;
        else
            currentMoveSpeed = 0;

        if (KeyListener.isKeyPressed(GLFW_KEY_A))
            currentTurnSpeed = turnSpeed;
        else if (KeyListener.isKeyPressed(GLFW_KEY_D))
            currentTurnSpeed = -turnSpeed;
        else
            currentTurnSpeed = 0;

        if (KeyListener.keyBeginPress(GLFW_KEY_SPACE))
            jump();
//        else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
//            position.y -= moveSpeed * DisplayManager.getDelta();
    }
}
