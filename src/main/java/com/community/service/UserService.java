package com.community.service;

import com.community.entity.UserEntity;
import com.community.dto.*;
import com.community.repository.UserRepository;
import com.community.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final String UPLOAD_DIR = "/uploads/";

    /* 회원가입 */
    public String signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String profileImageUrl = saveProfileImage(request.getProfileImage());

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .profileImage(profileImageUrl)
                .member(true)
                .build();
        userRepository.save(user);
        return "register_success";
    }

    /* 로그인 */
    public String login(UserLoginRequest request) {
        UserEntity user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(request.getEmail());
    }

    /* 회원정보 조회 */
    public UserResponseDTO getUserProfile(Long userId) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponseDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }

    /* 회원정보 수정 */
    public void updateProfile(Long userId, UserProfileRequest request) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getNickname() != null) user.setNickname(request.getNickname());

        if (request.getProfileImage() != null) {
            String profileImageUrl = saveProfileImage(request.getProfileImage());
            user.setProfileImage(profileImageUrl);
        }

        userRepository.save(user);
    }

    /* 비밀번호 변경 */
    public void updatePassword(Long userId, UserPasswordRequest request) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /* 회원탈퇴 */
    public void deactivateUser(Long userId) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.deactivate();
        userRepository.save(user);
    }

    /* 파일 저장 로직 */
    private String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File uploadFile = new File(UPLOAD_DIR + fileName);

        try {
            file.transferTo(uploadFile);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }
    }
}



