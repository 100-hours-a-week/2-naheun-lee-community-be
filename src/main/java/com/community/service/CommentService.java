package com.community.service;

import com.community.dto.CommentRequestDTO;
import com.community.dto.CommentResponseDTO;
import com.community.entity.CommentEntity;
import com.community.entity.PostEntity;
import com.community.entity.UserEntity;
import com.community.exception.ForbiddenException;
import com.community.exception.NotFoundException;
import com.community.repository.CommentRepository;
import com.community.repository.PostRepository;
import com.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    @Transactional
    public void createComment(Long userId, Long postId, CommentRequestDTO request) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post not found"));
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        CommentEntity comment = CommentEntity.builder()
                .post(post)
                .user(user)
                .content(request.getComment())
                .build();

        commentRepository.save(comment);
    }

    // 댓글 조회
    @Transactional
    public List<CommentResponseDTO> getComments(Long postId) {
        List<CommentEntity> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        return comments.stream().map(comment ->
                CommentResponseDTO.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .user(CommentResponseDTO.UserDTO.builder()
                                .userId(comment.getUser().getId())
                                .nickname(comment.getUser().getNickname())
                                .profileImgUrl(comment.getUser().getProfileImgUrl())
                                .isActive(comment.getUser().isActive())
                                .build())
                        .build()
        ).collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long userId, Long postId, Long commentId, CommentRequestDTO request) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment not found"));

        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("post not found");
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("permission denied");
        }

        comment.updateContent(request.getComment());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment not found"));
                
        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("post not found");
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("permission denied");
        }

        commentRepository.delete(comment);
    }
}

