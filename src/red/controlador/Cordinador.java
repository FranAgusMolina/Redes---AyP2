package red.controlador;

import net.datastructures.PositionalList;
import net.datastructures.Vertex;
import red.datos.CargarParametros;
import red.datos.Dato;
import red.interfaz.Interfaz;
import red.logica.Logica;
import red.logica.Red;
import red.modelo.Conexion;
import red.modelo.Equipo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Clase que representa el coordinador principal del sistema de red.
 * Se encarga de gestionar la lógica central y coordinar los diferentes módulos:
 * datos, lógica de red e interfaz de usuario.
 */
public class Cordinador {
    private Logica red = null;
    private HashMap<String, Equipo> equipos;
    private List<Conexion> conexiones;
    private Red datosRed;

    /**
     * Inicia todos los componentes del sistema en orden:
     * carga de datos, inicialización de la lógica, configuración de red y arranque de la UI.
     * Complejidad Temporal: O(V + E), donde V es el número de equipos y E el número de conexiones.
     */
    public void inicio()
    {
        inicioDatos();

        inicioLogica(equipos, conexiones);

        inicioRed();

        inicioUI();
    }

    /**
     * Inicializa la capa lógica del sistema con los equipos y conexiones cargados.
     * Crea el grafo de la red y lo prepara para operaciones de análisis.
     *
     * @param equipos Mapa de equipos indexado por dirección IP.
     * @param conexiones Lista de conexiones entre equipos.
     * Complejidad Temporal: O(V + E), donde V es el número de equipos y E el número de conexiones.
     */
    private void inicioLogica(HashMap<String, Equipo> equipos, List<Conexion> conexiones)
    {
        try {
            red = new Logica(equipos, conexiones);
        } catch (Exception e) {
            System.err.println("Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los parámetros de configuración y los datos de equipos y conexiones desde archivos.
     * Lee las rutas desde el archivo de propiedades y carga computadoras, routers y conexiones.
     *
     * Complejidad Temporal: O(V + E), donde V es el número de equipos y E el número de conexiones leídas.
     */
    private void inicioDatos()
    {
        try {
            CargarParametros.parametros();
        } catch (IOException e) {
            System.err.print("Error al cargar parámetros");
            System.exit(-1);
        }



        try {
            equipos = Dato.cargarEquipos(CargarParametros.getArchivoComputadoras(),CargarParametros.getArchivoRouters());
            conexiones = Dato.cargarConexiones(CargarParametros.getArchivoConexiones(), equipos);
        } catch (FileNotFoundException e) {
            System.err.print("Error al cargar archivos de datos");
            System.exit(-1);
        }
    }

    /**
     * Inicializa el objeto Red que almacena los datos para operaciones de la interfaz de usuario.
     *
     * Complejidad Temporal: O(1).
     */
    private void inicioRed()
    {
        datosRed = new Red(equipos, conexiones);
    }

    /**
     * Inicia el bucle principal de la interfaz de usuario.
     * Muestra un menú de opciones y ejecuta las operaciones seleccionadas hasta que el usuario decide salir.
     *
     * Complejidad Temporal: O(1) por iteración del bucle, sin contar las operaciones específicas invocadas.
     */
    private void inicioUI()
    {
        /* Interfaz */
        boolean on = true;
        while (on) {
            int opcion = Interfaz.opcion();
            switch (opcion) {
                case 5:
                    ejecutarMaxFlow();
                    break;
                case 4:
                    ejecutarMostrarGrafo();
                    break;
                case 3:
                    ejecutarPing();
                    break;

                case 2:
                    ejecutarTraceroute();
                    break;

                case 1:
                    ejecutarMST();
                    break;

                case 0:
                    Interfaz.salir();
                    on = false;
                    break;

                case -1:
                    Interfaz.salir();
                    on = false;

                default:
                    Interfaz.invalido();
                    break;
            }
        }
    }

    /**
     * Ejecuta el cálculo de flujo máximo entre dos equipos seleccionados por el usuario.
     * Solicita IP de origen y destino, valida que sean diferentes y muestra el resultado.
     *
     * Complejidad Temporal:
     * - BFS: O(V + E), para encontrar caminos de aumento.
     * - Edmonds-Karp: O(VE²), donde V es el número de vértices y E el número de aristas.
     * - calcularFlujoMaximo: O(VE²), dominado por Edmonds-Karp.
     */
    private void ejecutarMaxFlow() {
        try {
            String origen = Interfaz.leerIP(datosRed.getEquiposEncendidos());
            if (origen == null) return;

            String destino = Interfaz.leerIP(datosRed.getEquiposEncendidos());
            if (destino == null) return;

            if (origen.equals(destino)) {
                Interfaz.mostrarError("El origen y el destino no pueden ser el mismo equipo.");
                return;
            }

            int maxFlow = red.calcularFlujoMaximo(origen, destino);

            // Mostrar resultado
            Interfaz.mostrarFlujoMaximo(origen, destino, maxFlow);

        } catch (IllegalArgumentException e) {
            Interfaz.mostrarError(e.getMessage());
        } catch (Exception e) {
            Interfaz.mostrarError("Error al calcular flujo: " + e.getMessage());
        }
    }

    /**
     * Ejecuta la visualización gráfica del grafo de la red.
     * Muestra todos los equipos y conexiones en una ventana interactiva.
     *
     * Complejidad Temporal: O(V + E), para recorrer y dibujar todos los vértices y aristas.
     */
    private void ejecutarMostrarGrafo() {
        Interfaz.mostrarGrafo(red.getGrafo());
    }

    /**
     * Ejecuta la operación de ping sobre un equipo seleccionado por el usuario.
     * Verifica si el equipo está activo y muestra el resultado.
     *
     * Complejidad Temporal: O(1), acceso directo al HashMap por IP.
     */
    private void ejecutarPing(){
        String equipo = Interfaz.leerIP(datosRed.getEquipos());
        if(equipo.equals(null)) return;

        boolean estado = red.ping(equipo);
        Interfaz.ping(equipo, estado);
    }

    /**
     * Ejecuta el algoritmo de traceroute entre dos equipos seleccionados.
     * Calcula y muestra el camino más corto entre origen y destino.
     *
     * Complejidad Temporal:
     * - Dijkstra: O((V + E) log V), para calcular caminos más cortos.
     * - traceroute: O((V + E) log V), dominado por Dijkstra.
     */
    private void ejecutarTraceroute(){
        String destino = Interfaz.leerIP(datosRed.getEquiposEncendidos());
        if(destino.equals(null)) return;

        String origen = Interfaz.leerIP(datosRed.getEquiposEncendidos());
        if(origen.equals(null)) return;
        try{
            PositionalList<Vertex<Equipo>> traceroute = red.traceroute(destino, origen);
            Interfaz.resultadoTraceroute(origen, destino, traceroute);
        }
        catch(IllegalArgumentException e){
            Interfaz.mostrarError(e.getMessage());
        }
    }

    /**
     * Ejecuta el cálculo del Árbol de Expansión Mínima (MST) de la red.
     * Muestra las conexiones del MST ordenadas por latencia.
     *
     * Complejidad Temporal:
     * - Kruskal: O(E log E), donde E es el número de aristas.
     * - MST: O(E log E), dominado por Kruskal.
     */
    private void ejecutarMST(){
        List<String> mst = red.MST();
        Interfaz.MST(mst);
    }
}
