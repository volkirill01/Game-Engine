package engine.toolbox;

import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;

public class DefaultMeshes {

//    private static Material defaultMaterial = new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png"));

    public static Material getDefaultMaterial() { return new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png")); }

    public static TexturedModel DefaultCube() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultCube.obj"), new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png")));
    }

    public static TexturedModel DefaultSphere() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultSphere.obj"), new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png")));
    }
}
