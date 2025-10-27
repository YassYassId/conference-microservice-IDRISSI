package com.yassine.conferenceservice.feign;


import com.yassine.conferenceservice.model.Keynote;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

@Component
public class KeynoteFallback implements KeynoteRestClient{
    @Override
    public Keynote getKeynoteById(String id) {
        Keynote fallback = new Keynote();
        fallback.setId(id);
        fallback.setFirstName("Unknown");
        fallback.setLastName("Speaker");
        fallback.setEmail("unknown@example.com");
        fallback.setFunction("Unavailable");
        return fallback;
    }

    @Override
    public PagedModel<Keynote> getAllKeynotes() {
        return PagedModel.empty();
    }
}
