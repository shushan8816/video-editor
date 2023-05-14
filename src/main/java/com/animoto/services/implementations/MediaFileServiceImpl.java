package com.animoto.services.implementations;

import com.animoto.dto.requests.TrimMediaFileRequest;
import com.animoto.enums.OperationType;
import com.animoto.models.MediaFile;
import com.animoto.repositories.MediaFileRepository;
import com.animoto.security.JwtTokenProvider;
import com.animoto.services.interfaces.MediaFileService;
import com.animoto.utils.exceptions.BadRequestException;
import com.animoto.utils.exceptions.InternalErrorException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

    @Value(value = "${video.path}")
    private String videoPath;
    @Value(value = "${trimmed.video.path}")
    private String trimmedFilePath;
    @Value(value = "${concatenated.videos.path}")
    private String concatenatedFilePath;
    private String prefix = System.currentTimeMillis() + "_";
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
        mediaFile.setOperationType(OperationType.UPLOAD);
        log.info("Media file uploaded successfully ");
        mediaFileRepository.save(mediaFile);
    }

    private String uploadFile(MultipartFile file) {
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
    public void trimMediaFile(TrimMediaFileRequest trimMediaFileRequest) throws FileNotFoundException {
        int fileId = trimMediaFileRequest.getFileId();
        MediaFile mediaFile = mediaFileRepository.getById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));

        Path inputFilePath = Paths.get(videoPath, mediaFile.getFileName());
        String outputFileName = mediaFile.getFileName() + "trimmed";
        Path outputFilePath = Paths.get(trimmedFilePath, outputFileName);

        long startTimeMillis = getMillisFromString(trimMediaFileRequest.getStartTime());
        long endTimeMillis = getMillisFromString(trimMediaFileRequest.getEndTime());

        String[] cmd = {"ffmpeg", "-i", inputFilePath.toString(),
                "-ss", String.valueOf(startTimeMillis),
                "-to", String.valueOf(endTimeMillis),
                "-c", "copy", outputFilePath.toString()};

        Process process;
        try {
            process = new ProcessBuilder(cmd).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        File outputFile = new File(outputFilePath.toString());
        if (!outputFile.exists()) {
            throw new FileNotFoundException("Output file not created: " + outputFilePath);
        }

        MediaFile trimmedMediaFile = new MediaFile();
        trimmedMediaFile.setFileName(outputFileName);
        trimmedMediaFile.setOperationType(OperationType.TRIM);
        trimmedMediaFile.setUserId(mediaFile.getUserId());

        log.info("Video trimmed successfully");
        mediaFileRepository.save(trimmedMediaFile);
        log.info("Trimmed video saved successfully");

    }

    private long getMillisFromString(String timeString) {
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        String[] secondsParts = parts[2].split("\\.");
        int seconds = Integer.parseInt(secondsParts[0]);
        return (hours * 3600L + minutes * 60L + seconds) * 1000;
    }


    @Transactional
    @Override
    public void concatenateFiles(List<Integer> fileIds) throws FileNotFoundException {

        List<Path> inputVideoPaths = new ArrayList<>();
        Integer userId = null;

        for (int fileId : fileIds) {
            MediaFile mediaFile = mediaFileRepository.getById(fileId)
                    .orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));
            Path inputFilePath = Paths.get(videoPath, mediaFile.getFileName());
            inputVideoPaths.add(inputFilePath);

            if (userId == null) {
                userId = mediaFile.getUserId();
            }
        }

        String outputFileName = prefix + "concatenated_video.mp4";
        Path outputFilePath = Paths.get(concatenatedFilePath, outputFileName);

        try (OutputStream outputStream = Files.newOutputStream(outputFilePath);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

            for (Path videoPath : inputVideoPaths) {
                Files.copy(videoPath, bufferedOutputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MediaFile concatenatedMediaFile = new MediaFile();
        concatenatedMediaFile.setFileName(outputFileName);
        concatenatedMediaFile.setOperationType(OperationType.CONCATENATE);

        if (userId != null) {
            concatenatedMediaFile.setUserId(userId);
        }

        log.info("Videos concatenated successfully");
        mediaFileRepository.save(concatenatedMediaFile);
        log.info("Concatenated video saved successfully");
    }

    @Transactional
    @Override
    public void delete(int fileId) {
        mediaFileRepository.deleteById(fileId);
    }

}