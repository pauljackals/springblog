package net.pauljackals.springblog.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {
	void init();
	void store(MultipartFile file, Long idPost, String nameNew);
	Stream<Path> loadAll();
	List<Resource> loadAllAsResources();
	Path load(String filename, Long idPost);
	Resource loadAsResource(String filename, Long idPost);
	void deleteAll();
	void delete(String filename, Long idPost);
}
