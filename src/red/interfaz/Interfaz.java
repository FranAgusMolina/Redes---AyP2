package red.interfaz;

import javax.swing.*;

import net.datastructures.Graph;
import net.datastructures.PositionalList;
import net.datastructures.Vertex;
import red.interfaz.util.utilUI;
import red.modelo.*;
import red.logica.*;
import java.util.*;

/**
 * Clase que gestiona la interfaz de usuario del sistema de redes.
 * Proporciona métodos estáticos para mostrar información y solicitar entrada del usuario mediante diálogos.
 */
public class Interfaz {
    
    /**
     * Muestra un menú de opciones al usuario para seleccionar operaciones del sistema.
     *
     * @return Índice de la opción seleccionada por el usuario.
     * Complejidad Temporal: O(1).
     */
    public static int opcion() {
        String[] options = { "Salir", "Árbol de expansión mínimo", "Traceroute", "Ping", "Mapa de Red", "MaxFlow" };
        return JOptionPane.showOptionDialog(
                null,
                "Seleccione una operación:",
                "Sistema de Redes",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    /**
     * Solicita al usuario que seleccione una dirección IP de un mapa de equipos.
     *
     * @param datos Mapa de equipos disponibles para seleccionar.
     * @return Dirección IP seleccionada o null si el usuario cancela.
     * Complejidad Temporal: O(V log V), donde V es el número de equipos (dominado por el ordenamiento).
     */
    public static String leerIP(HashMap<String, Equipo> datos) {
        return utilUI.seleccionarIP("Seleccione equipo al que hacerle ping", "Seleccione la direccion IP del equipo:", datos);
    }

    /**
     * Muestra el resultado de una operación de ping.
     * Indica si el equipo está activo o no.
     *
     * @param equipo Dirección IP del equipo verificado.
     * @param estado Estado del equipo (true = activo, false = inactivo).
     * Complejidad Temporal: O(1).
     */
    public static void ping(String equipo, boolean estado){
          if (estado) {
            JOptionPane.showMessageDialog(null, "El equipo con la direccion IP" + equipo + " Esta activo");
        }else {
             JOptionPane.showMessageDialog(null, "El equipo con IP " + equipo + " no está activo o no se encuentra en la red.");
        }
    }

    /**
     * Muestra el resultado del algoritmo traceroute entre dos equipos.
     * Presenta la ruta completa con todos los saltos intermedios.
     *
     * @param ipOrigen Dirección IP del equipo origen.
     * @param ipDestino Dirección IP del equipo destino.
     * @param camino Lista posicional de vértices que representan el camino.
     * Complejidad Temporal: O(H), donde H es el número de saltos en el camino.
     */
    public static void resultadoTraceroute(String ipOrigen, String ipDestino, PositionalList<Vertex<Equipo>> camino) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Traceroute %s -> %s\n\n", ipOrigen, ipDestino));
        sb.append(String.format("%-4s %-18s\n", "Hop", "IP"));
        sb.append("--------------------------------\n");

        int hop = 0;
        for (Vertex<Equipo> conexion : camino) {
            hop++;
            String ip = "(" + conexion.getElement().getId()+ ") " + conexion.getElement().getIpAddress();
            sb.append(String.format("%-4d %-18s\n", hop, ip));
        }

        if (hop == 0) {
            JOptionPane.showMessageDialog(null, "No se encontró ruta entre " + ipOrigen + " y " + ipDestino + ".");
            return;
        }

        sb.append("\nTotal de saltos: ").append(hop);

        JTextArea outputTextArea = new JTextArea(sb.toString());
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        outputTextArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(null, outputTextArea, "Traceroute", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Muestra las conexiones del Árbol de Expansión Mínima (MST) de la red.
     * Presenta una tabla formateada con origen, destino y latencia de cada arista.
     *
     * @param mst Lista de cadenas formateadas describiendo las conexiones del MST.
     * Complejidad Temporal: O(E), donde E es el número de aristas en el MST.
     */
    public static void MST(List<String> mst) {
        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        outputTextArea.append("Arbol de Expansión Mínimo\n");
        outputTextArea.append("----------------------------------------\n");
        outputTextArea.append(String.format("%-10s %-10s %-15s\n", "Origen", "Destino", "Latencia (ms)"));
        outputTextArea.append("----------------------------------------\n");

        int total = 0;
        for (String p : mst) {
            String[] partes = p.split(" <--> | \\[Latencia: | ms");
            if (partes.length >= 3) {
                outputTextArea.append(String.format("%-10s %-10s %-15s\n", partes[0], partes[1], partes[2]));
                total+= Integer.parseInt(partes[2]);
            }
        }

        outputTextArea.append("----------------------------------------\n");
        outputTextArea.append("Total: " + total);


        JOptionPane.showMessageDialog(null, outputTextArea, "Árbol de expansión mínimo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Abre una ventana gráfica interactiva que visualiza el grafo completo de la red.
     * Permite ver todos los equipos y conexiones con la posibilidad de arrastrar nodos.
     *
     * @param grafo Grafo de la red a visualizar.
     * Complejidad Temporal: O(V + E), para renderizar todos los vértices y aristas.
     */
    public static void mostrarGrafo(Graph<Equipo, Conexion> grafo) {
        JDialog ventana = new JDialog((java.awt.Frame) null, "Visualización de la Red", true);
        ventana.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PanelRed panel = new PanelRed(grafo);
        ventana.add(panel);

        ventana.setSize(1280, 800);
        ventana.setLocationRelativeTo(null);

        ventana.setVisible(true);
    }

    /**
     * Muestra un mensaje de despedida y finaliza la aplicación.
     *
     * Complejidad Temporal: O(1).
     */
    public static void salir() {
    	JOptionPane.showMessageDialog(null, "Saliendo");
    	System.exit( 0 );
    }
    
    /**
     * Muestra un mensaje de error cuando el usuario ingresa una opción inválida.
     *
     * Complejidad Temporal: O(1).
     */
    public static void invalido() {
    	JOptionPane.showMessageDialog(null, "Opcion invalida");
    }

    /**
     * Muestra un mensaje de error personalizado al usuario.
     *
     * @param mensaje Texto del mensaje de error a mostrar.
     * Complejidad Temporal: O(1).
     */
    public static void mostrarError(String mensaje){
        utilUI.mostrarError(mensaje);
    }

    /**
     * Muestra el resultado del cálculo de flujo máximo entre dos equipos.
     * Presenta el origen, destino y la capacidad máxima de transferencia.
     *
     * @param ipOrigen Dirección IP del equipo origen.
     * @param ipDestino Dirección IP del equipo destino.
     * @param flujo Valor del flujo máximo calculado en Mbps.
     * Complejidad Temporal: O(1).
     */
    public static void mostrarFlujoMaximo(String ipOrigen, String ipDestino, int flujo) {
        String mensaje = String.format(
                """
                        Cálculo de Ancho de Banda Máximo (Max Flow)
                        
                        Origen: %s
                        Destino: %s
                        --------------------------------------------
                        Capacidad Máxima de Transferencia: %d Mbps""",
                ipOrigen, ipDestino, flujo
        );

        JOptionPane.showMessageDialog(null, mensaje, "Resultado Max Flow", JOptionPane.INFORMATION_MESSAGE);
    }
}
