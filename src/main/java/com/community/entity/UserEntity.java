package com.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.community.util.NullSafeUtils;

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

    @Column(nullable = false, name = "profileimg_url")
    private String profileImgUrl;

    @Column(nullable = false)
    private boolean isActive; 

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
        this.isActive = false;
        this.email = "deleted_" + this.id + "@deleted.com"; 
        this.nickname = null;
        this.profileImgUrl = null;
        this.updatedAt = LocalDateTime.now();
    }

    // 프로필 수정
    public void updateProfile(String nickname, String profileImg_url) {
        if (NullSafeUtils.hasText(nickname)) this.nickname = nickname;
        if (NullSafeUtils.hasText(profileImg_url)) this.profileImgUrl = profileImg_url;
        this.updatedAt = LocalDateTime.now();
    }

    // 비밀번호 수정
    public void updatePassword(String newPassword, BCryptPasswordEncoder encoder) {
        if (NullSafeUtils.hasText(newPassword)) {
            this.password = encoder.encode(newPassword);
            this.updatedAt = LocalDateTime.now();
        }
    }
}



