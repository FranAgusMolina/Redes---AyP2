package red.interfaz.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import red.modelo.Equipo;

/**
 * Clase utilitaria para operaciones comunes de la interfaz de usuario.
 * Proporciona métodos estáticos para mostrar diálogos y selector de equipos.
 */
public class utilUI {

    /**
     * Constructor por defecto.
     *
     * Complejidad Temporal: O(1).
     */
    public utilUI(){
    }

    /**
     * Muestra un ComboBox buscable con los equipos disponibles para que el usuario seleccione uno.
     * El combo box permite escribir para filtrar/buscar equipos.
     *
     * @param titulo Título de la ventana de diálogo.
     * @param mensaje Mensaje descriptivo para el usuario.
     * @param equipos Mapa de equipos disponibles (Clave: IP, Valor: Objeto Equipo).
     * @return La dirección IP seleccionada (string limpio) o null si el usuario cancela.
     * Complejidad Temporal: O(V log V), donde V es el número de equipos (dominado por el ordenamiento de la lista).
     */
    public static String seleccionarIP(String titulo, String mensaje, Map<String, Equipo> equipos) {

        if (equipos == null || equipos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay equipos disponibles.");
            return null;
        }

        List<String> opciones = new ArrayList<>();
        for (Equipo e : equipos.values()) {
            String etiqueta = e.getId() + " - " + e.getIpAddress();
            opciones.add(etiqueta);
        }

        Collections.sort(opciones);

        String[] arrayOpciones = opciones.toArray(new String[0]);
        JComboBox<String> comboBox = new JComboBox<>(arrayOpciones);
        comboBox.setEditable(true);

        int result = JOptionPane.showConfirmDialog(null, comboBox, mensaje,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) comboBox.getSelectedItem();
            if (seleccion != null && !seleccion.isEmpty()) {
                if (seleccion.contains(" - ")) {
                    return seleccion.split(" - ")[1].trim();
                }
                return seleccion.trim();
            }
        }

        return null;
    }

    public static void mostrarError(String mensaje){
        JOptionPane.showMessageDialog(null, mensaje, "Error",JOptionPane.ERROR_MESSAGE);
    }

}