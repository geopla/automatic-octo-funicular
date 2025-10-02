package de.vetad.bookworm;

import org.springframework.boot.SpringApplication;

public class TestBookwormApplication {

    public static void main(String[] args) {
        SpringApplication.from(BookwormApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
