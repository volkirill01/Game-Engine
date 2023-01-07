package engine.toolbox;

import engine.entities.GameObject;
import engine.imGui.InspectorWindow;
import engine.renderEngine.PickingTexture;
import engine.renderEngine.Window;
import engine.scene.Scene;
import engine.toolbox.input.KeyCode;
import engine.toolbox.input.KeyListener;
import engine.toolbox.input.MouseListener;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;


public class Mouse_EditorActions {

    public static String nonPickableTag = "##NonPickable";

    private GameObject holdingObject;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    public void pickupObject(GameObject go) {
        if (this.holdingObject != null)
            this.holdingObject.destroy();

        this.holdingObject = go;
        this.holdingObject.addTag(nonPickableTag);
        this.holdingObject.setNoSerialize();
        Window.get().getScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject newObj = this.holdingObject.copy();

        newObj.removeTag(nonPickableTag);
        this.holdingObject.doSerialization();
        Window.get().getScene().addGameObjectToScene(newObj);
    }

    public void update() {
        debounce -= Time.deltaTime();
        PickingTexture pickingTexture = Window.get().pickingTexture;
        Scene currentScene = Window.get().getScene();

        if (holdingObject != null) {
            holdingObject.transform.localPosition = new Vector3f(MouseListener.getWorld(), 0.0f);
            holdingObject.transform.localPosition = new Vector3f(
                    ((int) Math.floor(holdingObject.transform.localPosition.x / Window.getWidth()) * Window.getWidth()) + Window.getWidth() / 2.0f,
                    ((int) Math.floor(holdingObject.transform.localPosition.y / Window.getHeight()) * Window.getHeight()) + Window.getHeight() / 2.0f,
                    0.0f);

            if (MouseListener.mouseButtonDown(KeyCode.Mouse_Button_Left)) {
                float halfWidth = Window.getWidth() / 2.0f;
                float halfHeight = Window.getHeight() / 2.0f;
                if (MouseListener.isDragging(KeyCode.Mouse_Button_Left) &&
                        !blockInSquare(holdingObject.transform.localPosition.x - halfWidth, holdingObject.transform.localPosition.y - halfHeight)) {
                    place();
                } else if (!MouseListener.isDragging(KeyCode.Mouse_Button_Left) && debounce < 0 && !blockInSquare(holdingObject.transform.localPosition.x - halfWidth, holdingObject.transform.localPosition.y - halfHeight)) {
                    place();
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyDown(KeyCode.Escape) || MouseListener.mouseButtonDown(KeyCode.Mouse_Button_Right)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        } else {
            if (!MouseListener.isDragging(KeyCode.Mouse_Button_Left) && MouseListener.mouseButtonDown(KeyCode.Mouse_Button_Left) && debounce < 0) {
                GameObject pickedObj = getObjectOnMousePosition();
                if (pickedObj != null && !pickedObj.hasTag(nonPickableTag))
                    Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(pickedObj);
                else if (pickedObj == null)
                    Window.get().getImGuiLayer().getInspectorWindow().clearSelected();

                this.debounce = 0.2f;
            } else if (MouseListener.isDragging(KeyCode.Mouse_Button_Left)) {
                if (!boxSelectSet) {
                    Window.get().getImGuiLayer().getInspectorWindow().clearSelected();
                    boxSelectStart = MouseListener.getScreen();
                    boxSelectSet = true;
                }
                boxSelectEnd = MouseListener.getScreen();
                Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
                Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
                Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
//                DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize),
//                        new Vector2f(halfSize).mul(2.0f),
//                        0.0f, new Vector3f(0.7f, 0.7f, 0.7f));
            } else if (boxSelectSet) {
                boxSelectSet = false;
                int screenStartX = (int) boxSelectStart.x;
                int screenStartY = (int) boxSelectStart.y;
                int screenEndX = (int) boxSelectEnd.x;
                int screenEndY = (int) boxSelectEnd.y;
                boxSelectStart.zero();
                boxSelectEnd.zero();

                if (screenEndX < screenStartX) {
                    int tmp = screenStartX;
                    screenStartX = screenEndX;
                    screenEndX = tmp;
                }
                if (screenEndY < screenStartY) {
                    int tmp = screenStartY;
                    screenStartY = screenEndY;
                    screenEndY = tmp;
                }

                float[] gameObjectIds = pickingTexture.readPixels(
                        new Vector2i(screenStartX, screenStartY),
                        new Vector2i(screenEndX, screenEndY)
                );

                Set<Integer> uniqueGameObjectIds = new HashSet<>();
                for (float objectId : gameObjectIds)
                    uniqueGameObjectIds.add((int)objectId);

                if (uniqueGameObjectIds.size() > 2) {
                    for (Integer gameObjectId : uniqueGameObjectIds) {
                        GameObject pickedObj = Window.get().getScene().getGameObject(gameObjectId);
                        if (pickedObj != null && !pickedObj.hasTag(nonPickableTag))
                            Window.get().getImGuiLayer().getInspectorWindow().addActiveGameObject(pickedObj);
                    }
                } else {
                    for (Integer gameObjectId : uniqueGameObjectIds) {
                        GameObject pickedObj = Window.get().getScene().getGameObject(gameObjectId);
                        if (pickedObj != null && !pickedObj.hasTag(nonPickableTag))
                            Window.get().getImGuiLayer().getInspectorWindow().setActiveGameObject(pickedObj);
                    }
                }
            }
        }
    }

    private boolean blockInSquare(float x, float y) {
        InspectorWindow inspectorWindow = Window.get().getImGuiLayer().getInspectorWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Window.getHeight(), Window.getHeight()));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
        float[] gameObjectIds = inspectorWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (float gameObjectId : gameObjectIds) {
            if (gameObjectId >= 0) {
                GameObject pickedObj = Window.get().getScene().getGameObject((int)gameObjectId);
                if (pickedObj != null)
                    if (!pickedObj.hasTag(nonPickableTag))
                        return true;
            }
        }
        return false;
    }

    public static GameObject getObjectOnMousePosition() {
        int x = (int) MouseListener.getScreenX();
        int y = (int) MouseListener.getScreenY();
        int gameObjectId = Window.get().pickingTexture.readPixel(x, y);
        return Window.get().getScene().getGameObject(gameObjectId);
    }
}
