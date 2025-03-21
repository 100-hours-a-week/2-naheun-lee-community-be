package com.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = true, unique = true, length = 10)
    private String nickname;

    @Column(nullable = false, name = "profile_img")
    private String profileImg;

    @Column(nullable = false)
    private boolean member; 

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public void encodePassword(BCryptPasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

     // 탈퇴 회원 처리
    public void deactivate() { 
        this.member = false;
        this.email = "deleted_" + this.id + "@deleted.com"; 
        this.nickname = null;
    }
}



