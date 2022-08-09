package main.service;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;


@Service

public class ImageService {
    private static final int IMAGE_WIDTH = 36;
    private static final int IMAGE_HEIGHT = 36;



    public ResponseEntity<?> postImage(MultipartFile image) throws IOException {
        Map<String, Object> errors = new LinkedHashMap<>();
        Map<String, Object> responseMap = new LinkedHashMap<>();


        int maxSize = 1_000_000;
        if (image.getSize() <= maxSize) {
            File convertedImage = saveImage(image, false);
            String destination = StringUtils.cleanPath(convertedImage.getPath());
            if (!destination.endsWith("jpg") && !destination.endsWith("png")) {

                responseMap.put("result", false);
                errors.put("image", "Не правильный формат картинки");
                responseMap.put("errors", errors);
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("/" + destination, HttpStatus.OK);
        } else {

            responseMap.put("result", false);
            errors.put("image", "Размер файла превышает допустимый размер");
            responseMap.put("errors", errors);
            return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
        }
    }

    public File saveImage(MultipartFile image, boolean resizeImage) throws IOException {
        String firstPath = "upload/";
        String hash = String.valueOf(Math.abs(firstPath.hashCode()));
        String firstFolder = hash.substring(0, hash.length() / 3);
        int secondPart = 2 * hash.length() / 3;
        String secondFolder = hash.substring((hash.length() / 3) + 1, secondPart);
        String thirdFolder = hash.substring(1 + secondPart);

        File uploadFolder = new File(firstPath);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdir();
        }
        File folder1 = new File(firstPath + firstFolder);
        if (!folder1.exists()) {
            folder1.mkdir();
        }
        File folder2 = new File(firstPath + firstFolder + "/" + secondFolder);
        if (!folder2.exists()) {
            folder2.mkdir();
        }
        File folder3 = new File(firstPath + firstFolder + "/" + secondFolder + "/" + thirdFolder);
        if (!folder3.exists()) {
            folder3.mkdir();
        }


        String des = firstPath + firstFolder + "/" + secondFolder + "/" + thirdFolder + "/";
        Path path = Paths.get(des + image.getOriginalFilename());
        if (!resizeImage) {
            byte[] bytes;

            bytes = image.getBytes();


            Files.write(path, bytes);
        } else {
            Image photo = ImageIO.read(image.getInputStream());
            BufferedImage bufferedImage = resizeImage(photo);
            ImageIO.write(bufferedImage, "jpg", path.toFile());
        }


        return path.toFile();
    }

    private BufferedImage resizeImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
        graphics2D.dispose();
        return bufferedImage;
    }


}
