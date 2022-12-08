package engine.toolbox.customVariables;

import org.joml.Vector3f;

public class GameObjectTag {
    public String tag;
    public Vector3f color;

    public GameObjectTag(String tag, Vector3f color) {
        this.tag = tag;
        this.color = color;
    }
}
