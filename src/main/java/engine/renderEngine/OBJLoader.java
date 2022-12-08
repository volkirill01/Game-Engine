package engine.renderEngine;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import engine.renderEngine.models.RawModel;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class OBJLoader {

    public static RawModel loadOBJ(String filepath) {
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

        IntBuffer indicesB = ObjData.getFaceVertexIndices(obj);
        FloatBuffer verticesB = ObjData.getVertices(obj);
        FloatBuffer texCoordsB = ObjData.getTexCoords(obj, 2);
        FloatBuffer normalsB = ObjData.getNormals(obj);

        int[] indicesBArray = new int[indicesB.capacity()];
        for (int i = 0; i < indicesBArray.length; i++)
            indicesBArray[i] = indicesB.get(i);

        float[] verticesBArray = new float[verticesB.capacity()];
        for (int i = 0; i < verticesBArray.length; i++)
            verticesBArray[i] = verticesB.get(i);

        float[] texCoordsBArray = new float[texCoordsB.capacity()];
        for (int i = 0; i < texCoordsBArray.length; i++)
            texCoordsBArray[i] = texCoordsB.get(i);

        float[] normalsBArray = new float[normalsB.capacity()];
        for (int i = 0; i < normalsBArray.length; i++)
            normalsBArray[i] = normalsB.get(i);

        return Loader.get().loadToVAO(verticesBArray, texCoordsBArray, normalsBArray, indicesBArray);
    }
}
