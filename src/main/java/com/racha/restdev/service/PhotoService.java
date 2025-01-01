package com.racha.restdev.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.racha.restdev.model.Photo;
import com.racha.restdev.repository.PhotoRepository;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    public Photo save(Photo photo) {
        if (photo.getId() == null || photo.getId().isEmpty()) {
            Optional<Photo> maxIdOpt = findMaxId();
            String newidString = maxIdOpt.map(Photo::getId).orElse("0");
            Long newId = Long.parseLong(newidString) + 1;
            photo.setId(String.valueOf(newId));
        }
        return photoRepository.save(photo);
    }

    public Optional<Photo> findMaxId() {
        return photoRepository.findTopByOrderByIdDesc();
    }

    public Optional<Photo> findById(String id) {
        return photoRepository.findById(id);
    }

    public List<Photo> findByAlbum_id(String id) {
        return photoRepository.findByAlbum_id(id);
    }

    public void delete(Photo photo) {
        photoRepository.delete(photo);
    }
}
