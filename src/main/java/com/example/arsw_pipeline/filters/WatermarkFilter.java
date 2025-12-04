package com.example.arsw_pipeline.filters;

import com.example.arsw_pipeline.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
@ConditionalOnProperty(name = "app.role", havingValue = "WATERMARK")
public class WatermarkFilter {

    @Autowired
    private ImageService imageService;

    @PostMapping("/watermark")
    public String applyWatermark(@RequestParam("imagem") String nomeImagem) throws InterruptedException {
        String resultado = imageService.processarImagem(nomeImagem, "WATERMARK");
        System.out.println("🏁 [WATERMARK] Finalizado.");
        return "URL_FINAL/" + resultado;
    }
}