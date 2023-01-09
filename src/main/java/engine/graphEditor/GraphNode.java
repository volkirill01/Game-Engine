package engine.graphEditor;

import engine.TestFieldsWindow;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.nodeditor.NodeEditor;
import imgui.extension.nodeditor.flag.NodeEditorPinKind;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.ImGui;

public abstract class GraphNode {

    protected enum PinDirection {
        Input,
        Output
    }

    public int nodeId;
    public int inputPinId;
    public int outputPinId;

    public int outputNodeId = -1;

    private float nodeInputWidth = 0;
    private float nodeHeight = 0;

//    public GraphNode(final int nodeId, final int inputPinId, final int outputPintId) {
//        this.nodeId = nodeId;
//        this.inputPinId = inputPinId;
//        this.outputPinId = outputPintId;
//    }

    public void imgui() {
//        nodeInputWidth = 10.0f + TestFieldsWindow.getFloats[0];
//        nodeHeight = 10.0f + TestFieldsWindow.getFloats[1];

        drawInputSide();

//        drawPin(PinDirection.Input);
//        NodeEditor.beginPin(inputPinId, NodeEditorPinKind.Input);
//        ImGui.text("-> In");
//        NodeEditor.endPin();

        ImGui.sameLine();

        drawOutputSide();
//        drawPin(PinDirection.Output);
//        NodeEditor.beginPin(outputPinId, NodeEditorPinKind.Output);
//        ImGui.text("Out ->");
//        NodeEditor.endPin();
    }

    protected void drawInputSide() { }

    protected void drawOutputSide() { }

    protected void drawPin(PinDirection pinDirection) {
        NodeEditor.beginPin((pinDirection == PinDirection.Input) ? inputPinId : outputPinId, (pinDirection == PinDirection.Input) ? NodeEditorPinKind.Input : NodeEditorPinKind.Output);
        ImGui.text((pinDirection == PinDirection.Input) ? "-> In" : "Out ->");
        NodeEditor.endPin();
    }

    public int getInputPinId() { return inputPinId; }

    public int getOutputPinId() { return outputPinId; }

    public String getName() {
        return "Node " + (char) (64 + nodeId);
    }
}
