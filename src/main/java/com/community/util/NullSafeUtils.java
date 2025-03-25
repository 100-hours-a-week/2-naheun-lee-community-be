package com.community.util;

import org.springframework.web.multipart.MultipartFile;

public class NullSafeUtils {

    // 문자열이 null이 아니고 공백이 아닐 때 true
    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    // 문자열이 null이거나 공백일 때 true
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // 파일이 null이거나 비어 있을 때 true
    public static boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    // 파일이 null이 아니고 비어있지 않을 때 true
    public static boolean isPresent(MultipartFile file) {
        return file != null && !file.isEmpty();
    }
}

