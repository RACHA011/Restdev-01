package com.racha.restdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.racha.restdev.model.Album;

@Repository
public interface AlbumRepository extends MongoRepository<Album, String>{
    
    List<Album> findByAccount_id(String id);

     Optional<Album> findTopByOrderByIdDesc();

}
