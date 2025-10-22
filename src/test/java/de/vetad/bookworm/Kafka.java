package de.vetad.bookworm;

import org.testcontainers.utility.DockerImageName;

public class Kafka {

    public static final DockerImageName DOCKER_IMAGE_NAME;

    static {
        String proxy = System.getenv().getOrDefault("IMAGE_PROXY", "");
        String image = "confluentinc/cp-kafka:7.7.5";
        String imageName = "%s%s".formatted(proxy, image);

        DOCKER_IMAGE_NAME = DockerImageName
                .parse(imageName)
                .asCompatibleSubstituteFor("confluentinc/cp-kafka");
    }
}
