package com.raul.backend.service;

import com.raul.backend.dto.notifications.NotificationResponseDTO;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Notification;
import com.raul.backend.entity.User;
import com.raul.backend.enums.AuditAction;
import com.raul.backend.enums.NotificationType;
import com.raul.backend.repository.AuditLogRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogService auditLogService;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void notifyUpcomingInvoices() {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(3);

        System.out.println("=== NOTIFICAÇÃO SCHEDULER ===");
        System.out.println("Hoje: " + today + " | Limite: " + limit);

        List<Invoice> invoices = invoiceRepository.findUpcomingDueInvoices(today, limit);

        System.out.println("Faturas encontradas: " + invoices.size());

        for (Invoice invoice : invoices) {
            System.out.println("Processando fatura #" + invoice.getId());

            try {

                // PEGA USUÁRIO RESPONSÁVEL
                User user = invoice.getContract().getCreatedBy();

                if (user == null) continue;

                // ANTI-DUPLICAÇÃO (1 por fatura)
                boolean alreadyNotified = auditLogRepository.existsByEntityIdAndAction(
                        invoice.getId(),
                        AuditAction.INVOICE_DUE_SOON.name()
                );

                if (alreadyNotified) continue;

                // CRIA NOTIFICAÇÃO
                Notification notification = new Notification();
                notification.setTitle("Fatura próxima do vencimento");
                notification.setMessage(
                        "A fatura #" + invoice.getId() +
                                " vence em " + invoice.getDueDate()
                );
                notification.setType(NotificationType.INVOICE_DUE_SOON);
                notification.setIsRead(false);
                notification.setUser(user);

                notificationRepository.save(notification);

                // LOG (rastreabilidade)
                auditLogService.log(
                        invoice.getId(),
                        "INVOICE",
                        AuditAction.INVOICE_DUE_SOON, // <- muda aqui
                        user.getId(),
                        user.getName(),
                        "Notificação de vencimento enviada para fatura #" + invoice.getId()
                );

            } catch (Exception e) {

                System.out.println("Erro ao notificar fatura " + invoice.getId());

                e.printStackTrace();

                auditLogService.log(
                        invoice.getId(),
                        "INVOICE",
                        AuditAction.SYSTEM_ERROR,
                        null,
                        null,
                        "Erro ao notificar fatura #" + invoice.getId()
                );
            }
        }
    }

    @Transactional
    public void createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setUser(user);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(n -> new NotificationResponseDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getType(),
                        n.getIsRead(),
                        n.getUser().getId(),
                        n.getCreatedAt()
                ))
                .toList();
    }
}

