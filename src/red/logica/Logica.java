package red.logica;

import net.datastructures.*;
import red.modelo.Conexion;
import red.modelo.Equipo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Clase encargada de la lógica de negocio de la red de computadoras.
 * Maneja el grafo principal y la ejecución de algoritmos de análisis de red.
 */
public class Logica {

    private final Graph<Equipo, Conexion> red;
    private final HashMap<String, Vertex<Equipo>> vertices;

    /**
     * Resumen de Complejidades Temporales:
     *
     * MÉTODOS LÓGICOS:
     * - ping(ip): O(1) - Acceso directo a HashMap.
     * - traceroute(ipOrigen, ipDestino): O((V + E) log V) - Dominado por Dijkstra.
     * - MST(): O(E log E) - Dominado por algoritmo de Kruskal.
     * - calcularFlujoMaximo(ipOrigen, ipDestino): O(VE²) - Dominado por Edmonds-Karp.
     *
     * MÉTODOS AUXILIARES:
     * - crearGrafoActivo() y crearGrafoCapacidad(): O(V + E) - Recorre todos los vértices y aristas.
     *
     * ALGORITMOS UTILIZADOS (de GraphAlgorithms):
     * - BFS: O(V + E) - Búsqueda en anchura.
     * - Dijkstra (shortestPathLengths): O((V + E) log V) - Con heap adaptable.
     * - Kruskal (MST): O(E log E) - Con Union-Find.
     * - Edmonds-Karp (maxFlow): O(VE²) - Ford-Fulkerson con BFS.
     *
     * Donde:
     * V = número de vértices (equipos)
     * E = número de aristas (conexiones)
     */

    /**
     * Constructor que inicializa el grafo principal y carga los datos de equipos y conexiones.
     *
     * @param equipos Mapa ordenado de equipos donde la clave es el ID y el valor es el objeto Equipo.
     * @param conexiones Lista de objetos Conexion que definen las aristas del grafo.
     * Complejidad Temporal: O(V + E), donde V es el número de equipos y E el número de conexiones.
     * O(n)
     */
    public Logica(HashMap<String, Equipo> equipos, List<Conexion> conexiones) {
        red = new AdjacencyMapGraph<>(false);
        vertices = new HashMap<>();

        for (Equipo equipo : equipos.values()) {
            Vertex<Equipo> v = red.insertVertex(equipo);
            vertices.put(equipo.getIpAddress(), v);
        }

        for (Conexion con : conexiones) {
            Vertex<Equipo> v1 = vertices.get(con.getSource().getIpAddress());
            Vertex<Equipo> v2 = vertices.get(con.getTarget().getIpAddress());

            if (v1 != null && v2 != null) {
                if (red.getEdge(v1, v2) == null) {
                    red.insertEdge(v1, v2, con);
                }
            }
        }
    }

    /**
     * Verifica si un equipo con una dirección IP específica existe y se encuentra activo en la red.
     *
     * @param ip Dirección IP del equipo a verificar.
     * @return true si el equipo existe y su estado es activo, false en caso contrario.
     * Complejidad Temporal: O(1), acceso directo al HashMap por clave IP.
     */
    public boolean ping(String ip) {
        return vertices.get(ip).getElement().isStatus();
    }

