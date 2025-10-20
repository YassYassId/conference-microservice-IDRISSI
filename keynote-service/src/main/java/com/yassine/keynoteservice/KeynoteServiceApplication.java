package com.yassine.keynoteservice;

import com.yassine.keynoteservice.entities.Keynote;
import com.yassine.keynoteservice.repository.KeynoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.util.ConditionalOnBootstrapEnabled;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class KeynoteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeynoteServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(KeynoteRepository repository) {
        return  args -> {
            repository.save(Keynote.builder()
                    .id(UUID.randomUUID().toString())
                    .firstName("Yassine")
                    .lastName("IDRISSI")
                    .email("yassine@gmail.com")
                    .function("Software Engineer")
                    .build());
            repository.save(Keynote.builder()
                    .id(UUID.randomUUID().toString())
                    .firstName("Achraf")
                    .lastName("Hardizi")
                    .email("achraf@gmail.com")
                    .function("DevOps Engineer")
                    .build());
            repository.save(Keynote.builder()
                    .id(UUID.randomUUID().toString())
                    .firstName("Mohammed")
                    .lastName("Mest")
                    .email("Mohammed@gmail.com")
                    .function("AI Researcher")
                    .build());

            repository.findAll().forEach(keynote -> {
                System.out.println("--------------------------");
                System.out.println(keynote.toString());
                System.out.println("--------------------------");
            });
        };
    }
}
