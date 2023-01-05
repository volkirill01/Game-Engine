package engine.entities;

import com.google.gson.*;
import engine.components.Component;
import engine.components.Transform;
import engine.imGui.Console;
import engine.imGui.ConsoleMessage;
import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.Window;
import engine.renderEngine.components.MeshRenderer;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;
import engine.toolbox.customVariables.GameObjectTag;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");
        JsonArray tags = jsonObject.getAsJsonArray("tags");

        GameObject go = new GameObject(name);
        for (JsonElement e: components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.transform = go.getComponent(Transform.class);

        for (JsonElement e: tags) {
            GameObjectTag tag = context.deserialize(e, GameObjectTag.class);
            go.addTag(tag);
        }
        return go;
    }
}
