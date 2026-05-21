package com.raul.backend.repository;

import com.raul.backend.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByDocument(String document);

    boolean existsByDocumentAndIdNot(String document, Long id);

    Page<Client> findByDeletedAtIsNull (Pageable pageable);

    Page<Client> findByDeletedAtIsNullAndNameContainingIgnoreCaseOrDeletedAtIsNullAndDocumentContainingIgnoreCase(
            String name, String document, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.defaulter = true AND c.deletedAt IS NULL")
    List<Client> findDefaulters();

    @Query("""
    SELECT COUNT(c)
    FROM Client c
    WHERE c.defaulter = true
      AND c.deletedAt IS NULL
    """)
    Long countDefaulters();
}
