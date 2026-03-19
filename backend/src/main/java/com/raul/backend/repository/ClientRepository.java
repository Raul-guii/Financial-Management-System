package com.raul.backend.repository;

import com.raul.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByDocument(String document);

    boolean existsByDocumentAndIdNot(String document, Long id);
}
