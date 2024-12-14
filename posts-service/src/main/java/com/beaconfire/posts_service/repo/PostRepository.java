package com.beaconfire.posts_service.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beaconfire.posts_service.domain.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String>{
    List<Post> findByStatus(String status);
    List<Post> findByIsArchived(boolean isArchived);
    List<Post> findByVisibility(String visibility);
}
