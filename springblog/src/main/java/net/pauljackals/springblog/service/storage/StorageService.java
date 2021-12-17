package net.pauljackals.springblog.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
	void init();
	void store(MultipartFile file, String idPost);
	Stream<Path> loadAll();
	Path load(String filename, String idPost);
	Resource loadAsResource(String filename, String idPost);
	void deleteAll();
}
