package com.racha.restdev.util.apputils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

public class AppUtil {
    private static final String PATH = "src/main/resources/static/uploads/";

    public static String getPhotoUploadPath(String fileName, String folderName, String albumId) throws IOException {
        String path = PATH + albumId + "\\" + folderName;
        Files.createDirectories(Paths.get(path));
        return new File(path).getAbsolutePath() + "\\" + fileName;
    }

    public static BufferedImage getThumbnail(MultipartFile originalFile, Integer width) throws IOException {
        BufferedImage thumgImg = null;
        BufferedImage img = ImageIO.read(originalFile.getInputStream());
        thumgImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, width, Scalr.OP_ANTIALIAS);

        return thumgImg;
    }

    public static Resource getFileResource(String albumId, String folderName, String filename) throws IOException {
        String location = PATH + albumId + "/" + folderName + "/" + filename;
        Path path = Paths.get(location);
        if (Files.exists(path)) {
            return new UrlResource(path.toUri());
        }
        return null;
    }

    public static Boolean deletePhotoFromPath(String fileName, String folderName, String albumId) throws IOException {

        try {
            File file = new File(PATH + albumId + "\\" + folderName + "\\" + fileName);// file tobe deleted
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
