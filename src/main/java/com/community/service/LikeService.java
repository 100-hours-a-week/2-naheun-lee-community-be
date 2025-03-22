package com.community.service;

import com.community.entity.LikeEntity;
import com.community.entity.PostEntity;
import com.community.entity.UserEntity;
import com.community.exception.ConflictException;
import com.community.exception.NotFoundException;
import com.community.repository.LikeRepository;
import com.community.repository.PostRepository;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 좋아요 추가
    @Transactional
    public int likePost(Long userId, Long postId) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new ConflictException("already liked");
        }

        LikeEntity like = LikeEntity.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(like);

        return likeRepository.countByPost(post); 
    }

    // 좋아요 취소
    @Transactional
    public int unlikePost(Long userId, Long postId) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        LikeEntity like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new ConflictException("not liked"));

        likeRepository.delete(like);

        return likeRepository.countByPost(post); 
    }
}

