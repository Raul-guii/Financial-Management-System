package com.raul.backend.controller;

import com.raul.backend.dto.notifications.NotificationResponseDTO;
import com.raul.backend.entity.Notification;
import com.raul.backend.entity.User;
import com.raul.backend.repository.UserRepository;
import com.raul.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @PostMapping("/test")
    public String trigger() {
        notificationService.notifyUpcomingInvoices();
        return "Notificações executadas";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @GetMapping
    public List<NotificationResponseDTO> getMyNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return notificationService.getUserNotifications(user.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @GetMapping("/test-notifications")
    public String testNotifications() {
        notificationService.notifyUpcomingInvoices();
        return "Executado";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
