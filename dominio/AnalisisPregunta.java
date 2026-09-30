package com.tpv1.dominio;

public class AnalisisPregunta {
    private final Pregunta pregunta;
    private final int si;
    private final int no;
    private final double score;

    public AnalisisPregunta(Pregunta pregunta, int si, int no, double score) {
        this.pregunta = pregunta;
        this.si = si;
        this.no = no;
        this.score = score;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public int getSi() {
        return si;
    }

    public int getNo() {
        return no;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return pregunta + " -> Sí: " + si + " | No: " + no + " | score=" + score;
    }
}
