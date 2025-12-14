package red.datos;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Clase encargada de cargar y gestionar los parámetros de configuración desde el archivo properties.
 * Proporciona acceso a las rutas de los archivos de datos de equipos y conexiones.
 */
public class CargarParametros {
    private static String archivoComputadoras;
    private static String archivoRouters;
    private static String archivoConexiones;

    /**
     * Carga las rutas de los archivos desde el archivo de propiedades "config.properties".
     * Asigna las rutas a las variables estáticas para su posterior uso.
     *
     * @throws IOException Si el archivo de propiedades no se encuentra o no puede ser leído.
     * Complejidad Temporal: O(1), lectura de archivo de configuración de tamaño constante.
     */
    public static void parametros() throws IOException {    
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            archivoComputadoras = prop.getProperty("Computadoras");
            archivoRouters = prop.getProperty("Routers");
            archivoConexiones = prop.getProperty("Conexiones");
        }
    }

    /**
     * Obtiene la ruta del archivo de computadoras.
     *
     * @return Ruta del archivo de computadoras.
     * Complejidad Temporal: O(1).
     */
    public static String getArchivoComputadoras() {
        return archivoComputadoras;
    }

    /**
     * Obtiene la ruta del archivo de routers.
     *
     * @return Ruta del archivo de routers.
     * Complejidad Temporal: O(1).
     */
    public static String getArchivoRouters() {
        return archivoRouters;
    }

    /**
     * Obtiene la ruta del archivo de conexiones.
     *
     * @return Ruta del archivo de conexiones.
     * Complejidad Temporal: O(1).
     */
    public static String getArchivoConexiones() {
        return archivoConexiones;
    }
}
