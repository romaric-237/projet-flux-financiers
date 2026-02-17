package com.fluxfinanciers.repository;

import com.fluxfinanciers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
