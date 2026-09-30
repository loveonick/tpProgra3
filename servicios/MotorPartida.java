package com.tpv1.servicios;

import com.tpv1.algoritmos.EstrategiaDivideYConquista;
import com.tpv1.algoritmos.EstrategiaGreedy;
import com.tpv1.catalogo.CatalogoPersonajes;
import com.tpv1.dominio.DecisionMaquina;
import com.tpv1.dominio.Jugador;
import com.tpv1.dominio.JugadorHumano;
import com.tpv1.dominio.JugadorMaquina;
import com.tpv1.dominio.Partida;
import com.tpv1.dominio.Personaje;
import com.tpv1.dominio.Pregunta;
import com.tpv1.dominio.Respuesta;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorPartida {
    private final List<Personaje> personajesBase;
    private final ServicioFiltrado servicioFiltrado;

    public MotorPartida(List<Personaje> personajesBase) {
        this.personajesBase = new ArrayList<>(personajesBase);
        this.servicioFiltrado = new ServicioFiltrado();
    }

    public Partida crearPartidaHumanoVsMaquina(JugadorHumano humano, JugadorMaquina maquina) {
        Partida partida = new Partida(personajesBase);
        partida.registrarJugador(humano);
        partida.registrarJugador(maquina);
        partida.asignarSecreto(humano, humano.getPersonajeSecreto());
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(humano);
        jugadores.add(maquina);
        partida.completarSecretosAutomaticos(jugadores);
        if (partida.getSecreto(maquina).equals(humano.getPersonajeSecreto())) {
            throw new IllegalStateException("La máquina no puede elegir el mismo personaje que el usuario");
        }
        return partida;
    }

    public Partida crearPartidaMaquinaVsMaquina(JugadorMaquina maquina1, JugadorMaquina maquina2) {
        Partida partida = new Partida(personajesBase);
        partida.registrarJugador(maquina1);
        partida.registrarJugador(maquina2);
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(maquina1);
        jugadores.add(maquina2);
        partida.completarSecretosAutomaticos(jugadores);
        if (partida.getSecreto(maquina1).equals(partida.getSecreto(maquina2))) {
            throw new IllegalStateException("Las máquinas no pueden compartir el mismo secreto");
        }
        return partida;
    }

    public boolean jugarHumanoVsMaquina(Scanner scanner, String nombreUsuario) {
        CatalogoPersonajes catalogo = new CatalogoPersonajes();
        List<Personaje> personajes = catalogo.getPersonajes();
        System.out.println("Seleccione su personaje secreto (ingrese el id):");
        for (Personaje personaje : personajes) {
            System.out.println(personaje);
        }
        int id = leerEntero(scanner, "ID del personaje");
        Personaje secretoHumano = catalogo.buscarPorId(id);
        JugadorHumano humano = new JugadorHumano(nombreUsuario, secretoHumano);
        JugadorMaquina maquina = new JugadorMaquina("Máquina 1", new EstrategiaGreedy());
        Partida partida = crearPartidaHumanoVsMaquina(humano, maquina);

        System.out.println("\nTu secreto es: " + secretoHumano.getNombre());
        System.out.println("La máquina eligió un personaje secreto. ¡Comienza el juego!");

        while (!partida.estaTerminada()) {
            boolean turnoHumanoTerminado = false;

            while (!turnoHumanoTerminado) {
                List<Personaje> cands = partida.obtenerCandidatos(humano, maquina);
                System.out.println("\n╔══════════════════════════════╗");
                System.out.println("  TURNO HUMANO");
                System.out.println("  Opciones restantes: " + cands.size());
                System.out.println("╚══════════════════════════════╝");

                System.out.println("1. Hacer pregunta");
                System.out.println("2. Realizar suposición (Adivinar)");
                System.out.println("3. Ver candidatos restantes");
                System.out.println("4. Mostrar historial público");
                int opcion = leerEntero(scanner, "Opción");

                switch (opcion) {
                    case 1:
                        Pregunta pregunta = elegirPregunta(scanner);
                        Respuesta respuesta = partida.responderPregunta(humano, maquina, pregunta);
                        System.out.println("\nRespuesta de la máquina: " + (respuesta.isAfirmativa() ? "SÍ" : "NO"));

                        List<Personaje> restantesHumano = partida.obtenerCandidatos(humano, maquina);
                        System.out.println(">>> A la máquina le quedan " + restantesHumano.size() + " candidatos posibles.");
                        turnoHumanoTerminado = true;
                        break;
                    case 2:
                        System.out.println("\nSeleccione el ID del personaje que sospecha:");
                        int idSospecha = leerEntero(scanner, "ID de la suposición");
                        Personaje sospechado = catalogo.buscarPorId(idSospecha);
                        boolean acerto = partida.intentarSuposicion(humano, maquina, sospechado);
                        if (acerto) {
                            System.out.println("¡Adivinaste! Ganaste la partida.");
                            return true;
                        }
                        System.out.println("La suposición fue incorrecta. " + sospechado.getNombre() + " queda descartado.");
                        turnoHumanoTerminado = true;
                        break;
                    case 3:
                        System.out.println("\n=== CANDIDATOS RESTANTES (" + cands.size() + ") ===");
                        for (Personaje p : cands) {
                            System.out.println("  " + p.toString());
                        }
                        break;
                    case 4:
                        System.out.println("\n=== HISTORIAL PÚBLICO ===");
                        for (Partida.RegistroPublico reg : partida.getHistorialPublico()) {
                            System.out.println("  " + reg.getEmisor() + " -> " + reg.getReceptor() + ": " + reg.getPregunta() + " = " + (reg.isRespuesta() ? "Sí" : "No"));
                        }
                        break;
                    default:
                        System.out.println("Opción inválida.");
                        break;
                }
            }

            if (partida.estaTerminada()) {
                return false;
            }

            System.out.println("\n=== TURNO MÁQUINA ===");

            List<Partida.RegistroPublico> histMaquina = new ArrayList<>();
            for (Partida.RegistroPublico reg : partida.getHistorialPublico()) {
                if (reg.getReceptor().equals(humano.getNombre())) {
                    histMaquina.add(reg);
                }
            }

            DecisionMaquina decision = maquina.decidir(partida.obtenerCandidatos(maquina, humano), histMaquina, humano);
            System.out.println("Estrategia: " + maquina.getEstrategia().nombreEstrategia());
            System.out.println("Motivo: " + decision.getMotivo());

            if (decision.isRealizarSuposicion()) {
                boolean acierto = partida.intentarSuposicion(maquina, humano, decision.getPersonajeObjetivo());
                if (acierto) {
                    System.out.println("La máquina adivinó tu personaje. ¡Perdiste!");
                    return false;
                }
                System.out.println("La máquina falló su suposición sobre " + decision.getPersonajeObjetivo().getNombre());
            } else {
                Respuesta rMaquina = partida.responderPregunta(maquina, humano, decision.getPregunta());
                System.out.println(rMaquina);
            }
        }
        return false;
    }

    public Partida crearPartidaHumanoVsMaquinas(JugadorHumano humano, JugadorMaquina maquina1, JugadorMaquina maquina2) {
        Partida partida = new Partida(personajesBase);
        partida.registrarJugador(humano);
        partida.registrarJugador(maquina1);
        partida.registrarJugador(maquina2);
        partida.asignarSecreto(humano, humano.getPersonajeSecreto());
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(humano);
        jugadores.add(maquina1);
        jugadores.add(maquina2);
        partida.completarSecretosAutomaticos(jugadores);
        return partida;
    }

    public boolean jugarHumanoVsMaquinas(Scanner scanner, String nombreUsuario) {
        CatalogoPersonajes catalogo = new CatalogoPersonajes();
        List<Personaje> personajes = catalogo.getPersonajes();

        System.out.println("\nPersonajes disponibles:");
        for (Personaje p : personajes) {
            System.out.println(p);
        }
        int id = leerEntero(scanner, "Elegí tu personaje secreto (ID)");
        Personaje secretoHumano = catalogo.buscarPorId(id);

        JugadorHumano humano   = new JugadorHumano(nombreUsuario, secretoHumano);
        JugadorMaquina maquina1 = new JugadorMaquina("Máquina 1 [D&C]",    new EstrategiaDivideYConquista());
        JugadorMaquina maquina2 = new JugadorMaquina("Máquina 2 [Greedy]", new EstrategiaGreedy());

        Partida partida = crearPartidaHumanoVsMaquinas(humano, maquina1, maquina2);

        System.out.println("\nTu personaje: " + secretoHumano.getNombre());
        System.out.println("Las máquinas han elegido sus secretos en silencio.");
        System.out.println("─────────────────────────────────────────");

        while (!partida.estaTerminada()) {
            boolean turnoHumanoTerminado = false;

            while (!turnoHumanoTerminado) {
                List<Personaje> candsM1 = partida.obtenerCandidatos(humano, maquina1);
                List<Personaje> candsM2 = partida.obtenerCandidatos(humano, maquina2);

                System.out.println("\n╔══════════════════════════════╗");
                System.out.println("  TURNO HUMANO");
                System.out.println("  Opciones restantes en M1: " + candsM1.size());
                System.out.println("  Opciones restantes en M2: " + candsM2.size());
                System.out.println("╚══════════════════════════════╝");

                System.out.println("1. Preguntar a AMBAS máquinas (A la vez)");
                System.out.println("2. Adivinar personaje de Máquina 1");
                System.out.println("3. Adivinar personaje de Máquina 2");
                System.out.println("4. Ver candidatos restantes (Detalle)");
                System.out.println("5. Ver historial público");

                int opcion = leerEntero(scanner, "Opción");
                switch (opcion) {
                    case 1: {
                        Pregunta pregunta = elegirPregunta(scanner);
                        Respuesta r1 = partida.responderPregunta(humano, maquina1, pregunta);
                        Respuesta r2 = partida.responderPregunta(humano, maquina2, pregunta);

                        System.out.println("\n=== RESULTADOS DE TU PREGUNTA ===");
                        System.out.println(">> Máquina 1 responde: " + (r1.isAfirmativa() ? "SÍ" : "NO") + " (Le quedan " + partida.obtenerCandidatos(humano, maquina1).size() + " candidatos)");
                        System.out.println(">> Máquina 2 responde: " + (r2.isAfirmativa() ? "SÍ" : "NO") + " (Le quedan " + partida.obtenerCandidatos(humano, maquina2).size() + " candidatos)");
                        turnoHumanoTerminado = true;
                        break;
                    }
                    case 2: {
                        int idAdivina = leerEntero(scanner, "\nID que suponés para M1");
                        Personaje supuesto = catalogo.buscarPorId(idAdivina);
                        if (partida.intentarSuposicion(humano, maquina1, supuesto)) {
                            System.out.println("¡Adivinaste el personaje de Máquina 1! Ganaste.");
                            return true;
                        }
                        System.out.println("Incorrecto. " + supuesto.getNombre() + " queda descartado para M1.");
                        List<Personaje> restantesM1 = partida.obtenerCandidatos(humano, maquina1);
                        if (restantesM1.size() == 1) {
                            System.out.println("→ El personaje secreto de M1 era: " + restantesM1.get(0).getNombre());
                        }
                        turnoHumanoTerminado = true;
                        break;
                    }
                    case 3: {
                        int idAdivina = leerEntero(scanner, "\nID que suponés para M2");
                        Personaje supuesto = catalogo.buscarPorId(idAdivina);
                        if (partida.intentarSuposicion(humano, maquina2, supuesto)) {
                            System.out.println("¡Adivinaste el personaje de Máquina 2! Ganaste.");
                            return true;
                        }
                        System.out.println("Incorrecto. " + supuesto.getNombre() + " queda descartado para M2.");
                        List<Personaje> restantesM2 = partida.obtenerCandidatos(humano, maquina2);
                        if (restantesM2.size() == 1) {
                            System.out.println("→ El personaje secreto de M2 era: " + restantesM2.get(0).getNombre());
                        }
                        turnoHumanoTerminado = true;
                        break;
                    }
                    case 4:
                        System.out.println("\n=== CANDIDATOS VÁLIDOS PARA M1 (" + candsM1.size() + ") ===");
                        for (Personaje p : candsM1) { System.out.println("  " + p.toString()); }

                        System.out.println("\n=== CANDIDATOS VÁLIDOS PARA M2 (" + candsM2.size() + ") ===");
                        for (Personaje p : candsM2) { System.out.println("  " + p.toString()); }
                        break;
                    case 5:
                        System.out.println("\n=== HISTORIAL PÚBLICO ===");
                        for (Partida.RegistroPublico reg : partida.getHistorialPublico()) {
                            System.out.println("  " + reg.getEmisor() + " → " + reg.getReceptor()
                                    + ": " + reg.getPregunta() + " = " + (reg.isRespuesta() ? "Sí" : "No"));
                        }
                        break;
                    default:
                        System.out.println("Opción inválida.");
                        break;
                }
            }

            if (partida.estaTerminada()) break;

            List<Partida.RegistroPublico> histHumanoM1 = new ArrayList<>();
            for (Partida.RegistroPublico reg : partida.getHistorialPublico()) {
                if (reg.getEmisor().equals(maquina1.getNombre()) && reg.getReceptor().equals(humano.getNombre())) {
                    histHumanoM1.add(reg);
                }
            }

            System.out.println("\n┌── TURNO MÁQUINA 1 [D&C] ──────────────────────┐");
            List<Personaje> candidatosHumanoM1 = partida.obtenerCandidatos(maquina1, humano);
            System.out.println("  M1 está analizando a " + candidatosHumanoM1.size() + " candidatos tuyos.");

            DecisionMaquina decisionM1 = maquina1.decidir(candidatosHumanoM1, histHumanoM1, humano);
            System.out.println("  Motivo: " + decisionM1.getMotivo());
            if (decisionM1.isRealizarSuposicion()) {
                System.out.println("  Suposición: " + decisionM1.getPersonajeObjetivo().getNombre());
                if (partida.intentarSuposicion(maquina1, humano, decisionM1.getPersonajeObjetivo())) {
                    System.out.println("  ✗ ¡Máquina 1 adivinó correctamente!");
                    System.out.println("  → Tu personaje era: " + partida.getSecreto(humano).getNombre());
                    System.out.println("  Perdiste.");
                    return false;
                }
                System.out.println("  ✗ Suposición incorrecta.");
                List<Personaje> restantesM1 = partida.obtenerCandidatos(maquina1, humano);
                if (restantesM1.size() == 1) {
                    System.out.println("  → Tu personaje secreto es: " + restantesM1.get(0).getNombre());
                }
            } else {
                System.out.println("  Pregunta: " + decisionM1.getPregunta());
                Respuesta r = partida.responderPregunta(maquina1, humano, decisionM1.getPregunta());
                System.out.println("  Respuesta: " + (r.isAfirmativa() ? "Sí" : "No")
                        + " | Candidatos: " + r.getCandidatosAntes() + " → " + r.getCandidatosDespues());
            }
            System.out.println("└───────────────────────────────────────────────┘");

            if (partida.estaTerminada()) break;

            List<Partida.RegistroPublico> histHumanoM2 = new ArrayList<>();
            for (Partida.RegistroPublico reg : partida.getHistorialPublico()) {
                if (reg.getEmisor().equals(maquina2.getNombre()) && reg.getReceptor().equals(humano.getNombre())) {
                    histHumanoM2.add(reg);
                }
            }

            System.out.println("\n┌── TURNO MÁQUINA 2 [Greedy] ───────────────────┐");
            List<Personaje> candidatosHumanoM2 = partida.obtenerCandidatos(maquina2, humano);
            System.out.println("  M2 está analizando a " + candidatosHumanoM2.size() + " candidatos tuyos.");

            DecisionMaquina decisionM2 = maquina2.decidir(candidatosHumanoM2, histHumanoM2, humano);
            System.out.println("  Motivo: " + decisionM2.getMotivo());
            if (decisionM2.isRealizarSuposicion()) {
                System.out.println("  Suposición: " + decisionM2.getPersonajeObjetivo().getNombre());
                if (partida.intentarSuposicion(maquina2, humano, decisionM2.getPersonajeObjetivo())) {
                    System.out.println("  ✗ ¡Máquina 2 adivinó correctamente!");
                    System.out.println("  → Tu personaje era: " + partida.getSecreto(humano).getNombre());
                    System.out.println("  Perdiste.");
                    return false;
                }
                System.out.println("  ✗ Suposición incorrecta.");
                List<Personaje> restantesM2 = partida.obtenerCandidatos(maquina2, humano);
                if (restantesM2.size() == 1) {
                    System.out.println("  → Tu personaje secreto es: " + restantesM2.get(0).getNombre());
                }
            } else {
                System.out.println("  Pregunta: " + decisionM2.getPregunta());
                Respuesta r = partida.responderPregunta(maquina2, humano, decisionM2.getPregunta());
                System.out.println("  Respuesta: " + (r.isAfirmativa() ? "Sí" : "No")
                        + " | Candidatos: " + r.getCandidatosAntes() + " → " + r.getCandidatosDespues());
            }
            System.out.println("└───────────────────────────────────────────────┘");
        }

        return false;
    }

    public void jugarMaquinaVsMaquina() {
        JugadorMaquina maquina1 = new JugadorMaquina("Máquina 1", new EstrategiaGreedy());
        JugadorMaquina maquina2 = new JugadorMaquina("Máquina 2", new com.tpv1.algoritmos.EstrategiaDivideYConquista());
        Partida partida = crearPartidaMaquinaVsMaquina(maquina1, maquina2);

        int turno = 1;
        while (!partida.estaTerminada()) {
            System.out.println("\n=====================================");
            System.out.println("TURNO - " + turno);
            System.out.println("=====================================");
            jugarTurnoMaquina(partida, maquina1, maquina2);
            if (partida.estaTerminada()) {
                break;
            }
            jugarTurnoMaquina(partida, maquina2, maquina1);
            turno++;
        }
        System.out.println("Partida finalizada. Ganador: " + partida.getGanador().getNombre());
    }

    private void jugarTurnoMaquina(Partida partida, JugadorMaquina maquinaActual, JugadorMaquina rival) {
        System.out.println("Máquina actual: " + maquinaActual.getNombre() + " | Estrategia: " + maquinaActual.getEstrategia().nombreEstrategia());

        List<Personaje> candidatos = partida.obtenerCandidatos(maquinaActual, rival);
        System.out.println("Candidatos actuales (" + candidatos.size() + "):");
        for (Personaje p : candidatos) {
            System.out.println("  " + p.toString());
        }

        List<Partida.RegistroPublico> histPropio = new ArrayList<>();
        for (Partida.RegistroPublico reg : partida.getHistorialPublico()) {
            if (reg.getReceptor().equals(rival.getNombre())) {
                histPropio.add(reg);
            }
        }

        DecisionMaquina decision = maquinaActual.decidir(candidatos, histPropio, rival);
        System.out.println("Motivo: " + decision.getMotivo());

        if (decision.isRealizarSuposicion()) {
            System.out.println("Suposición: " + decision.getPersonajeObjetivo().getNombre());
            boolean acierto = partida.intentarSuposicion(maquinaActual, rival, decision.getPersonajeObjetivo());
            if (acierto) {
                System.out.println("La máquina " + maquinaActual.getNombre() + " ganó la partida.");
            }
        } else {
            System.out.println("Pregunta elegida: " + decision.getPregunta());
            System.out.println("Alternativas evaluadas:");
            for (int i = 0; i < decision.getAlternativas().size() && i < 4; i++) {
                System.out.println("  - " + decision.getAlternativas().get(i));
            }
            Respuesta respuesta = partida.responderPregunta(maquinaActual, rival, decision.getPregunta());
            System.out.println("Respuesta del rival: " + (respuesta.isAfirmativa() ? "Sí" : "No"));
            System.out.println("Candidatos antes: " + respuesta.getCandidatosAntes() + " | después: " + respuesta.getCandidatosDespues());
        }
    }

    private Pregunta elegirPregunta(Scanner scanner) {
        System.out.println("\nSeleccione la pregunta que desea realizar:");
        List<Pregunta> preguntas = servicioFiltrado.obtenerPreguntasDisponibles();
        for (int i = 0; i < preguntas.size(); i++) {
            System.out.println((i + 1) + ". " + preguntas.get(i));
        }
        int opcion = leerEntero(scanner, "Opcion");
        if (opcion < 1 || opcion > preguntas.size()) {
            throw new IllegalArgumentException("Pregunta inválida");
        }
        return preguntas.get(opcion - 1);
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        System.out.print(mensaje + ": ");
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Debe ser un número entero.");
            scanner.next();
            System.out.print(mensaje + ": ");
        }
        return scanner.nextInt();
    }
}