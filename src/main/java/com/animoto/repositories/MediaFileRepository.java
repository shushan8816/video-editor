package com.animoto.repositories;

import com.animoto.models.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Integer> {

    @Query(value = "SELECT fileName FROM MediaFile WHERE id = ?1")
    Optional<String> getFileName(int id);

    Optional<MediaFile> getById(int id);

}
