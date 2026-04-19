package com.raul.backend.repository;

import com.raul.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByDocument(String document);

    boolean existsByDocumentAndIdNot(String document, Long id);

    @Query("SELECT c FROM Client c WHERE c.defaulter = true AND c.deletedAt IS NULL")
    List<Client> findDefaulters();
}