    /**
     * Calcula el camino óptimo (menor latencia) entre dos equipos utilizando el algoritmo de Dijkstra.
     * Se consideran únicamente los nodos y conexiones que están activos.
     *
     * @param ipOrigen Dirección IP del equipo de origen.
     * @param ipDestino Dirección IP del equipo de destino.
     * @return Una lista posicional de vértices que representa la ruta desde el origen hasta el destino.
     * @throws IllegalArgumentException Si alguno de los equipos no existe, no está activo, o no hay camino entre ellos.
     * Complejidad Temporal:
     * - crearGrafoActivo: O(V + E), para filtrar elementos activos.
     * - shortestPathLengths (Dijkstra): O((V + E) log V), utilizando heap adaptable.
     * - spTree: O(V + E), para reconstruir el árbol de caminos mínimos.
     * - shortestPathList: O(V), para reconstruir el camino específico.
     * - traceroute: O((V + E) log V), dominado por Dijkstra.
     * - traceroute: O( n log(n) ))
     */
    public PositionalList<Vertex<Equipo>> traceroute(String ipOrigen, String ipDestino) {

        PositionalList<Vertex<Equipo>> camino;

        Graph<Equipo, Integer> grafoActivo = crearGrafoActivo();

        Vertex<Equipo> origenNode = null;
        Vertex<Equipo> destinoNode = null;

        for (Vertex<Equipo> v : grafoActivo.vertices()) {
            String ip = v.getElement().getIpAddress();
            if (ip.equals(ipOrigen)) {
                origenNode = v;
            }
            if (ip.equals(ipDestino)) {
                destinoNode = v;
            }
        }


        if (origenNode == null || destinoNode == null) {
            throw new IllegalArgumentException("Uno o ambos equipos no se encuentran activos o no existen en la red.");
        }


        try{
            camino = GraphAlgorithms.shortestPathList(grafoActivo, origenNode, destinoNode);
        } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("No se encontró una ruta entre el equipo (" + origenNode.getElement().getId() + ") " + origenNode.getElement().getIpAddress() + " y el equipo (" + destinoNode.getElement().getId() + ") " + destinoNode.getElement().getIpAddress() + ".");
        }

