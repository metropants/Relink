package me.metropanties.relink.service.impl;

import lombok.RequiredArgsConstructor;
import me.metropanties.relink.entity.Redirect;
import me.metropanties.relink.repository.RedirectRepository;
import me.metropanties.relink.request.RedirectRequest;
import me.metropanties.relink.service.RedirectService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedirectServiceImpl implements RedirectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectService.class);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private final RedirectRepository repository;
    private final ThreadPoolTaskScheduler scheduler;

    @Value("${redirect.id.length}")
    private int idLength;

    private String generateRedirectId() {
        String id = RandomStringUtils.random(this.idLength, true, true);
        while (this.repository.existsById(id)) {
            id = RandomStringUtils.random(this.idLength, true, true);
        }
        return id;
    }

    private Redirect createRedirect(RedirectRequest request) {
        if (request.getLink() == null) {
            throw new IllegalArgumentException("Request link cannot be null!");
        }

        final String id = generateRedirectId();
        Redirect.RedirectBuilder<?, ?> redirect = Redirect.builder()
                .id(id)
                .link(request.getLink());
        if (request.getExpires() != null && request.getExpires()) {
            redirect.expirationInMillis(new Date(System.currentTimeMillis() + EXPIRATION_TIME).getTime());
        }

        return this.repository.save(redirect.build());
    }

    private Redirect createExpiringRedirect(RedirectRequest request) {
        Redirect redirect = createRedirect(request);
        if (request.getExpires() != null && request.getExpires()) {
            this.scheduler.schedule(() -> {
                String id = redirect.getId();
                if (this.repository.existsById(id)) {
                    LOGGER.info("Deleting expired redirect with id {}.", id);
                    this.repository.deleteById(id);
                }
            }, new Date(System.currentTimeMillis() + EXPIRATION_TIME));
        }
        return redirect;
    }

    @Override
    public Redirect create(RedirectRequest request) {
        if (request.getExpires() != null && request.getExpires()) {
            return createExpiringRedirect(request);
        } else {
            return createRedirect(request);
        }
    }

    @Override
    public void delete(Redirect redirect) {
        this.repository.delete(redirect);
    }

    @Override
    public Optional<Redirect> find(String id) {
        return this.repository.findById(id);
    }

    @Override
    public List<Redirect> findAll() {
        return this.repository.findAll();
    }

}
