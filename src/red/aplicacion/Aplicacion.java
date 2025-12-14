package red.aplicacion;

import red.controlador.Cordinador;

/**
 * Clase principal de la aplicación de gestión de redes.
 * Punto de entrada del programa que inicializa el coordinador del sistema.
 */
public class Aplicacion {
    /**
     * Metodo principal que inicia la aplicación.
     * Crea una instancia del coordinador y ejecuta el metodo de inicio.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     * Complejidad Temporal: O(1) para la creación e inicio del coordinador.
     */
    public  static void main(String[] args){
        Cordinador app = new Cordinador();
        app.inicio();
    }
}
