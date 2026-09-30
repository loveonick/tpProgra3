package com.tpv1.persistencia;

import java.util.HashMap;
import java.util.Map;

public class Marcador {
    private final Map<String, Integer> partidasGanadas = new HashMap<>();

    public void registrarVictoria(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        partidasGanadas.put(nombreUsuario, partidasGanadas.getOrDefault(nombreUsuario, 0) + 1);
    }

    public Map<String, Integer> getPartidasGanadas() {
        return new HashMap<>(partidasGanadas);
    }
}
