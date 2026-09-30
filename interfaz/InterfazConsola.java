package com.tpv1.interfaz;

import com.tpv1.catalogo.CatalogoPersonajes;
import com.tpv1.dominio.Personaje;
import com.tpv1.persistencia.RepositorioMarcador;
import com.tpv1.servicios.MotorPartida;
import com.tpv1.servicios.ServicioMarcador;

import java.util.Map;
import java.util.Scanner;

public class InterfazConsola {
    private final Scanner scanner = new Scanner(System.in);
    private final RepositorioMarcador repositorioMarcador = new RepositorioMarcador("data/marcador.csv");
    private final ServicioMarcador servicioMarcador = new ServicioMarcador(repositorioMarcador);

    public void iniciar() {
        while (true) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione una opción");
            if (opcion == -1) {
                System.out.println("Gracias por jugar.");
                return;
            }
            switch (opcion) {
                case 1:
                    jugarHumanoVsMaquina();
                    break;
                case 2:
                    jugarHumanoVsMaquinas();
                    break;
                case 3:
                    jugarMaquinaVsMaquina();
                    break;
                case 4:
                    mostrarMarcador();
                    break;
                case 5:
                    System.out.println("Gracias por jugar.");
                    return;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n=====================================");
        System.out.println("  Adivina Quién - TP de Java");
        System.out.println("=====================================");
        System.out.println("1. Humano vs Máquina      (Greedy)");
        System.out.println("2. Humano vs 2 Máquinas   (D&C + Greedy)");
        System.out.println("3. Máquina vs Máquina");
        System.out.println("4. Ver marcador");
        System.out.println("5. Salir");
    }

    private void jugarHumanoVsMaquina() {
        System.out.print("Ingrese su nombre: ");
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("No hay más entrada disponible. Saliendo del juego.");
            return;
        }
        String nombre = scanner.nextLine();
        MotorPartida motor = new MotorPartida(CatalogoPersonajes.crearListaBase());
        boolean ganoHumano = motor.jugarHumanoVsMaquina(scanner, nombre);
        if (ganoHumano) {
            servicioMarcador.registrarVictoria(nombre);
            System.out.println("Registro guardado en el marcador persistente.");
        }
    }

    private void jugarHumanoVsMaquinas() {
        System.out.print("Ingrese su nombre: ");
        if (!scanner.hasNextLine()) {
            System.out.println("No hay entrada disponible.");
            return;
        }
        String nombre = scanner.nextLine();
        
        // Mostrar sub-menú de modos de juego
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("  SELECCIONA EL MODO DE JUEGO");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. Humano vs Duo (adivina 1 de 2)");
        System.out.println("2. Cualquier Rival (adivina 2 de 2)");
        int modoOpcion = leerEntero("Opción");
        
        String modo = (modoOpcion == 2) ? "CUALQUIER_RIVAL" : "HUMANO_VS_DUO";
        
        MotorPartida motor = new MotorPartida(CatalogoPersonajes.crearListaBase());
        boolean ganoHumano = motor.jugarHumanoVsMaquinas(scanner, nombre, modo);
        if (ganoHumano) {
            servicioMarcador.registrarVictoria(nombre);
            System.out.println("Victoria registrada en el marcador.");
        }
    }

    private void jugarMaquinaVsMaquina() {
        MotorPartida motor = new MotorPartida(CatalogoPersonajes.crearListaBase());
        motor.jugarMaquinaVsMaquina();
    }

    private void mostrarMarcador() {
        Map<String, Integer> marcador = servicioMarcador.obtenerTodos();
        if (marcador.isEmpty()) {
            System.out.println("Todavía no hay partidas ganadas registradas.");
            return;
        }
        System.out.println("=== Marcador ===");
        for (Map.Entry<String, Integer> entrada : marcador.entrySet()) {
            System.out.println(entrada.getKey() + " -> " + entrada.getValue() + " victorias");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            if (!scanner.hasNextLine()) {
                return -1;
            }
            String entrada = scanner.nextLine();
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
}
