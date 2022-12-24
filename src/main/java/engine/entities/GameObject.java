package engine.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.TestFieldsWindow;
import engine.components.Component;
import engine.components.ComponentDeserializer;
import engine.components.Transform;
import engine.imGui.Console;
import engine.imGui.ConsoleMessage;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.components.ObjectRenderer;
import engine.toolbox.customVariables.GameObjectTag;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameObject {

    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    private List<GameObjectTag> tags;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;

    private boolean isDeath = false;

    private transient List<Class> addComponentBlackList = new ArrayList<>(){{
        add(Transform.class);
        add(ObjectRenderer.class);
    }};

    public GameObject(String name) {
        this.name = name;
        this.tags = new ArrayList<>();
        this.components = new ArrayList<>();

//        addComponent(new Transform());
//        this.transform = getComponent(Transform.class);

        this.uid = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    Console.log(ConsoleMessage.MessageType.Error, "Error: Casting component.");
                    assert false: "Error: Casting component.";
                }
            }
        }
        return null;
    }

    public List<Component> getAllComponents() { return this.components; }

    public List<String> getAllTags() {
        List<String> curTags = new ArrayList<>();
        for (GameObjectTag tag : tags)
            curTags.add(tag.tag);
        return curTags;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    public boolean hasComponent(Class component) {
        for (Component c : this.components)
            if (c.getClass() == component)
                return true;
        return false;
    }

    public boolean hasTag(GameObjectTag tag) { return this.tags.contains(tag); }

    public boolean hasTag(String tag) {
        for (GameObjectTag goTag : this.tags)
            if (goTag.tag.equals(tag))
                return true;
        return false;
    }

    public void addTag(GameObjectTag tag) { this.tags.add(tag); }

    public void addTag(String tag) { this.tags.add(new GameObjectTag(tag, new Vector3f(1.0f, 0.0f, 0.0f))); }

    public void removeTag(GameObjectTag tag) { this.tags.remove(tag); }

    public void removeTag(String tag) { this.tags.removeIf(goTag -> goTag.tag.equals(tag)); }

    public void editorUpdate() { for (Component component : components) component.editorUpdate(); }

    public void update() { for (Component component : components) component.update(); }

    public void start() { for (int i = 0; i < components.size(); i++) components.get(i).start(); }

    public boolean isHover() {
//        Vector2f start = new Vector2f(
//                transform.position.x - (transform.scale.x / 2.0f),
//                transform.position.y - (transform.scale.y / 2.0f));
//        Vector2f end = new Vector2f(start).add(new Vector2f(
//                transform.scale.x, transform.scale.y));

//        Vector2f startScreenf = MouseListener.worldToScreen(start);
//        Vector2f endScreenf = MouseListener.worldToScreen(end);
//        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
//        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
//
//        if (MouseListener.getScreenX() < endScreen.x && MouseListener.getScreenX() > startScreen.x)
//            if (MouseListener.getScreenY() < endScreen.y && MouseListener.getScreenY() > startScreen.y) {
//                float[] gameObjectIds = Window.getImGuiLayer().getInspectorWindow().getPickingTexture().readPixels(startScreen, endScreen);
//
//                for (float gameObjectId : gameObjectIds) {
//                    if (gameObjectId >= 0) {
////                        GameObject pickedObj = Window.getScene().getGameObject((int) gameObjectId);
////                        if (!pickedObj.hasTag("-ENonPickable"))
//                              return true;
//                    }
//                }
//            }
        return false;
    }

    public void imgui() { // GameObject Inspector
        drawTags();

        name = EditorImGui.field_Text_NoLabel("Name", name, "Name");
        float checkboxSize = 37.0f;

        for (int i = 0; i < components.size(); i++) {
            ImGui.pushID("##component" + components.get(i).getUid());

//            if (!components.get(i).isActive()) {  // ------------------------------------------
//                ImVec4 buttonDisabledColor = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
//                ImVec4 textDisabledColor = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
//
//                ImGui.pushStyleColor(ImGuiCol.Text, textDisabledColor.x, textDisabledColor.y, textDisabledColor.z, textDisabledColor.w);
//                ImGui.pushStyleColor(ImGuiCol.CheckMark, textDisabledColor.x, textDisabledColor.y, textDisabledColor.z, textDisabledColor.w);
//
//                ImGui.pushStyleColor(ImGuiCol.Button, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
//                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
//                ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
//
//                ImGui.pushStyleColor(ImGuiCol.Header, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
//                ImGui.pushStyleColor(ImGuiCol.HeaderHovered, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
//                ImGui.pushStyleColor(ImGuiCol.HeaderActive, buttonDisabledColor.x, buttonDisabledColor.y, buttonDisabledColor.z, buttonDisabledColor.w);
//            }

            ImGui.columns(2, "", false);

            //<editor-fold desc="Is Active checkbox">
            ImGui.setColumnWidth(0, checkboxSize);
            if (components.get(i).getClass() != Transform.class) {
                if (ImGui.checkbox("##isActive", components.get(i).isActive()))
                    components.get(i).setActive(!components.get(i).isActive());
            } else {
                ImVec4 textDisabled = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
                ImGui.pushStyleColor(ImGuiCol.CheckMark, textDisabled.x, textDisabled.y, textDisabled.z, textDisabled.w);
                ImGui.checkbox("##isActive", true);
                ImGui.popStyleColor();
            }
            ImGui.nextColumn();
            //</editor-fold>

            if (!components.get(i).isActive())
                EditorImGui.pushDisabled();

            ImGui.setColumnWidth(1, ImGui.getWindowWidth() - ImGui.getStyle().getWindowPaddingX() - checkboxSize);
            boolean collapsingHeader = ImGui.collapsingHeader(components.get(i).getClass().getSimpleName(),
                    components.get(i).getClass() == Transform.class ? ImGuiTreeNodeFlags.DefaultOpen :
                    ImGuiTreeNodeFlags.None);
            ImGui.setItemAllowOverlap();

            ImVec2 headerPos = ImGui.getCursorPos();
            //</editor-fold>

            if (!components.get(i).isActive())
                EditorImGui.popDisabled();

            //<editor-fold desc="Dropdown menu">
            ImGui.sameLine();
            ImGui.setCursorPos(ImGui.getWindowWidth() - (16.0f * 2.0f) + 3.0f - 8.0f, ImGui.getCursorPosY() + (16.0f / 2.0f) - 8.0f);

            if (EditorImGui.BeginButtonDropDownImage(
                    Loader.get().loadTexture("engineFiles/images/utils/icon=ellipsis-solid(32x32).png").getTextureID(),
                    "ComponentMenu", new ImVec2(18, 18), components.get(i).isActive() ? ImGui.getStyle().getColor(ImGuiCol.Text) : ImGui.getStyle().getColor(ImGuiCol.TextDisabled), true)) {
                if (ImGui.menuItem("Reset"))
                    components.get(i).reset();

                ImGui.separator();
                if (components.get(i).getClass() != Transform.class) {
                    if (ImGui.menuItem("Remove Component")) {
                        removeComponent(components.get(i).getClass());
                        i--;
                    }
                } else
                    ImGui.textDisabled("Remove Component");

                if (i > 1) {
                    if (ImGui.menuItem("Move Up"))
                        swapTwoComponents(i, i - 1);
                } else
                    ImGui.textDisabled("Move Up"); // TODO IF MOVE UP OR DOWN DON'T WORK, REPLACE IF STATEMENTS

                if (i > 1 && i < components.size() - 1) {
                    if (ImGui.menuItem("Move Down"))
                        swapTwoComponents(i, i + 1);
                } else
                    ImGui.textDisabled("Move Down");

                EditorImGui.EndButtonDropDown();
            }
            //</editor-fold>

            ImGui.columns(1);
            ImGui.setCursorPos(headerPos.x, headerPos.y);

            if (!components.get(i).isActive())
                EditorImGui.pushDisabled();

            if (collapsingHeader) {
                components.get(i).imgui();
                if (i < components.size() - 1)
                    ImGui.separator();
            }

            if (!components.get(i).isActive())
                EditorImGui.popDisabled();

            ImGui.popID();
        }
        ImGui.separator();

        float centerOfWindow = ImGui.getWindowContentRegionMaxX() / 2.0f;
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 6.0f);
        boolean isOpen = EditorImGui.horizontalCenterButton("Add component", 60.0f);
        float popupPosY = ImGui.getCursorPosY();
        ImVec2 popupPosition = new ImVec2(centerOfWindow - 105.0f, popupPosY);

        if (EditorImGui.BeginPopup("ComponentAdder", popupPosition, isOpen)) {
            for (Component c : Component.getAllComponents()) {
                if (getComponent(c.getClass()) == null && !addComponentBlackList.contains(c.getClass()))
                    if (ImGui.menuItem(c.getClass().getSimpleName())) {
//                        if (c.getClass()== UIRenderer.class) {
//                            SpriteRenderer spr = getComponent(SpriteRenderer.class);
//                            removeComponent(SpriteRenderer.class);
//                            ((UIRenderer) c).setTexture(spr.getTexture());
//                            ((UIRenderer) c).setColor(spr.getColor());
//                            ((UIRenderer) c).setDirty();
//                            getComponent(Transform.class).zIndex++;
//                            getComponent(Transform.class).zIndex--;
//                            addComponent(c);
//                        } else if (c.getClass()== MeshRenderer.class) {
//                            MeshRenderer mesh = getComponent(MeshRenderer.class);
//                            removeComponent(SpriteRenderer.class);
//                            ((MeshRenderer) c).setTexture(mesh.getTexture());
//                            ((MeshRenderer) c).setColor(mesh.getColor());
//                            ((MeshRenderer) c).setDirty();
//                            getComponent(Transform.class).zIndex++;
//                            getComponent(Transform.class).zIndex--;
//                            addComponent(c);
//                        } else
                        addComponent(c);
                    }
            }
            EditorImGui.EndPopup();
        }
    }

    private void swapTwoComponents(int firstIndex, int secondIndex) { Collections.swap(components, firstIndex, secondIndex); }

    private void drawTags() {
        ImGui.beginMenuBar();
        ImGui.text("Tags");
        ImGui.sameLine();
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        ImGui.text("\uF005");
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).tag.startsWith("##"))
                continue;

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, -4.0f + 2.5f);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 6.0f);

            ImVec2 tmp = new ImVec2();
            ImGui.calcTextSize(tmp, tags.get(i).tag);
            if (tags.get(i).tag.equals(""))
                ImGui.calcTextSize(tmp, "Tag");

            ImVec4 color = new ImVec4(tags.get(i).color.x / 255.0f, tags.get(i).color.y / 255.0f, tags.get(i).color.z / 255.0f, 1.0f);
            ImVec4 bgColor = new ImVec4((tags.get(i).color.x - 130) / 255.0f, (tags.get(i).color.y - 90) / 255.0f, (tags.get(i).color.z - 20) / 255.0f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.Button, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color.x + 0.07f, color.y + 0.07f, color.z + 0.07f, color.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color.x, color.y, color.z, color.w);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, bgColor.x, bgColor.y, bgColor.z, bgColor.w);
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, color.w);

            ImGui.beginChildFrame(1919 + i, tmp.x + 35.0f, 26.0f, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

            ImGui.setCursorPosY(ImGui.getCursorPosY() + 2.8f);
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 0);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 8.0f, 3.0f);
            ImGui.setNextItemWidth(tmp.x + 14.0f);
            tags.get(i).tag = drawTagInput("tag" + i, tags.get(i).tag);
            ImGui.popStyleVar();
            ImGui.popStyleColor();

            ImGui.sameLine();
            ImGui.setCursorPos(ImGui.getCursorPosX() - 5.0f + 2.1f, ImGui.getCursorPosY() + 6.1f - 1.4f);
            if (tags.get(i).tag.equals(""))
                ImGui.setCursorPos(ImGui.getCursorPosX() + 10.0f - 5.5f, ImGui.getCursorPosY());

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 7.4f - 0.3f, -2.4f + 0.6f);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
            if (ImGui.button("##deleteTag" + i)) {
                tags.remove(tags.get(i));
                i--;
            }
            ImGui.sameLine();
            ImGui.pushStyleColor(ImGuiCol.Text, bgColor.x, bgColor.y, bgColor.z, bgColor.w);
            ImGui.setCursorPos(ImGui.getCursorPosX() - 19.4f + 2.8f, ImGui.getCursorPosY() - 5.92f + 1.0f);
            ImGui.text("\uEEE4");
            ImGui.popStyleVar(2);

            ImGui.endChildFrame();
            ImGui.popStyleColor(7);
            ImGui.popStyleVar(2);
        }

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX(), ImGui.getStyle().getFramePaddingY() - 2.0f);

        ImGui.setCursorPosY(ImGui.getCursorPosY() + 1.0f);
        if (ImGui.button("\uEFC2"))
            showAddTag = true;

        ImGui.popStyleVar();
        ImGui.endMenuBar();

        showCreateTagPopup();

        if (showAddTag)
            ImGui.openPopup("\t\t\t\t\t\t\t\t\t\t \uF005 Add new Tag");
    }

    public static boolean showAddTag = false;
    private transient Vector3f newTagColor = new Vector3f(216 / 255.0f, 162 / 255.0f, 77 / 255.0f);
    private final transient String newTagNameRef = "New tag";
    private transient String newTagName = newTagNameRef;
    private void showCreateTagPopup() {
        ImGui.setNextWindowSize(438.0f, 550.0f);
        ImGui.setNextWindowPos(Window.get().windowSize.x / 2.0f - ImGui.getWindowSizeX() / 2.0f + Window.get().windowPosition.x,
                        Window.get().windowSize.y / 2.0f - ImGui.getWindowSizeY() / 3.0f + Window.get().windowPosition.y);

        if (showAddTag && ImGui.beginPopupModal("\t\t\t\t\t\t\t\t\t\t \uF005 Add new Tag", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            ImVec2 tmp = new ImVec2();
            ImGui.calcTextSize(tmp, "Choose tag color");
            ImGui.setCursorPosX((ImGui.getContentRegionAvailX() / 2.0f) - (tmp.x / 2.0f));
            ImGui.text("Choose tag color");

            float[] imColor = {newTagColor.x, newTagColor.y, newTagColor.z};
            if (ImGui.colorPicker3("##tagColor", imColor))
                newTagColor.set(imColor[0], imColor[1], imColor[2]);

            ImGui.separator();
            ImGui.spacing();

            tmp = new ImVec2();
            ImGui.calcTextSize(tmp, "Enter new tag");
            ImGui.setCursorPos((ImGui.getContentRegionAvailX() / 2.0f) - (tmp.x / 2.0f), ImGui.getCursorPosY() + 5.0f);
            ImGui.text("Enter new tag");

            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10.0f, ImGui.getStyle().getFramePaddingY());
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 5.0f);
            newTagName = drawTagInput("newTagInput", newTagName);
            ImGui.popStyleVar();

            ImGui.setCursorPos(ImGui.getContentRegionAvailX() / 6.0f, ImGui.getCursorPosY() + 10.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getContentRegionAvailX() / 6.0f, ImGui.getStyle().getFramePaddingY());
            if (ImGui.button("Close")) {
                newTagName = newTagNameRef;
                showAddTag = false;
            }

            ImGui.sameLine();
            if (ImGui.button("Ok")) {
                tags.add(new GameObjectTag(newTagName, new Vector3f(
                        newTagColor.x * 255.0f,
                        newTagColor.y * 255.0f,
                        newTagColor.z * 255.0f)));
                newTagName = newTagNameRef;
                showAddTag = false;
            }
            ImGui.popStyleVar();
            ImGui.endPopup();
        }
    }

    private String drawTagInput(String label, String text) {
        ImGui.pushID(label);

        float start = ImGui.getCursorPosX();
        ImString outString = new ImString(text, 20);

        if (ImGui.inputText("##" + label, outString)) {
            ImGui.popID();
            return outString.get();
        }
        if (text.equals("")) {
            ImGui.sameLine();
            ImVec4 color = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, color.w);
            ImGui.setCursorPosX(start + 10.0f);
            ImGui.text("Tag");
            ImGui.popStyleColor();
        }
        ImGui.popID();
        if (text.equals(newTagNameRef))
            ImGui.setKeyboardFocusHere(-1);

        return text;
    }

    public void destroy() {
        this.isDeath = true;

        for (int i = 0; i < transform.childs.size(); i++) {
            GameObject child = transform.childs.get(i);
            transform.childs.remove(i);
            child.destroy();
            i--;
        }

        if (transform.parent != null) {
            transform.parent.transform.removeChild(this);
            transform.parent = null;
        }

        for (Component component : components) component.destroy();
    }

    public GameObject copy() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);
        obj.generateUid();
        for (Component c: obj.getAllComponents())
            c.generateId();

//        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
//        if (sprite != null && sprite.getTexture() != null)
//            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));

        return obj;
    }

    public boolean isDeath() { return this.isDeath; }

    public static void init(int maxId) { ID_COUNTER = maxId; }

    public int getUid() { return this.uid; }

    public void setNoSerialize() { this.doSerialization = false; }

    public boolean doSerialization() { return this.doSerialization; }

    public void generateUid() { this.uid = ID_COUNTER++; }

    public void addChildsToScene() {
        for (GameObject child : transform.childs) {
//            Window.getScene().addGameObjectToScene(child);
            child.addChildsToScene();
            child.transform.parent = this;

            if (transform.mainParent != null)
                child.transform.mainParent = transform.mainParent;
            else
                child.transform.mainParent = this;
        }
    }
}
