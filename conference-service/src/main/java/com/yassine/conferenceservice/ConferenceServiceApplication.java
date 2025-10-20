package com.yassine.conferenceservice;

import com.yassine.conferenceservice.entities.Conference;
import com.yassine.conferenceservice.entities.Review;
import com.yassine.conferenceservice.enums.confType;
import com.yassine.conferenceservice.feign.KeynoteRestClient;
import com.yassine.conferenceservice.model.Keynote;
import com.yassine.conferenceservice.repositories.ConferenceRepository;
import com.yassine.conferenceservice.repositories.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.*;

@SpringBootApplication
@EnableFeignClients
public class ConferenceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConferenceServiceApplication.class, args);
	}

	// Create a commandeline runner bean to initialize some data
	@Bean
	CommandLineRunner commandLineRunner(ConferenceRepository conferenceRepository, ReviewRepository reviewRepository, KeynoteRestClient keynoteRestClient) {
		return args -> {
			// --- Initialize Conferences ---
			Conference c1 = Conference.builder()
					.id(UUID.randomUUID().toString())
					.title("AI & Data Summit 2025")
					.type(confType.ACADEMIC)
					.startDate(new Date())
					.duration(2.5)
					.nbParticipants(300)
					.score(4.7)
					.build();

			Conference c2 = Conference.builder()
					.id(UUID.randomUUID().toString())
					.title("Tech Expo Casablanca 2025")
					.type(confType.COMMERCIAL)
					.startDate(new Date())
					.duration(1.0)
					.nbParticipants(500)
					.score(4.3)
					.build();

			List<Conference> conferences = List.of(c1, c2);
			conferenceRepository.saveAll(conferences);

			// --- Initialize Reviews ---
			Review r1 = Review.builder()
					.id(UUID.randomUUID().toString())
					.reviewDate(new Date())
					.comments("Very insightful and well-organized conference!")
					.score(5)
					.conference(c1)
					.build();

			Review r2 = Review.builder()
					.id(UUID.randomUUID().toString())
					.reviewDate(new Date())
					.comments("Good topics but too short.")
					.score(4)
					.conference(c1)
					.build();

			Review r3 = Review.builder()
					.id(UUID.randomUUID().toString())
					.reviewDate(new Date())
					.comments("Lots of interesting exhibitors!")
					.score(5)
					.conference(c2)
					.build();

			reviewRepository.saveAll(List.of(r1, r2, r3));

			// --- Fetch all keynotes ---
			List<Keynote> allKeynotes = new ArrayList<>(keynoteRestClient.getAllKeynotes().getContent());

			// --- Assign keynotes and IDs to conferences ---
			Random random = new Random();
			conferences.forEach(conference -> {
				int numberOfKeynotes = 1 + random.nextInt(allKeynotes.size()); // random number of keynotes
				List<Keynote> assignedKeynotes = new ArrayList<>();
				List<String> assignedIds = new ArrayList<>();

				while (assignedKeynotes.size() < numberOfKeynotes) {
					Keynote keynote = allKeynotes.get(random.nextInt(allKeynotes.size()));
					if (!assignedKeynotes.contains(keynote)) {
						assignedKeynotes.add(keynote);
						assignedIds.add(keynote.getId());
					}
				}

				conference.setKeynotes(assignedKeynotes);   // transient list
				conference.setKeynoteIds(assignedIds);      // persisted list
				conferenceRepository.save(conference);
			});

			// --- Print results ---
			System.out.println("=== Conferences ===");
			conferenceRepository.findAll().forEach(conf -> {
				System.out.println(conf.getTitle() + " (" + conf.getType() + ")");
				System.out.println("Keynotes:");
				conf.getKeynotes().forEach(k ->
						System.out.println(" - " + k.getFirstName() + " " + k.getLastName()));
				System.out.println("Keynote IDs: " + conf.getKeynoteIds());
			});

			System.out.println("\n=== Reviews ===");
			reviewRepository.findAll().forEach(review ->
					System.out.println(review.getComments() + " -> " + review.getConference().getTitle()));
		};
	}
}
