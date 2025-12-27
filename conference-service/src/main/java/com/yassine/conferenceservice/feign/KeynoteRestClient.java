package com.yassine.conferenceservice.feign;

import com.yassine.conferenceservice.model.Keynote;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "keynote-service", url = "http://localhost:8081", fallback = KeynoteFallback.class)
public interface KeynoteRestClient {
    @GetMapping("/v1/keynotes/{id}")
    Keynote getKeynoteById(@PathVariable String id);

    @GetMapping("/v1/keynotes")
    PagedModel<Keynote> getAllKeynotes();
}
