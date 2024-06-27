package dev.dynamic.nodes;

import dev.dynamic.Main;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HeartbeatManager extends WebSocketServer {

    private static final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());
    private static final Set<CacheNode> respondedNodes = Collections.synchronizedSet(new HashSet<>());

    public HeartbeatManager(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Main.logger.info("New connection from {}", webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Main.logger.info("Closed connection to {}", webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        Main.logger.info("Message from {}: {}", webSocket.getRemoteSocketAddress(), s);
        if (NodeManager.nodeMap.containsKey(Integer.valueOf(s))) {
            respondedNodes.add(NodeManager.getNode(Integer.parseInt(s)));
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Main.logger.error("An error occurred with the connection to {}", webSocket.getRemoteSocketAddress(), e);
    }

    @Override
    public void onStart() {
        Main.logger.info("Server started successfully!");
    }

    public void broadcastMessage(String message) {
        synchronized (clients) {
            for (WebSocket client : clients) {
                if (client.isOpen()) {
                    client.send(message);
                }
            }
        }
    }

    public void startHeartbeatManager() {
        Thread thread = new Thread(() -> {
            NodeManager.nodeMap.forEach((id, node) -> {
                if (!respondedNodes.contains(node)) {
                    Main.logger.warn("Node {} did not respond to the heartbeat request.", id);
                    NodeManager.getNode(id).setAlive(false);
                }
            });
        });
        thread.start();
    }
}
