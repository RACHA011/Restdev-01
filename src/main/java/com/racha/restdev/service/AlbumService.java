package com.racha.restdev.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.racha.restdev.model.Album;
import com.racha.restdev.repository.AlbumRepository;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    public Album save(Album album) {
        if (album.getId() == null || album.getId().isEmpty()) {

            Optional<Album> maxIdOpt = findMaxId();
            String newidString = maxIdOpt.map(Album::getId).orElse("0");
            Long newId = Long.parseLong(newidString) + 1;
            album.setId(String.valueOf(newId));
        }
        return albumRepository.save(album);
    }

    public Optional<Album> findMaxId() {
        return albumRepository.findTopByOrderByIdDesc();
    }

    public Optional<Album> findById(String id) {
        return albumRepository.findById(id);
    }

    public List<Album> findByAccount_id(String id) {
        return albumRepository.findByAccount_id(id);
    }

    public void deleteAlbum(Album album) {
        albumRepository.delete(album);
    }
}
