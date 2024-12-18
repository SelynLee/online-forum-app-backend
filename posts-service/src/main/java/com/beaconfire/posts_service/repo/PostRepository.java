package com.beaconfire.posts_service.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.beaconfire.posts_service.domain.Accessibility;
import com.beaconfire.posts_service.domain.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String>{
    List<Post> findByIsArchived(boolean isArchived);
    List<Post> findByAccessibility(Accessibility accessibility);
    List<Post> findByUserId(Integer userId);
    @Query("{ 'postReplies.replyId': ?0 }")
    Optional<Post> findByPostRepliesReplyId(String replyId);

}
