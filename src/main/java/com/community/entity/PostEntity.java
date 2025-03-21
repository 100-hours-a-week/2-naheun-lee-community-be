package com.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity user; 

    @Column(nullable = false, length = 26)
    private String title; 

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "post_img")
    private String postImg; 

    @Column(nullable = false)
    private int views = 0; 

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt; 

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    // 수정 시 updatedAt 자동 갱신
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가 메서드
    public void incrementViews() {
        this.views += 1;
    }
}

