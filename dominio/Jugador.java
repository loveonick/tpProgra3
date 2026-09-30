package com.tpv1.dominio;

public abstract class Jugador {
    private final String nombre;

    protected Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract boolean esHumano();
}
