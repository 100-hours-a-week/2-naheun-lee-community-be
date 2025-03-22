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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가 
    public void incrementViews() {
        this.views += 1;
    }

    // 게시글 수정
    public void update(String title, String content, String newImagePath) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
    
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    
        if (newImagePath != null && !newImagePath.isBlank()) {
            this.postImg = newImagePath;
        }
    
        this.updatedAt = LocalDateTime.now();
    }
}

