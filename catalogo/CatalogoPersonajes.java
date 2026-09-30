package com.tpv1.catalogo;

import com.tpv1.algoritmos.AlgoritmoOrdenamiento;
import com.tpv1.dominio.ColorPelo;
import com.tpv1.dominio.Genero;
import com.tpv1.dominio.Personaje;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CatalogoPersonajes {
    private final List<Personaje> personajes = new ArrayList<>();

    public CatalogoPersonajes() {
        inicializarPersonajes();
    }

    public void inicializarPersonajes() {
        personajes.clear();
        List<Personaje> base = new ArrayList<>();
        base.add(new Personaje(0, "Matias", Genero.MASCULINO, false, false, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Leo", Genero.MASCULINO, true, true, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Nicolas", Genero.MASCULINO, false, false, ColorPelo.AMARILLO));
        base.add(new Personaje(0, "Julian", Genero.MASCULINO, true, false, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Gustavo", Genero.MASCULINO, false, true, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Dante", Genero.MASCULINO, false, false, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Ruben", Genero.MASCULINO, true, true, ColorPelo.AMARILLO));
        base.add(new Personaje(0, "Santiago", Genero.MASCULINO, false, true, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Martin", Genero.MASCULINO, true, false, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Diego", Genero.MASCULINO, false, false, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Luciano", Genero.MASCULINO, true, false, ColorPelo.NEGRO));

        base.add(new Personaje(0, "Cecilia", Genero.FEMENINO, false, false, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Marta", Genero.FEMENINO, true, true, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Lucia", Genero.FEMENINO, false, false, ColorPelo.AMARILLO));
        base.add(new Personaje(0, "Analia", Genero.FEMENINO, true, false, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Rosa", Genero.FEMENINO, false, true, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Carla", Genero.FEMENINO, false, false, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Laura", Genero.FEMENINO, true, true, ColorPelo.AMARILLO));
        base.add(new Personaje(0, "Paula", Genero.FEMENINO, false, true, ColorPelo.NEGRO));
        base.add(new Personaje(0, "Veronica", Genero.FEMENINO, true, false, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Melina", Genero.FEMENINO, false, false, ColorPelo.COLORADO));
        base.add(new Personaje(0, "Noelia", Genero.FEMENINO, true, false, ColorPelo.AMARILLO));
        base.add(new Personaje(0, "Valeria", Genero.FEMENINO, false, false, ColorPelo.NEGRO));

        // Ordenamiento con MergeSort propio (O(n log n) - estable y predecible)
        AlgoritmoOrdenamiento.mergeSort(base, Comparator.comparing(Personaje::getGenero));
        int idActual = 1;
        for (Personaje personaje : base) {
            personajes.add(new Personaje(idActual++, personaje.getNombre(), personaje.getGenero(), personaje.isCalvo(), personaje.isUsaLentes(), personaje.getColorPelo()));
        }
    }

    public List<Personaje> getPersonajes() {
        return new ArrayList<>(personajes);
    }

    public Personaje buscarPorId(int id) {
        for (Personaje personaje : personajes) {
            if (personaje.getId() == id) {
                return personaje;
            }
        }
        throw new IllegalArgumentException("No existe un personaje con id " + id);
    }

    public static List<Personaje> crearListaBase() {
        return new CatalogoPersonajes().getPersonajes();
    }
}
