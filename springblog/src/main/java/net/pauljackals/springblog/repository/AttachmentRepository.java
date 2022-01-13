package net.pauljackals.springblog.repository;

import org.springframework.data.repository.CrudRepository;

import net.pauljackals.springblog.domain.Attachment;

public interface AttachmentRepository extends CrudRepository<Attachment, Long> {
    
}
