package com.animoto.services.implementations;

import com.animoto.models.MediaFile;
import com.animoto.repositories.MediaFileRepository;
import com.animoto.security.JwtTokenProvider;
import com.animoto.services.interfaces.MediaFileService;
import com.animoto.utils.exceptions.BadRequestException;
import com.animoto.utils.exceptions.InternalErrorException;
import com.animoto.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

    @Value(value = "${video.path}")
    private String videoPath;
    private final MediaFileRepository mediaFileRepository;
    private final JwtTokenProvider provider;


    @Transactional
    @Override
    public void uploadMediaFile(MultipartFile file, HttpHeaders headers) throws BadRequestException, InternalErrorException {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(" File is required ");
        }
        String fileName = this.uploadFile(file);

        if (fileName == null) {
            throw new InternalErrorException("Failed to upload file");
        }

        String token = Objects.requireNonNull(headers.getFirst("Authorization"))
                .replaceAll("Bearer", "").strip();

        int userId = provider.extractClaim(token, key -> key.get("userId", Integer.class));

        MediaFile mediaFile = new MediaFile(
                fileName,
                userId
        );

        log.info("Media file uploaded successfully ");
        mediaFileRepository.save(mediaFile);
    }

    private String uploadFile(MultipartFile file) {
        String prefix = System.currentTimeMillis() + "_";
        String fileName = prefix + file.getOriginalFilename();
        Path copyLocation = Paths.get(videoPath);

        try {
            Files.copy(file.getInputStream(), copyLocation.resolve(fileName));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
        return fileName;
    }

    @Override
    public byte[] getMediaFile(int fileId, HttpHeaders headers) throws FileNotFoundException {
        String fileName = mediaFileRepository.getFileName(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));

        File file = new File(videoPath, fileName);
        byte[] fileBytes;
        try {
            fileBytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileBytes;
    }

    @Transactional
    @Override
    public void delete(int fileId) {
        mediaFileRepository.deleteById(fileId);
    }

}