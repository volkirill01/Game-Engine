package engine.graphEditor;

import engine.graphEditor.graphNodes.GraphNode_Multiply;

import java.util.HashMap;
import java.util.Map;

public final class Graph {

    public int nextNodeId = 1;
    public int nextPinId = 100;

    public final Map<Integer, GraphNode> nodes = new HashMap<>();

    public Graph() {
        final GraphNode first = createGraphNode(new GraphNode_Multiply());
        final GraphNode second = createGraphNode(new GraphNode_Multiply());
        first.outputNodeId = second.nodeId;
    }

    public GraphNode createGraphNode(GraphNode node) {
//        final GraphNode node = new GraphNode(nextNodeId++, nextPinId++, nextPinId++);
        node.nodeId = nextNodeId++;
        node.inputPinId = nextPinId++;
        node.outputPinId = nextPinId++;
        this.nodes.put(node.nodeId, node);
        return node;
    }

    public GraphNode findByInput(final long inputPinId) {
        for (GraphNode node : nodes.values()) {
            if (node.getInputPinId() == inputPinId) {
                return node;
            }
        }
        return null;
    }

    public GraphNode findByOutput(final long outputPinId) {
        for (GraphNode node : nodes.values()) {
            if (node.getOutputPinId() == outputPinId) {
                return node;
            }
        }
        return null;
    }
}