package com.community.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.community.dto.CommentResponseDTO;
import com.community.dto.PostRequestDTO;
import com.community.dto.PostResponseDTO;
import com.community.dto.PostUpdateDTO;
import com.community.entity.PostEntity;
import com.community.entity.UserEntity;
import com.community.exception.NotFoundException;
import com.community.exception.UnauthorizedException;
import com.community.exception.BadRequestException;
import com.community.repository.PostRepository;
import com.community.repository.UserRepository;
import com.community.repository.LikeRepository;
import com.community.repository.CommentRepository;
import com.community.service.CommentService;
import com.community.util.NullSafeUtils;
import com.community.util.FileHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final FileHandler fileHandler;
    
    // 게시글 작성
    @Transactional
    public void createPost(Long userId, PostRequestDTO request, MultipartFile imageFile) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String imageUrl = null;
        try{
            if (NullSafeUtils.isPresent(imageFile)) imageUrl = fileHandler.saveFile(imageFile, "postuploads"); 

            PostEntity post = PostEntity.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .postImgUrl(imageUrl)
                    .user(user)
                    .views(0)
                    .build();

            postRepository.save(post);
        } catch (Exception e) {
            if (imageUrl != null) fileHandler.deleteFile(imageUrl);
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
                .postImgUrl(post.getPostImgUrl())
                .views(post.getViews())
                .likesCount(likeRepository.countByPost(post))
                .commentsCount(commentRepository.countByPost(post))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .user(PostResponseDTO.UserDTO.builder()
                        .nickname(post.getUser().getNickname())
                        .profileImgUrl(post.getUser().getProfileImgUrl())
                        .isActive(post.getUser().isActive())
                        .build())
                .build()).collect(Collectors.toList());
    }

    // 게시글 조회
    public PostResponseDTO getPostDTOById(Long postId, Long userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

         List<CommentResponseDTO> comments = commentService.getComments(postId);
         boolean isLiked = likeRepository.existsByUserAndPost(user, post);
    
        return PostResponseDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImgUrl(post.getPostImgUrl())
                .views(post.getViews())
                .likesCount(likeRepository.countByPost(post)) 
                .commentsCount(commentRepository.countByPost(post)) 
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .user(PostResponseDTO.UserDTO.builder()
                        .nickname(post.getUser().getNickname())
                        .profileImgUrl(post.getUser().getProfileImgUrl())
                        .build())
                .comments(comments)
                .isLikedByCurrentUser(isLiked)
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

        String title = request != null ? request.getTitle() : null;
        String content = request != null ? request.getContent() : null;
        boolean hasImage = NullSafeUtils.isPresent(imageFile);

        if (!NullSafeUtils.hasText(title) && !NullSafeUtils.hasText(content) && !hasImage) {
            throw new BadRequestException("No valid fields to update");
        }

        String imageUrl = null;
        try {
            if (hasImage) {
                fileHandler.deleteFile(post.getPostImgUrl()); 
                imageUrl = fileHandler.saveFile(imageFile, "postuploads"); 
            }
            post.update(title, content, imageUrl); 

        } catch (Exception e) {
            if (imageUrl != null) fileHandler.deleteFile(imageUrl);
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
}


