package com.raul.backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DefaulterScheduler {

    private final ClientService clientService;

    public DefaulterScheduler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    // @Scheduled(fixedRate = 10000)
    public void run() {
        clientService.identifyDefaulters();
    }
}
