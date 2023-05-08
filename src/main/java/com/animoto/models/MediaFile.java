package com.animoto.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MediaFile extends AbstractModel {

    @Column(nullable = false,unique = true)
    private String fileName;

    @Column(nullable = false)
    private int userId;

    public MediaFile(String fileName, int userId) {
        this.fileName = fileName;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaFile)) return false;
        if (!super.equals(o)) return false;
        MediaFile mediaFile = (MediaFile) o;
        return getUserId() == mediaFile.getUserId() && Objects.equals(getFileName(), mediaFile.getFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFileName(), getUserId());
    }
}
