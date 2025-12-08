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
import java.util.List;
import java.util.TreeMap;

/**
 * Clase que representa el coordinador principal del sistema de red.
 * Se encarga de gestionar la lógica central y coordinar los diferentes módulos.
 */
public class Cordinador {
    private Logica red = null;
    private TreeMap<String, Equipo> equipos;
    private List<Conexion> conexiones;
    private Red datosRed;

    public void inicio()
    {
        inicioDatos();

        inicioLogica(equipos, conexiones);

        inicioRed();

        inicioUI();
    }

    private void inicioLogica(TreeMap<String, Equipo> equipos, List<Conexion> conexiones)
    {
        try {
            red = new Logica(equipos, conexiones);
            System.out.println("-----------Grafo cargado exitosamente.-----------");
        } catch (Exception e) {
            System.err.println("Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicia la carga de datos del coordinador
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

    private void inicioRed()
    {
        datosRed = new Red(equipos, conexiones);
    }

    /**
     * Inicia la ui
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

    private void ejecutarMostrarGrafo() {
        Interfaz.mostrarGrafo(red.getGrafo());
    }

    private void ejecutarPing(){
        String equipo = Interfaz.leerIP(datosRed.getEquipos());
        if(equipo.equals(null)) return; //el usuario cancelo la operacion

        boolean estado = red.ping(equipo);
        Interfaz.ping(equipo, estado);
    }

    private void ejecutarTraceroute(){
        String destino = Interfaz.leerIP(datosRed.getEquiposEncendidos());
        if(destino.equals(null)) return; //el usuario cancelo la operacion

        String origen = Interfaz.leerIP(datosRed.getEquiposEncendidos());
        if(origen.equals(null)) return; //el usuario cancelo la operacion
        try{
            PositionalList<Vertex<Equipo>> traceroute = red.traceroute(destino, origen);
            Interfaz.resultadoTraceroute(origen, destino, traceroute);
        }
        catch(IllegalArgumentException e){
            Interfaz.mostrarError(e.getMessage());
        }
    }

    private void ejecutarMST(){
        List<String> mst = red.MST();
        Interfaz.MST(mst);
    }
}
