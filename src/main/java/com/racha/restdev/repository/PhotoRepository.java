package com.racha.restdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.racha.restdev.model.Photo;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {
    List<Photo> findByAlbum_id(String id);

    Optional<Photo> findTopByOrderByIdDesc();

}
