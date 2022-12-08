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
    public Vector3f scale;
    public Vector3f localScale;
    public Vector3f localRotation;
    public Vector3f rotation;
    public transient GameObject parent = null;
    public transient GameObject mainParent = null;
    public List<GameObject> childs = new ArrayList<>();

    public Transform() { init(new Vector3f(), new Vector3f(1.0f), new Vector3f(1.0f), new ArrayList<>()); }

    public Transform(Vector3f position) { init(position, new Vector3f(1.0f), new Vector3f(1.0f), new ArrayList<>()); }

    public Transform(Vector3f position, Vector3f scale) { init(position, scale, new Vector3f(1.0f), new ArrayList<>()); }

    public Transform(Vector3f position, Vector3f scale, ArrayList<GameObject> childs) { init(position, scale, new Vector3f(1.0f), childs); }

    public Transform(Vector3f position, Vector3f scale, Vector3f localRotation, ArrayList<GameObject> childs) { init(position, scale, localRotation, childs); }

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) { init(position, scale, rotation, new ArrayList<>()); }

    public Transform(Vector3f position, ArrayList<GameObject> childs) { init(position, new Vector3f(1.0f), new Vector3f(1.0f), childs); }

    public void init(Vector3f position, Vector3f scale, Vector3f rotation, ArrayList<GameObject> childs) {
        this.position = position;
        this.localPosition = this.position;
        this.scale = scale;
        this.localScale =  this.scale;
        this.localRotation = rotation;
        this.rotation = this.localRotation;
        this.childs = childs;
    }

    public void set(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.localPosition = position;
        this.localRotation = rotation;
        this.localScale = scale;
    }

    public void increasePosition(Vector3f position) { this.localPosition.add(position); }

    public void increaseRotation(Vector3f rotation) { this.localRotation.add(rotation); }

    public void increaseScale(Vector3f scale) { this.localScale.add(scale); }

    @Override
    public void imgui() {
//        EditorImGui.inputText("Parent", parent != null ? parent.name : "null", "");
//        EditorImGui.inputText("Main Parent", mainParent != null ? mainParent.name : "null", "");

        if (this.parent != null) {
            EditorImGui.drawVec3Control("Position", this.localPosition);
            EditorImGui.drawVec3Control("Rotation", this.rotation);
            EditorImGui.drawVec3Control("Scale", this.localScale, 1.0f);
        } else {
            EditorImGui.drawVec3Control("Position", this.position);
            EditorImGui.drawVec3Control("Rotation", this.rotation);
            EditorImGui.drawVec3Control("Scale", this.scale, 1.0f);
        }
    }

    public Transform copy() {
        Transform copy = new Transform(new Vector3f(this.position), new Vector3f(this.scale), this.rotation, new ArrayList<>(childs));
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
    public void update() { // TODO закончить изменение позиции на локальную и размера
        if (parent != null) {
            position = new Vector3f(
                    (localPosition.x + mainParent.transform.localPosition.x) + parent.transform.localScale.x,
                    (localPosition.y + mainParent.transform.localPosition.y) + parent.transform.localScale.y,
                    (localPosition.z + mainParent.transform.localPosition.z) + parent.transform.localScale.z);
            scale = new Vector3f(
                    localScale.x * mainParent.transform.localScale.x,
                    localScale.y * mainParent.transform.localScale.y,
                    localScale.z * mainParent.transform.localScale.z);
            localRotation = parent.transform.rotation.add(this.rotation);
        } else {
            position = localPosition;
            scale = localScale;
            localRotation = rotation;
        }
    }

    @Override
    public void editorUpdate() {
        if (parent != null) {
            position = new Vector3f(
                    (localPosition.x + parent.transform.localPosition.x + mainParent.transform.localPosition.x),
                    (localPosition.y + parent.transform.localPosition.y + mainParent.transform.localPosition.y),
                    (localPosition.z + parent.transform.localPosition.z + mainParent.transform.localPosition.z));
            scale = new Vector3f(
                    localScale.x * mainParent.transform.localScale.x,
                    localScale.y * mainParent.transform.localScale.y,
                    localScale.z * mainParent.transform.localScale.z);
            localRotation = parent.transform.rotation.add(this.rotation);
        } else {
            position = localPosition;
            scale = localScale;
            localRotation = rotation;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return  t.position.equals(this.position) &&
                t.localPosition.equals(this.localPosition) &&
                t.scale.equals(this.scale) &&
                t.localScale.equals(this.localScale) &&
                t.rotation == this.rotation &&
                t.localRotation == this.localRotation &&
                t.parent == this.parent &&
                t.mainParent == this.mainParent &&
                t.childs.equals(this.childs);
    }

    public void addChild(GameObject child) {
        this.childs.add(child);

        Vector3f oldScale = child.transform.localScale;

        child.transform.parent = this.gameObject;

        if (this.mainParent != null) {
            child.transform.mainParent = this.mainParent;
            if (child.transform.childs != null)
                gameObject.setMainParentInChilds(this.mainParent);
        }
        else {
            child.transform.mainParent = this.gameObject;
            if (child.transform.childs != null)
                gameObject.setMainParentInChilds(this.gameObject);
        }

        child.transform.position = new Vector3f(child.transform.position.sub(this.gameObject.transform.localPosition));
        child.transform.scale = new Vector3f(oldScale); // TODO пофиксить баг с изменением размера

    }

    public void removeChild(GameObject child) {
        this.childs.remove(child);
        child.transform.parent = null;
        child.transform.mainParent = null;
    }

    public int getChildCount() { return this.childs.size(); }

    public void drawInSceneHierarchy(int level) {
        float startX = ImGui.getCursorStartPosX() - 2.0f;

        boolean treeNodeOpen = SceneHierarchyWindow.doTreeNode(gameObject, startX, level);

        if (treeNodeOpen) {
            if (getChildCount() > 0) {
                level++;
                for (GameObject child : this.childs) {
                    Window.getImGuiLayer().getSceneHierarchy().globalGameObjectIndex++;
                    if (child != null)
                        child.transform.drawInSceneHierarchy(level);
                }
            }
            ImGui.treePop();
        }
    }
}
