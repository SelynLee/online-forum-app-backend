package com.beaconfire.posts_service.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.beaconfire.posts_service.domain.Post;

public interface PostRepository extends MongoRepository<Post, String>{
    List<Post> findByStatus(String status);
    List<Post> findByIsArchived(boolean isArchived);
    List<Post> findByCategory(String category);
    List<Post> findByTagsIn(List<String> tags);
    List<Post> findByVisibility(String visibility);
}
