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
        return albumRepository.save(album);
    }

    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    public List<Album> findByAccount_id(Long id) {
        return albumRepository.findByAccount_id(id);
    }

    public void deleteAlbum(Album album) {
        albumRepository.delete(album);
    }
}
