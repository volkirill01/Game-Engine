package engine.renderEngine.guis;

import engine.entities.GameObject;
import engine.renderEngine.Loader;
import engine.renderEngine.Window;
import engine.renderEngine.models.RawModel;
import engine.toolbox.Maths;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class UIRenderer {

    private final RawModel quad;
    private UIShader shader;

    private static List<UIImage> UIs = new ArrayList<>();

    public UIRenderer() {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
        this.quad = Loader.get().loadToVAO(positions, 2);
        shader = new UIShader("engineFiles/shaders/UIs/uiVertexShader.glsl", "engineFiles/shaders/UIs/uiFragmentShader.glsl");
    }

    public void render() {
        UIs.clear();

        for (GameObject go : Window.get().getScene().getGameObjects())
            if (go.doSerialization() && go.getComponent(UIImage.class) != null && go.getComponent(UIImage.class).isActive())
                if (!Window.get().runtimePlaying) {
                    if (!go.isVisible())
                        continue;
                    if (go.transform.mainParent != null && !go.transform.mainParent.isVisible())
                        continue;
                    if (go.transform.parent != null && !go.transform.parent.isVisible())
                        continue;

                    UIs.add(go.getComponent(UIImage.class));
                }

        shader.start();
        glBindVertexArray(this.quad.getVaoID());
        glEnableVertexAttribArray(0);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
//        glDisable(GL_CULL_FACE); // TODO 2 SIDED MESHES
        for (UIImage gui : UIs) {
            if (gui.gameObject != null && gui.isActive()) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, gui.getTexture().getTextureID());
                Matrix4f transformationMatrix = Maths.createTransformationMatrix(gui.gameObject.transform.localPosition, gui.gameObject.transform.localRotation, gui.gameObject.transform.localScale);
                shader.loadUniformMatrix("transformationMatrix", transformationMatrix);
                glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
            }
        }
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() { shader.cleanUp(); }
}
