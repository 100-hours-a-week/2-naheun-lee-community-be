package com.community.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long postId;
    private String title;
    private String content;
    private String postImgUrl;
    private int views;
    private int likesCount;
    private int commentsCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private UserDTO user;
    private List<CommentResponseDTO> comments;
    private boolean isLikedByCurrentUser;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDTO {
        private Long userId;
        private String nickname;
        private String profileImgUrl;
        private boolean isActive;
    }
}

