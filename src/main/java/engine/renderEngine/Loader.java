package engine.renderEngine;

import engine.assets.Asset;
import engine.assets.assetsTypes.*;
import engine.renderEngine.models.RawModel;
import engine.renderEngine.textures.Material;
import engine.renderEngine.textures.TextureFilterMode;
import engine.renderEngine.textures.Texture;
import engine.renderEngine.textures.TextureData;
import org.apache.commons.io.FileUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL33;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Loader {

    private static Loader instance;

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();
    private Map<String, Texture> texturesFiles = new HashMap<>();
    private Map<String, Material> materials = new HashMap<>();

    private Map<String, Asset> assets = new HashMap<>();

    public static Loader get() {
        if (instance == null)
            instance = new Loader();

        return instance;
    }

    public Material loadMaterial(String filepath) {
        if (materials.containsKey(filepath))
            return materials.get(filepath);

        Map<String, Object> data = loadMeta(filepath, "engineFiles/defaultAssets/defaultMaterial.material", false);

//        Texture albedo = get().loadTexture(data.get("albedo").toString());
        Material material = new Material(filepath, data);

        materials.put(filepath, material);
        return material;
    }

    public void saveMaterial(String filepath, Map<String, Object> data) {
        StringBuilder fileMeta = new StringBuilder();

        for (String line : data.keySet())
            fileMeta.append(line).append(" = ").append(data.get(line)).append("\n");

        try {
            FileUtils.writeStringToFile(new File(filepath), fileMeta.toString(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Asset_Material loadAsset_Material(String filepath) {
        if (assets.containsKey(filepath))
            return (Asset_Material) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Material file = new Asset_Material(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultMaterial.material", false), get().loadTexture("engineFiles/images/icons/icon=file-code-solid-(256x256).png"));

        assets.put(filepath, file);
        return file;
    }
//    public void saveMaterial(Material material) {
//        StringBuilder fileMeta = new StringBuilder();
//
//        for (String line : material.getData().keySet())
//            fileMeta.append(line).append(" = ").append(material.getData().get(line)).append("\n");
//
//        try {
//            FileUtils.writeStringToFile(new File(material.getFilepath()), fileMeta.toString(), "UTF-8");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    //<editor-fold desc="Assets Files">
    public Asset_Scene loadAsset_Scene(String filepath) {
        if (assets.containsKey(filepath))
            return (Asset_Scene) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Scene file = new Asset_Scene(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultScene.meta", true), false);

        assets.put(filepath, file);
        return file;
    }

    public Asset_Image loadAsset_Image(String filepath, Texture fileIcon) {
        if (assets.containsKey(filepath))
            return (Asset_Image) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Image file = new Asset_Image(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultImage.meta", true), fileIcon);

        assets.put(filepath, file);
        return file;
    }

    public Asset_Sound loadAsset_Sound(String filepath) {
        if (assets.containsKey(filepath))
            return (Asset_Sound) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Sound file = new Asset_Sound(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultSound.meta", true));

        assets.put(filepath, file);
        return file;
    }
    public Asset_Shader loadAsset_Shader(String filepath) {
        if (assets.containsKey(filepath))
            return (Asset_Shader) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Shader file = new Asset_Shader(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultShader.meta", true));

        assets.put(filepath, file);
        return file;
    }

    public Asset_Model loadAsset_Model(String filepath) {
        if (assets.containsKey(filepath))
            return (Asset_Model) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Model file = new Asset_Model(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultModel.meta", true),
                loadTexture("engineFiles/images/icons/icon=cube-solid-(256x256).png")); // TODO DISPLAY MODEL (NOT STATIC ICON)

        assets.put(filepath, file);
        return file;
    }

    public Asset_Other loadAsset_Other(String filepath) {
        if (assets.containsKey(filepath))
            return (Asset_Other) assets.get(filepath);

        String[] tmp = filepath.replace("\\", "/").split("/");
        String fileName = tmp[tmp.length - 1];

        Asset_Other file = new Asset_Other(filepath, fileName,
                loadMeta(filepath, "engineFiles/defaultAssets/defaultOther.meta", true));

        assets.put(filepath, file);
        return file;
    }
    //</editor-fold>

    //<editor-fold desc="Meta Files">
    public Map<String, Object> loadMeta(String filepath, String defaultData_filepath, boolean addMetaSuffix) {
        Map<String, Object> data = new HashMap<>();
        List<String> lines = loadFileMeta_Lines(filepath, addMetaSuffix);

        if (lines == null)
            return loadMeta(defaultData_filepath, "", addMetaSuffix);

        for (String line : lines)
            data.put(line.split(" = ")[0], line.split(" = ")[1]);

        return data;
    }

    private List<String> loadFileMeta_Lines(String filepath, boolean addMetaSuffix) {
        File modelMeta = new File(addMetaSuffix ? (filepath.endsWith(".meta") ? filepath : filepath + ".meta") : filepath);
        List<String> lines = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(modelMeta));
            String st;

            while ((st = br.readLine()) != null)
                lines.add(st);
        } catch (IOException e) {
            return null;
//            throw new RuntimeException(e);
        }

        return lines;
    }

    public void saveMeta(String filepath, Map<String, Object> data) {
        StringBuilder fileMeta = new StringBuilder();

        for (String line : data.keySet())
            fileMeta.append(line).append(" = ").append(data.get(line)).append("\n");

        try {
            FileUtils.writeStringToFile(new File(filepath + ".meta"), fileMeta.toString(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //</editor-fold>

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices, String filepath) {
        int vaoID = createVao();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length, filepath);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVao();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return vaoID;
    }

    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVao();
        storeDataInAttributeList(0, dimensions, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimensions);
    }

    public int createEmptyVbo(int floatCount) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, floatCount * Float.BYTES, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int increasedDataLength, int offset) {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindVertexArray(vao);
        glVertexAttribPointer(attribute, dataSize, GL_FLOAT, false, increasedDataLength * Float.BYTES, offset * Float.BYTES);
        GL33.glVertexAttribDivisor(attribute, 1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * Float.BYTES, GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void updateTexture(Texture texture) {
        // Generate texture on GPU
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

        // Set texture parameters
        // Repeat image in both directions
        if (texture.isRepeatHorizontally())
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // X
        else
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP); // X

        if (texture.isRepeatVertically())
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // Y
        else
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP); // Y

        if (texture.getFilterMode() == TextureFilterMode.Nearest) {
            // When stretch the texture image, pixelate it
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            // When shrinking an image, pixelate it
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        } else {                                          // if (texture.getFilterMode() == FilterMode.Linear)
            // When stretch the texture image, blur it
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            // When shrinking an image, blur it
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }

        if (texture.isUseMipmaps()) {
            glGenerateMipmap(GL_TEXTURE_2D);
            if (texture.getFilterMode() == TextureFilterMode.Nearest)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
            else                                              // if (texture.getFilterMode() == FilterMode.Linear)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.6f); // изменяем уровень mipmap чтобы они начинались с большей дистанции, чем меньше число тем дальше старт mipmap
        } else if (texture.isUseAnisotropicFiltering()) {// use anisotropic filtering
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
            float amount = Math.min(4.0f,  glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        }
    }

    public Texture loadTexture(String filepath) { return loadTexture(filepath, false, true); }

    public Texture loadTexture(String filepath, boolean useMipmaps) { return loadTexture(filepath, useMipmaps, false); }

    public Texture loadTexture(String filepath, boolean useMipmaps, boolean useAnisotropicFiltering) {
        if (texturesFiles.containsKey(filepath)) {
//            updateTexture(texturesFiles.get(filepath));
            return texturesFiles.get(filepath);
        }

        // Generate texture on GPU
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // X
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // Y
//        // When stretch the texture image, pixelate it
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        // When shrinking an image, pixelate it
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // When stretch the texture image, blur it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // When shrinking an image, blur it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            if (channels.get(0) == 3)
                glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            else if (channels.get(0) == 4)
                glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB_ALPHA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            else
                assert false: "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
        } else
            assert false: "Error: (Texture) Could ton load image '" + filepath + "'";

        stbi_image_free(image);
        if (useMipmaps) {
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.6f); // изменяем уровень mipmap чтобы они начинались с большей дистанции, чем меньше число тем дальше старт mipmap
        } else if (useAnisotropicFiltering) {// use anisotropic filtering
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
            float amount = Math.min(4.0f,  glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        }

        Texture texture = new Texture(textureID, filepath, width.get(0), height.get(0));

        texturesFiles.put(filepath, texture);

        return texture;
    }

    public int loadCubeMap(String[] textureFiles) {
        int texID = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile(textureFiles[i]);

//            // right, left, top, bottom, back, front // TODO FIX SKYBOXES
//            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA,
//                    data.getWidth(), data.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
//                    data.getBuffer());
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // linear smoothing of texture // TODO REPLACE THIS IF YOU USE PIXEL ART SKYBOX
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // linear smoothing of texture // TODO REPLACE THIS IF YOU USE PIXEL ART SKYBOX
        textures.add(texID);
        return texID;
    }

    private TextureData decodeTextureFile(String filepath) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = null;
        try {
            image = stbi_load(filepath, width, height, channels, 0);
            stbi_image_free(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new TextureData(image, width.get(0), height.get(0));
//        int width = 0;
//        int height = 0;
//        ByteBuffer buffer = null;
//        try {
//            FileInputStream in = new FileInputStream(filepath);
//            PNGDecoder decoder = new PNGDecoder(in);
//            width = decoder.getWidth();
//            height = decoder.getHeight();
//            buffer = ByteBuffer.allocate(4 * width * height);
//            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
//            buffer.flip();
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Tried to load texture '" + filepath + "', didn`t work");
//            System.exit(-1);
//        }
//        return new TextureData(buffer, width, height);
    }

    public void cleanUp() {
        for (int vao : vaos)
            glDeleteVertexArrays(vao);

        for (int vbo : vbos)
            glDeleteBuffers(vbo);

        for (int texture : textures)
            glDeleteTextures(texture);
    }

    private int createVao() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIndicesBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIndicesBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
