package com.tpv1.dominio;

public class Respuesta {
    private final String emisor;
    private final String receptor;
    private final Pregunta pregunta;
    private final boolean afirmativa;
    private final int candidatosAntes;
    private final int candidatosDespues;

    public Respuesta(String emisor, String receptor, Pregunta pregunta, boolean afirmativa, int candidatosAntes, int candidatosDespues) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.pregunta = pregunta;
        this.afirmativa = afirmativa;
        this.candidatosAntes = candidatosAntes;
        this.candidatosDespues = candidatosDespues;
    }

    public boolean isAfirmativa() {
        return afirmativa;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public int getCandidatosAntes() {
        return candidatosAntes;
    }

    public int getCandidatosDespues() {
        return candidatosDespues;
    }

    @Override
    public String toString() {
        return emisor + " pregunta a " + receptor + ": " + pregunta + " -> " + (afirmativa ? "Sí" : "No") + ". Candidatos: " + candidatosAntes + " -> " + candidatosDespues;
    }
}
