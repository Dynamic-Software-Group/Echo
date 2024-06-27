package dev.dynamic;

import dev.dynamic.nodes.HeartbeatManager;
import dev.dynamic.nodes.NodeManager;
import dev.dynamic.storage.NodePersistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.sql.SQLException;

public class Main {

    public static Logger logger = LogManager.getLogger();
    public static HeartbeatManager heartbeatManager;

    public static void main(String[] args) throws SQLException {
        NodePersistence.setupPersistence();
        NodePersistence.loadNodes();
        heartbeatManager = new HeartbeatManager(new InetSocketAddress("localhost", 8887));
        heartbeatManager.start();
        heartbeatManager.startHeartbeatManager();

        NodeManager.deployAllNodes();

        // Add shutdown listener
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NodeManager.nodeMap.values().forEach(node -> {
                try {
                    NodePersistence.saveNode(node);
                } catch (SQLException e) {
                    logger.error("An error occurred while saving the nodes to the database.", e);
                }
            });
        }));
    }
}