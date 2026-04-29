package com.fluxfinanciers.service;

import com.fluxfinanciers.entity.AuditLog;
import com.fluxfinanciers.entity.User;
import com.fluxfinanciers.enums.ActionAudit;
import com.fluxfinanciers.enums.Role;
import com.fluxfinanciers.repository.AuditLogRepository;
import com.fluxfinanciers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void log(ActionAudit action, String entiteType, Long entiteId, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            AuditLog log = new AuditLog();
            log.setAction(action);
            log.setEntiteType(entiteType);
            log.setEntiteId(entiteId);
            log.setDetails(details);
            log.setDateAction(LocalDateTime.now());
            log.setUser(user);
            auditLogRepository.save(log);
        });
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findAll() {
        return auditLogRepository.findAllByOrderByDateActionDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findByCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(u -> auditLogRepository.findByUserIdOrderByDateActionDesc(u.getId()))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findForCurrentUserOrAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).map(user -> {
            if (user.getRole() == Role.ADMIN) {
                return auditLogRepository.findAllByOrderByDateActionDesc();
            }
            return auditLogRepository.findByUserIdOrderByDateActionDesc(user.getId());
        }).orElse(List.of());
    }
}