package de.vetad.bookworm;

import org.testcontainers.utility.DockerImageName;

public class Kafka {

    public static final DockerImageName DOCKER_IMAGE_NAME;

    static {

        // when using a proxy to pull an image set environment variable IMAGE_PROXY with a trailing '/', e.g.
        // IMAGE_PROXY=repository.acme.com/docker-proxy/

        var proxy = System.getenv().getOrDefault("IMAGE_PROXY", "");
        var image = "confluentinc/cp-kafka:7.7.5";
        var imageName = "%s%s".formatted(proxy, image);

        DOCKER_IMAGE_NAME = DockerImageName
                .parse(imageName)
                .asCompatibleSubstituteFor("confluentinc/cp-kafka");
    }
}
