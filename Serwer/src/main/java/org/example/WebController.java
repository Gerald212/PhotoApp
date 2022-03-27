package org.example;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/test")
public class WebController {

    @GetMapping("/hello")
    public String abc(){
        return "abc";
    }

    @PostMapping("/put_image")
    public void putImage(@RequestPart(name = "img") MultipartFile img){
        //storageService.store(img);
        System.out.println( "cos przyszlo" );
        try {
            byte[] bytes = img.getBytes();

            Path path = Paths.get("./zdjecia/" + img.getOriginalFilename());
            Files.createDirectories(path.getParent());

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println( "Zaladowano zdjecie: " + img.getOriginalFilename() );
    }
}