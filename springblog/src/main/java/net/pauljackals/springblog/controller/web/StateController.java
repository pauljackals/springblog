package net.pauljackals.springblog.controller.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.pauljackals.springblog.controller.exceptions.StateExportException;
import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;

@Controller
public class StateController {
    private AuthorManager authorManager;
    private PostManager postManager;

    public StateController(
        @Autowired AuthorManager authorManager,
        @Autowired PostManager postManager
    ) {
        this.authorManager = authorManager;
        this.postManager = postManager;
    }

    @GetMapping("/state")
    public String getState() {
        return "state";
    }

    @GetMapping("/state/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadState() {
        List<Post> posts = postManager.getPosts();
        List<Author> authors = authorManager.getAuthors();

        StringBuilder postsCSVBuilder = new StringBuilder("id,post_content,tags\n");
        StringBuilder authorsCSVBuilder = new StringBuilder("id,first_name,last_name,username\n");
        StringBuilder postsAuthorsCSVBuilder = new StringBuilder("id_post,id_author\n");
        StringBuilder commentsCSVBuilder = new StringBuilder("id,username,id_post,comment_content\n");
        StringBuilder attachmentsCSVBuilder = new StringBuilder("id_post,filename\n");

        Map<String, Integer> authorsIds = new HashMap<>();
        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            int id = i+1;
            authorsCSVBuilder.append(String.format("%d,%s,%s,%s\n", id, author.getFirstName(), author.getLastName(), author.getUsername()));
            authorsIds.put(author.getId(), id);
        }
        int idComment = 1;
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            int id = i+1;
            postsCSVBuilder.append(String.format("%d,\"%s\",%s\n", id, post.getPostContent().replace("\"", "\"\""), post.getTags()));
            for (Author author : post.getAuthors()) {
                postsAuthorsCSVBuilder.append(String.format("%d,%d\n", id, authorsIds.get(author.getId())));
            }
            for (Comment comment : post.getComments()) {
                commentsCSVBuilder.append(String.format("%d,%s,%d,\"%s\"\n", idComment, comment.getUser().getUsername(), id, comment.getCommentContent().replace("\"", "\"\"")));
                idComment++;
            }
            for (Attachment attachment : post.getAttachments()) {
                attachmentsCSVBuilder.append(String.format("%d,%s\n", id, attachment.getFilename()));
            }
        }

        String[] filesContents = new String[]{
            postsCSVBuilder.toString(),
            authorsCSVBuilder.toString(),
            postsAuthorsCSVBuilder.toString(),
            commentsCSVBuilder.toString(),
            attachmentsCSVBuilder.toString()
        };
        String[] filenames = new String[] {
            "Posts.csv",
            "Authors.csv",
            "Posts_Authors.csv",
            "Comments.csv",
            "Attachments.csv"
        };
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream);
        Resource zip;
        try {
            for (int i = 0; i < filesContents.length; i++) {
                String fileContent = filesContents[i];
                String filename = filenames[i];
    
                ZipEntry zipEntry = new ZipEntry(filename);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(fileContent.getBytes());
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            zip = new ByteArrayResource(byteOutputStream.toByteArray());
            byteOutputStream.close();
        
        } catch(IOException e) {
            throw new StateExportException();
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"state.zip\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/zip")
            .body(zip);
    }
}
