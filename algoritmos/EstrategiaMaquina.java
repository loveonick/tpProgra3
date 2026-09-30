package com.tpv1.algoritmos;

import com.tpv1.dominio.DecisionMaquina;
import com.tpv1.dominio.Jugador;
import com.tpv1.dominio.Partida;
import com.tpv1.dominio.Personaje;

import java.util.List;

public interface EstrategiaMaquina {
    DecisionMaquina seleccionarAccion(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico, Jugador rival);
    String nombreEstrategia();
}
