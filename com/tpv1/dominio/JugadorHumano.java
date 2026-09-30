package com.tpv1.dominio;

public class JugadorHumano extends Jugador {
    private final Personaje personajeSecreto;

    public JugadorHumano(String nombre, Personaje personajeSecreto) {
        super(nombre);
        this.personajeSecreto = personajeSecreto;
    }

    public Personaje getPersonajeSecreto() {
        return personajeSecreto;
    }

    @Override
    public boolean esHumano() {
        return true;
    }
}
