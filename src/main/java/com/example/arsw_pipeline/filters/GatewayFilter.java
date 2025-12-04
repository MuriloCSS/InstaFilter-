package com.example.arsw_pipeline.filters;

import com.example.arsw_pipeline.pipes.Pipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@ConditionalOnProperty(name = "app.role", havingValue = "GATEWAY")
public class GatewayFilter {

    @Autowired
    private Pipe nextPipe;

    @GetMapping("/upload")
    public String uploadImage(@RequestParam("imagem") String nomeImagem) {
        System.out.println("\n📥 [GATEWAY] Recebido: " + nomeImagem);
        return nextPipe.transport(nomeImagem);
    }
}