package com.animoto.services.interfaces;

import com.animoto.models.MediaFile;
import com.animoto.utils.exceptions.BadRequestException;
import com.animoto.utils.exceptions.InternalErrorException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface MediaFileService {

    void uploadMediaFile( MultipartFile file, HttpHeaders headers) throws BadRequestException, InternalErrorException;
    byte[] getMediaFile(int fileId, HttpHeaders headers) throws FileNotFoundException;
    void delete(int fileId);
}
