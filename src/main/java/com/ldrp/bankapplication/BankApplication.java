package com.ldrp.bankapplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//url for documentation Swagger UI = http://localhost:8080/swagger-ui/index.html#/
@OpenAPIDefinition(
        info = @Info(
                title = "My Bank App",
                description = "Backend Rest APIs for my Bank",
                version = "v1.0",
                contact = @Contact(
                        name = "Nand Patel",
                        email = "nand.patel.255@gmail.com",
                        url = "https://github.com/Nand255"
                ),
                license = @License(
                        name = "my company",
                        url = "https://github.com/Nand255"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "this is description about documentation of my Bank App",
                        url = "https://github.com/Nand255"
        )
)
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}
