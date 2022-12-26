package engine.renderEngine;

import de.javagl.obj.*;
import engine.renderEngine.models.Mesh;
import engine.renderEngine.models.RawModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OBJLoader {

    public static Mesh loadOBJ(String filepath) {
        List<RawModel> models = new ArrayList<>();

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Obj obj;
        try {
            obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(obj);

        if (materialGroups.size() > 1) {
            for (String matObjKey : materialGroups.keySet()) {
                Obj matObj = materialGroups.get(matObjKey);
                int[] indicesB = ObjData.getFaceVertexIndicesArray(matObj);
                float[] verticesB = ObjData.getVerticesArray(matObj);
                float[] texCoordsB = ObjData.getTexCoordsArray(matObj, 2);
                float[] normalsB = ObjData.getNormalsArray(matObj);

                models.add(Loader.get().loadToVAO(verticesB, texCoordsB, normalsB, indicesB, matObjKey));
            }
        } else {
            int[] indicesB = ObjData.getFaceVertexIndicesArray(obj);
            float[] verticesB = ObjData.getVerticesArray(obj);
            float[] texCoordsB = ObjData.getTexCoordsArray(obj, 2);
            float[] normalsB = ObjData.getNormalsArray(obj);

            models.add(Loader.get().loadToVAO(verticesB, texCoordsB, normalsB, indicesB, "Material"));
        }

        return new Mesh(models, filepath);
    }
}
