package com.community.repository;

import com.community.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsById(Long id);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM UserEntity u WHERE u.id = :id")
    Optional<UserEntity> findUserById(@Param("id") Long id);
}

