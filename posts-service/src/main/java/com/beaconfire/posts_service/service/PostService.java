package com.beaconfire.posts_service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.beaconfire.posts_service.domain.Metadata;
import com.beaconfire.posts_service.domain.Post;
import com.beaconfire.posts_service.domain.PostReply;
import com.beaconfire.posts_service.domain.SubReply;
import com.beaconfire.posts_service.repo.PostRepository;

@Service
public class PostService {
	//need to talk with the user service to get userID
	//need to talk with attachements
	private final PostRepository postRepository;
	
	public PostService(PostRepository postRepository) {
		this.postRepository=postRepository;
	}
	
	public Post createPost(Post post) {
	    post.setPostId(UUID.randomUUID().toString());
	    post.setCreated_at(new java.util.Date());
	    post.setUpdated_at(new java.util.Date());
	    
	    //  the metadata
	    Metadata defaultMetadata = new Metadata();
	    defaultMetadata.setViews(0);
	    defaultMetadata.setLikes(0);
	    defaultMetadata.setTotalReplies(0);
	    defaultMetadata.setBookmarks(0);
	    defaultMetadata.setCreatedAt(new java.util.Date());
	    defaultMetadata.setUpdatedAt(new java.util.Date());
	    post.setMetadata(defaultMetadata);

	    return postRepository.save(post);
	}


    public Post updatePost(String postId, Post updatedPost) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setArchived(updatedPost.isArchived());
        existingPost.setUpdated_at(new java.util.Date());
        return postRepository.save(existingPost);
    }

    public void deletePost(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        post.setStatus("Deleted");
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByStatus(String status) {
        return postRepository.findByStatus(status);
    }

    public Post getPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
    }

    public Post updateMetadata(String postId, Metadata metadata) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        
        existingPost.setMetadata(metadata);
        existingPost.setUpdated_at(new Date());
        
        return postRepository.save(existingPost);
    }
    
    public Post addReplyToPost(String postId, PostReply reply) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        reply.setReplyId(UUID.randomUUID().toString());
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
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        PostReply targetReply = post.getPostReplies().stream()
                .filter(reply -> reply.getReplyId().equals(replyId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Reply not found with ID: " + replyId));

        subReply.setSubReplyId(UUID.randomUUID().toString());
        subReply.setCreated_at(new Date());
        subReply.setUpdated_at(new Date());

        if (targetReply.getSubReplies() == null) {
            targetReply.setSubReplies(new ArrayList<>());
        }

        targetReply.getSubReplies().add(subReply);
        return postRepository.save(post);
    }



	
	
}
