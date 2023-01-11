package engine.graphEditor.graphNodes;

import engine.graphEditor.GraphNode;
import imgui.internal.ImGui;

public class GraphNode_Multiply extends GraphNode {

    @Override
    public String getName() { return "Multiply"; }

    @Override
    protected void drawInputSide() {
        drawPin(PinDirection.Input);
//        ImGui.text("Test");
//        ImGui.dragFloat("Test2", new float[]{ 1.0f });
    }

    @Override
    protected void drawOutputSide() {
        drawPin(PinDirection.Output);
    }
}
