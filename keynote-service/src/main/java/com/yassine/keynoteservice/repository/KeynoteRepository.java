package com.yassine.keynoteservice.repository;

import com.yassine.keynoteservice.entities.Keynote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@RepositoryRestResource
@Repository
public interface KeynoteRepository extends JpaRepository<Keynote,String> {
    boolean existsByEmail(String email);
    Optional<Keynote> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, String id);
}
