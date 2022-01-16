package net.pauljackals.springblog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import net.pauljackals.springblog.domain.Attachment;
import net.pauljackals.springblog.repository.AttachmentRepository;
import net.pauljackals.springblog.service.storage.StorageService;

@Service
@Transactional
public class AttachmentManager {
    private AttachmentRepository attachmentRepository;
    private StorageService storageService;
    
    public AttachmentManager(
        AttachmentRepository attachmentRepository,
        StorageService storageService
    ) {
        this.attachmentRepository = attachmentRepository;
        this.storageService = storageService;
    }

    public List<Attachment> getAttachments(){
        return (List<Attachment>) attachmentRepository.findAll();
    }

    public Attachment addAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public List<Attachment> addAttachments(List<Attachment> attachments) {
        return (List<Attachment>) attachmentRepository.saveAll(attachments);
    }

    public Attachment removeAttachment(Attachment attachment, Long idPost) {
        if(attachmentRepository.existsById(attachment.getId())) {
            storageService.delete(attachment.getFilename(), idPost);
            attachmentRepository.delete(attachment);
            return attachment;

        } else {
            return null;
        }
    }
}
