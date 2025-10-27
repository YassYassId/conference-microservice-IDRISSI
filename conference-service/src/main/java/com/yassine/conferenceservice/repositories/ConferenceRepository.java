package com.yassine.conferenceservice.repositories;

import com.yassine.conferenceservice.entities.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference,String> {
}
