package com.signatureapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SignatureAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SignatureAppApplication.class, args);
    }
}