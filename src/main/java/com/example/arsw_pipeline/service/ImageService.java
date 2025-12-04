package com.example.arsw_pipeline.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageService {

    private static final String SHARED_FOLDER = System.getProperty("java.io.tmpdir") + "/instafilter/";
    private final Map<String, String> cacheMemoria = new ConcurrentHashMap<>();

    public ImageService() {
        new File(SHARED_FOLDER).mkdirs();
    }

    public String processarImagem(String nomeArquivo, String tipoFiltro) throws InterruptedException {
        String chaveCache = nomeArquivo + "_" + tipoFiltro;

        if (cacheMemoria.containsKey(chaveCache)) {
            System.out.println("⚡ [CACHE HIT] Imagem '" + nomeArquivo + "' já processada para " + tipoFiltro);
            return cacheMemoria.get(chaveCache);
        }

        long tempoEspera = "RESIZE".equals(tipoFiltro) ? 1000 : 3000;

        System.out.println("⚙️ [PROCESSANDO] Aplicando " + tipoFiltro + " em " + nomeArquivo + " (Aguarde " + tempoEspera/1000 + "s...)");
        Thread.sleep(tempoEspera);

        String novoNome = "processado_" + tipoFiltro + "_" + nomeArquivo;

        cacheMemoria.put(chaveCache, novoNome);
        return novoNome;
    }
}