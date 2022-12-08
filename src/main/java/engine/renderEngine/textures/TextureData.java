package engine.renderEngine.textures;

import java.nio.ByteBuffer;

public class TextureData {

    private int width;
    private int height;
    private ByteBuffer buffer;

    public TextureData(ByteBuffer buffer, int width, int height) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public ByteBuffer getBuffer() { return this.buffer; }
}
