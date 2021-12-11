package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.PostAuthor;
import net.pauljackals.springblog.domain.PostsWithComments;
import net.pauljackals.springblog.domain.SearchSettings;
import net.pauljackals.springblog.domain.Sorting;
import net.pauljackals.springblog.domain.User;

@Service
@Getter
public class PostManager {
    private List<Post> posts;
    private CommentManager commentManager;
    private AttachmentManager attachmentManager;
    
    public PostManager(
        @Autowired List<Post> posts,
        @Autowired List<PostAuthor> postsAuthors,
        @Autowired AuthorManager authorManager,
        @Autowired AttachmentManager attachmentManager,
        @Autowired CommentManager commentManager
    ) {
        this.posts = Collections.synchronizedList(new ArrayList<>());
        this.commentManager = commentManager;
        this.attachmentManager = attachmentManager;

        List<Author> authors = authorManager.getAuthors();
        List<Attachment> attachments = attachmentManager.getAttachments();
        List<Comment> comments = commentManager.getComments();

        for (Post post : posts) {
            int idPost = post.getIdCSV();
            List<Integer> postCurrentAuthorsIds = new ArrayList<>();
            for (PostAuthor postAuthor : postsAuthors) {
                if(idPost == postAuthor.getIdPost()) {
                    postCurrentAuthorsIds.add(postAuthor.getIdAuthor());
                }
            }
            if(postCurrentAuthorsIds.size() > 0) {
                int counter = postCurrentAuthorsIds.size();
                for (int i=0; i<authors.size(); i++) {
                    Author author = authors.get(i);
                    int idAuthor = author.getIdCSV();
                    if(postCurrentAuthorsIds.contains(idAuthor)) {
                        post.addAuthor(author);
                        counter--;
                    }
                    if(counter == 0) {
                        break;
                    }
                }
            }

            for (Attachment attachment : attachments) {
                if(attachment.getIdPost() == idPost) {
                    post.addAttachment(attachment);
                }
            }
            for (Comment comment : comments) {
                if(comment.getIdPost() == idPost) {
                    post.addComment(comment);
                }
            }
            addPost(post, true);
        }
    }

    public List<Post> getPosts(SearchSettings searchSettings) {
        List<Post> posts = new ArrayList<>();
        String tag = searchSettings.getTag();
        String authors = searchSettings.getAuthors();
        String word = searchSettings.getWord();
        for (Post post : this.posts) {

            if(tag!=null && tag.length()>0 && !post.getTags().matches(String.format("(.+ )?%s( .+)?", tag))) {
                continue;
            }
            if(authors!=null && authors.length()>0) {
                boolean continueFlag = false;
                List<Author> postAuthors = post.getAuthors();
                for(String author : authors.split(" ")) {
                    if(postAuthors.stream().noneMatch(postAuthor -> postAuthor.getUsername().equals(author))) {
                        continueFlag = true;
                        break;
                    }
                }
                if(continueFlag) {
                    continue;
                }
            }
            if(word!=null && word.length()>0 && !post.getPostContent().toLowerCase().matches(String.format("(.+[ \\p{Punct}])?%s([ \\p{Punct}].+)?", word.toLowerCase()))) {
                continue;
            }
            posts.add(post);
        }

        if(posts.size()>1) {
            Sorting sortByComments = searchSettings.getSortByComments();
            Sorting sortByAuthors = searchSettings.getSortByAuthors();
            Sorting sortByContent = searchSettings.getSortByContent();
            Comparator<Post> comparator = null;

            if(sortByComments!=null && !sortByComments.equals(Sorting.OFF)) {
                int modifier = sortByComments.equals(Sorting.ASCENDING) ? -1 : 1;
                comparator = Comparator.comparing(Post::getComments, (a, b) -> (a.size() - b.size())*modifier);
            }
            if(sortByAuthors!=null && !sortByAuthors.equals(Sorting.OFF)) {
                int modifier = sortByAuthors.equals(Sorting.ASCENDING) ? -1 : 1;
                if(comparator==null) {
                    comparator = Comparator.comparing(Post::getAuthors, (a, b) -> (a.size() - b.size())*modifier);
                } else {
                    comparator = comparator.thenComparing(Post::getAuthors, (a, b) -> (a.size() - b.size())*modifier);
                }
            }
            if(sortByContent!=null && !sortByContent.equals(Sorting.OFF)) {
                int modifier = sortByContent.equals(Sorting.ASCENDING) ? -1 : 1;
                if(comparator==null) {
                    comparator = Comparator.comparing(Post::getPostContent, (a, b) -> a.compareTo(b)*modifier);
                } else {
                    comparator = comparator.thenComparing(Post::getPostContent, (a, b) -> a.compareTo(b)*modifier);
                }
            }
            if(comparator!=null) {
                Collections.sort(posts, comparator);
            }
        }

        return posts;
    }

    public Post getPost(String id) {
        Post postToReturn = null;
        for (Post post : posts) {
            if(post.getId().equals(id)) {
                postToReturn = post;
                break;
            }
        }
        return postToReturn;
    }
    public List<Post> getPosts(Author author) {
        List<Post> posts = new ArrayList<>();
        for (Post post : this.posts) {
            if(post.getAuthors().contains(author)) {
                posts.add(post);
            }
        }
        return posts;
    }
    public PostsWithComments getPostsWithComments(User user) {
        List<Post> posts = new ArrayList<>();
        List<List<Comment>> comments = new ArrayList<>();
        List<Comment> commentsAll = new ArrayList<>();

        for (Post post : this.posts) {
            List<Comment> commentsPart = new ArrayList<>();
            for (Comment comment : post.getComments()) {
                if(comment.getUser().equals(user)) {
                    commentsPart.add(comment);
                    commentsAll.add(comment);
                }
            }
            if(commentsPart.size() > 0) {
                comments.add(commentsPart);
                posts.add(post);
            }
        }
        return new PostsWithComments(posts, comments, commentsAll);
    }

    public Post addPost(Post post, boolean isFromCSV) {
        Post postNew;
        if(!isFromCSV) {
            postNew = new Post(
                UUID.randomUUID().toString(),
                post.getPostContent(),
                post.getTags(),
                post.getAuthors()
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

    public Post removePost(String id) {
        Post post = getPost(id);

        if(post!=null) {
            posts.remove(post);
            for(Comment comment : post.getComments()) {
                commentManager.removeComment(comment);
            }
            for(Attachment attachment : post.getAttachments()) {
                attachmentManager.removeAttachment(attachment);
            }
        }

        return post;
    }

    public Post updatePost(String id, Post postUpdated) {
        Post post = getPost(id);

        if(post != null) {
            post.setPostContent(postUpdated.getPostContent());
            post.setTags(postUpdated.getTags());
            post.setAuthors(postUpdated.getAuthors());
        }

        return post;
    }
}
