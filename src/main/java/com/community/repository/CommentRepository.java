package com.community.repository;

import com.community.entity.CommentEntity;
import com.community.entity.PostEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdOrderByCreatedAtDesc(Long postId);

    int countByPost(PostEntity post);
}
