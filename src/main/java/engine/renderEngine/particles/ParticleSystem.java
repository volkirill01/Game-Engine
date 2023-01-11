package engine.renderEngine.particles;

import engine.assets.Asset;
import engine.components.Component;
import engine.imGui.Console;
import engine.imGui.ConsoleMessage;
import engine.imGui.EditorImGui;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.particles.particleSystemComponents_PSC.ParticleSystemComponent;
import engine.renderEngine.textures.Texture;
import engine.toolbox.Time;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem extends Component {

    private Texture texture;

    private int particlesPerSecond = 20;
//    private transient Vector3f direction = new Vector3f(0.0f, 0.0f, 0.0f);
//    private float coneAngle = 0.0f;
//    private float gravityComplient = 0.0f;
//    private float averageSpeed, averageLifeLength, averageScale;
//    private float speedError, lifeError, scaleError;
//    private boolean randomRotation = false;
//    private boolean useBlend = false;

    private transient Vector3f velocity;
    private transient FloatBuffer gravity;

    private transient float playTime;

    private List<ParticleSystemComponent> components;

    private transient Random random;

    public ParticleSystem(Texture texture) {
        this.texture = Loader.get().loadTexture(texture.getFilepath());

        this.random = new Random();

//        averageSpeed = 1.0f;
//        averageLifeLength = 1.0f;
//        averageScale = 1.0f;
//
//        speedError = 0.0f;
//        lifeError = 0.0f;
//        scaleError = 0.0f;

        playTime = 0.0f;

        components = new ArrayList<>();
    }

    @Override
    public void editorUpdate() {
        updateParticleSystem();

        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == this.gameObject ||
                (this.gameObject.transform.parent != null &&
                        Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == this.gameObject.transform.parent) ||
                (this.gameObject.transform.mainParent != null &&
                        Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == this.gameObject.transform.mainParent))
            generateParticles();
    }

    @Override
    public void update() {
        updateParticleSystem();

        if (Window.get().runtimePlaying)
            generateParticles();
    }

    private void updateParticleSystem() {
        if (velocity == null)
            velocity = new Vector3f(0.0f, 1.0f, 0.0f);

        if (gravity == null) {
            gravity = BufferUtils.createFloatBuffer(1);
            gravity.put(0.0f);
        }
        gravity.clear();

        this.texture.update();

        if (this.random == null)
            this.random = new Random();

        if (this.components != null && this.components.size() > 0)
            for (ParticleSystemComponent component : this.components)
                component.update(playTime, random, velocity, gravity);
    }

    public <T extends ParticleSystemComponent> T getComponent(Class<T> componentClass) {
        if (this.components != null && this.components.size() > 0)
            for (ParticleSystemComponent c : components) {
                if (componentClass.isAssignableFrom(c.getClass())) {
                    try {
                        return componentClass.cast(c);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        Console.log("Error: Casting component.", ConsoleMessage.MessageType.Error);
                        assert false: "Error: Casting component.";
                    }
                }
            }
        return null;
    }

    public void addComponent(ParticleSystemComponent c) {
        if (this.components != null)
            this.components.add(c);
        else
            this.components = new ArrayList<>(){{
                add(c);
            }};
    }

    private void generateParticles() {
        float particlesToCreate = particlesPerSecond * Time.deltaTime();
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;

        if (!Window.get().runtimePlaying) {
            if (!gameObject.isVisible())
                return;

            if (gameObject.transform.mainParent != null && !gameObject.transform.mainParent.isVisible())
                return;
            if (gameObject.transform.parent != null && !gameObject.transform.parent.isVisible())
                return;
        }

        for (int i = 0; i < count; i++)
            emitParticle();

        if (Math.random() < partialParticle)
            emitParticle();
    }

    private void emitParticle() {
//        Vector3f velocity = new Vector3f(0.0f);
//        if (direction != null)
//            velocity = generateRandomUnitVectorWithinCone(direction, coneAngle);
//        else
//        velocity = generateRandomUnitVector();

//        velocity.normalize();
//        velocity.mul(generateValue(averageSpeed, speedError));
//        float scale = generateValue(averageScale, scaleError);
//        float lifeLength = generateValue(averageLifeLength, lifeError);
        Vector3f _position = new Vector3f(gameObject.transform.localPosition);
        Vector3f _velocity = new Vector3f(velocity);

        new Particle(Loader.get().loadTexture(texture.getFilepath()), false, _position, _velocity, gravity.get(0), 1.0f, 0.0f, 1.0f);
    }

    private float generateValue(float average, float errorMargin) {
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    private float generateRotation() {
//        if (randomRotation)
//            return random.nextFloat() * 360f;
//        else
        return 0;
    }

    private Vector3f generateRandomUnitVector() {
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        return new Vector3f(x, y, z);
    }

    private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
        float cosAngle = (float) Math.cos(angle);
        Random random = new Random();
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        Vector4f direction = new Vector4f(x, y, z, 1);
        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
            Vector3f rotateAxis = coneDirection.cross(new Vector3f(0, 0, 1));
            rotateAxis.normalize();
            float rotateAngle = (float) Math.acos(coneDirection.dot(new Vector3f(0, 0, 1)));
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.rotate(-rotateAngle, rotateAxis);
            direction = rotationMatrix.transform(direction);
        } else if (coneDirection.z == -1)
            direction.z *= -1;

        return new Vector3f(direction.x, direction.y, direction.z);
    }

    @Override
    public void imgui() {
        this.texture = (Texture) EditorImGui.field_Asset("Texture", this.texture, Asset.AssetType.Texture);

        this.particlesPerSecond = EditorImGui.field_Int("Particles Amount", this.particlesPerSecond, 1, 0);

        if (EditorImGui.collapsingHeader("Components")) {
            if (components != null && components.size() > 0)
                for (ParticleSystemComponent component : this.components) {
                    if (EditorImGui.collapsingHeader(component.getComponentName(), 10.0f, true))
                        component.imgui();
                }

            float centerOfWindow = ImGui.getWindowContentRegionMaxX() / 2.0f;
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 6.0f);

            ImVec4 buttonColor = ImGui.getStyle().getColor(ImGuiCol.FrameBg);
            ImVec4 buttonHoveredColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgHovered);
            ImVec4 buttonActiveColor = ImGui.getStyle().getColor(ImGuiCol.FrameBgActive);

            ImGui.pushStyleColor(ImGuiCol.Button, buttonColor.x, buttonColor.y, buttonColor.z, buttonColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, buttonHoveredColor.x, buttonHoveredColor.y, buttonHoveredColor.z, buttonHoveredColor.w);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, buttonActiveColor.x, buttonActiveColor.y, buttonActiveColor.z, buttonActiveColor.w);
            boolean isOpen = EditorImGui.horizontalCenterButton("Add ParticleSystem component", 50.0f);
            ImGui.popStyleColor(3);
            float popupPosY = ImGui.getCursorPosY();
            ImVec2 popupPosition = new ImVec2(centerOfWindow - 105.0f, popupPosY);

            if (EditorImGui.BeginPopup("PSC_ComponentAdder", popupPosition, isOpen)) {
                for (ParticleSystemComponent c : ParticleSystemComponent.allComponents) {
                    if (getComponent(c.getClass()) == null)
                        if (ImGui.menuItem(c.getComponentName()))
                            addComponent(c);
                }
                EditorImGui.EndPopup();
            }
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 8.0f);
        }
    }

    @Override
    public void reset() {
        this.texture = null;
    }

