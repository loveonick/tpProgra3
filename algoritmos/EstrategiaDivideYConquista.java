package com.tpv1.algoritmos;

import com.tpv1.dominio.AnalisisPregunta;
import com.tpv1.dominio.DecisionMaquina;
import com.tpv1.dominio.Jugador;
import com.tpv1.dominio.Partida;
import com.tpv1.dominio.Personaje;
import com.tpv1.dominio.Pregunta;
import com.tpv1.servicios.ServicioFiltrado;

import java.util.ArrayList;
import java.util.List;

public class EstrategiaDivideYConquista implements EstrategiaMaquina {
    private final ServicioFiltrado servicioFiltrado = new ServicioFiltrado();

    @Override
    public DecisionMaquina seleccionarAccion(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico, Jugador rival) {
        if (candidatos == null || candidatos.isEmpty()) {
            throw new IllegalArgumentException("No hay candidatos disponibles para la máquina");
        }

        if (candidatos.size() == 1) {
            return new DecisionMaquina(true, null, candidatos.get(0),
                    "Dividir y conquistar: el subproblema ya quedó reducido a un único candidato y se sugiere directamente.",
                    new ArrayList<>(), candidatos.size());
        }

        AnalisisPregunta mejor = buscarMejorPreguntaRecursiva(candidatos, historialPublico, 0);
        if (mejor == null) {
            Personaje objetivo = candidatos.get(0);
            return new DecisionMaquina(true, null, objetivo,
                    "Se reutiliza la mejor alternativa ya explorada, dado que no quedan preguntas útiles.",
                    new ArrayList<>(), candidatos.size());
        }

        String motivo = "Divide y conquista:\n" +
                "  • El conjunto se divide en mitades\n" +
                "  • Se resuelve cada subproblema recursivamente\n" +
                "  • Se combina la mejor decisión del conjunto\n" +
                "  • Pregunta elegida es la mejor entre la solución local y las soluciones de los subproblemas.";
        return new DecisionMaquina(false, mejor.getPregunta(), null, motivo, obtenerAlternativas(candidatos, historialPublico), candidatos.size());
    }

    private AnalisisPregunta buscarMejorPreguntaRecursiva(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico, int profundidad) {
        if (candidatos.size() <= 1) {
            return null;
        }
        if (candidatos.size() <= 2) {
            return mejorPreguntaLocal(candidatos, historialPublico);
        }

        List<List<Personaje>> mitades = dividirEnMitades(candidatos);
        AnalisisPregunta izquierda = buscarMejorPreguntaRecursiva(mitades.get(0), historialPublico, profundidad + 1);
        AnalisisPregunta derecha = buscarMejorPreguntaRecursiva(mitades.get(1), historialPublico, profundidad + 1);
        AnalisisPregunta local = mejorPreguntaLocal(candidatos, historialPublico);

        AnalisisPregunta mejor = local;
        if (izquierda != null && (mejor == null || izquierda.getScore() > mejor.getScore())) {
            mejor = izquierda;
        }
        if (derecha != null && (mejor == null || derecha.getScore() > mejor.getScore())) {
            mejor = derecha;
        }
        return mejor;
    }

    private List<List<Personaje>> dividirEnMitades(List<Personaje> candidatos) {
        int mitad = candidatos.size() / 2;
        List<Personaje> izquierda = new ArrayList<>(candidatos.subList(0, mitad));
        List<Personaje> derecha = new ArrayList<>(candidatos.subList(mitad, candidatos.size()));
        List<List<Personaje>> resultado = new ArrayList<>();
        resultado.add(izquierda);
        resultado.add(derecha);
        return resultado;
    }

    private AnalisisPregunta mejorPreguntaLocal(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico) {
        List<Pregunta> preguntas = new ArrayList<>();
        for (Pregunta pregunta : servicioFiltrado.obtenerPreguntasDisponibles()) {
            if (!servicioFiltrado.preguntaSeRepiteEnHistorial(historialPublico, pregunta)) {
                preguntas.add(pregunta);
            }
        }
        if (preguntas.isEmpty()) {
            return null;
        }

        AnalisisPregunta mejor = null;
        for (Pregunta pregunta : preguntas) {
            int si = 0;
            int no = 0;
            for (Personaje personaje : candidatos) {
                if (pregunta.aplicaA(personaje)) {
                    si++;
                } else {
                    no++;
                }
            }
            double score = 1.0 / (Math.abs(si - no) + 1.0);
            AnalisisPregunta actual = new AnalisisPregunta(pregunta, si, no, score);
            if (mejor == null || actual.getScore() > mejor.getScore()) {
                mejor = actual;
            }
        }
        return mejor;
    }

    private List<AnalisisPregunta> obtenerAlternativas(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico) {
        List<AnalisisPregunta> alternativas = new ArrayList<>();
        for (Pregunta pregunta : servicioFiltrado.obtenerPreguntasDisponibles()) {
            if (servicioFiltrado.preguntaSeRepiteEnHistorial(historialPublico, pregunta)) {
                continue;
            }
            int si = 0;
            int no = 0;
            for (Personaje personaje : candidatos) {
                if (pregunta.aplicaA(personaje)) {
                    si++;
                } else {
                    no++;
                }
            }
            double score = 1.0 / (Math.abs(si - no) + 1.0);
            alternativas.add(new AnalisisPregunta(pregunta, si, no, score));
        }
        alternativas.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return alternativas;
    }

    @Override
    public String nombreEstrategia() {
        return "Divide y Conquista";
    }
}
