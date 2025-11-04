package de.vetad.bookworm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class BookwormApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookwormApplication.class, args);
    }

}
