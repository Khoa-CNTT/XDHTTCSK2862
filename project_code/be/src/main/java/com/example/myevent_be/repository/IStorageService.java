package com.example.myevent_be.repository;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {
    public String storeFile(MultipartFile file);
    public Stream<Path> loadAll(); // load all file inside a folder
    public byte[] readFileContent(String fileName); // chứa ds ảnh
    public void deleteAllFiles();
}
