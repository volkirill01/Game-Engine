package engine.renderEngine.font.fontRendering;

import engine.renderEngine.Loader;
import engine.renderEngine.font.fontMeshCreator.FontType;
import engine.renderEngine.font.fontMeshCreator.UIText;
import engine.renderEngine.font.fontMeshCreator.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {

    private static Map<FontType, List<UIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init() { renderer = new FontRenderer(); }

    public static void render() { renderer.render(texts); }

    public static void loadText(UIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.get().loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<UIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void removeText(UIText text) {
        List<UIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty())
            texts.remove(text.getFont());
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }
}
