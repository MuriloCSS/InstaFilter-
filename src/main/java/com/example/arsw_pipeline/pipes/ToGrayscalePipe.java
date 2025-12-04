package com.example.arsw_pipeline.pipes;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@Component("toGrayscalePipe")
public class ToGrayscalePipe implements Pipe {

    private final RestTemplate restTemplate = new RestTemplate();
    private final CircuitBreaker circuitBreaker;
    private final String NEXT_STEP_URL = "http://localhost:8081/api/process/grayscale?imagem=";

    public ToGrayscalePipe(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig configManual = CircuitBreakerConfig.custom()
                .failureRateThreshold(1)
                .minimumNumberOfCalls(3)
                .slidingWindowSize(5)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .permittedNumberOfCallsInHalfOpenState(1)
                .build();
        this.circuitBreaker = CircuitBreaker.of("filtroGrayscaleManual", configManual);
    }

    @Override
    public String transport(String payload) {
        String estado = circuitBreaker.getState().name();
        int falhas = circuitBreaker.getMetrics().getNumberOfFailedCalls();
        int total = circuitBreaker.getMetrics().getNumberOfBufferedCalls();

        System.out.println(String.format("➡️ [RESIZE -> GRAYSCALE] Estado CB: %s | Falhas: %d/%d", estado, falhas, total));

        Supplier<String> callSupplier = () -> restTemplate.postForObject(NEXT_STEP_URL + payload, null, String.class);

        try {
            return circuitBreaker.executeSupplier(callSupplier);
        } catch (Exception e) {
            return fallback(payload, e);
        }
    }

    private String fallback(String imagem, Throwable t) {
        if (t instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            System.out.println("⛔ [CB OPEN] Circuito Abriu! Devolvendo imagem sem GrayScale...");
            return "URL_FINAL/" + imagem + "_SemGrayScale_SemWaterMark_(CIRCUITO_ABERTO)";
        }
        System.out.println("🚨 [REDE] Tentando conectar...");
        System.out.println("🚨 [REDE] Erro ao conectar no Grayscale.");
        return "⚠️ TENTATIVA_FALHOU_DE_REDE_PARA_GRAYSCALE";
    }
}