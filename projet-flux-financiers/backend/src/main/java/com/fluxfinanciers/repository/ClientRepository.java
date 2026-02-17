package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
