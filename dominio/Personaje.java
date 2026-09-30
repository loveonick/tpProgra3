package com.tpv1.dominio;

import java.util.Objects;

public class Personaje {
    private final int id;
    private final String nombre;
    private final Genero genero;
    private final boolean calvo;
    private final boolean usaLentes;
    private final ColorPelo colorPelo;

    public Personaje(int id, String nombre, Genero genero, boolean calvo, boolean usaLentes, ColorPelo colorPelo) {
        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
        this.calvo = calvo;
        this.usaLentes = usaLentes;
        this.colorPelo = colorPelo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Genero getGenero() {
        return genero;
    }

    public boolean isCalvo() {
        return calvo;
    }

    public boolean isUsaLentes() {
        return usaLentes;
    }

    public ColorPelo getColorPelo() {
        return colorPelo;
    }

    public boolean tieneColorPelo(ColorPelo color) {
        return this.colorPelo == color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personaje personaje = (Personaje) o;
        return id == personaje.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "#" + id + " - " + nombre + " | genero=" + genero + " | calvo=" + calvo + " | lentes=" + usaLentes + " | colorPelo=" + colorPelo;
    }
}
