package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByDateActionDesc();

    List<AuditLog> findByUserIdOrderByDateActionDesc(Long userId);
}