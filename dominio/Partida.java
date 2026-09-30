package com.tpv1.dominio;

import com.tpv1.servicios.ServicioFiltrado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Partida {
    private final List<Personaje> personajesDisponibles;
    private final Map<Jugador, Personaje> secretos = new HashMap<>();
    // Modificado: Clave que relaciona "Emisor-Receptor" para mantener listas de candidatos 100% independientes
    private final Map<String, List<Personaje>> candidatosPorJugador = new HashMap<>();
    private final List<RegistroPublico> historialPublico = new ArrayList<>();
    private final ServicioFiltrado servicioFiltrado = new ServicioFiltrado();
    private final List<String> eventos = new ArrayList<>();
    private Jugador ganador;
    private boolean terminada;

    public Partida(List<Personaje> personajesDisponibles) {
        this.personajesDisponibles = new ArrayList<>(personajesDisponibles);
    }

    public void registrarJugador(Jugador jugador) {
        // Inicialización diferida manejada directamente en obtenerCandidatos
    }

    public void asignarSecreto(Jugador jugador, Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("El personaje no puede ser nulo");
        }
        if (secretos.containsKey(jugador)) {
            throw new IllegalArgumentException("El jugador ya tiene un personaje secreto fijado y no puede cambiarlo");
        }
        Set<Personaje> ocupados = new HashSet<>(secretos.values());
        if (ocupados.contains(personaje)) {
            throw new IllegalArgumentException("Ese personaje ya fue seleccionado por otro jugador");
        }
        secretos.put(jugador, personaje);
        eventos.add(jugador.getNombre() + " eligió a " + personaje.getNombre());
    }

    public void completarSecretosAutomaticos(List<Jugador> jugadores) {
        List<Personaje> usados = new ArrayList<>(secretos.values());
        List<Personaje> restantes = new ArrayList<>();
        for (Personaje personaje : personajesDisponibles) {
            if (!usados.contains(personaje)) {
                restantes.add(personaje);
            }
        }

        for (Jugador jugador : jugadores) {
            if (secretos.containsKey(jugador)) {
                continue;
            }
            if (restantes.isEmpty()) {
                throw new IllegalStateException("No quedan personajes disponibles para asignar secretos");
            }
            int indice = (int) (Math.random() * restantes.size());
            Personaje elegido = restantes.remove(indice);
            asignarSecreto(jugador, elegido);
        }
    }

    public Personaje getSecreto(Jugador jugador) {
        return secretos.get(jugador);
    }

    // Modificado: Requiere emisor y receptor para mantener las listas y conteos asilados
    public List<Personaje> obtenerCandidatos(Jugador emisor, Jugador receptor) {
        String clave = emisor.getNombre() + "-" + receptor.getNombre();
        if (!candidatosPorJugador.containsKey(clave)) {
            candidatosPorJugador.put(clave, new ArrayList<>(personajesDisponibles));
        }
        return new ArrayList<>(candidatosPorJugador.get(clave));
    }

    public List<Personaje> getPersonajesDisponibles() {
        return new ArrayList<>(personajesDisponibles);
    }

    public Respuesta responderPregunta(Jugador emisor, Jugador receptor, Pregunta pregunta) {
        if (emisor == null || receptor == null || pregunta == null) {
            throw new IllegalArgumentException("Los datos de la pregunta son inválidos.");
        }
        Personaje secreto = secretos.get(receptor);
        boolean afirmativa = pregunta.aplicaA(secreto);

        List<Personaje> actuales = obtenerCandidatos(emisor, receptor);
        List<Personaje> luegoFiltrar = servicioFiltrado.filtrar(actuales, pregunta, afirmativa);

        String clave = emisor.getNombre() + "-" + receptor.getNombre();
        candidatosPorJugador.put(clave, luegoFiltrar);

        Respuesta respuesta = new Respuesta(emisor.getNombre(), receptor.getNombre(), pregunta, afirmativa, actuales.size(), luegoFiltrar.size());
        historialPublico.add(new RegistroPublico(emisor.getNombre(), receptor.getNombre(), pregunta, afirmativa, actuales.size(), luegoFiltrar.size()));
        eventos.add("Pregunta: " + emisor.getNombre() + " -> " + receptor.getNombre() + ": " + pregunta + " = " + (afirmativa ? "Sí" : "No"));
        return respuesta;
    }

    public boolean intentarSuposicion(Jugador emisor, Jugador receptor, Personaje supuesto) {
        if (supuesto == null) {
            throw new IllegalArgumentException("La suposición no puede ser nula");
        }
        if (secretos.get(receptor).equals(supuesto)) {
            terminada = true;
            ganador = emisor;
            eventos.add(emisor.getNombre() + " adivinó correctamente a " + receptor.getNombre() + " con " + supuesto.getNombre());
            return true;
        }

        List<Personaje> actuales = obtenerCandidatos(emisor, receptor);
        actuales.remove(supuesto);

        String clave = emisor.getNombre() + "-" + receptor.getNombre();
        candidatosPorJugador.put(clave, actuales);
        eventos.add(emisor.getNombre() + " falló la suposición sobre " + supuesto.getNombre() + ". El personaje queda descartado");
        return false;
    }

    public List<RegistroPublico> getHistorialPublico() {
        return new ArrayList<>(historialPublico);
    }

    public boolean estaTerminada() {
        return terminada;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public List<String> getEventos() {
        return new ArrayList<>(eventos);
    }

    public static class RegistroPublico {
        private final String emisor;
        private final String receptor;
        private final Pregunta pregunta;
        private final boolean respuesta;
        private final int candidatosAntes;
        private final int candidatosDespues;

        public RegistroPublico(String emisor, String receptor, Pregunta pregunta, boolean respuesta, int candidatosAntes, int candidatosDespues) {
            this.emisor = emisor;
            this.receptor = receptor;
            this.pregunta = pregunta;
            this.respuesta = respuesta;
            this.candidatosAntes = candidatosAntes;
            this.candidatosDespues = candidatosDespues;
        }

        public String getEmisor() { return emisor; }
        public String getReceptor() { return receptor; }
        public Pregunta getPregunta() { return pregunta; }
        public boolean isRespuesta() { return respuesta; }
        public int getCandidatosAntes() { return candidatosAntes; }
        public int getCandidatosDespues() { return candidatosDespues; }
    }
}