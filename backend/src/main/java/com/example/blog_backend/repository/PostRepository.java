package com.example.blog_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog_backend.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
