package com.raul.backend.repository;

import com.raul.backend.entity.RefundRequest;
import com.raul.backend.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {

    List<RefundRequest> findAllByOrderByRequestedAtDesc();

    List<RefundRequest> findByStatus(RefundStatus status);

    Optional<RefundRequest> findByPaymentId(Long paymentId);

    Optional<RefundRequest> findByPaymentIdAndStatus(Long paymentId, RefundStatus status);
}
