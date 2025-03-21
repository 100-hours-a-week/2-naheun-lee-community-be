package com.community.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.community.dto.PostRequestDTO;
import com.community.dto.PostResponseDTO;
import com.community.dto.PostUpdateDTO;
import com.community.entity.PostEntity;
import com.community.entity.UserEntity;
import com.community.exception.NotFoundException;
import com.community.exception.UnauthorizedException;
import com.community.repository.PostRepository;
import com.community.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    // 게시글 작성
    @Transactional
    public void createPost(Long userId, PostRequestDTO request, MultipartFile imageFile) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String imageUrl = null;
        try{
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = saveImage(imageFile); 
            }

            PostEntity post = PostEntity.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .postImg(imageUrl)
                    .user(user)
                    .views(0)
                    .build();

            postRepository.save(post);
        } catch (Exception e) {
            if (imageUrl != null) {
                deleteImage(imageUrl);
            }
            throw e;
        }
    }

    // 게시글 리스트 조회
    public List<PostResponseDTO> getAllPosts() {
        List<PostEntity> posts = postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream().map(post -> PostResponseDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImg(post.getPostImg())
                .views(post.getViews())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .user(PostResponseDTO.UserDTO.builder()
                        .nickname(post.getUser().getNickname())
                        .profileImg(post.getUser().getProfileImg())
                        .build())
                .build()).collect(Collectors.toList());
    }

    // 게시글 조회
    public PostResponseDTO getPostDTOById(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
    
        return PostResponseDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImg(post.getPostImg())
                .views(post.getViews())
                .likesCount(0) // 추후 연동 가능
                .commentsCount(0) // 추후 연동 가능
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .user(PostResponseDTO.UserDTO.builder()
                        .nickname(post.getUser().getNickname())
                        .profileImg(post.getUser().getProfileImg())
                        .build())
                .build();
    }
    

    // 조회수 증가
    @Transactional
    public void incrementViews(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        post.incrementViews();
        postRepository.save(post);
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, Long userId, PostUpdateDTO request, MultipartFile imageFile) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Permission denied");
        }

        String imageUrl = null;
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                deleteImage(post.getPostImg());
                imageUrl = saveImage(imageFile);
            }
    
            post.update(
                request != null ? request.getTitle() : null,
                request != null ? request.getContent() : null,
                imageUrl
            );
    
        } catch (Exception e) {
            if (imageUrl != null) {
                deleteImage(imageUrl);
            }
            throw e;
        }
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Permission denied");
        }

        postRepository.delete(post);
    }

    // 파일 저장 로직 
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; 
        }
    
        try {
            String uploadDir = System.getProperty("user.dir") + "/postuploads/"; 
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); 
            }
    
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String savedFilePath = uploadDir + fileName;
    
            File destinationFile = new File(savedFilePath);
            file.transferTo(destinationFile);
    
            return savedFilePath; 
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    // 파일 삭제 로직 
    private void deleteImage(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("Failed to delete profile image: " + filePath);
                }
            }
        }
    }
}


