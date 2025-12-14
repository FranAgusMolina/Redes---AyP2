package red.logica;

import red.modelo.Conexion;
import red.modelo.Equipo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que almacena los datos de equipos y conexiones para operaciones de la interfaz de usuario.
 * Actúa como una capa de acceso a datos simplificada para consultas de la UI.
 */
public class Red
{
    HashMap<String, Equipo> equipos;
    List<Conexion> conexiones;

    /**
     * Constructor que inicializa la red con los equipos y conexiones proporcionados.
     *
     * @param equipos Mapa de equipos indexado por dirección IP.
     * @param conexiones Lista de conexiones entre equipos.
     * Complejidad Temporal: O(1).
     */
    public Red(HashMap<String, Equipo> equipos, List<Conexion> conexiones) {
        this.equipos = equipos;
        this.conexiones = conexiones;
    }

    public List<Conexion> getConexiones() {
        return conexiones;
    }
    public void setConexiones(List<Conexion> conexiones) {
        this.conexiones = conexiones;
    }
    public HashMap<String, Equipo> getEquipos() {
        return equipos;
    }
    public void setEquipos(HashMap<String, Equipo> equipos) {
        this.equipos = equipos;
    }

    /**
     * Filtra y retorna un mapa conteniendo únicamente los equipos que están activos (status = true).
     *
     * @return HashMap con los equipos activos indexados por dirección IP.
     * Complejidad Temporal: O(V), donde V es el número de equipos en el mapa.
     */
    public HashMap<String, Equipo> getEquiposEncendidos()
    {
        HashMap<String, Equipo> equiposEncendidos = new HashMap<>();
        for(Map.Entry<String, Equipo> e: equipos.entrySet())
        {
            if(e.getValue().isStatus())
            {
                equiposEncendidos.put(e.getKey(), e.getValue());
            }
        }

        return equiposEncendidos;
    }
}
