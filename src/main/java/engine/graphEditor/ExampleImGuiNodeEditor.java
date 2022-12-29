package engine.graphEditor;

import imgui.ImVec4;
import imgui.extension.nodeditor.NodeEditor;
import imgui.extension.nodeditor.NodeEditorConfig;
import imgui.extension.nodeditor.NodeEditorContext;
import imgui.extension.nodeditor.flag.NodeEditorPinKind;
import imgui.extension.nodeditor.flag.NodeEditorStyleVar;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.ImGui;
import imgui.type.ImLong;

public class ExampleImGuiNodeEditor {

    private static final NodeEditorContext CONTEXT;

    static {
        NodeEditorConfig config = new NodeEditorConfig();
        config.setSettingsFile(null);
        CONTEXT = new NodeEditorContext(config);
    }

    private ImVec4 titleColor = new ImVec4(1.0f, 1.0f, 1.0f, 1.0f);

    public ExampleImGuiNodeEditor() {
        titleColor.x = 288.0f / 255.0f; // Red title
        titleColor.y = 50.0f / 255.0f;
        titleColor.z = 35.0f / 255.0f;
    }

    public void imgui(Graph currentGraph) {
        ImGui.setNextWindowSize(500, 400, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 200, ImGuiCond.Once);
        if (ImGui.begin(" Test GraphEditor ")) {
            if (ImGui.button("Focus on content"))
                NodeEditor.navigateToContent(1);

            NodeEditor.setCurrentEditor(CONTEXT);
            NodeEditor.begin("Node Editor");

            for (int i = 0; i < currentGraph.nodes.values().size(); i++) {
                Graph.GraphNode node = (Graph.GraphNode) currentGraph.nodes.values().toArray()[i];

                NodeEditor.pushStyleVar(NodeEditorStyleVar.NodePadding, 1.0f, 1.0f, 1.0f, 8.0f);
                NodeEditor.beginNode(node.nodeId);

                float startX = ImGui.getCursorPosX();
                float startY = ImGui.getCursorPosY();

                ImGui.pushStyleColor(ImGuiCol.Button, titleColor.x, titleColor.y, titleColor.z, titleColor.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, titleColor.x, titleColor.y, titleColor.z, titleColor.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, titleColor.x, titleColor.y, titleColor.z, titleColor.w);
                ImGui.pushStyleColor(ImGuiCol.Border, 0.0f, 0.0f, 0.0f, 0.0f);
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 35.0f, 4.1f);
                ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 99.0f);
                ImGui.button("##TitleBackground(Top)");
                ImGui.setItemAllowOverlap();
                ImGui.popStyleVar(2);

                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 35.0f, -2.0f);
                ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);
                ImGui.setCursorPos(startX, startY + 12.4f);
                ImGui.button("##TitleBackground(Bottom)");
                ImGui.setItemAllowOverlap();
                ImGui.popStyleVar(2);
                ImGui.popStyleColor(4);

                ImGui.setCursorPos(startX + ImGui.getStyle().getFramePaddingX(), startY + ImGui.getStyle().getFramePaddingY() - 0.5f);
                ImGui.text(node.getName());

                NodeEditor.beginPin(node.getInputPinId(), NodeEditorPinKind.Input);
                ImGui.text("-> In");
                NodeEditor.endPin();

                ImGui.sameLine();

                NodeEditor.beginPin(node.getOutputPinId(), NodeEditorPinKind.Output);
                ImGui.text("Out ->");
                NodeEditor.endPin();

                NodeEditor.endNode();
                NodeEditor.popStyleVar(1);
            }

            if (NodeEditor.beginCreate()) {
                final ImLong a = new ImLong();
                final ImLong b = new ImLong();
                if (NodeEditor.queryNewLink(a, b)) {
                    final Graph.GraphNode source = currentGraph.findByOutput(a.get());
                    final Graph.GraphNode target = currentGraph.findByInput(b.get());
                    if (source != null && target != null && source.outputNodeId != target.nodeId && NodeEditor.acceptNewItem()) {
                        source.outputNodeId = target.nodeId;
                    }
                }
            }
            NodeEditor.endCreate();

            int uniqueLinkId = 1;
            for (Graph.GraphNode node : currentGraph.nodes.values()) {
                if (currentGraph.nodes.containsKey(node.outputNodeId)) {
                    NodeEditor.link(uniqueLinkId++, node.getOutputPinId(), currentGraph.nodes.get(node.outputNodeId).getInputPinId());
                }
            }

            NodeEditor.suspend();

            final long nodeWithContextMenu = NodeEditor.getNodeWithContextMenu();
            if (nodeWithContextMenu != -1) {
                ImGui.openPopup("node_context");
                ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), (int) nodeWithContextMenu);
            }

            if (ImGui.isPopupOpen("node_context")) {
                final int targetNode = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"));
                if (ImGui.beginPopup("node_context")) {
                    if (ImGui.button("Delete " + currentGraph.nodes.get(targetNode).getName())) {
                        currentGraph.nodes.remove(targetNode);
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }
            }

            if (NodeEditor.showBackgroundContextMenu()) {
                ImGui.openPopup("node_editor_context");
            }

            if (ImGui.beginPopup("node_editor_context")) {
                if (ImGui.button("Create New Node")) {
                    final Graph.GraphNode node = currentGraph.createGraphNode();
                    final float canvasX = NodeEditor.toCanvasX(ImGui.getMousePosX());
                    final float canvasY = NodeEditor.toCanvasY(ImGui.getMousePosY());
                    NodeEditor.setNodePosition(node.nodeId, canvasX, canvasY);
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }

            NodeEditor.resume();
            NodeEditor.end();
        }
        ImGui.end();
    }
}
