package dev.dynamic.storage;

import co.aikar.idb.DB;
import dev.dynamic.nodes.CacheNode;
import lombok.experimental.UtilityClass;

import java.sql.SQLException;

@UtilityClass
public class NodePersistence {

    public void setupPersistence() throws SQLException {
        DB.executeUpdate("CREATE TABLE IF NOT EXISTS nodes (node_id INT PRIMARY KEY, ip_addr VARCHAR(255), container_id VARCHAR(255), port INT)");
    }

    public void saveNode(CacheNode node) throws SQLException {
        DB.executeUpdate("INSERT INTO nodes (node_id, ip_addr, container_id, port) VALUES (?, ?, ?, ?)", node.getNodeId(), node.getIpAddr(), node.getContainerId(), node.getPort());
    }

    public void removeNode(int nodeId) throws SQLException {
        DB.executeUpdate("DELETE FROM nodes WHERE node_id = ?", nodeId);
    }

    public void loadNodes() throws SQLException {
        DB.getResults("SELECT * FROM nodes").forEach(row -> {
            CacheNode node = new CacheNode();
            node.setNodeId(row.getInt("node_id"));
            node.setIpAddr(row.getString("ip_addr"));
            node.setContainerId(row.getString("container_id"));
            node.setPort(row.getInt("port"));
            node.setAlive(false); // Wait for initial heartbeat, which is in 500 millis
            node.deployContainer();
        });
    }
}
