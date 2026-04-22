package com.raul.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    // @Scheduled(fixedRate = 60000)
    public void run() {
        System.out.println("[" + LocalDateTime.now() + "] Scheduler rodando...");
        notificationService.notifyUpcomingInvoices();
    }
}
