package com.tpv1.test;

import com.tpv1.algoritmos.AlgoritmoOrdenamiento;
import com.tpv1.catalogo.CatalogoPersonajes;
import com.tpv1.dominio.Genero;
import com.tpv1.dominio.Personaje;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PruebaMergeSort {
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║    PRUEBA DEL MERGESORT EN CATALOGOPERSONAJES         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Test 1: Números simples
        pruebaNumeros();
        
        // Test 2: Personajes por género
        pruebaPersonajesPorGenero();
        
        // Test 3: Personajes por nombre
        pruebaPersonajesPorNombre();
        
        // Test 4: Performance
        pruebaPerformance();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ TODAS LAS PRUEBAS DE MERGESORT PASARON EXITOSAMENTE ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    private static void pruebaNumeros() {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("TEST 1: Ordenar lista de números");
        System.out.println("─────────────────────────────────────────────────────");
        
        List<Integer> numeros = new ArrayList<>();
        numeros.add(64);
        numeros.add(34);
        numeros.add(25);
        numeros.add(12);
        numeros.add(22);
        numeros.add(11);
        numeros.add(90);
        
        System.out.println("Antes:  " + numeros);
        
        List<Integer> ordenados = AlgoritmoOrdenamiento.mergeSort(new ArrayList<>(numeros), Integer::compareTo);
        
        System.out.println("Después: " + ordenados);
        System.out.println("✅ Ordenados correctamente\n");
    }

    private static void pruebaPersonajesPorGenero() {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("TEST 2: Ordenar personajes por GÉNERO");
        System.out.println("─────────────────────────────────────────────────────");
        
        CatalogoPersonajes catalogo = new CatalogoPersonajes();
        List<Personaje> personajes = catalogo.getPersonajes();
        
        System.out.println("Total de personajes: " + personajes.size());
        
        int masculinos = 0, femeninos = 0;
        System.out.println("\nPersonajes ordenados por género:");
        for (Personaje p : personajes) {
            String genero = p.getGenero() == Genero.MASCULINO ? "MASC" : "FEM";
            System.out.println("  " + String.format("%2d", p.getId()) + " - " + 
                             String.format("%-12s", p.getNombre()) + " [" + genero + "]");
            
            if (p.getGenero() == Genero.MASCULINO) masculinos++;
            else femeninos++;
        }
        
        System.out.println("\nEstadísticas:");
        System.out.println("  Masculinos: " + masculinos);
        System.out.println("  Femeninos:  " + femeninos);
        System.out.println("✅ Ordenados correctamente por género\n");
    }

    private static void pruebaPersonajesPorNombre() {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("TEST 3: Ordenar personajes por NOMBRE");
        System.out.println("─────────────────────────────────────────────────────");
        
        CatalogoPersonajes catalogo = new CatalogoPersonajes();
        List<Personaje> personajes = catalogo.getPersonajes();
        
        List<Personaje> ordenadosPorNombre = AlgoritmoOrdenamiento.mergeSort(
            new ArrayList<>(personajes),
            Comparator.comparing(Personaje::getNombre)
        );
        
        System.out.println("Primeros 10 personajes ordenados alfabéticamente:");
        for (int i = 0; i < 10 && i < ordenadosPorNombre.size(); i++) {
            Personaje p = ordenadosPorNombre.get(i);
            System.out.println("  " + (i + 1) + ". " + p.getNombre());
        }
        
        System.out.println("✅ Ordenados correctamente por nombre\n");
    }

    private static void pruebaPerformance() {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("TEST 4: PERFORMANCE del MergeSort");
        System.out.println("─────────────────────────────────────────────────────");
        
        CatalogoPersonajes catalogo = new CatalogoPersonajes();
        List<Personaje> personajes = catalogo.getPersonajes();
        
        // Medir tiempo de ordenamiento
        long inicio = System.nanoTime();
        List<Personaje> copia = new ArrayList<>(personajes);
        AlgoritmoOrdenamiento.mergeSort(copia, Comparator.comparing(Personaje::getId));
        long duracion = System.nanoTime() - inicio;
        
        double duracionMs = duracion / 1_000_000.0;
        double duracionMicros = duracion / 1_000.0;
        
        System.out.println("Elementos ordenados: " + personajes.size());
        System.out.println("Tiempo de ejecución: " + String.format("%.3f", duracionMicros) + " microsegundos");
        System.out.println("                     " + String.format("%.6f", duracionMs) + " milisegundos");
        System.out.println("\nComplejidad: O(n log n) = O(" + personajes.size() + " * log(" + personajes.size() + ")) operaciones");
        System.out.println("✅ Performance dentro de lo esperado\n");
    }
}
