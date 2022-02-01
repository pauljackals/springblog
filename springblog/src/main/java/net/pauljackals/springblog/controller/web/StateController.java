package net.pauljackals.springblog.controller.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.validation.Valid;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.text.StringEscapeUtils;
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
import net.pauljackals.springblog.domain.helpers.StateFiles;
import net.pauljackals.springblog.domain.helpers.dto.AttachmentDTO;
import net.pauljackals.springblog.domain.helpers.dto.CommentDTO;
import net.pauljackals.springblog.domain.helpers.dto.PostAuthor;
import net.pauljackals.springblog.exceptions.StateExportException;
import net.pauljackals.springblog.exceptions.StateImportException;
import net.pauljackals.springblog.service.AuthorManager;
import net.pauljackals.springblog.service.PostManager;
import net.pauljackals.springblog.service.UserManager;
import net.pauljackals.springblog.service.storage.StorageService;

@Controller
public class StateController {
    private AuthorManager authorManager;
    private PostManager postManager;
    private UserManager userManager;
    private StorageService storageService;

    private String[] filenames;

    public StateController(
        @Autowired AuthorManager authorManager,
        @Autowired PostManager postManager,
        @Autowired UserManager userManager,
        @Autowired StorageService storageService
    ) {
        this.authorManager = authorManager;
        this.postManager = postManager;
        this.userManager = userManager;
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
        postManager.reset();
        userManager.reset();
        authorManager.reset();

        storageService.deleteAll();
        storageService.init();

        authorManager.setup(List.of(
            new Author("John", "Doe", "johndoe1", "johndoe1@example.com", "123"),
            new Author("Mary", "Jane", "maryjane123", "maryjane123@example.com", "123"),
            new Author("Bob", "Johnson", "bobby55", "bobby55@example.com", "123")
        ));

        return "redirect:/";
    }

    @GetMapping("/state/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadState() {
        List<Post> posts = postManager.getPosts();
        List<Author> authors = authorManager.getAuthors();

        StringBuilder postsCSVBuilder = new StringBuilder("id,post_content,tags\n");
        StringBuilder authorsCSVBuilder = new StringBuilder("id,first_name,last_name,username,email,password\n");
        StringBuilder postsAuthorsCSVBuilder = new StringBuilder("id_post,id_author\n");
        StringBuilder commentsCSVBuilder = new StringBuilder("id,username,id_post,comment_content\n");
        StringBuilder attachmentsCSVBuilder = new StringBuilder("id_post,filename\n");

        for (Author author : authors) {
            authorsCSVBuilder.append(String.format("%d,%s,%s,%s,%s,%s\n", author.getId(), author.getFirstName(), author.getLastName(), author.getUsername(), author.getEmail(), author.getPassword()));
        }
        for(Post post : posts) {
            Long idPost = post.getId();
            postsCSVBuilder.append(String.format("%d,\"%s\",%s\n", idPost, StringEscapeUtils.escapeCsv(post.getPostContent()), post.getTags()));
            for (Author author : post.getAuthors()) {
                postsAuthorsCSVBuilder.append(String.format("%d,%d\n", idPost, author.getId()));
            }
            for (Comment comment : post.getComments()) {
                commentsCSVBuilder.append(String.format("%d,%s,%d,%s\n", comment.getId(), comment.getUser().getUsername(), idPost, StringEscapeUtils.escapeCsv(comment.getCommentContent())));
            }
            for (Attachment attachment : post.getAttachments()) {
                attachmentsCSVBuilder.append(String.format("%d,%s\n", idPost, attachment.getFilename()));
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
                ZipEntry zipEntry = new ZipEntry("upload/" + attachmentFile.getFilename());
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

            List<AttachmentDTO> attachmentsDTO = new ArrayList<>();
            List<CommentDTO> commentsDTO = new ArrayList<>();
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
                                    Map.entry("id", "id"),
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
                                    Map.entry("id", "id"),
                                    Map.entry("first_name", "firstName"),
                                    Map.entry("last_name", "lastName"),
                                    Map.entry("username", "username"),
                                    Map.entry("email", "email"),
                                    Map.entry("password", "password")
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
                            commentsDTO = parseCSV(
                                CommentDTO.class,
                                Map.ofEntries(
                                    Map.entry("id", "id"),
                                    Map.entry("username", "username"),
                                    Map.entry("id_post", "idPost"),
                                    Map.entry("comment_content", "commentContent")
                                ),
                                file
                            );
                            break;

                        case "Attachments.csv":
                            attachmentsDTO = parseCSV(
                                AttachmentDTO.class,
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

            postManager.reset();
            userManager.reset();
            authorManager.reset();

            List<Long> authorsIds = new ArrayList<>();
            authors.forEach(author -> {
                authorsIds.add(author.getId());
                author.setId(null);
            });
            List<Author> authorsSaved = authorManager.setup(authors);
            Map<Long, Author> authorsMapping = new HashMap<>();
            for (int i = 0; i < authorsIds.size(); i++) {
                authorsMapping.put(authorsIds.get(i), authorsSaved.get(i));
            }

            List<Long> postsIds = new ArrayList<>();
            for (Post post : posts) {
                Long idPost = post.getId();
                postsIds.add(idPost);
                post.setId(null);
                post.setAuthors(
                    postsAuthors.stream().filter(postAuthor -> postAuthor.getIdPost().equals(idPost))
                        .map(postAuthor -> authorsMapping.get(postAuthor.getIdAuthor()))
                        .collect(Collectors.toList())
                );

                post.setComments(
                    commentsDTO.stream().filter(comment -> comment.getIdPost().equals(idPost))
                        .map(comment -> new Comment(
                            comment.getCommentContent(),
                            userManager.createUserIfNew(comment.getUsername())
                        )).collect(Collectors.toList())
                );

                post.setAttachments(
                    attachmentsDTO.stream().filter(attachment -> attachment.getIdPost().equals(idPost))
                        .map(attachment -> new Attachment(
                            attachment.getFilename()
                        )).collect(Collectors.toList())
                );
            }
            List<Post> postsSaved = postManager.setup(posts);
            Map<Long, Long> postsIdsMapping = new HashMap<>();
            for (int i = 0; i < postsIds.size(); i++) {
                postsIdsMapping.put(postsIds.get(i), postsSaved.get(i).getId());
            }

            storageService.deleteAll();
            storageService.init();

            if(upload.size()>1 || upload.size()==1 && upload.get(0).getOriginalFilename().length()>0) {
                for(MultipartFile file : upload) {
                    String[] filenameSplit = file.getOriginalFilename().split("_", 2);
                    storageService.store(file, postsIdsMapping.get(Long.parseLong(filenameSplit[0])), filenameSplit[1]);
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
