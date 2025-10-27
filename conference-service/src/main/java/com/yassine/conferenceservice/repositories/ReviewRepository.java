package com.yassine.conferenceservice.repositories;

import com.yassine.conferenceservice.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,String> {
    List<Review> findByConferenceId(String id);
}
