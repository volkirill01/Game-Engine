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
import engine.imGui.InspectorWindow;
import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.components.ObjectRenderer;
import engine.renderEngine.guis.UIRenderer;
import engine.renderEngine.guis.UIImage;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.particles.particleSystemComponents_PSC.PSComponentDeserializer;
import engine.renderEngine.particles.particleSystemComponents_PSC.ParticleSystemComponent;
import engine.renderEngine.textures.Material;
import engine.toolbox.customVariables.Color;
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
    private transient int uid = -1;

    public String name;
    private List<GameObjectTag> tags;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;

    private transient boolean isDeath = false;

    private transient List<Class> addComponentBlackList = new ArrayList<>(){{
        add(Transform.class);
        add(ObjectRenderer.class);
    }};

    public GameObject(String name) {
        this.name = name;
        this.tags = new ArrayList<>();
        this.components = new ArrayList<>();

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

    public List<String> getAllTagsNames() {
        List<String> curTags = new ArrayList<>();
        for (GameObjectTag tag : tags)
            curTags.add(tag.tag);
        return curTags;
    }

    public List<GameObjectTag> getAllTags() { return tags; }

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

        if (c.getClass() == UIImage.class) {
            ((UIImage) c).setTexture(Loader.get().loadTexture(((UIImage) c).getTexture().getFilepath()));
        } if (c.getClass() == MeshRenderer.class) {
            MeshRenderer renderer = (MeshRenderer) c;
            List<Material> materials = new ArrayList<>();

            if (renderer.getModel() != null) {
                for (int i = 0; i < renderer.getModel().getMaterials().size(); i++)
                    materials.add(Loader.get().loadMaterial(renderer.getModel().getMaterials().get(i).getFilepath()));
                renderer.setModel(new TexturedModel(OBJLoader.loadOBJ(renderer.getModel().getFilepath()), materials));
            } else {
                renderer.setModel(null);
            }
        }
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

    public void editorUpdate() {
        for (Component component : components)
            if (component.isActive())
                component.editorUpdate();
    }

    public void update() {
        for (Component component : components)
            if (component.isActive())
                component.update();
    }

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
        name = EditorImGui.field_Text_NoLabel("Name", name, "Name");

        for (int i = 0; i < components.size(); i++) {
            ImGui.pushID("##component" + components.get(i).getUid());

            if (!components.get(i).isActive())
                EditorImGui.pushDisabled();

            ImVec2 startCursorPos = ImGui.getCursorPos();

            //<editor-fold desc="Header">
            ImVec4 headerColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
            ImVec4 headerHoveredColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.Header, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.HeaderActive, 0.0f, 0.0f, 0.0f, 0.0f);
            ImGui.pushStyleColor(ImGuiCol.Button, headerColor.x, headerColor.y, headerColor.z, headerColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, headerHoveredColor.x, headerHoveredColor.y, headerHoveredColor.z, headerHoveredColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, headerHoveredColor.x, headerHoveredColor.y, headerHoveredColor.z, headerHoveredColor.w);
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getContentRegionAvailX() / 2.0f, ImGui.getStyle().getFramePaddingY());
            ImGui.button("##componentBackground");
            ImGui.setItemAllowOverlap();
            ImGui.popStyleVar();

            ImGui.setCursorPos(startCursorPos.x + 35.0f, startCursorPos.y);
            ImVec4 separatorColor = ImGui.getStyle().getColor(ImGuiCol.Separator);
            separatorColor.w -= 0.2f;
            EditorImGui.drawRectangle(startCursorPos, new ImVec2(ImGui.getContentRegionAvailX() + 35.0f, 2.0f), separatorColor);
            ImGui.setItemAllowOverlap();

            ImGui.setCursorPos(startCursorPos.x + 28.0f, startCursorPos.y);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, ImGui.getStyle().getFramePaddingX() - 3.0f, ImGui.getStyle().getFramePaddingY());
            boolean collapsingHeader = ImGui.collapsingHeader(components.get(i).getClass().getSimpleName(),
                    components.get(i).getClass() == Transform.class ? ImGuiTreeNodeFlags.DefaultOpen :
                    ImGuiTreeNodeFlags.None);
            ImGui.setItemAllowOverlap();
            ImGui.popStyleVar();

            ImGui.popStyleVar();
            ImGui.popStyleColor(7);

            ImVec2 headerPos = ImGui.getCursorPos();
            if (!components.get(i).isActive())
                EditorImGui.popDisabled();
            //</editor-fold>

            //<editor-fold desc="Is Active checkbox">
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 2.5f, 2.5f);
            ImGui.setCursorPos(startCursorPos.x + 3.0f, startCursorPos.y + 2.5f);
            if (components.get(i).getClass() != Transform.class) {
                if (ImGui.checkbox("##isActive", components.get(i).isActive()))
                    components.get(i).setActive(!components.get(i).isActive());
            } else {
                ImVec4 textDisabled = ImGui.getStyle().getColor(ImGuiCol.TextDisabled);
                ImGui.pushStyleColor(ImGuiCol.CheckMark, textDisabled.x, textDisabled.y, textDisabled.z, textDisabled.w);
                ImGui.checkbox("##isActive", true);
                ImGui.popStyleColor();
            }
            ImGui.popStyleVar();
            //</editor-fold>

            //<editor-fold desc="Dropdown menu">
            ImGui.sameLine();
            ImGui.setCursorPos(ImGui.getWindowWidth() - (16.0f * 2.0f) - ImGui.getStyle().getWindowPaddingX(), ImGui.getCursorPosY() + (16.0f / 2.0f) - 8.0f);

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
                    ImGui.textDisabled("Move Up");

                if (i > 0 && i < components.size() - 1) {
                    if (ImGui.menuItem("Move Down"))
                        swapTwoComponents(i, i + 1);
                } else
                    ImGui.textDisabled("Move Down");

                EditorImGui.EndButtonDropDown();
            }
            //</editor-fold>

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

        ImGui.setCursorPosY(ImGui.getCursorPosY() + ImGui.getStyle().getItemSpacingY() + 4.0f);
        ImGui.separator();

        float centerOfWindow = ImGui.getWindowContentRegionMaxX() / 2.0f;
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 5.0f);
        boolean isOpen = EditorImGui.horizontalCenterButton("Add component", 60.0f);
        float popupPosY = ImGui.getCursorPosY();
        ImVec2 popupPosition = new ImVec2(centerOfWindow - 105.0f, popupPosY);

        if (EditorImGui.BeginPopup("ComponentAdder", popupPosition, isOpen)) {
            for (Component c : Component.allComponents) {
                if (getComponent(c.getClass()) == null && !addComponentBlackList.contains(c.getClass()))
                    if (ImGui.menuItem(c.getClass().getSimpleName()))
                        addComponent(c);
            }
            EditorImGui.EndPopup();
        }
    }

    private void swapTwoComponents(int firstIndex, int secondIndex) { Collections.swap(components, firstIndex, secondIndex); }

    public void destroy() {
        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == this)
            Window.get().getImGuiLayer().getInspectorWindow().clearSelected();

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
                .registerTypeAdapter(ParticleSystemComponent.class, new PSComponentDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);
        obj.generateUid();

        for (Component c: obj.getAllComponents())
            c.generateId();

        return obj;
    }

    public boolean isDeath() { return this.isDeath; }

    public static void init(int maxId) { ID_COUNTER = maxId; }

    public int getUid() { return this.uid; }

    public void setNoSerialize() { this.doSerialization = false; }

    public boolean doSerialization() { return this.doSerialization; }

    public void generateUid() { this.uid = ID_COUNTER++; }
}
