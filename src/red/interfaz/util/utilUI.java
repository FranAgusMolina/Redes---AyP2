package red.interfaz.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import red.modelo.Equipo; // Importante para poder usar el objeto Equipo

public class utilUI {

    public utilUI(){
    }

    /**
     * Método genérico para mostrar un ComboBox buscable a partir de un mapa de Equipos.
     * @param titulo Título de la ventana
     * @param mensaje Mensaje para el usuario
     * @param equipos Mapa de equipos (Clave: ID, Valor: Objeto Equipo)
     * @return La IP seleccionada (string limpio) o null si cancela.
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
        comboBox.setEditable(true); // Permite escribir para filtrar/buscar

        int result = JOptionPane.showConfirmDialog(null, comboBox, mensaje,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) comboBox.getSelectedItem();
            if (seleccion != null && !seleccion.isEmpty()) {
                if (seleccion.contains(" - ")) {
                    return seleccion.split(" - ")[1].trim();
                }
                return seleccion.trim(); // Si el usuario escribió solo la IP a mano
            }
        }

        return null;
    }

    public static void mostrarError(String mensaje){
        JOptionPane.showMessageDialog(null, mensaje, "Error",JOptionPane.ERROR_MESSAGE);
    }

}