package engine.gizmo;

import engine.renderEngine.Window;

public class GizmoSystem {

    private ScaleGizmo scaleGizmo;

    public GizmoSystem() {
        this.scaleGizmo = new ScaleGizmo();
    }

    public void update() {
        if (Window.get().getImGuiLayer().getInspectorWindow().getActiveGameObject() == null)
            return;

        scaleGizmo.update();
    }
}
