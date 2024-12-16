package com.beaconfire.posts_service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.beaconfire.posts_service.domain.Accessibility;
import com.beaconfire.posts_service.domain.Metadata;
import com.beaconfire.posts_service.domain.Post;
import com.beaconfire.posts_service.domain.PostReply;
import com.beaconfire.posts_service.domain.SubReply;
import com.beaconfire.posts_service.exception.InvalidAccessibilityException;
import com.beaconfire.posts_service.exception.PostNotFoundException;
import com.beaconfire.posts_service.exception.ReplyNotFoundException;
import com.beaconfire.posts_service.repo.PostRepository;

import jakarta.validation.Valid;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    


    public Post createPost(Post post) {
//    	validateAccessibility(post.getAccessibility());
        
    	post.setPostId(UUID.randomUUID().toString());
        post.setCreated_at(new Date());
        post.setUpdated_at(new Date());

        Metadata defaultMetadata = new Metadata();
        defaultMetadata.setViews(0);
        defaultMetadata.setLikes(0);
        defaultMetadata.setTotalReplies(0);
        defaultMetadata.setBookmarks(0);
        defaultMetadata.setCreatedAt(new Date());
        defaultMetadata.setUpdatedAt(new Date());
        post.setMetadata(defaultMetadata);

        return postRepository.save(post);
    }

    public Post updatePost(String postId, Post updatedPost) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Check if the accessibility is being changed
//        if (!existingPost.getAccessibility().equals(updatedPost.getAccessibility())) {
//            throw new IllegalArgumentException("You are not allowed to change the accessibility of the post.");
//        }

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setArchived(updatedPost.isArchived());
        existingPost.setUpdated_at(new Date());

        return postRepository.save(existingPost);
    }


    public void deletePost(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));
        post.setAccessibility(Accessibility.DELETED);
        postRepository.save(post);
    }
    public Post updateAccessibility(String postId, Accessibility accessibility) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Update accessibility
        existingPost.setAccessibility(accessibility);
        existingPost.setUpdated_at(new Date());

        return postRepository.save(existingPost);
    }


    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

//    public List<Post> getPostsByStatus(String status) {
//        try {
//            Accessibility.valueOf(status.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new InvalidPostStatusException("Invalid post status: " + status);
//        }
//        return postRepository.findByStatus(status);
//    }

    public Post getPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));
    }

    public Post addReplyToPost(String postId, PostReply reply) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        reply.setReplyId(UUID.randomUUID().toString());
        reply.setDeleted(false);
        reply.setCreated_at(new Date());
        reply.setUpdated_at(new Date());

        if (post.getPostReplies() == null) {
            post.setPostReplies(new ArrayList<>());
        }

        post.getPostReplies().add(reply);
        return postRepository.save(post);
    }

    public Post addSubReplyToReply(String postId, String replyId, SubReply subReply) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        PostReply targetReply = post.getPostReplies().stream()
                .filter(reply -> reply.getReplyId().equals(replyId))
                .findFirst()
                .orElseThrow(() -> new ReplyNotFoundException("Reply not found with ID: " + replyId));

        subReply.setSubReplyId(UUID.randomUUID().toString());
        subReply.setDeleted(false);
        subReply.setCreated_at(new Date());
        subReply.setUpdated_at(new Date());

        if (targetReply.getSubReplies() == null) {
            targetReply.setSubReplies(new ArrayList<>());
        }

        targetReply.getSubReplies().add(subReply);
        return postRepository.save(post);
    }
    public Post updateMetadata(String postId, Metadata metadata) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        
        existingPost.setMetadata(metadata);
        existingPost.setUpdated_at(new Date());
        
        return postRepository.save(existingPost);
    }



    public List<Post> getPostsByAccessibility(Accessibility accessibility) {
       
        return postRepository.findByAccessibility(accessibility);
    }



    public Post updateReply(String postId, String replyId, PostReply updatedReply) {
        // Find the post by ID
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Find the target reply by ID
        PostReply targetReply = existingPost.getPostReplies().stream()
                .filter(reply -> reply.getReplyId().equals(replyId))
                .findFirst()
                .orElseThrow(() -> new PostNotFoundException("Reply not found with ID: " + replyId));

        // Update reply fields
        targetReply.setComment(updatedReply.getComment());
        targetReply.setUpdated_at(new Date());

        // Save and return the updated post
        return postRepository.save(existingPost);
    }
    public Post updateSubReply(String postId, String replyId, String subReplyId, SubReply updatedSubReply) {
        // Find the post by ID
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Find the target reply by ID
        PostReply targetReply = existingPost.getPostReplies().stream()
                .filter(reply -> reply.getReplyId().equals(replyId))
                .findFirst()
                .orElseThrow(() -> new PostNotFoundException("Reply not found with ID: " + replyId));

        // Find the target sub-reply by ID
        SubReply targetSubReply = targetReply.getSubReplies().stream()
                .filter(subReply -> subReply.getSubReplyId().equals(subReplyId))
                .findFirst()
                .orElseThrow(() -> new PostNotFoundException("Sub-reply not found with ID: " + subReplyId));

        // Update sub-reply fields
        targetSubReply.setComment(updatedSubReply.getComment());
        targetSubReply.setUpdated_at(new Date());

        // Save and return the updated post
        return postRepository.save(existingPost);
    }

    
    


}
