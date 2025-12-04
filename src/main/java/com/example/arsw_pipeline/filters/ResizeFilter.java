package com.example.arsw_pipeline.filters;

import com.example.arsw_pipeline.service.ImageService;
import com.example.arsw_pipeline.pipes.Pipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
@ConditionalOnProperty(name = "app.role", havingValue = "RESIZE")
public class ResizeFilter {

    @Autowired
    private ImageService imageService;

    @Autowired
    private Pipe nextPipe;

    @PostMapping("/resize")
    public String applyResize(@RequestParam("imagem") String nomeImagem) throws InterruptedException {
        System.out.println("⚙️ [RESIZE] Processando...");
        String resultado = imageService.processarImagem(nomeImagem, "RESIZE");
        return nextPipe.transport(resultado);
    }
}