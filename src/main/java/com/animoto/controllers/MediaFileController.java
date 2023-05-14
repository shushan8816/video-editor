package com.animoto.controllers;

import com.animoto.dto.requests.ConcatenateFilesRequest;
import com.animoto.dto.requests.TrimMediaFileRequest;
import com.animoto.services.interfaces.MediaFileService;
import com.animoto.utils.exceptions.BadRequestException;
import com.animoto.utils.exceptions.InternalErrorException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/media-file")
@RequiredArgsConstructor
public class MediaFileController {

    private final MediaFileService mediaFileService;

    @GetMapping(value = "/{fileId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadMediaFile(@PathVariable int fileId,
                                                    @Parameter(hidden = true) @RequestHeader HttpHeaders headers) throws IOException {
        return ResponseEntity.ok(mediaFileService.getMediaFile(fileId, headers));
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> uploadMediaFile(@RequestPart MultipartFile file,
                                                @Parameter(hidden = true) @RequestHeader HttpHeaders headers) throws BadRequestException, InternalErrorException {
        mediaFileService.uploadMediaFile(file, headers);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/trim")
    public ResponseEntity<Void> trimVideo(@RequestBody TrimMediaFileRequest trimMediaFileRequest) throws IOException {
        mediaFileService.trimMediaFile(trimMediaFileRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/concatenate")
    public ResponseEntity<Void> concatenateVideos(@RequestBody ConcatenateFilesRequest concatenateFilesRequest) throws IOException {
        mediaFileService.concatenateFiles(concatenateFilesRequest.getFileIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteMediaFile(@PathVariable(value = "fileId") int fileId) {
        mediaFileService.delete(fileId);
        return ResponseEntity.ok().build();
    }
}