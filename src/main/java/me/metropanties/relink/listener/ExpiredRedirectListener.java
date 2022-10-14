package me.metropanties.relink.listener;

import lombok.RequiredArgsConstructor;
import me.metropanties.relink.entity.Redirect;
import me.metropanties.relink.service.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredRedirectListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredRedirectListener.class);

    private final RedirectService service;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void checkExpiredRedirects() {
        LOGGER.info("Checking for expired redirects...");
        for (Redirect redirect : this.service.findAll()) {
            if (redirect.getExpirationInMillis() == null) {
                continue;
            }

            long now = System.currentTimeMillis();
            if (now > redirect.getExpirationInMillis()) {
                LOGGER.info("Found expired redirect: {}, deleting.", redirect.getId());
                this.service.delete(redirect);
            }
        }
    }

}
