package dev.dynamic.nodes;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.Data;

import java.time.Duration;
import java.util.Arrays;

@Data
public class CacheNode {
    private int nodeId;
    private String ipAddr;
    private String containerId;
    private int port;
    private boolean isAlive; // Check if the node is attached to the manager

    public DockerClient connect() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://" + ipAddr + ":" + port)
                .withDockerTlsVerify(true)
                .build();

        DockerHttpClient client = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, client);
    }

    /**
     * Deploys the container to the node
     * If the container is already deployed, it will start the container
     */
    public void deployContainer() {
        DockerClient client = connect();
        Image image = client.listImagesCmd().exec().stream()
                .filter(img -> img.getRepoTags() != null && Arrays.stream(img.getRepoTags()).anyMatch(tag -> tag.contains("dynamic-cache")))
                .findFirst()
                .orElse(null);

        if (image == null) {
            // TODO: build the image from the Dockerfile
        }

        if (containerId == null) {
            this.containerId = client.createContainerCmd(image.getId())
                    .withName("dynamic-cache-" + nodeId)
                    .exec()
                    .getId();
        }
        client.startContainerCmd(containerId).exec();
    }
}
