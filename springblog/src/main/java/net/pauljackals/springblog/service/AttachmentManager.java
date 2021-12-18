package net.pauljackals.springblog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.service.storage.StorageService;

@Service
@Getter
public class AttachmentManager {
    private List<Attachment> attachments;
    private StorageService storageService;
    
    public AttachmentManager(
        @Autowired List<Attachment> attachments,
        @Autowired StorageService storageService
    ) {
        this.storageService = storageService;
        setup(attachments);
    }
    
    public void setup(List<Attachment> attachments) {
        this.attachments = Collections.synchronizedList(new ArrayList<>());
        for (Attachment attachment : attachments) {
            addAttachment(attachment, true);
        }
    }

    public Attachment addAttachment(Attachment attachment, boolean isFromCSV) {
        Attachment attachmentNew;
        if(!isFromCSV) {
            attachmentNew = new Attachment(
                UUID.randomUUID().toString(),
                attachment.getFilename()
            );
        } else {
            attachment.setId(UUID.randomUUID().toString());
            attachmentNew = attachment;
        }
        attachments.add(attachmentNew);
        return attachmentNew;
    }
    public Attachment addAttachment(Attachment attachment) {
        return addAttachment(attachment, false);
    }

    public List<Attachment> addAttachments(List<Attachment> attachments) {
        List<Attachment> attachmentsNew = new ArrayList<>();
        for (Attachment attachment : attachments) {
            attachmentsNew.add(addAttachment(attachment));
        }
        return attachmentsNew;
    }

    public Attachment removeAttachment(Attachment attachment, String idPost) {
        boolean result = attachments.remove(attachment);
        if(result) {
            storageService.delete(attachment.getFilename(), idPost);
            return attachment;
        } else {
            return null;
        }
    }
}
