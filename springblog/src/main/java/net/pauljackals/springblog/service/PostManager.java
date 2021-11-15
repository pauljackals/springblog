package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Post;

@Service
@Getter
@Order(2)
public class PostManager {
    private List<Post> posts;
    
    public PostManager(@Autowired List<Post> posts) {
        this.posts = Collections.synchronizedList(new ArrayList<>());
    }

    public Post addPost(Post post, boolean isFromCSV) {
        Post postNew;
        if(!isFromCSV) {
            postNew = new Post(
                UUID.randomUUID().toString(),
                post.getPostContent(),
                post.getTags()
            );
        } else {
            post.setId(UUID.randomUUID().toString());
            postNew = post;
        }
        posts.add(postNew);
        return postNew;
    }
    public Post addPost(Post post) {
        return addPost(post, false);
    }
}
