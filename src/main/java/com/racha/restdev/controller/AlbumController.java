package com.racha.restdev.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.racha.restdev.model.Account;
import com.racha.restdev.model.Album;
import com.racha.restdev.model.Photo;
import com.racha.restdev.payload.auth.album.AlbumPayLoadDTO;
import com.racha.restdev.payload.auth.album.AlbumViewDTO;
import com.racha.restdev.payload.auth.album.PhotoDTO;
import com.racha.restdev.payload.auth.album.PhotoPayloadDTO;
import com.racha.restdev.payload.auth.album.PhotoViewDTO;
import com.racha.restdev.service.AccountService;
import com.racha.restdev.service.AlbumService;
import com.racha.restdev.service.PhotoService;
import com.racha.restdev.util.apputils.AppUtil;
import com.racha.restdev.util.constants.AlbumError;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@Tag(name = "Album Controller", description = "Controller for Album and photo management")
@Slf4j
public class AlbumController {
    static final String PHOTO_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_WIDTH = 300;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PhotoService photoService;

    @PostMapping(value = "/albums/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "please add a valid name or description")
    @ApiResponse(responseCode = "201", description = "Album Added successfully")
    @Operation(summary = "Add a new Album")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<AlbumViewDTO> album(@Valid @RequestBody AlbumPayLoadDTO albumPayLoadDTO,
            Authentication authentication) {
        try {
            Album album = new Album();
            album.setName(albumPayLoadDTO.getName());
            album.setDescription(albumPayLoadDTO.getDescription());

            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);

            Account account = optionalAccount.get();
            album.setAccount(account);

            album = albumService.save(album);

            AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), null);

