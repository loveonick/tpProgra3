package com.tpv1.dominio;

public class Pregunta {
    private final Filtro filtro;
    private final String enunciado;

    public Pregunta(Filtro filtro) {
        this.filtro = filtro;
        this.enunciado = filtro.descripcion();
    }

    public Pregunta(TipoFiltro tipo, Object valor) {
        this(new Filtro(tipo, valor));
    }

    public Filtro getFiltro() {
        return filtro;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public boolean aplicaA(Personaje personaje) {
        return filtro.aplicaA(personaje);
    }

    @Override
    public String toString() {
        return enunciado;
    }
}
