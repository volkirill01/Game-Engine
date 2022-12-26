package engine.renderEngine.models;

import engine.renderEngine.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Mesh {

    private String filepath;
    private transient List<RawModel> models;

    private String metaFilepath;
    private transient Map<String, Object> metaData;

    public Mesh(RawModel model) {
        this.filepath = "_Generated";
        this.models = new ArrayList<>(){{
            add(model);
        }};
    }

    public Mesh(List<RawModel> models, String filepath) {
        this.filepath = filepath;
        this.models = models;

        this.metaFilepath = this.filepath;
        this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultModel.meta", true);
    }

    public void update() {
        if (!this.filepath.equals("_Generated"))
            this.metaData = Loader.get().loadMeta(this.metaFilepath, "engineFiles/defaultAssets/defaultModel.meta", true);
    }

    public String getFilepath() { return this.filepath; }

    public List<RawModel> getModels() { return this.models; }

    public float getSizeMultiplayer() {
        if (!this.filepath.equals("_Generated"))
            return Float.parseFloat(this.metaData.get("modelSize").toString());
        return 1.0f;
    }
}
