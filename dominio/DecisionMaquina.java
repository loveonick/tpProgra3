package com.tpv1.dominio;

import java.util.ArrayList;
import java.util.List;

public class DecisionMaquina {
    private final boolean realizarSuposicion;
    private final Pregunta pregunta;
    private final Personaje personajeObjetivo;
    private final String motivo;
    private final List<AnalisisPregunta> alternativas;
    private final int candidatosAntes;

    public DecisionMaquina(boolean realizarSuposicion, Pregunta pregunta, Personaje personajeObjetivo,
                          String motivo, List<AnalisisPregunta> alternativas, int candidatosAntes) {
        this.realizarSuposicion = realizarSuposicion;
        this.pregunta = pregunta;
        this.personajeObjetivo = personajeObjetivo;
        this.motivo = motivo;
        this.alternativas = new ArrayList<>(alternativas);
        this.candidatosAntes = candidatosAntes;
    }

    public boolean isRealizarSuposicion() {
        return realizarSuposicion;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public Personaje getPersonajeObjetivo() {
        return personajeObjetivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public List<AnalisisPregunta> getAlternativas() {
        return new ArrayList<>(alternativas);
    }

    public int getCandidatosAntes() {
        return candidatosAntes;
    }
}
