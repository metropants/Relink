package me.metropanties.relink.service;

import me.metropanties.relink.entity.Redirect;
import me.metropanties.relink.request.RedirectRequest;

import java.util.List;
import java.util.Optional;

public interface RedirectService {

    Redirect create(RedirectRequest request);

    void delete(Redirect redirect);

    Optional<Redirect> find(String id);

    List<Redirect> findAll();

}
