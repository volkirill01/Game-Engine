package engine.graphEditor;

import imgui.extension.nodeditor.NodeEditor;
import imgui.extension.nodeditor.flag.NodeEditorPinKind;
import imgui.internal.ImGui;

public abstract class GraphNode {

    public int nodeId;
    public int inputPinId;
    public int outputPinId;

    public int outputNodeId = -1;

//    public GraphNode(final int nodeId, final int inputPinId, final int outputPintId) {
//        this.nodeId = nodeId;
//        this.inputPinId = inputPinId;
//        this.outputPinId = outputPintId;
//    }

    public void imgui() {
        NodeEditor.beginPin(inputPinId, NodeEditorPinKind.Input);
        ImGui.text("-> In");
        NodeEditor.endPin();

        ImGui.sameLine();

        NodeEditor.beginPin(outputPinId, NodeEditorPinKind.Output);
        ImGui.text("Out ->");
        NodeEditor.endPin();
    }

    public int getInputPinId() {
        return inputPinId;
    }

    public int getOutputPinId() {
        return outputPinId;
    }

    public String getName() {
            return "Node " + (char) (64 + nodeId);
        }
}
