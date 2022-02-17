package net.pauljackals.springblog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import net.pauljackals.springblog.exceptions.ResourceNotFoundException;
import net.pauljackals.springblog.service.storage.StorageFileNotFoundException;
import net.pauljackals.springblog.service.storage.StorageService;

@Controller
public class AttachmentController {
    private StorageService storageService;

    public AttachmentController(@Autowired StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/attachment/{idPost}/{filename}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String idPost, @PathVariable String filename) {

		try {
            Long idPostParsed = Long.parseLong(idPost);

            Resource file = storageService.loadAsResource(filename, idPostParsed);
            String[] filenameSplit = file.getFilename().split("_", 2);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", filenameSplit[1]))
                .body(file);

        } catch(StorageFileNotFoundException|NumberFormatException e) {
            throw new ResourceNotFoundException();
        }
 	}
}
