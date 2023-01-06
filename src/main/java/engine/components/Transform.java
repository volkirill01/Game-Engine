package engine.components;

import engine.entities.GameObject;
import engine.imGui.EditorImGui;
import engine.imGui.SceneHierarchyWindow;
import engine.renderEngine.Window;
import imgui.ImGui;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Transform extends Component {

    public Vector3f position;
    public Vector3f localPosition;
    public Vector3f rotation;
    public Vector3f localRotation;
    public Vector3f scale;
    public Vector3f localScale;

    public transient GameObject parent = null;
    public transient GameObject mainParent = null;
    public List<GameObject> childs = new ArrayList<>();

    public Transform() { init(new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), new ArrayList<>()); }

    public Transform(Vector3f localPosition) { init(localPosition, new Vector3f(0.0f), new Vector3f(1.0f), new ArrayList<>()); }

    public Transform(Vector3f localPosition, Vector3f localScale) { init(localPosition, new Vector3f(0.0f), localScale, new ArrayList<>()); }

    public Transform(Vector3f localPosition, Vector3f localScale, ArrayList<GameObject> childs) { init(localPosition, new Vector3f(0.0f), localScale, childs); }

    public Transform(Vector3f localPosition, Vector3f localRotation, Vector3f localScale, ArrayList<GameObject> childs) { init(localPosition, localRotation, localScale, childs); }

    public Transform(Vector3f localPosition, Vector3f localRotation, Vector3f localScale) { init(localPosition, localRotation, localScale, new ArrayList<>()); }

    public Transform(Vector3f localPosition, ArrayList<GameObject> childs) { init(localPosition, new Vector3f(0.0f), new Vector3f(1.0f), childs); }

    public void init(Vector3f position, Vector3f rotation, Vector3f scale, ArrayList<GameObject> childs) {
        this.position = position;
        this.localPosition = position;
        this.scale = scale;
        this.localScale = scale;
        this.rotation = rotation;
        this.localRotation = rotation;
        this.childs = childs;
    }

    public void set(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    @Override
    public void editorUpdate() {
        if (this.parent != null) { // TODO ADD RELATIVE TO PARENT ROTATION
            this.position.set(this.localPosition).add(parent.transform.position);
            this.rotation.set(this.localRotation).add(parent.transform.rotation);
            this.scale.set(this.localScale).mul(parent.transform.scale);
        } else {
            this.position.set(this.localPosition);
            this.rotation.set(this.localRotation);
            this.scale.set(this.localScale);
        }
    }

    public void increasePosition(Vector3f position) { this.localPosition.add(position); }

    public void increasePosition(float x, float y, float z) { this.localPosition.add(x, y, z); }

    public void increasePosition(float position) { this.localPosition.add(position, position, position); }

    public void increaseRotation(Vector3f rotation) { this.localRotation.add(rotation); }

    public void increaseRotation(float x, float y, float z) { this.localRotation.add(x, y, z); }

    public void increaseRotation(float rotation) { this.localRotation.add(rotation, rotation, rotation); }

    public void increaseScale(Vector3f scale) { this.localScale.add(scale); }

    public void increaseScale(float x, float y, float z) { this.localScale.add(x, y, z); }

    public void increaseScale(float scale) { this.localScale.add(scale, scale, scale); }

    @Override
    public void imgui() {
//        EditorImGui.field_Text("Parent", parent != null ? parent.name : "null", "");
//        EditorImGui.field_Text("Main Parent", mainParent != null ? mainParent.name : "null", "");

//        this.position = EditorImGui.field_Vector3f("World Position", this.position);
//        this.rotation = EditorImGui.field_Vector3f("World Rotation", this.rotation);
//        this.scale = EditorImGui.field_Vector3f("World Scale", this.scale, new Vector3f(1.0f));

        this.localPosition = EditorImGui.field_Vector3f("Position", this.localPosition);
        this.localRotation = EditorImGui.field_Vector3f("Rotation", this.localRotation);
        this.localScale = EditorImGui.field_Vector3f("Scale", this.localScale, new Vector3f(1.0f));
//        }
    }

    public Transform copy() {
        Transform copy = new Transform(new Vector3f(this.position), new Vector3f(this.scale), new Vector3f(this.rotation), new ArrayList<>(childs));
//        for (GameObject child : copy.childs)
//            child.getComponent(SpriteRenderer.class).setDirty(); // TODO пофиксить баг с чёрными комиями

        return copy;
    }

    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
        to.rotation = this.rotation;
        to.childs = this.childs;

//        for (GameObject child : to.childs)
//            child.getComponent(SpriteRenderer.class).setDirty(); // TODO пофиксить баг с чёрными комиями
    }

    @Override
    public void reset() {
        this.localPosition = new Vector3f(0.0f);
        this.localRotation = new Vector3f(0.0f);
        this.localScale = new Vector3f(1.0f);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return  t.position.equals(this.position) &&
                t.rotation == this.rotation &&
                t.scale.equals(this.scale) &&
                t.parent == this.parent &&
                t.mainParent == this.mainParent &&
                t.childs.equals(this.childs);
    }

    public void addChild(GameObject child) {
        if (child.transform.parent != null) {
            child.transform.parent.transform.removeChild(child);
        }

        this.childs.add(child);
        child.transform.parent = this.gameObject;

//        Vector3f oldScale = child.transform.scale;

        if (this.mainParent != null) {
            child.transform.mainParent = this.mainParent;
            if (child.transform.childs != null)
                child.transform.setMainParentInChilds(this.mainParent);
        } else {
            child.transform.mainParent = this.gameObject;
            if (child.transform.childs != null)
                child.transform.setMainParentInChilds(this.gameObject);
        }

//        child.transform.localPosition.sub(this.position);
//        child.transform.localRotation.sub(this.rotation);
//        child.transform.localScale.div(this.scale); // TODO пофиксить баг с изменением размера
    }

    public void removeChild(GameObject child) {
        this.childs.remove(child);
        child.transform.parent = null;
        child.transform.mainParent = null;
    }

    public int getChildCount() { return this.childs.size(); }

    public void setMainParentInChilds(GameObject mainParent) {
        for (GameObject child : this.childs)
            child.transform.mainParent = mainParent;
    }

    public void addChildsToScene() {
        for (GameObject child : this.childs) {
            Window.get().getScene().addGameObjectToScene(child);
            child.transform.addChildsToScene();
            child.transform.parent = this.gameObject;

            if (this.mainParent != null) {
                child.transform.mainParent = this.mainParent;
                if (child.transform.childs != null)
                    child.transform.setMainParentInChilds(this.mainParent);
            } else {
                child.transform.mainParent = this.gameObject;
                if (child.transform.childs != null)
                    child.transform.setMainParentInChilds(this.gameObject);
            }
        }
    }

    public void drawInSceneHierarchy(int level) {
        float startX = ImGui.getCursorStartPosX() - 2.0f;

        boolean treeNodeOpen = SceneHierarchyWindow.doTreeNode(gameObject, startX, level);

        if (treeNodeOpen) {
            if (getChildCount() > 0) {
                level++;
                for (int i = 0; i < this.childs.size(); i++) {
                    Window.get().getImGuiLayer().getSceneHierarchy().globalGameObjectIndex++;
                    if (this.childs.get(i) != null)
                        this.childs.get(i).transform.drawInSceneHierarchy(level);
                }
            }
            ImGui.treePop();
        }
    }
}
