package me.modernpage.repository;

import lombok.Data;
import org.apache.tika.Tika;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Repository
@ConfigurationProperties(prefix = "upload.path")
public class FileSystemRepository {
    private String userimages;
    private String postfiles;
    private String groupimages;

    private final Tika tika = new Tika();

    public String saveProfileImage(byte[] content, String parentDir, String name) throws IOException {
        return save(content, userimages, parentDir, name);
    }

    public String savePostFile(byte[] content, String parentDir, String name) throws IOException {
        return save(content, postfiles, parentDir, name);
    }

    public String saveGroupImage(byte[] content, String parentDir, String name) throws IOException {
        return save(content, groupimages, parentDir, name);
    }

    private String save(byte[] content, String path, String parentDir, String name) throws IOException {
        String mimeType = tika.detect(content);
        String ext = mimeType.split("/")[1];
        Path newFile = Paths.get(path + parentDir + "/" + name + "." + ext);
        if(!Files.exists(newFile.getParent()))
            Files.createDirectory(newFile.getParent());
        Files.write(newFile, content);
        return newFile.toAbsolutePath().toString();
    }

    public void remove(String path) throws IOException {
        Files.delete(Paths.get(path));
    }

    public FileSystemResource findInFileSystem(String location) {
        return new FileSystemResource(Paths.get(location));
    }
}
