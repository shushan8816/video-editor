package com.animoto.controllers;

import com.animoto.repositories.MediaFileRepository;
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

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/media-file")
@RequiredArgsConstructor
public class MediaFileController {

    private final MediaFileService mediaFileService;
    private final MediaFileRepository mediaFileRepository;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> uploadFile(@RequestPart MultipartFile file,
                                           @Parameter(hidden = true) @RequestHeader HttpHeaders headers) throws BadRequestException, InternalErrorException {
        mediaFileService.uploadMediaFile(file, headers);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{fileId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> getMediaFile(@PathVariable int fileId,
                                               @Parameter(hidden = true) @RequestHeader HttpHeaders headers) throws FileNotFoundException {
        return ResponseEntity.ok(mediaFileService.getMediaFile(fileId, headers));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteMediaFile(@PathVariable(value = "fileId") int fileId) {
        mediaFileRepository.deleteById(fileId);
        return ResponseEntity.ok().build();
    }
}