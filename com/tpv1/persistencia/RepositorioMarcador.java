package com.tpv1.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioMarcador {
    private final Path rutaArchivo;

    public RepositorioMarcador(String rutaArchivo) {
        this.rutaArchivo = Paths.get(rutaArchivo);
        inicializarArchivo();
    }

    public void guardar(String nombreUsuario, int partidasGanadas) {
        Map<String, Integer> mapa = cargar();
        mapa.put(nombreUsuario, partidasGanadas);
        escribir(mapa);
    }

    public void registrarVictoria(String nombreUsuario) {
        Map<String, Integer> mapa = cargar();
        int actuales = mapa.getOrDefault(nombreUsuario, 0);
        mapa.put(nombreUsuario, actuales + 1);
        escribir(mapa);
    }

    public Map<String, Integer> cargar() {
        Map<String, Integer> resultado = new HashMap<>();
        if (!Files.exists(rutaArchivo)) {
            return resultado;
        }

        try {
            List<String> lineas = Files.readAllLines(rutaArchivo, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                if (linea == null || linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(",");
                if (partes.length >= 2) {
                    String nombre = partes[0].trim();
                    int victorias = Integer.parseInt(partes[1].trim());
                    resultado.put(nombre, victorias);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo de marcador: " + rutaArchivo, e);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("El archivo de marcador está corrupto o tiene un formato inválido.", e);
        }
        return resultado;
    }

    private void escribir(Map<String, Integer> marcador) {
        try {
            Path directorio = rutaArchivo.getParent();
            if (directorio != null) {
                Files.createDirectories(directorio);
            }
            StringBuilder contenido = new StringBuilder();
            for (Map.Entry<String, Integer> entrada : marcador.entrySet()) {
                contenido.append(entrada.getKey()).append(',').append(entrada.getValue()).append(System.lineSeparator());
            }
            Files.write(rutaArchivo, contenido.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el marcador en " + rutaArchivo, e);
        }
    }

    private void inicializarArchivo() {
        try {
            Path directorio = rutaArchivo.getParent();
            if (directorio != null) {
                Files.createDirectories(directorio);
            }
            if (!Files.exists(rutaArchivo)) {
                Files.write(rutaArchivo, "".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar la persistencia del marcador", e);
        }
    }
}
