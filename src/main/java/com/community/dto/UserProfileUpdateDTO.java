package com.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserProfileUpdateDTO {
    @Size(max = 10, message = "닉네임은 최대 10자까지 입력 가능합니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9가-힣]+$",
        message = "닉네임은 공백 없이 한글, 영문, 숫자만 입력 가능합니다."
    )
    private String nickname;
}

