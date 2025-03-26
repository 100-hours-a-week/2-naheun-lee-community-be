package com.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.community.util.NullSafeUtils;

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
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; 

    @Column(nullable = false, length = 26)
    private String title; 

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "postimg_url")
    private String postImgUrl;

    @Column(nullable = false)
    private int views = 0; 

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt; 

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가 
    public void incrementViews() {
        this.views += 1;
    }

    // 게시글 수정
    public void update(String title, String content, String postImg_url) {
        if (NullSafeUtils.hasText(title)) this.title = title;
        if (NullSafeUtils.hasText(content)) this.content = content;
        if (NullSafeUtils.hasText(postImg_url)) this.postImgUrl = postImg_url;
        this.updatedAt = LocalDateTime.now();
    }
}

