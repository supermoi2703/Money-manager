package com.springboot.moneymanager.repository;

import com.springboot.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long>{
    // tim kiem theo email
    Optional<ProfileEntity> findByEmail(String email);
}
