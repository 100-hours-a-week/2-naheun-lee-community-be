package com.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column
    private String postImg;

    @Column(nullable = false)
    private int views = 0;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}

