package com.firstservice.userservice.service;

import com.firstservice.userservice.domain.UserImage;
import com.firstservice.userservice.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

@Service
public class FileServiceImpl {
    @Autowired
    private FileRepository fileRepo;

    public UserImage store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        UserImage FileDB = new UserImage(fileName, file.getContentType(), file.getBytes());

        return fileRepo.save(FileDB);
    }

    public UserImage getFile(Long id) {
        return fileRepo.findById(id).get();
    }

    public Stream<UserImage> getAllFiles() {
        return fileRepo.findAll().stream();
    }
}
