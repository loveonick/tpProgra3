package com.tpv1.dominio;

public class Filtro {
    private final TipoFiltro tipo;
    private final Object valor;

    public Filtro(TipoFiltro tipo, Object valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public TipoFiltro getTipo() {
        return tipo;
    }

    public Object getValor() {
        return valor;
    }

    public boolean aplicaA(Personaje personaje) {
        switch (tipo) {
            case GENERO:
                return personaje.getGenero() == (Genero) valor;
            case CALVICIE:
                return personaje.isCalvo() == (Boolean) valor;
            case LENTES:
                return personaje.isUsaLentes() == (Boolean) valor;
            case COLOR_PELO:
                return personaje.getColorPelo() == (ColorPelo) valor;
            default:
                throw new IllegalArgumentException("Tipo de filtro no soportado: " + tipo);
        }
    }

    public String descripcion() {
        switch (tipo) {
            case GENERO:
                return "¿Es " + valor + "?";
            case CALVICIE:
                return "¿Es calvo?";
            case LENTES:
                return "¿Usa lentes?";
            case COLOR_PELO:
                return "¿Tiene pelo " + valor + "?";
            default:
                return "Pregunta sin especificación";
        }
    }
}
