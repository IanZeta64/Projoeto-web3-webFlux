package br.com.ada.jogosapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JogosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JogosApiApplication.class, args);
        System.out.println("Ola, Mundo!");
    }

}