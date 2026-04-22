package com.raul.backend.controller;

import com.raul.backend.entity.Notification;
import com.raul.backend.entity.User;
import com.raul.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/test")
    public String trigger() {
        notificationService.notifyUpcomingInvoices();
        return "Notificações executadas";
    }

    @GetMapping
    public List<Notification> getMyNotifications(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return notificationService.getUserNotifications(user.getId());
    }

    @GetMapping("/test-notifications")
    public String testNotifications() {
        notificationService.notifyUpcomingInvoices();
        return "Executado";
    }
}
