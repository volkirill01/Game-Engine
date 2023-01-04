package engine.toolbox;

import engine.renderEngine.Loader;
import engine.renderEngine.OBJLoader;
import engine.renderEngine.models.TexturedModel;
import engine.renderEngine.textures.Material;

public class DefaultMeshes {

//    private static Material defaultMaterial = new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png"));

//    public static Material getDefaultMaterial() { return new Material(Loader.get().loadTexture("engineFiles/images/utils/whitePixel.png")); }

    public static String getDefaultMaterialPath() { return "engineFiles/defaultAssets/defaultMaterial.material"; }

    public static TexturedModel DefaultCube() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultCube.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }

    public static TexturedModel DefaultSphere() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultSphere.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }

    public static TexturedModel DefaultCapsule() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultCapsule.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }

    public static TexturedModel DefaultCylinder() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultCylinder.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }

    public static TexturedModel DefaultPlane() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultPlane.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }

    public static TexturedModel DefaultCone() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultCone.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }

    public static TexturedModel DefaultPyramid() {
        return new TexturedModel(OBJLoader.loadOBJ("engineFiles/defaultMeshes/defaultPyramid.obj"), Loader.get().loadMaterial(getDefaultMaterialPath()));
    }
}