        return camino;
    }

    /**
     * Calcula el Árbol de Expansión Mínima (MST) de la red activa basándose en la latencia de las conexiones.
     * Utiliza el algoritmo de Kruskal con estructura Union-Find para detectar ciclos.
     *
     * @return Una lista de cadenas de texto formateadas describiendo las conexiones del MST y sus latencias.
     * Complejidad Temporal:
     * - crearGrafoActivo: O(V + E), para filtrar elementos activos.
     * - Kruskal (MST): O(E log E), donde E es el número de aristas, dominado por la ordenación de aristas.
     * - Union-Find: O(α(V)), donde α es la función inversa de Ackermann (prácticamente constante).
     * - Formateo de resultados: O(V), para construir la lista de strings.
     * - MST: O(E log E), dominado por el algoritmo de Kruskal.
     * - MST: O( n log(n) )
     */
    public List<String> MST() {
        Graph<Equipo, Integer> grafoActivo = crearGrafoActivo();
        PositionalList<Edge<Integer>> mstEdges = GraphAlgorithms.MST(grafoActivo);

        List<String> resultado = new ArrayList<>();
        for (Edge<Integer> e : mstEdges) {
            Vertex<Equipo>[] endpoints = grafoActivo.endVertices(e);
            String linea = endpoints[0].getElement().getId() + " <--> " +
                    endpoints[1].getElement().getId() + " [Latencia: " + e.getElement() + " ms]\n";
            resultado.add(linea);
        }
        return resultado;
    }

    /**
     * Calcula el flujo máximo entre dos equipos en la red activa utilizando el algoritmo de Edmonds-Karp.
     * Este algoritmo es una implementación del metodo Ford-Fulkerson que utiliza BFS para encontrar caminos de aumento.
     *
     * @param ipOrigen Dirección IP del equipo origen (fuente).
     * @param ipDestino Dirección IP del equipo destino (sumidero).
     * @return Valor entero que representa el flujo máximo entre origen y destino.
     * @throws IllegalArgumentException Si alguno de los equipos no es válido o no está activo.
     * Complejidad Temporal:
     * - crearGrafoCapacidad: O(V + E), para crear el grafo con capacidades.
     * - BFS: O(V + E), para encontrar un camino de aumento en cada iteración.
     * - Edmonds-Karp: O(VE²), ya que hay como máximo O(VE) iteraciones y cada una ejecuta BFS.
     * - calcularFlujoMaximo: O(VE²), dominado por Edmonds-Karp.
     * - calcularFlujoMaximo: O(n²)
     */
    public int calcularFlujoMaximo(String ipOrigen, String ipDestino) {
        Graph<Equipo, Integer> grafoCap = crearGrafoCapacidad();

        Vertex<Equipo> source = null;
        Vertex<Equipo> sink = null;

        // Buscar los vértices en el nuevo grafo
        for (Vertex<Equipo> v : grafoCap.vertices()) {
            if (v.getElement().getIpAddress().equals(ipOrigen)) source = v;
            if (v.getElement().getIpAddress().equals(ipDestino)) sink = v;
        }

        if (source == null || sink == null) {
            throw new IllegalArgumentException("Origen o destino no válidos o inactivos.");
        }

        return GraphAlgorithms.maxFlow(grafoCap, source, sink);
    }

    /**
     * Crea y retorna una copia del grafo original que incluye únicamente los equipos y conexiones activos.
     * Las aristas del nuevo grafo utilizan la latencia (Integer) como peso para los algoritmos.
     *
     * @return Un grafo no dirigido con los elementos activos de la red.
     * Complejidad Temporal: O(V + E), donde V es el número de vértices y E el número de aristas;
     * requerida para recorrer y filtrar todos los vértices y aristas del grafo original.
     */
    private Graph<Equipo, Integer> crearGrafoActivo() {
        Graph<Equipo, Integer> grafoActivo = new AdjacencyMapGraph<>(false);
        HashMap<String, Vertex<Equipo>> mapaActivos = new HashMap<>();

        for (Vertex<Equipo> v : red.vertices()) {
            if (v.getElement().isStatus()) {
                Vertex<Equipo> nuevoV = grafoActivo.insertVertex(v.getElement());
                mapaActivos.put(v.getElement().getIpAddress(), nuevoV);
            }
        }

        for (Edge<Conexion> e : red.edges()) {
            Conexion c = e.getElement();
            if (c.isStatus()) {
                Vertex<Equipo> v1 = mapaActivos.get(c.getSource().getIpAddress());
                Vertex<Equipo> v2 = mapaActivos.get(c.getTarget().getIpAddress());

                if (v1 != null && v2 != null) {
                    grafoActivo.insertEdge(v1, v2, c.getLatencia());
                }
            }
        }
        return grafoActivo;
    }

    /**
     * Crea un grafo donde las aristas tienen como peso el ancho de banda (bandwidth).
     * Solo incluye elementos activos para operaciones de flujo máximo.
     *
     * @return Un grafo no dirigido con capacidades en las aristas.
     * Complejidad Temporal: O(V + E), donde V es el número de vértices y E el número de aristas.
     */
    private Graph<Equipo, Integer> crearGrafoCapacidad() {
        Graph<Equipo, Integer> grafoCap = new AdjacencyMapGraph<>(false); // false = no dirigido según tu diseño
        HashMap<String, Vertex<Equipo>> mapaActivos = new HashMap<>();

        for (Vertex<Equipo> v : red.vertices()) {
            if (v.getElement().isStatus()) {
                Vertex<Equipo> nuevoV = grafoCap.insertVertex(v.getElement());
                mapaActivos.put(v.getElement().getIpAddress(), nuevoV);
            }
        }

        for (Edge<Conexion> e : red.edges()) {
            Conexion c = e.getElement();
            if (c.isStatus()) {
                Vertex<Equipo> v1 = mapaActivos.get(c.getSource().getIpAddress());
                Vertex<Equipo> v2 = mapaActivos.get(c.getTarget().getIpAddress());

                if (v1 != null && v2 != null) {
                    grafoCap.insertEdge(v1, v2, c.getBandwidth());
                }
            }
        }
        return grafoCap;
    }

    /**
     * Obtiene el grafo principal de la red con todos sus equipos y conexiones.
     *
     * @return El grafo completo de la red.
     * Complejidad Temporal: O(1).
     */
    public Graph<Equipo, Conexion> getGrafo() {
        return this.red;
    }
}