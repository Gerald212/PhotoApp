package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class WebController {

    @GetMapping("/hello")
    public String abc(){
        return "abc";
    }

    @PostMapping("/put_image")
    public void putImage(){

    }
}