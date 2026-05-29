package com.soporte.soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <--- AGREGA ESTO

@SpringBootApplication
@EnableFeignClients 
public class SoporteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoporteApplication.class, args);
    }

}