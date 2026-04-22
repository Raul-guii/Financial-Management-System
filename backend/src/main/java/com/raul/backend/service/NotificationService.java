package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Notification;
import com.raul.backend.entity.User;
import com.raul.backend.enums.NotificationType;
import com.raul.backend.repository.AuditLogRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogService auditLogService;
    private final AuditLogRepository auditLogRepository;

    public void notifyUpcomingInvoices() {

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(3);

        List<Invoice> invoices =
                invoiceRepository.findUpcomingDueInvoices(today, limit);

        for (Invoice invoice : invoices) {

            try {

                // PEGA USUÁRIO RESPONSÁVEL
                User user = invoice.getContract().getCreatedBy();

                if (user == null) continue;

                // ANTI-DUPLICAÇÃO (1 por fatura)
                boolean alreadyNotified =
                        auditLogRepository.existsByEntityIdAndAction(
                                invoice.getId(),
                                NotificationType.INVOICE_DUE_SOON.name()
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
                        NotificationType.INVOICE_DUE_SOON,
                        user.getId()
                );

            } catch (Exception e) {

                System.out.println("Erro ao notificar fatura " + invoice.getId());

                auditLogService.log(
                        invoice.getId(),
                        "INVOICE",
                        NotificationType.SYSTEM,
                        null
                );
            }
        }
    }


    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}

