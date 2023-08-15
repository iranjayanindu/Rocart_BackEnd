package com.masai.controller;

import com.masai.models.AiDTO;
import com.masai.models.DuplicatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
public class ImageUploadController {
    @Value("${upload.path}")
    private String uploadDirectory;

    private String url = "http://127.0.0.1:5000/detect";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/upload")
    public ResponseEntity<AiDTO> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        Path filePath = null;
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
           filePath = Paths.get(uploadDirectory, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            DuplicatesDTO forObject = restTemplate.getForObject(url, DuplicatesDTO.class);
            System.out.println("object : "+ forObject.getDuplicates().isEmpty());
            if(forObject.getDuplicates().isEmpty()){
                AiDTO build = AiDTO.builder()
                        .duplicate(false)
                        .build();
                return new ResponseEntity<>(build,HttpStatus.OK);
            }else{
                AiDTO build = AiDTO.builder()
                        .duplicate(true)
                        .build();
                Files.deleteIfExists(filePath);
                return new ResponseEntity<>(build,HttpStatus.OK);
            }
        } catch (Exception e) {
            System.out.println("e : "+e);
            Files.deleteIfExists(filePath);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/image")
    public ResponseEntity<?> callAi(){
        String forObject = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(forObject);
    }
}
