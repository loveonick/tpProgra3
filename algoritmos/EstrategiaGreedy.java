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

public class EstrategiaGreedy implements EstrategiaMaquina {
    private final ServicioFiltrado servicioFiltrado = new ServicioFiltrado();

    @Override
    public DecisionMaquina seleccionarAccion(List<Personaje> candidatos, List<Partida.RegistroPublico> historialPublico, Jugador rival) {
        if (candidatos == null || candidatos.isEmpty()) {
            throw new IllegalArgumentException("No hay candidatos disponibles para la máquina");
        }

        if (candidatos.size() == 1) {
            return new DecisionMaquina(true, null, candidatos.get(0),
                    "El conjunto tiene un único candidato: la mejor decisión local es suponer directamente.",
                    new ArrayList<>(), candidatos.size());
        }

        List<AnalisisPregunta> alternativas = new ArrayList<>();
        List<Pregunta> preguntas = servicioFiltrado.obtenerPreguntasDisponibles();
        for (Pregunta pregunta : preguntas) {
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

        if (alternativas.isEmpty()) {
            Personaje candidato = candidatos.get(0);
            return new DecisionMaquina(true, null, candidato,
                    "No quedan preguntas no repetidas. Se realiza una suposición directa por agotamiento de alternativas.",
                    new ArrayList<>(), candidatos.size());
        }

        alternativas.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        AnalisisPregunta seleccionada = alternativas.get(0);
        String motivo = "Greedy: evaluación local del beneficio inmediato. La pregunta seleccionada presenta la partición más equilibrada (Sí=" +
                seleccionada.getSi() + ", No=" + seleccionada.getNo() + ") porque minimiza el peor caso y maximiza la reducción inmediata del conjunto.";

        return new DecisionMaquina(false, seleccionada.getPregunta(), null, motivo, alternativas, candidatos.size());
    }

    @Override
    public String nombreEstrategia() {
        return "Greedy";
    }
}
