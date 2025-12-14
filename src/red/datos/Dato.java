package red.datos;

import red.modelo.*;
import java.io.*;
import java.util.*;

/**
 * Clase encargada de la carga de datos de equipos y conexiones desde archivos.
 * Proporciona métodos estáticos para leer computadoras, routers y conexiones.
 */
public class Dato {

	/**
	 * Carga los routers desde un archivo de texto delimitado por punto y coma.
	 * Cada línea del archivo representa un router con sus atributos.
	 *
	 * @param archivoRouters Ruta de acceso al archivo de routers.
	 * @return HashMap con todos los routers asociados a su dirección IP como clave.
	 * @throws FileNotFoundException Si el archivo de routers no existe.
	 * Complejidad Temporal: O(R), donde R es el número de routers en el archivo.
	 */
    private static HashMap<String, Equipo> cargarRouters(String archivoRouters) throws FileNotFoundException {
		Scanner read;

		HashMap<String, Equipo> equipo = new HashMap<>();
		
		
			read = new Scanner(new File(archivoRouters));
			read.useDelimiter("\\s*;\\s*");
		
			String id, ipAddress, macAddress, status, ubicacion, modelo, firmware, throughput;
			
			while (read.hasNext()) {
				id = read.next();
				ipAddress = read.next();
                macAddress = read.next();
                status = read.next();
                ubicacion = read.next();
				modelo = read.next();
                firmware = read.next();
                throughput= read.next();
                equipo.put(ipAddress, new Router(id, ipAddress, macAddress, Boolean.parseBoolean(status), ubicacion, modelo, firmware, Integer.parseInt(throughput)));
                
			}
			read.close();
		
		return equipo;
	}
    /**
     * Carga las computadoras desde un archivo de texto delimitado por punto y coma.
     * Cada línea del archivo representa una computadora con sus atributos.
     *
     * @param archivoComputadoras Ruta de acceso al archivo de computadoras.
     * @return HashMap con todas las computadoras asociadas a su dirección IP como clave.
     * @throws FileNotFoundException Si el archivo de computadoras no existe.
     * Complejidad Temporal: O(C), donde C es el número de computadoras en el archivo.
     */
	private static HashMap<String, Equipo> cargarComputadoras(String archivoComputadoras) throws FileNotFoundException {
		Scanner read;
		HashMap<String, Equipo> equipo = new HashMap<>();
		
			read = new Scanner(new File(archivoComputadoras));
			read.useDelimiter("\\s*;\\s*");
			
			String id, ipAddress, macAddress, status, ubicacion;
			while (read.hasNext()) {				
                id = read.next();
				ipAddress = read.next();
                macAddress = read.next();
				status = read.next();
                ubicacion = read.next();
				equipo.put(ipAddress,new Computadora(id, ipAddress, macAddress, Boolean.parseBoolean(status), ubicacion));
			}
			read.close();
		
		return equipo;
	}



	/**
	 * Combina los mapas de computadoras y routers en un único mapa de equipos.
	 * Carga ambos tipos de equipos y los unifica bajo una sola estructura de datos.
	 *
	 * @param archivoComputadoras Ruta de acceso al archivo de computadoras.
	 * @param archivoRouters Ruta de acceso al archivo de routers.
	 * @return HashMap con todos los equipos asociados a su dirección IP como clave.
	 * @throws FileNotFoundException Si alguno de los archivos no se encuentra.
	 * Complejidad Temporal: O(C + R), donde C es el número de computadoras y R el número de routers.
	 */
    public static HashMap<String, Equipo> cargarEquipos(String archivoComputadoras, String archivoRouters) throws FileNotFoundException {
        HashMap<String, Equipo> equipos = new HashMap<>();

        equipos.putAll(Dato.cargarRouters(archivoRouters));
        equipos.putAll(Dato.cargarComputadoras(archivoComputadoras));
        
        return equipos;
    }

    /**
     * Lee las conexiones desde un archivo de texto delimitado por punto y coma.
     * Cada línea representa una conexión entre dos equipos identificados por su IP.
     * Solo crea conexiones si ambos equipos existen en el mapa proporcionado.
     *
     * @param archivoConexiones Ruta de acceso al archivo de conexiones.
     * @param equipos HashMap de equipos previamente cargados.
     * @return Lista de objetos Conexion que representan las aristas del grafo.
     * @throws FileNotFoundException Si el archivo de conexiones no existe.
     * Complejidad Temporal: O(E), donde E es el número de conexiones en el archivo.
     */
    public static List<Conexion> cargarConexiones(String archivoConexiones, HashMap<String, Equipo> equipos) throws FileNotFoundException {
        Scanner read;
        List<Conexion> conexiones = new ArrayList<Conexion>();

        read = new Scanner(new File(archivoConexiones));
        read.useDelimiter("\\s*;\\s*");
        Equipo e1, e2;
        String ipEquipo1, ipEquipo2, tipoConexion, bandwidth, latencia, errorRate;
        
        while (read.hasNext()) {
        	ipEquipo1 = read.next();
            ipEquipo2 = read.next();
        	if (equipos.containsKey(ipEquipo1) && equipos.containsKey(ipEquipo2)) {
        		e1 = equipos.get(ipEquipo1);
        		e2 = equipos.get(ipEquipo2);
        		tipoConexion = read.next();
        		bandwidth = read.next();
        		latencia = read.next();
        		errorRate = read.next();
        		conexiones.add(new Conexion(e1, e2, tipoConexion, Integer.parseInt(bandwidth), Integer.parseInt(latencia), Double.parseDouble(errorRate)));
        	} else {
        		System.out.println("Error IPs de los equipos no encontrados en el mapa");
        	}
        }
	    read.close();

	    return conexiones;
    }
}

