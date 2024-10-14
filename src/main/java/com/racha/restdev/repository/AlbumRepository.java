package com.racha.restdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.racha.restdev.model.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>{
    
    List<Album> findByAccount_id(Long id);

}
