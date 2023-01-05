package engine.components;

import engine.entities.GameObject;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.guis.UIImage;
import engine.renderEngine.particles.ParticleSystem;
import engine.renderEngine.textures.Texture;
import engine.toolbox.customVariables.Color;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class Component {

    private static int ID_COUNTER = 0;
    private transient int uid = -1;
    private boolean isActive = true;

    public transient GameObject gameObject;

    public static final List<Component> allComponents = new ArrayList<>(){{
        add(new MeshRenderer(null));
        add(new Light(Color.White, 1.0f)); // rotation -40, 45, -30

        Texture particleTexture = Loader.get().loadTexture("Assets/fireParticles.png");
        add(new ParticleSystem(particleTexture));

        add(new UIImage(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png")));

//        FontType font = new FontType(Loader.get().loadTexture("Assets/fonts/candara.png", false, false).getTextureID(), new File("Assets/fonts/candara.fnt"));
//        UIText text = new UIText("Text", 5, font, 0.4f, true); // maxLineLength is horizontal size of text rect (1 - all screen width, 0.5 half of the screen)
//        text.setFontParams(new Color(20, 20, 20), 7, 0.5f, 0.1f);
//        text.setDropShadow(new Color(0, 0, 0, 60), new Vector2f(-0.001f, -0.001f), 0.04f, 0.2f);
//        text.setBorder(new Color(40, 40, 40, 255), 0.1f, 0.1f);
//        add(text);
    }};

    public Component() { }

    public static void addGlobalComponent(Component component) { allComponents.add(component); }

    public void start() { }

    public void editorUpdate() { }

    public void update() { }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) { }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) { }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        // ONE WAY COLLISION (FOR PLATFORMS, ONE WAY DOORS)
    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        // (INVERT OF preSolve)
    }

    public abstract void reset();

    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field: fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient)
                    continue;

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate)
                    field.setAccessible(true);

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int) value;
                    field.set(this, EditorImGui.field_Int(name, val));
                } else if (type == float.class) {
                    float val = (float) value;
                    field.set(this, EditorImGui.field_Float(name, val));
                } else if (type == boolean.class) {
                    field.set(this, EditorImGui.field_Boolean(name, (boolean) value));
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    val.set(EditorImGui.field_Vector2f(name, val));
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    val.set(EditorImGui.field_Vector3f(name, val));
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = { val.x, val.y, val.z, val.w };
                    if (ImGui.dragFloat4(name, imVec))
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum) value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (EditorImGui.enumCombo(name, index, enumValues, enumValues.length))
                        field.set(this, type.getEnumConstants()[index.get()]);
                } else if (type == String.class) {
                    field.set(this, EditorImGui.field_Text(name, (String) value, ""));
                } else if (type == Color.class) {
                    Color val = (Color) value;
                    EditorImGui.filed_Color(name, val);
                }

                if (isPrivate)
                    field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
    }

    public void generateId() { if (this.uid == -1)  this.uid = ID_COUNTER++; }

    public void generateId(boolean forceToGenerate) {
        if (forceToGenerate) {
            this.uid = -1;
            this.generateId();
        } else
            this.generateId();
    }

    public int getUid() { return this.uid; }

    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValues : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValues.name();
            i++;
        }
        return enumValues;
    }

    private int indexOf(String str, String[] arr) {
        for (int i = 0; i < arr.length; i++)
            if (str.equals(arr[i]))
                return i;

        return -1;
    }

    public void destroy() { } // ON DESTROY

    public static void init(int maxId) { ID_COUNTER = maxId; }

//    public static List<Component> getAllComponents() {
////        Reflections reflections = new Reflections("engine");
////        Set<Class<? extends Component>> allClasses = reflections.getSubTypesOf(Component.class);
////        List<Component> components = new ArrayList<>();
////
////        for (Class<? extends Component> c : allClasses) {
////            try { components.add(c.newInstance()); }
////            catch (InstantiationException | IllegalAccessException e) {
//////                System.out.println("Error " + c);
////                continue;
////            }
////        }
//
//        return allComponents;
//    }

    public boolean isActive() { return this.isActive; }

    public void setActive(boolean active) { this.isActive = active; }
}