// -----------------------------------------------------------------------------------------------------------------------

//    private float particlesPerSecond, averageSpeed, gravityComplient, averageLifeLength, averageScale;
//
//    private float speedError, lifeError, scaleError = 0;
//    private boolean randomRotation = false;
//    private Vector3f direction;
//    private float directionDeviation = 0;
//    private boolean useBlend = false;
//
//    private transient Texture texture;
//
//    private transient Random random;
//
//    public ParticleSystem(Texture texture, float particlesPerSecond, float speed, float gravityComplient, float lifeLength, float scale, boolean randomRotation, boolean useBlend) {
//        this.particlesPerSecond = particlesPerSecond;
//        this.averageSpeed = speed;
//        this.gravityComplient = gravityComplient;
//        this.averageLifeLength = lifeLength;
//        this.averageScale = scale;
//        this.texture = Loader.get().loadTexture(texture.getFilepath());
//        this.randomRotation = randomRotation;
//        this.useBlend = useBlend;
//    }
//
//    public void setDirection(Vector3f direction, float deviation) {
//        this.direction = new Vector3f(direction);
//        this.directionDeviation = (float) (deviation * Math.PI);
//    }
//
//    public void randomizeRotation() {
//        randomRotation = true;
//    }
//
//    public void setSpeedError(float error) {
//        this.speedError = error * averageSpeed;
//    }
//
//    public void setLifeError(float error) {
//        this.lifeError = error * averageLifeLength;
//    }
//
//    public void setScaleError(float error) {
//        this.scaleError = error * averageScale;
//    }
//
//    public boolean isUseBlend() { return this.useBlend; }
//
//    public void setUseBlend(boolean useBlend) { this.useBlend = useBlend; }
//
//    @Override
//    public void editorUpdate() {
//        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == this.gameObject)
//            generateParticles();
//    }
//
//    @Override
//    public void update() {
//        if (Window.get().runtimePlaying)
//            generateParticles();
//    }
//
//    private void generateParticles() {
//        float delta = Window.getDelta();
//        float particlesToCreate = particlesPerSecond * delta;
//        int count = (int) Math.floor(particlesToCreate);
//        float partialParticle = particlesToCreate % 1;
//        for (int i = 0; i < count; i++) {
//            emitParticle(gameObject.transform.position);
//        }
//        if (Math.random() < partialParticle) {
//            emitParticle(gameObject.transform.position);
//        }
//    }
//
//    private void emitParticle(Vector3f center) {
//        Vector3f velocity;
//        if (direction != null)
//            velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
//        else
//            velocity = generateRandomUnitVector();
//
//        velocity.normalize();
//        velocity.mul(generateValue(averageSpeed, speedError));
//        float scale = generateValue(averageScale, scaleError);
//        float lifeLength = generateValue(averageLifeLength, lifeError);
//        new Particle(texture, useBlend, new Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale);
//    }
//
//    private float generateValue(float average, float errorMargin) {
//        if (this.random == null)
//            this.random = new Random();
//        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
//        return average + offset;
//    }
//
//    private float generateRotation() {
//        if (this.random == null)
//            this.random = new Random();
//        if (randomRotation) {
//            return random.nextFloat() * 360f;
//        } else {
//            return 0;
//        }
//    }
//
//    private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
//        float cosAngle = (float) Math.cos(angle);
//        Random random = new Random();
//        float theta = (float) (random.nextFloat() * 2f * Math.PI);
//        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
//        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
//        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
//        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
//
//        Vector4f direction = new Vector4f(x, y, z, 1);
//        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
//            Vector3f rotateAxis = coneDirection.cross(new Vector3f(0, 0, 1));
//            rotateAxis.normalize();
//            float rotateAngle = (float) Math.acos(coneDirection.dot(new Vector3f(0, 0, 1)));
//            Matrix4f rotationMatrix = new Matrix4f();
//            rotationMatrix.rotate(-rotateAngle, rotateAxis);
//            direction = rotationMatrix.transform(direction);
//        } else if (coneDirection.z == -1) {
//            direction.z *= -1;
//        }
//        return new Vector3f(direction.x, direction.y, direction.z);
//    }
//
//    private Vector3f generateRandomUnitVector() {
//        if (this.random == null)
//            this.random = new Random();
//        float theta = (float) (random.nextFloat() * 2f * Math.PI);
//        float z = (random.nextFloat() * 2) - 1;
//        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
//        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
//        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
//        return new Vector3f(x, y, z);
//    }
//
//    @Override
//    public void imgui() {
//        this.particlesPerSecond = EditorImGui.field_Float("particlesPerSecond", this.particlesPerSecond);
//        this.averageSpeed = EditorImGui.field_Float("averageSpeed", this.averageSpeed);
//        this.gravityComplient = EditorImGui.field_Float("gravityComplient", this.gravityComplient);
//        this.averageLifeLength = EditorImGui.field_Float("averageLifeLength", this.averageLifeLength);
//        this.averageScale = EditorImGui.field_Float("averageScale", this.averageScale);
//
//        this.speedError = EditorImGui.field_Float("speedError", this.speedError);
//        this.lifeError = EditorImGui.field_Float("lifeError", this.lifeError);
//        this.scaleError = EditorImGui.field_Float("scaleError", this.scaleError);
//
//        this.randomRotation = EditorImGui.field_Boolean("randomRotation", this.randomRotation);
//        if (direction != null)
//            this.direction = EditorImGui.field_Vector3f("direction", this.direction);
//
//        this.directionDeviation = EditorImGui.field_Float("directionDeviation", this.directionDeviation);
//        this.useBlend = EditorImGui.field_Boolean("useBlend", this.useBlend);
//
//        this.texture = (Texture) EditorImGui.field_Asset("texture", this.texture, Asset.AssetType.Texture);
//    }
//
//    @Override
//    public void reset() {
//
//    }

