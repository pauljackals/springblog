package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.User;
import net.pauljackals.springblog.domain.helpers.PostAuthor;
import net.pauljackals.springblog.domain.helpers.PostsWithComments;
import net.pauljackals.springblog.domain.helpers.SearchSettings;
import net.pauljackals.springblog.domain.helpers.Sorting;
import net.pauljackals.springblog.repository.PostRepository;

@Service
@Transactional
public class PostManager {
    private PostRepository postRepository;
    private CommentManager commentManager;
    private AttachmentManager attachmentManager;
    
    public PostManager(
        PostRepository postRepository,
        AttachmentManager attachmentManager,
        CommentManager commentManager
    ) {
        this.postRepository = postRepository;
        this.commentManager = commentManager;
        this.attachmentManager = attachmentManager;
    }

    public void setup(
        List<Post> posts,
        List<PostAuthor> postsAuthors,
        List<Author> authors
    ) {
        // this.posts = Collections.synchronizedList(new ArrayList<>());

        // List<Attachment> attachments = attachmentManager.getAttachments();
        // List<Comment> comments = commentManager.getComments();

        // for (Post post : posts) {
        //     int idPost = post.getIdCSV();
        //     List<Integer> postCurrentAuthorsIds = new ArrayList<>();
        //     for (PostAuthor postAuthor : postsAuthors) {
        //         if(idPost == postAuthor.getIdPost()) {
        //             postCurrentAuthorsIds.add(postAuthor.getIdAuthor());
        //         }
        //     }
        //     if(postCurrentAuthorsIds.size() > 0) {
        //         int counter = postCurrentAuthorsIds.size();
        //         for (int i=0; i<authors.size(); i++) {
        //             Author author = authors.get(i);
        //             // int idAuthor = author.getIdCSV();
        //             int idAuthor = -1;
        //             if(postCurrentAuthorsIds.contains(idAuthor)) {
        //                 post.addAuthor(author);
        //                 counter--;
        //             }
        //             if(counter == 0) {
        //                 break;
        //             }
        //         }
        //     }

        //     for (Attachment attachment : attachments) {
        //         // if(attachment.getIdPost() == idPost) {
        //         if(-1 == idPost) {
        //             post.addAttachment(attachment);
        //         }
        //     }
        //     for (Comment comment : comments) {
        //         if(comment.getIdPost() == idPost) {
        //             post.addComment(comment);
        //         }
        //     }
        //     addPost(post, true);
        // }
        postRepository.deleteAll();
        postRepository.saveAll(posts);
    }

    public List<Post> getPosts() {
        return (List<Post>) postRepository.findAll();
    }

    public List<Post> getPosts(SearchSettings searchSettings) {
        List<Post> posts = new ArrayList<>();
        String tag = searchSettings.getTag();
        String authors = searchSettings.getAuthors();
        String word = searchSettings.getWord();
        for (Post post : postRepository.findAll()) {

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

    public Post getPost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()) {
            return post.get();
        
        } else {
            return null;
        }
    }
    public List<Post> getPosts(Author author) {
        return postRepository.findAllByAuthorsContaining(author);
    }
    public PostsWithComments getPostsWithComments(User user) {
        List<Post> posts = new ArrayList<>();
        List<List<Comment>> comments = new ArrayList<>();
        List<Comment> commentsAll = new ArrayList<>();

        for (Post post : getPosts()) {
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

    public Post addPost(Post post) {
        return postRepository.save(post);
    }

    public Post removePost(Long id) {
        Post post = getPost(id);

        if(post!=null) {
            postRepository.delete(post);
            for(Comment comment : post.getComments()) {
                commentManager.removeComment(comment);
            }
            for(Attachment attachment : post.getAttachments()) {
                attachmentManager.removeAttachment(attachment, post.getId());
            }
        }

        return post;
    }

    public Post updatePost(Long id, Post postUpdated) {
        Post post = getPost(id);

        if(post != null) {
            post.setPostContent(postUpdated.getPostContent());
            post.setTags(postUpdated.getTags());
            post.setAuthors(postUpdated.getAuthors());
            post.setAttachments(postUpdated.getAttachments());
            postRepository.save(post);
        }

        return post;
    }
}
