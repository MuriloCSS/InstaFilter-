package com.example.arsw_pipeline.pipes;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@Component("toWatermarkPipe")
public class ToWatermarkPipe implements Pipe {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CircuitBreaker circuitBreaker;
    private final String NEXT_STEP_URL = "http://localhost:8082/api/process/watermark?imagem=";

    public ToWatermarkPipe(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig configManual = CircuitBreakerConfig.custom()
                .failureRateThreshold(25)
                .minimumNumberOfCalls(3)
                .slidingWindowSize(4)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .permittedNumberOfCallsInHalfOpenState(1)
                .build();

        this.circuitBreaker = CircuitBreaker.of("filtroWatermarkManual", configManual);
    }

    @Override
    public String transport(String payload) {
        int falhas = circuitBreaker.getMetrics().getNumberOfFailedCalls();
        String estado = circuitBreaker.getState().name();

        System.out.println(String.format(
                "➡️ [WATERMARK] Chamada Iniciada. Estado: %s | Falhas Acumuladas: %d/3",
                estado, falhas
        ));

        Supplier<String> callSupplier = () ->
                restTemplate.postForObject(NEXT_STEP_URL + payload, null, String.class);

        try {
            return circuitBreaker.executeSupplier(callSupplier);
        } catch (Exception e) {
            return fallback(payload, e);
        }
    }

    private String fallback(String imagem, Throwable t) {
        if (t instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            System.out.println("⛔ [CB OPEN] Circuito Abriu! Devolvendo imagem sem watermark...");
            return "URL_FINAL/" + imagem + "_SemWaterMark_(CIRCUITO_ABERTO)";
        }
        System.out.println("🚨 [REDE] Tentando conectar...");
        System.out.println("🚨 [REDE] Erro ao conectar no WaterMark.");
        return "⚠️ TENTATIVA_FALHOU_DE_REDE_PARA_WATERMARK";
    }
}