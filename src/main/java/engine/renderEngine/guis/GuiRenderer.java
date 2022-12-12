package engine.renderEngine.guis;

import engine.renderEngine.Loader;
import engine.renderEngine.models.RawModel;
import engine.toolbox.Maths;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class GuiRenderer {

    private final RawModel quad;
    private GuiShader shader;

    public GuiRenderer() {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
        this.quad = Loader.get().loadToVAO(positions, 2);
        shader = new GuiShader("engineFiles/shaders/guis/guiVertexShader.glsl", "engineFiles/shaders/guis/guiFragmentShader.glsl");
    }

    public void render(List<GuiTexture> guis) {
        shader.start();
        glBindVertexArray(this.quad.getVaoID());
        glEnableVertexAttribArray(0);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
//        glDisable(GL_CULL_FACE); // TODO DELETE
        for (GuiTexture gui : guis) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, gui.getTexture().getTextureID());
            Matrix4f transformationMatrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getRotation(), gui.getScale());
            shader.loadUniformMatrix("transformationMatrix", transformationMatrix);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() { shader.cleanUp(); }
}
