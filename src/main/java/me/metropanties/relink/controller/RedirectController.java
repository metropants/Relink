package me.metropanties.relink.controller;

import lombok.RequiredArgsConstructor;
import me.metropanties.relink.entity.Redirect;
import me.metropanties.relink.request.RedirectRequest;
import me.metropanties.relink.service.RedirectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService service;

    @Value("${app.domain}")
    private String domain;

    @PostMapping("/")
    public ResponseEntity<Map<String, String>> create(@RequestBody RedirectRequest request) {
        try {
            Redirect redirect = this.service.create(request);
            return ResponseEntity.ok(Map.of(
                    "id", redirect.getId(),
                    "original", redirect.getLink(),
                    "shortened", domain + "/" + redirect.getId(),
                    "expires", String.valueOf(redirect.getExpirationInMillis() != null && redirect.getExpirationInMillis() > 0)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public RedirectView redirect(@PathVariable String id) {
        return this.service.find(id).map(redirect -> {
            URI uri = URI.create(redirect.getLink());
            RedirectView view = new RedirectView();
            view.setUrl(uri.toString());
            view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return view;
        }).orElse(new RedirectView("http://localhost:8080/"));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Map<String, String>> find(@PathVariable String id) {
        return this.service.find(id).map(redirect -> ResponseEntity.ok(Map.of(
                        "original", redirect.getLink(),
                        "shortened", domain + "/" + redirect.getId()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/find/")
    public ResponseEntity<List<Redirect>> findAll() {
        return ResponseEntity.ok(this.service.findAll());
    }

}