            return ResponseEntity.status(HttpStatus.CREATED).body(albumViewDTO);
        } catch (Exception e) {
            log.error(AlbumError.ADD_ALBUM_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/albums", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of Albums")
    @ApiResponse(responseCode = "401", description = "Token missing or invalid")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "List of Albums")
    @SecurityRequirement(name = "rachadev-demo-api")
    public List<AlbumViewDTO> albums(Authentication authentication) {
        // We use this to get the account info from the otheticated account
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        List<AlbumViewDTO> albums = new ArrayList<>();
        for (Album album : albumService.findByAccount_id(account.getId())) {
            List<PhotoDTO> photos = new ArrayList<>();
            for (Photo photo : photoService.findByAlbum_id(album.getId())) {
                String link = "/albums/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
                photos.add(new PhotoDTO(photo.getId(), photo.getName(),
                        photo.getDescription(), photo.getOriginalFilename(), link));
            }
            albums.add(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos));
        }
        return albums;
    }

    @GetMapping(value = "/albums/{album_id}", produces = "application/json")
    @ApiResponse(responseCode = "400", description = "Please enter a valid name or description")
    @ApiResponse(responseCode = "200", description = "Album")
    @ApiResponse(responseCode = "403", description = "Forbidden: Account does not own the album")
    @ApiResponse(responseCode = "404", description = "Album not found")
    @Operation(summary = "Album")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<AlbumViewDTO> albumsById(@PathVariable("album_id") Long album_id,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (!account.getId().equals(album.getAccount().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<PhotoDTO> photos = new ArrayList<>();
        for (Photo photo : photoService.findByAlbum_id(album.getId())) {
            String link = "/albums/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
            photos.add(new PhotoDTO(photo.getId(), photo.getName(),
                    photo.getDescription(), photo.getOriginalFilename(), link));
        }
        AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos);

        return ResponseEntity.ok(albumViewDTO);
    }

    @PutMapping(value = "/albums/{album_id}/update", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "please enter a valid name or description")
    @ApiResponse(responseCode = "204", description = "Album Updated")
    @Operation(summary = "Album update")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<AlbumViewDTO> albumUpdate(@Valid @RequestBody AlbumPayLoadDTO albumPayLoadDTO,
            @PathVariable("album_id") Long album_id,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (!account.getId().equals(album.getAccount().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        album.setName(albumPayLoadDTO.getName());
        album.setDescription(albumPayLoadDTO.getDescription());
        albumService.save(album);

        List<PhotoDTO> photos = new ArrayList<>();
        for (Photo photo : photoService.findByAlbum_id(album.getId())) {
            String link = "/albums/" + album.getId() + "/photos/" + photo.getId() + "/download-photo";
            photos.add(new PhotoDTO(photo.getId(), photo.getName(),
                    photo.getDescription(), photo.getOriginalFilename(), link));
        }
        AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), album.getName(), album.getDescription(), photos);

        return ResponseEntity.ok(albumViewDTO);
    }

    @DeleteMapping(value = "/albums/{album_id}/delete")
    @ApiResponse(responseCode = "202", description = "Album Deleted")
    @Operation(summary = "Album Delete")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<String> albumdelete(
            @PathVariable("album_id") Long album_id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (!account.getId().equals(album.getAccount().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            for (Photo photo : photoService.findByAlbum_id(album.getId())) {

                photoService.delete(photo);
            }
            albumService.deleteAlbum(album);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping(value = "/albums/{album_id}/upload-photos", consumes = { "multipart/form-data" })
    @ApiResponse(responseCode = "400", description = "Please check the payload or token")
    @ApiResponse(responseCode = "201", description = "Photo uploaded successfully")
    @Operation(summary = "Upload photo in Album")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<List<HashMap<String, List<?>>>> photos(
            @RequestPart(required = true) MultipartFile[] files,
            @PathVariable("album_id") Long album_id, Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.orElseThrow(() -> new RuntimeException("Account not found"));

        // Check if the user is the owner of the album
        Album album = albumService.findById(album_id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Album not found"));

        if (!account.getId().equals(album.getAccount().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<String> fileNamesWithError = new ArrayList<>();
        List<PhotoViewDTO> fileNamesWithSuccess = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {
            String contentType = file.getContentType();
            if (contentType != null &&
                    (contentType.equals("image/jpg") || contentType.equals("image/jpeg")
                            || contentType.equals("image/png"))) {

                try {
                    // Read file as byte array
                    byte[] imageData = file.getBytes();

                    Photo photo = new Photo();
                    photo.setName("");
                    photo.setOriginalFilename(file.getOriginalFilename());
                    photo.setImageData(imageData);
                    photo.setAlbum(album);

                    // Save photo in database
                    photoService.save(photo);

                    PhotoViewDTO photoViewDTO = new PhotoViewDTO(photo.getId(), photo.getName(),
                            photo.getDescription());
                    fileNamesWithSuccess.add(photoViewDTO);

                } catch (Exception e) {
                    log.debug(AlbumError.UPLOAD_PHOTO_ERROR.toString() + ": " + e.getMessage());
                    fileNamesWithError.add(file.getOriginalFilename());
                }
            } else {
                fileNamesWithError.add(file.getOriginalFilename());
            }
        });

        HashMap<String, List<?>> results = new HashMap<>();
        results.put("SUCCESS", fileNamesWithSuccess);
        results.put("ERRORS", fileNamesWithError);

        List<HashMap<String, List<?>>> response = new ArrayList<>();
        response.add(results);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/albums/{album_id}/photos/{photos_id}/delete")
    @Operation(summary = "Delete Photo")
    @ApiResponse(responseCode = "202", description = "Photo Deleted Successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden: Unauthorized access")
    @ApiResponse(responseCode = "400", description = "Bad Request: Invalid photo or album")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<String> deletePhoto(@PathVariable("album_id") Long album_id,
            @PathVariable("photos_id") Long photo_id, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            Optional<Album> optionalAlbum = albumService.findById(album_id);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (!account.getId().equals(album.getAccount().getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Optional<Photo> optionalPhoto = photoService.findById(photo_id);
            if (optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if (photo.getAlbum().getId() != album_id) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }

                photoService.delete(photo);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(value = "/albums/{album_id}/photos/{photos_id}/update", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update photo", description = "Update the photo with the given ID from the specified album")
    @ApiResponse(responseCode = "400", description = "please upload a valid photo")
    @ApiResponse(responseCode = "204", description = "Photo Updated")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<PhotoViewDTO> updatePhoto(@Valid @RequestBody PhotoPayloadDTO PhotoPayloadDTO,
            @PathVariable("album_id") Long albumId,
            @PathVariable("photos_id") Long photoId,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            Account account = optionalAccount.get();

            // check if the user is the owner of the album
            Optional<Album> optionalAlbum = albumService.findById(albumId);
            Album album;
            if (optionalAlbum.isPresent()) {
                album = optionalAlbum.get();
                if (account.getId() != album.getAccount().getId()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Optional<Photo> optionalPhoto = photoService.findById(photoId);

            if (optionalPhoto.isPresent()) {
                Photo photo = optionalPhoto.get();
                if (photo.getAlbum().getId() != albumId) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
                photo.setName(PhotoPayloadDTO.getName());
                photo.setDescription(PhotoPayloadDTO.getDescription());
                photoService.save(photo);
                PhotoViewDTO photoViewDTO = new PhotoViewDTO(photo.getId(), photo.getName(), photo.getDescription());
                return ResponseEntity.ok(photoViewDTO);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }
    }

    @GetMapping("/albums/{album_id}/photos/{photos_id}/download-photo")
    @Operation(summary = "Download a photo from an album", description = "Downloads the photo with the given ID from the specified album")
    @ApiResponse(responseCode = "200", description = "Successfully downloaded the photo")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "404", description = "Photo or album not found")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") Long albumId,
            @PathVariable("photos_id") Long photoId, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        // check if the user is the owner of the album
        Optional<Album> optionalAlbum = albumService.findById(albumId);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Photo photo = photoService.findById(photoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Set appropriate content type
                .body(photo.getImageData());
    }

    @GetMapping("/albums/{album_id}/photos/{photos_id}/download-thumbnails")
    @Operation(summary = "Download a photo from an album", description = "Downloads the photo with the given ID from the specified album")
    @ApiResponse(responseCode = "200", description = "Successfully downloaded the photo")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "404", description = "Photo or album not found")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<?> downloadThumbnail(@PathVariable("album_id") Long albumId,
            @PathVariable("photos_id") Long photoId, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        // check if the user is the owner of the album
        Optional<Album> optionalAlbum = albumService.findById(albumId);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Photo photo = photoService.findById(photoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Set appropriate content type
                .body(photo.getImageData());
    }

    public ResponseEntity<?> downloadFile(Long albumId, Long photoId, String folberName,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();

        // check if the user is the owner of the album
        Optional<Album> optionalAlbum = albumService.findById(albumId);
        Album album;
        if (optionalAlbum.isPresent()) {
            album = optionalAlbum.get();
            if (account.getId() != album.getAccount().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<Photo> optionalPhoto = photoService.findById(photoId);
        if (optionalPhoto.isPresent()) {
            Photo photo = optionalPhoto.get();
            if (photo.getAlbum().getId() != albumId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            Resource resource = null;
            try {
                resource = AppUtil.getFileResource(albumId, folberName, photo.getOriginalFilename());
                if (resource == null) {
                    return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
                }
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            }

            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + photo.getOriginalFilename()
                    + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
