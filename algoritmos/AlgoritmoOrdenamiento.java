package com.tpv1.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementación de MergeSort para ordenamiento de listas genéricas.
 * Tiempo: O(n log n) en mejor, peor y promedio.
 * Espacio: O(n) para arrays auxiliares.
 * Estabilidad: Sí, mantiene el orden relativo de elementos iguales.
 */
public class AlgoritmoOrdenamiento {

    /**
     * Ordena una lista usando MergeSort con comparador personalizado.
     * 
     * @param lista Lista a ordenar
     * @param comparador Comparador para determinar el orden
     * @param <T> Tipo de elementos en la lista
     * @return La misma lista ordenada in-place
     */
    public static <T> List<T> mergeSort(List<T> lista, Comparator<T> comparador) {
        if (lista.size() <= 1) {
            return lista;
        }
        
        List<T> resultado = new ArrayList<>(lista);
        mergeSortRecursivo(resultado, 0, resultado.size() - 1, comparador);
        return resultado;
    }

    /**
     * Implementación recursiva de MergeSort.
     */
    private static <T> void mergeSortRecursivo(List<T> lista, int izq, int der, Comparator<T> comparador) {
        if (izq < der) {
            int mid = (izq + der) / 2;
            
            // Ordenar mitad izquierda
            mergeSortRecursivo(lista, izq, mid, comparador);
            
            // Ordenar mitad derecha
            mergeSortRecursivo(lista, mid + 1, der, comparador);
            
            // Fusionar las dos mitades
            fusionar(lista, izq, mid, der, comparador);
        }
    }

    /**
     * Fusiona dos sublistas ordenadas en una sola lista ordenada.
     */
    private static <T> void fusionar(List<T> lista, int izq, int mid, int der, Comparator<T> comparador) {
        List<T> izquierda = new ArrayList<>(lista.subList(izq, mid + 1));
        List<T> derecha = new ArrayList<>(lista.subList(mid + 1, der + 1));

        int i = 0, j = 0, k = izq;

        while (i < izquierda.size() && j < derecha.size()) {
            if (comparador.compare(izquierda.get(i), derecha.get(j)) <= 0) {
                lista.set(k++, izquierda.get(i++));
            } else {
                lista.set(k++, derecha.get(j++));
            }
        }

        while (i < izquierda.size()) {
            lista.set(k++, izquierda.get(i++));
        }

        while (j < derecha.size()) {
            lista.set(k++, derecha.get(j++));
        }
    }
}
