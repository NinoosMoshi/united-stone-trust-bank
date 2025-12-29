package com.ninos.auth_users.repository;

import com.ninos.auth_users.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findByCode(String code);
    void deleteByUserId(Long userId);
}

