package com.beaconfire.posts_service.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beaconfire.posts_service.domain.Accessibility;
import com.beaconfire.posts_service.domain.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String>{
    List<Post> findByIsArchived(boolean isArchived);
    List<Post> findByAccessibility(Accessibility accessibility);
}