// -----------------------------------------------------------------------------------------------------------------------

    // Simple Particle system
//    private float particlesPerSecond;
//    private float speed;
//    private float gravityComplient;
//    private float lifeLength;
//
//    public ParticleSystem(float particlesPerSecond, float speed, float gravityComplient, float lifeLength) {
//        this.particlesPerSecond = particlesPerSecond;
//        this.speed = speed;
//        this.gravityComplient = gravityComplient;
//        this.lifeLength = lifeLength;
//    }
//
//    public void generateParticles(Vector3f systemCenter){
//        float delta = DisplayManager.getDelta();
//        float particlesToCreate = particlesPerSecond * delta;
//        int count = (int) Math.floor(particlesToCreate);
//        float partialParticle = particlesToCreate % 1;
//        for(int i=0;i<count;i++){
//            emitParticle(systemCenter);
//        }
//        if(Math.random() < partialParticle){
//            emitParticle(systemCenter);
//        }
//    }
//
//    private void emitParticle(Vector3f center){
//        float dirX = (float) Math.random() * 2f - 1f;
//        float dirZ = (float) Math.random() * 2f - 1f;
//        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
//        velocity.normalize();
//        velocity.mul(speed);
//        new Particle(new Vector3f(center), velocity, gravityComplient, lifeLength, 0, 1);
//    }
}
