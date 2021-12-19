package net.pauljackals.springblog.controller.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.validation.Valid;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.domain.Author;
import net.pauljackals.springblog.domain.Comment;
import net.pauljackals.springblog.domain.Post;
import net.pauljackals.springblog.domain.PostAuthor;
import net.pauljackals.springblog.domain.helpers.StateFiles;
import net.pauljackals.springblog.exceptions.StateExportException;
import net.pauljackals.springblog.exceptions.StateImportException;
import net.pauljackals.springblog.service.AttachmentManager;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.CommentManager;
import net.pauljackals.springblog.service.PostManager;
import net.pauljackals.springblog.service.UserManager;
import net.pauljackals.springblog.service.storage.StorageService;

@Controller
public class StateController {
    private AuthorManager authorManager;
    private PostManager postManager;
    private CommentManager commentManager;
    private UserManager userManager;
    private AttachmentManager attachmentManager;
    private StorageService storageService;

    private String[] filenames;

    public StateController(
        @Autowired AuthorManager authorManager,
        @Autowired PostManager postManager,
        @Autowired CommentManager commentManager,
        @Autowired UserManager userManager,
        @Autowired AttachmentManager attachmentManager,
        @Autowired StorageService storageService
    ) {
        this.authorManager = authorManager;
        this.postManager = postManager;
        this.commentManager = commentManager;
        this.userManager = userManager;
        this.attachmentManager = attachmentManager;
        this.storageService = storageService;

        filenames = new String[] {
            "Posts.csv",
            "Authors.csv",
            "Posts_Authors.csv",
            "Comments.csv",
            "Attachments.csv"
        };
    }

    @GetMapping("/state")
    public String getState(Model model) {
        model.addAllAttributes(Map.ofEntries(
            Map.entry("stateFiles", new StateFiles()),
            Map.entry("filenames", filenames)
        ));
        return "state";
    }

    @GetMapping("/state/reset")
    public String resetState() {
        attachmentManager.setup(new ArrayList<>());
        userManager.setup();
        commentManager.setup(new ArrayList<>());
        authorManager.setup(new ArrayList<>());
        postManager.setup(new ArrayList<>(), new ArrayList<>(), authorManager.getAuthors());

        storageService.deleteAll();
        storageService.init();

        authorManager.addAuthor(new Author("", "John", "Doe", "johndoe1"));

        return "redirect:/";
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
        Map<String, String> attachmentsFilenames = new HashMap<>();
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
                String attachmentFilename = attachment.getFilename();
                attachmentsCSVBuilder.append(String.format("%d,%s\n", id, attachmentFilename));
                attachmentsFilenames.put(
                    post.getId() + "_" + attachmentFilename,
                    id + "_" + attachmentFilename
                );
            }
        }

        String[] filesContents = new String[]{
            postsCSVBuilder.toString(),
            authorsCSVBuilder.toString(),
            postsAuthorsCSVBuilder.toString(),
            commentsCSVBuilder.toString(),
            attachmentsCSVBuilder.toString()
        };

        List<Resource> attachmentsFiles = storageService.loadAllAsResources();

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
            for (Resource attachmentFile : attachmentsFiles) {
                ZipEntry zipEntry = new ZipEntry("upload/" + attachmentsFilenames.get(attachmentFile.getFilename()));
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(attachmentFile.getInputStream().readAllBytes());
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

    private <T> List<T> parseCSV(Class<T> beanClass, Map<String, String> mapping, MultipartFile file) throws IOException, CsvException {
        HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(beanClass);
        strategy.setColumnMapping(mapping);

        Reader reader = new InputStreamReader(file.getInputStream());
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
            .withMappingStrategy(strategy)
            .withThrowExceptions(false)
            .build();
            
        List<T> beans = csvToBean.parse();
        reader.close();

        if(csvToBean.getCapturedExceptions().size()>0) {
            throw new CsvException();
        }

        return beans;
    }

    @PostMapping("/state")
    public String setState(
        @Valid @ModelAttribute StateFiles stateFiles,
        Errors errors,
        Model model
    ) {
        if(!errors.hasErrors()) {
            List<MultipartFile> files = stateFiles.getFiles();
            List<MultipartFile> upload = stateFiles.getUpload();

            List<Attachment> attachments = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();
            List<PostAuthor> postsAuthors = new ArrayList<>();
            List<Author> authors = new ArrayList<>();
            List<Post> posts = new ArrayList<>();

            try {
                for (MultipartFile file : files) {

                    switch (file.getOriginalFilename()) {
                        case "Posts.csv":
                            posts = parseCSV(
                                Post.class,
                                Map.ofEntries(
                                    Map.entry("id", "idCSV"),
                                    Map.entry("post_content", "postContent"),
                                    Map.entry("tags", "tags")
                                ),
                                file
                            );
                            break;

                        case "Authors.csv":
                            authors = parseCSV(
                                Author.class,
                                Map.ofEntries(
                                    Map.entry("id", "idCSV"),
                                    Map.entry("first_name", "firstName"),
                                    Map.entry("last_name", "lastName"),
                                    Map.entry("username", "username")
                                ),
                                file
                            );
                            break;

                        case "Posts_Authors.csv":
                            postsAuthors = parseCSV(
                                PostAuthor.class,
                                Map.ofEntries(
                                    Map.entry("id_post", "idPost"),
                                    Map.entry("id_author", "idAuthor")
                                ),
                                file
                            );
                            break;

                        case "Comments.csv":
                            comments = parseCSV(
                                Comment.class,
                                Map.ofEntries(
                                    Map.entry("id", "idCSV"),
                                    Map.entry("username", "username"),
                                    Map.entry("id_post", "idPost"),
                                    Map.entry("comment_content", "commentContent")
                                ),
                                file
                            );
                            break;

                        case "Attachments.csv":
                            attachments = parseCSV(
                                Attachment.class,
                                Map.ofEntries(
                                    Map.entry("id_post", "idPost"),
                                    Map.entry("filename", "filename")
                                ),
                                file
                            );
                            break;
                    
                        default:
                            break;
                    }
                }

            } catch(CsvException|IOException e) {
                throw new StateImportException();
            }

            storageService.deleteAll();
            storageService.init();

            attachmentManager.setup(attachments);
            userManager.setup();
            commentManager.setup(comments);
            authorManager.setup(authors);
            postManager.setup(posts, postsAuthors, authorManager.getAuthors());

            if(upload.size()>1 || upload.size()==1 && upload.get(0).getOriginalFilename().length()>0) {
                Map<Integer, String> postsIds = new HashMap<>();
                for (Post post : posts) {
                    postsIds.put(post.getIdCSV(), post.getId());
                }
                for(MultipartFile file : upload) {
                    String[] filenameSplit = file.getOriginalFilename().split("_", 2);
                    storageService.store(file, postsIds.get(Integer.parseInt(filenameSplit[0])), filenameSplit[1]);
                }
            }
        }

        if(errors.hasErrors()) {
            model.addAttribute("filenames", filenames);
            return "state";
        }

        return "redirect:/";
    }
}
