package com.community.dto;

import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private AuthorDTO author;

    @Getter
    @Setter
    @Builder
    public static class AuthorDTO {
        private String nickname;
        private String profileImg;
    }
}

