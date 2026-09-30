package com.tpv1.servicios;

import com.tpv1.persistencia.Marcador;
import com.tpv1.persistencia.RepositorioMarcador;

import java.util.Map;

public class ServicioMarcador {
    private final RepositorioMarcador repositorioMarcador;
    private final Marcador marcadorEnMemoria = new Marcador();

    public ServicioMarcador(RepositorioMarcador repositorioMarcador) {
        this.repositorioMarcador = repositorioMarcador;
        Map<String, Integer> cargado = repositorioMarcador.cargar();
        for (Map.Entry<String, Integer> entrada : cargado.entrySet()) {
            for (int i = 0; i < entrada.getValue(); i++) {
                marcadorEnMemoria.registrarVictoria(entrada.getKey());
            }
        }
    }

    public void registrarVictoria(String nombreUsuario) {
        marcadorEnMemoria.registrarVictoria(nombreUsuario);
        repositorioMarcador.registrarVictoria(nombreUsuario);
    }

    public Map<String, Integer> obtenerTodos() {
        return marcadorEnMemoria.getPartidasGanadas();
    }
}
