package engine.scene;

import engine.entities.GameObject;
import engine.renderEngine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private GameObject levelEditorStuff;
//    public static Gizmo gizmo;

//    private Spritesheet sprites;

    public LevelEditorSceneInitializer(String scenePath) { this.scenePath = scenePath; }

    @Override
    public void init(Scene scene) {
        Window.get().runtimePlaying = false;
//        Spritesheet gizmos = AssetPool.getSpritesheet("engineFiles/images/gizmos.png");
//        sprites = AssetPool.getSpritesheet("Assets/images/spritesheets/decorationsAndBlocks.png");

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
//        levelEditorStuff.addComponent(new MouseControls());
//        levelEditorStuff.addComponent(new KeyControls());
//        levelEditorStuff.addComponent(new GridLines());
//        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
//        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);
//        Window.setLevelEditorStuff(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene) {
//        AssetPool.getShader("engineFiles/shaders/default.glsl");
//
//        AssetPool.addSpritesheet("engineFiles/images/gizmos.png",
//                new Spritesheet(AssetPool.getTexture("engineFiles/images/gizmos.png"),
//                        85, 256, 3, 0, 0));

//        AssetPool.addSpritesheet("Assets/images/spritesheets/decorationsAndBlocks.png",
//                new Spritesheet(AssetPool.getTexture("Assets/images/spritesheets/decorationsAndBlocks.png"),
//                        16, 16, 81, 0, 0));
//        AssetPool.addSpritesheet("Assets/images/spritesheet.png",
//                new Spritesheet(AssetPool.getTexture("Assets/images/spritesheet.png"),
//                        16, 16, 26, 0, 0));
//        AssetPool.addSpritesheet("Assets/images/turtle.png",
//                new Spritesheet(AssetPool.getTexture("Assets/images/turtle.png"),
//                        16, 24, 4, 0, 0));
//        AssetPool.addSpritesheet("Assets/images/bigSpritesheet.png",
//                new Spritesheet(AssetPool.getTexture("Assets/images/bigSpritesheet.png"),
//                        16, 32, 42, 0, 0));
//        AssetPool.addSpritesheet("Assets/images/pipes.png",
//                new Spritesheet(AssetPool.getTexture("Assets/images/pipes.png"),
//                        32, 32, 4, 0, 0));
//        AssetPool.addSpritesheet("Assets/images/items.png",
//                new Spritesheet(AssetPool.getTexture("Assets/images/items.png"),
//                        16, 16, 43, 0, 0));

//        for (GameObject g: scene.getGameObjects()) {
//            if (g.getComponent(SpriteRenderer.class) != null) {
//                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
//                if (spr.getTexture() != null)
//                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
//            }
//
//            if (g.getComponent(AnimationStateMachine.class) != null) {
//                AnimationStateMachine stateMachine = g.getComponent(AnimationStateMachine.class);
//                stateMachine.refreshTextures();
//            }
//        }
    }

    @Override
    public void imgui() {
//        gizmo = levelEditorStuff.getComponent(GizmoSystem.class).usingGizmo == 0 ?
//                levelEditorStuff.getComponent(TranslateGizmo.class) : levelEditorStuff.getComponent(ScaleGizmo.class);

//        ImGui.begin("Level Editor Stuff");
//        levelEditorStuff.imgui();
//        ImGui.end();

//        levelEditorStuff.getComponent(GizmoSystem.class).imgui();

//        ImGui.begin("Objects");
//
//        if (ImGui.beginTabBar("WindowTabBar")) {
//            if (ImGui.beginTabItem("Solid Blocks")) {
//                ImVec2 windowsPos = new ImVec2();
//                ImGui.getWindowPos(windowsPos);
//                ImVec2 windowSize = new ImVec2();
//                ImGui.getWindowSize(windowSize);
//                ImVec2 itemSpacing = new ImVec2();
//                ImGui.getStyle().getItemSpacing(itemSpacing);
//
//                float windowX2 = windowsPos.x + windowSize.x;
//                for (int i = 0; i < sprites.size(); i++) {
//                    if (i == 34) continue;
//                    if (i >= 38 && i < 61) continue;
//
//                    Sprite sprite = sprites.getSprite(i);
//                    float spriteWidth = sprite.getWidth() * 4;
//                    float spriteHeight = sprite.getHeight() * 4;
//                    int id = sprite.getTextureId();
//                    Vector2f[] textureCoords = sprite.getTexCoords();
//
//                    ImGui.pushID(i);
//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                        GameObject object = Prefabs.generateSpriteObject("SolidBlock" + i, sprite, 1.0f, 1.0f);
//                        Rigidbody2D rb = new Rigidbody2D();
//                        rb.setBodyType(BodyType.Static);
//                        object.addComponent(rb);
//                        Box2DCollider box2DCollider = new Box2DCollider();
//                        box2DCollider.setHalfSize(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
//                        object.addComponent(box2DCollider);
//                        object.addTag("-EGround");
//                        if (i == 12) {
//                            object.addComponent(new BreakableBlock());
//                        }
//                        // Attach this to the mouse cursor
//                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                    }
//                    ImGui.popID();
//
//                    ImVec2 lastButtonPos = new ImVec2();
//                    ImGui.getItemRectMax(lastButtonPos);
//                    float lastButtonX2 = lastButtonPos.x;
//                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
//                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
//                        ImGui.sameLine();
//                }
//                ImGui.endTabItem();
//            }
//
//            if (ImGui.beginTabItem("Decoration Blocks")) {
//                ImVec2 windowsPos = new ImVec2();
//                ImGui.getWindowPos(windowsPos);
//                ImVec2 windowSize = new ImVec2();
//                ImGui.getWindowSize(windowSize);
//                ImVec2 itemSpacing = new ImVec2();
//                ImGui.getStyle().getItemSpacing(itemSpacing);
//
//                float windowX2 = windowsPos.x + windowSize.x;
//                for (int i = 34; i < 61; i++) {
//                    if (i >= 35 && i < 38) continue;
//                    if (i >= 42 && i < 45) continue;
//
//                    Sprite sprite = sprites.getSprite(i);
//                    float spriteWidth = sprite.getWidth() * 4;
//                    float spriteHeight = sprite.getHeight() * 4;
//                    int id = sprite.getTextureId();
//                    Vector2f[] textureCoords = sprite.getTexCoords();
//
//                    ImGui.pushID(i);
//                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                        GameObject object = Prefabs.generateSpriteObject("DecorationBlock" + i, sprite, 1.0f, 1.0f);
//                        object.transform.zIndex = -1;
//                        // Attach this to the mouse cursor
//                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                    }
//                    ImGui.popID();
//
//                    ImVec2 lastButtonPos = new ImVec2();
//                    ImGui.getItemRectMax(lastButtonPos);
//                    float lastButtonX2 = lastButtonPos.x;
//                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
//                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
//                        ImGui.sameLine();
//                }
//                ImGui.endTabItem();
//            }
//
//            if (ImGui.beginTabItem("Prefabs")) {
//                int uid = 0;
//                Spritesheet playerSprites = AssetPool.getSpritesheet("Assets/images/spritesheet.png");
//                Sprite sprite = playerSprites.getSprite(0);
//                float spriteWidth = sprite.getWidth() * 4;
//                float spriteHeight = sprite.getHeight() * 4;
//                int id = sprite.getTextureId();
//                Vector2f[] textureCoords = sprite.getTexCoords();
//
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateMario();
//                    // Attach this to the mouse cursor
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                Spritesheet items = AssetPool.getSpritesheet("Assets/images/items.png");
//                sprite = items.getSprite(7);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateCoin();
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = items.getSprite(0);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateQuestionBlock();
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = playerSprites.getSprite(14);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateGoomba();
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                Spritesheet turtle = AssetPool.getSpritesheet("Assets/images/turtle.png");
//                sprite = turtle.getSprite(0);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateTurtle();
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = items.getSprite(6);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateFlagtop();
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = items.getSprite(33);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generateFlagPole();
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                Spritesheet pipes = AssetPool.getSpritesheet("Assets/images/pipes.png");
//                sprite = pipes.getSprite(0);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generatePipe(Direction.Down);
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = pipes.getSprite(1);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generatePipe(Direction.Up);
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = pipes.getSprite(2);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generatePipe(Direction.Right);
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
//                ImGui.sameLine();
//
//                sprite = pipes.getSprite(3);
//                id = sprite.getTextureId();
//                textureCoords = sprite.getTexCoords();
//                ImGui.pushID(uid++);
//                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
//                    GameObject object = Prefabs.generatePipe(Direction.Left);
//                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
//                }
//                ImGui.popID();
////                ImGui.sameLine();
//
////                sprite = new Sprite();
////                sprite.setTexture(AssetPool.getTexture("Assets/images/spike.png"));
////                id = sprite.getTextureId();
////                textureCoords = sprite.getTexCoords();
////                ImGui.pushID(uid++);
////                if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoords[2].x, textureCoords[0].y, textureCoords[0].x, textureCoords[2].y)) {
////                    GameObject object = Prefabs.generateSpike();
////                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
////                }
////                ImGui.popID();
//
//                ImGui.endTabItem();
//            }
//            ImGui.endTabBar();
//        }
//
//        ImGui.end();
    }
}
