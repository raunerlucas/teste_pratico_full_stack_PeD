package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestão Industrial — API REST")
                        .version("1.0.0")
                        .description("""
                                API para gerenciamento de insumos e otimização de produção industrial.
                                
                                **Funcionalidades principais:**
                                - CRUD de Matérias-Primas (Raw Materials)
                                - CRUD de Produtos (Products)
                                - Cálculo de Otimização de Produção
                                """)
                        .contact(new Contact()
                                .name("Equipe Backend")
                                .email("contato@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Docker")
                ));
    }
}

