package com.community.dto;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 26, message = "제목은 최대 26자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @Size(max = 1, message = "게시글 이미지는 하나만 업로드 가능합니다.")
    private String postImg;
}

