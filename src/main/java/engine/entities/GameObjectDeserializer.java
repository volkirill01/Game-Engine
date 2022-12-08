package engine.entities;

import com.google.gson.*;
import engine.components.Component;
import engine.components.Transform;
import engine.toolbox.customVariables.GameObjectTag;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");
        JsonArray childs = jsonObject.getAsJsonArray("childs");
        JsonArray tags = jsonObject.getAsJsonArray("tags");

        GameObject go = new GameObject(name);
        for (JsonElement e: components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        if (childs != null)
            for (JsonElement e: childs) {
                GameObject child = context.deserialize(e, GameObject.class);
                go.transform.addChild(child);
                child.transform.parent = go;

                if (go.transform.mainParent != null)
                    child.transform.mainParent = go.transform.mainParent;
                else
                    child.transform.mainParent = go;
            }
        for (JsonElement e: tags) {
            GameObjectTag tag = context.deserialize(e, GameObjectTag.class);
            go.addTag(tag);
        }
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}
