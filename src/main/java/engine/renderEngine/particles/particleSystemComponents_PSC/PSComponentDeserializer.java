package engine.renderEngine.particles.particleSystemComponents_PSC;

import com.google.gson.*;
import engine.components.Component;

import java.lang.reflect.Type;

public class PSComponentDeserializer implements JsonSerializer<ParticleSystemComponent>, JsonDeserializer<ParticleSystemComponent> {

    @Override
    public ParticleSystemComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type '" + type + "' " + e);
        }
    }

    @Override
    public JsonElement serialize(ParticleSystemComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}
