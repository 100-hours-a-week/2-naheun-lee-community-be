package com.community.repository;

import com.community.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsById(Long id);

    // email로 사용자 조회 (탈퇴한 회원 조회 불가능)
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.isActive = true")
    Optional<UserEntity> findActiveUserByEmail(@Param("email") String email);

    // id로 사용자 조회 (탈퇴한 회원 조회 불가능)
    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.isActive = true")
    Optional<UserEntity> findActiveUserById(@Param("id") Long id);

}

