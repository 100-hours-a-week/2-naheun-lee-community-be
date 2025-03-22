package com.community.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDTO {
    @Size(max = 26, message = "제목은 최대 26자까지 입력할 수 있습니다.")
    private String title;

    private String content;
}
