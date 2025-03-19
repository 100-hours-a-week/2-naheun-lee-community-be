package com.community.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    @Size(max = 10, message = "닉네임은 최대 10자까지 입력 가능합니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9가-힣]+$",
        message = "닉네임은 공백 없이 한글, 영문, 숫자만 입력 가능합니다."
    )
    private String nickname;

    @NotNull(message = "프로필 사진은 필수입니다.")
    private MultipartFile profileImage;
}

