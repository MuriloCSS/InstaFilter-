package com.example.arsw_pipeline.config;

import com.example.arsw_pipeline.pipes.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PipeConfig {

    @Value("${app.role}")
    private String role;

    @Bean
    @Primary
    public Pipe activePipe(ToResizePipe resizePipe,
                           ToGrayscalePipe grayscalePipe,
                           ToWatermarkPipe watermarkPipe) {

        switch (role) {
            case "GATEWAY":   return resizePipe;
            case "RESIZE":    return grayscalePipe;
            case "GRAYSCALE": return watermarkPipe;
            case "WATERMARK": return new NullPipe();
            default:          return new NullPipe();
        }
    }

    static class NullPipe implements Pipe {
        @Override
        public String transport(String payload) {
            return "FIM_DO_PROCESSO: " + payload;
        }
    }
}