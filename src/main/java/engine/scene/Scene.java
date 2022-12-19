package engine.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.components.Component;
import engine.components.ComponentDeserializer;
import engine.components.Transform;
import engine.entities.Camera;
import engine.entities.GameObject;
import engine.entities.GameObjectDeserializer;
import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;
import org.joml.Vector3f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private String scenePath;
    public String sceneName;
//    private Renderer renderer;
    private Camera camera;
    private boolean isRunning;
    private List<GameObject> gameObjects;
    private List<GameObject> pendingObjects;
//    private Physics2D physics2D;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.scenePath = sceneInitializer.getScenePath();
        this.sceneName = this.scenePath.replace("\\", "/").split("/")[this.scenePath.replace("\\", "/").split("/").length - 1];
        this.sceneInitializer = sceneInitializer;
//        this.physics2D = new Physics2D();
//        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.pendingObjects = new ArrayList<>();
        this.isRunning = false;
    }

//    public Physics2D getPhysics() { return this.physics2D; }

    public void setCamera(Camera camera) { this.camera = camera; }

    public void init() {
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
//            this.renderer.add(go);

//            for (GameObject child : go.transform.childs) no uncomment
//                child.start(); no uncomment

//            this.physics2D.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
            go.addChildsToScene();
        } else {
            pendingObjects.add(go);
            go.addChildsToScene();
        }
    }

    public void destroy() {
        for (GameObject go: gameObjects)
            go.destroy();
    }

    public <T extends Component> GameObject getGameObjectWith(Class<T> calzz) {
        for (GameObject go: gameObjects)
            if (go.getComponent(calzz) != null)
                return go;

        return null;
    }

    public List<GameObject> getGameObjects() { return this.gameObjects; }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);
    }

    public GameObject getGameObject(String gameObjectName) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.name.equals(gameObjectName))
                .findFirst();
        return result.orElse(null);
    }

    public GameObject getGameObjectWithTag(String tag) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getAllTags().contains(tag))
                .findFirst();
        return result.orElse(null);
    }

    public List<GameObject> getGameObjectsWithTag(String tag) {
        List<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getAllTags().contains(tag))
                .toList();

        if (result.size() > 0)
            return result;

        return null;
    }

    public void editorUpdate() {
//        this.camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate();

//            for (GameObject child : go.transform.childs)
//                child.editorUpdate(deltaTime);

            if (go.isDeath()) {
                gameObjects.remove(i);
//                this.renderer.destroyGameObject(go);
//                this.physics2D.destroyGameObject(go);
                i--;
            }
        }

        for (GameObject go: pendingObjects) {
            gameObjects.add(go);
            go.start();
//            this.renderer.add(go);
//            this.physics2D.add(go);
        }
        pendingObjects.clear();

//        if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) // TODO MAKE CAMERA MOVABLE WITH KEYS
//            camera.position.y -= deltaTime * 150f;
//        else if (KeyListener.isKeyPressed(GLFW_KEY_UP))
//            camera.position.y += deltaTime * 150f;
//        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))
//            camera.position.x -= deltaTime * 150f;
//        else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))
//            camera.position.x += deltaTime * 150f;
    }

    public void update() {
//        this.camera.adjustProjection();
//        this.physics2D.update(deltaTime);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update();

//            for (GameObject child : go.transform.childs)
//                child.update(deltaTime);

            if (go.isDeath()) {
                gameObjects.remove(i);
//                this.renderer.destroyGameObject(go);
//                this.physics2D.destroyGameObject(go);
                i--;
            }
        }

        for (GameObject go: pendingObjects) {
            gameObjects.add(go);
            go.start();
//            this.renderer.add(go);
//            this.physics2D.add(go);
        }
        pendingObjects.clear();
    }

    public void render() {  } // this.renderer.render();

    public Camera camera() { return this.camera; }

    public void imgui() { this.sceneInitializer.imgui(); }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        try {
            FileWriter writer = new FileWriter(this.scenePath);
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj: this.gameObjects)
                if (obj.doSerialization() && obj.transform.parent == null)
                    objsToSerialize.add(obj);

            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(this.scenePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;

            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for (Component c: objs[i].getAllComponents())
                    if (c.getUid() > maxCompId)
                        maxCompId = c.getUid();
                if (objs[i].getUid() > maxGoId)
                    maxGoId = objs[i].getUid();
            }

            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }

        Window.get().getImGuiLayer().getInspectorWindow().clearSelected();
    }
}
