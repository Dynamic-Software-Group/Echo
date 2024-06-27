package dev.dynamic;

import dev.dynamic.nodes.HeartbeatManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class Main {

    public static Logger logger = LogManager.getLogger();
    public static HeartbeatManager heartbeatManager;

    public static void main(String[] args) {
        heartbeatManager = new HeartbeatManager(new InetSocketAddress("localhost", 8887));
        heartbeatManager.start();
        heartbeatManager.startHeartbeatManager();
    }
}