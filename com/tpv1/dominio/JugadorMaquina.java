package com.tpv1.dominio;

import com.tpv1.algoritmos.EstrategiaMaquina;

import java.util.List;

public class JugadorMaquina extends Jugador {
    private final EstrategiaMaquina estrategia;

    public JugadorMaquina(String nombre, EstrategiaMaquina estrategia) {
        super(nombre);
        this.estrategia = estrategia;
    }

    public EstrategiaMaquina getEstrategia() {
        return estrategia;
    }

    public DecisionMaquina decidir(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico, Jugador rival) {
        return estrategia.seleccionarAccion(candidatos, historialPublico, rival);
    }

    @Override
    public boolean esHumano() {
        return false;
    }
}
