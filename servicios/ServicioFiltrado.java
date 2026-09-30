package com.tpv1.servicios;

import com.tpv1.dominio.AnalisisPregunta;
import com.tpv1.dominio.ColorPelo;
import com.tpv1.dominio.Filtro;
import com.tpv1.dominio.Genero;
import com.tpv1.dominio.Personaje;
import com.tpv1.dominio.Pregunta;
import com.tpv1.dominio.TipoFiltro;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ServicioFiltrado {
    public List<Personaje> filtrar(List<Personaje> candidatos, Pregunta pregunta, boolean respuesta) {
        if (candidatos == null || candidatos.isEmpty()) {
            return new ArrayList<>();
        }
        List<Personaje> resultado = new ArrayList<>();
        for (Personaje personaje : candidatos) {
            boolean cumple = pregunta.aplicaA(personaje);
            if (cumple == respuesta) {
                resultado.add(personaje);
            }
        }
        return resultado;
    }

    public List<Pregunta> obtenerPreguntasDisponibles() {
        List<Pregunta> preguntas = new ArrayList<>();
        preguntas.add(new Pregunta(TipoFiltro.GENERO, Genero.MASCULINO));
        preguntas.add(new Pregunta(TipoFiltro.GENERO, Genero.FEMENINO));

        // Se deja únicamente la variante "true" para las preguntas booleanas
        // Ya que la respuesta "No" de la máquina automáticamente filtra el caso "false"
        preguntas.add(new Pregunta(TipoFiltro.CALVICIE, true));
        preguntas.add(new Pregunta(TipoFiltro.LENTES, true));

        preguntas.add(new Pregunta(TipoFiltro.COLOR_PELO, ColorPelo.COLORADO));
        preguntas.add(new Pregunta(TipoFiltro.COLOR_PELO, ColorPelo.NEGRO));
        preguntas.add(new Pregunta(TipoFiltro.COLOR_PELO, ColorPelo.AMARILLO));
        return preguntas;
    }

    public List<AnalisisPregunta> evaluarPreguntas(List<Personaje> candidatos, List<Pregunta> preguntas) {
        List<AnalisisPregunta> analisis = new ArrayList<>();
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
            analisis.add(new AnalisisPregunta(pregunta, si, no, score));
        }
        analisis.sort(Comparator.comparingDouble(AnalisisPregunta::getScore).reversed());
        return analisis;
    }

    public AnalisisPregunta mejorPregunta(List<Personaje> candidatos) {
        List<AnalisisPregunta> analisis = evaluarPreguntas(candidatos, obtenerPreguntasDisponibles());
        return analisis.isEmpty() ? null : analisis.get(0);
    }

    public boolean preguntaYaRealizada(List<Pregunta> preguntadasAntes, Pregunta pregunta) {
        for (Pregunta preguntada : preguntadasAntes) {
            if (preguntada.getFiltro().getTipo() == pregunta.getFiltro().getTipo() &&
                    String.valueOf(preguntada.getFiltro().getValor()).equals(String.valueOf(pregunta.getFiltro().getValor()))) {
                return true;
            }
        }
        return false;
    }

    public boolean preguntaSeRepiteEnHistorial(List<com.tpv1.dominio.Partida.RegistroPublico> historial, Pregunta pregunta) {
        if (historial == null) {
            return false;
        }
        for (com.tpv1.dominio.Partida.RegistroPublico registro : historial) {
            Pregunta preguntaHistorial = registro.getPregunta();
            if (preguntaHistorial.getFiltro().getTipo() == pregunta.getFiltro().getTipo() &&
                    String.valueOf(preguntaHistorial.getFiltro().getValor()).equals(String.valueOf(pregunta.getFiltro().getValor()))) {
                return true;
            }
        }
        return false;
    }
}