package com.textilima.textilima; // Tu paquete base

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // Importa ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = "com.textilima.textilima") // Asegúrate que esto apunta a tu paquete base
public class TextilimaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextilimaApplication.class, args);
    }


}