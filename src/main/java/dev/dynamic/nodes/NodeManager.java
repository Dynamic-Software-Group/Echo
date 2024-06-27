package dev.dynamic.nodes;

import dev.dynamic.Main;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class NodeManager {

    public final Map<Integer, CacheNode> nodeMap = new HashMap<>();

    public void addNode(CacheNode node) {
        nodeMap.put(node.getNodeId(), node);
        Main.logger.info("Added node {} to the manager.", node.getNodeId());
    }

    public void removeNode(int nodeId) {
        nodeMap.remove(nodeId);
        Main.logger.info("Removed node {} from the manager.", nodeId);
    }

    public CacheNode getNode(int nodeId) {
        return nodeMap.get(nodeId);
    }

    public void deployNode(int nodeId) {
        CacheNode node = getNode(nodeId);
        node.deployContainer();
    }

    /**
     * Should only be used on startup, for anything else use deployNode(int nodeId)
     */
    public void deployAllNodes() {
        nodeMap.values().forEach(CacheNode::deployContainer);
    }

}
