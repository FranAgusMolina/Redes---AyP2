package red.interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import net.datastructures.Graph;
import net.datastructures.Vertex;
import net.datastructures.Edge;
import red.modelo.Equipo;
import red.modelo.Conexion;

/**
 * Panel personalizado para la visualización gráfica interactiva del grafo de red.
 * Permite arrastrar nodos y muestra equipos y conexiones con sus propiedades.
 */
public class PanelRed extends JPanel {
    private final Graph<Equipo, Conexion> grafo;
    private final Map<Vertex<Equipo>, Point> coordenadas;
    private static final int RADIO_NODO = 30;

    private Vertex<Equipo> nodoSeleccionado = null;
    private Point offsetMouse = null;

    /**
     * Constructor que inicializa el panel con el grafo a visualizar.
     * Calcula las coordenadas iniciales de los nodos y configura los listeners de ratón.
     *
     * @param grafo Grafo de la red a visualizar.
     * Complejidad Temporal: O(V), donde V es el número de vértices del grafo.
     */
    public PanelRed(Graph<Equipo, Conexion> grafo) {
        this.grafo = grafo;
        this.coordenadas = new HashMap<>();
        this.setBackground(Color.WHITE);

        calcularCoordenadasIniciales();

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handlerMousePressed(e.getPoint());
            } //detecta si se hizo click en un nodo

            @Override
            public void mouseReleased(MouseEvent e) {
                nodoSeleccionado = null;
            }


            @Override
            public void mouseDragged(MouseEvent e) {
                handlerMouseDragged(e.getPoint());
            } //mueve el nodo mientras el click este presionado

        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    /**
     * Calcula las coordenadas iniciales de todos los vértices del grafo.
     * Distribuye los nodos en una cuadrícula para evitar superposición.
     *
     * Complejidad Temporal: O(V), donde V es el número de vértices.
     */
    private void calcularCoordenadasIniciales() {
        int numVertices = grafo.numVertices();

        int columnas = (int) Math.ceil(Math.sqrt(numVertices));

        int inicioX = 100;
        int inicioY = 100;
        int separacionX = 150;
        int separacionY = 120;

        int i = 0;
        for (Vertex<Equipo> v : grafo.vertices()) {
            int fila = i / columnas;
            int col = i % columnas;

            int x = inicioX + (col * separacionX);
            int y = inicioY + (fila * separacionY);

            coordenadas.put(v, new Point(x, y));
            i++;
        }
    }

    /**
     * Detecta si un clic del ratón ha seleccionado algún nodo del grafo.
     * Utiliza detección de colisión circular.
     *
     * @param click Punto donde se hizo clic.
     * Complejidad Temporal: O(V), donde V es el número de vértices (en el peor caso).
     */
    private void handlerMousePressed(Point click) {
        for (Map.Entry<Vertex<Equipo>, Point> entry : coordenadas.entrySet()) {
            Point p = entry.getValue();
            if (click.distance(p.x + RADIO_NODO/2.0, p.y + RADIO_NODO/2.0) <= RADIO_NODO/2.0) {
                nodoSeleccionado = entry.getKey();
                offsetMouse = new Point(click.x - p.x, click.y - p.y);
                break;
            }
        }
    }

    /**
     * Mueve el nodo seleccionado a una nueva posición y redibuja el panel.
     *
     * @param actual Posición actual del ratón.
     * Complejidad Temporal: O(1) para mover el nodo, O(V + E) para repaint.
     */
    private void handlerMouseDragged(Point actual) {
        if (nodoSeleccionado != null) {
            Point nuevaPos = new Point(actual.x - offsetMouse.x, actual.y - offsetMouse.y);
            coordenadas.put(nodoSeleccionado, nuevaPos);
            repaint();
        }
    }

    /**
     * Dibuja el grafo completo en el panel: aristas con sus pesos y vértices con sus IDs.
     * Las aristas activas se muestran en gris sólido, las inactivas con línea punteada roja.
     * Los nodos activos se muestran en verde, los inactivos en rojo salmón.
     *
     * @param g Contexto gráfico para dibujar.
     * Complejidad Temporal: O(V + E), donde V es el número de vértices y E el número de aristas.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Edge<Conexion> e : grafo.edges()) {
            Vertex<Equipo>[] ends = grafo.endVertices(e);
            Point p1 = coordenadas.get(ends[0]);
            Point p2 = coordenadas.get(ends[1]);

            int c1x = p1.x + RADIO_NODO/2;
            int c1y = p1.y + RADIO_NODO/2;
            int c2x = p2.x + RADIO_NODO/2;
            int c2y = p2.y + RADIO_NODO/2;

            if (e.getElement().isStatus()) {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setStroke(new BasicStroke(2));
            } else {
                g2d.setColor(new Color(255, 100, 100));
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
            }

            g2d.drawLine(c1x, c1y, c2x, c2y);

            String data = e.getElement().getLatencia() + "ms/ " + e.getElement().getBandwidth() + "Mbps";
            int midX = (c1x + c2x)/2;
            int midY = (c1y + c2y)/2;

            g2d.setColor(new Color(255, 255, 255, 200));
            FontMetrics fm = g2d.getFontMetrics();
            int w = fm.stringWidth(data);
            g2d.fillRect(midX - w/2, midY - fm.getAscent(), w, fm.getHeight());

            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(data, midX - w/2, midY);
        }

        g2d.setStroke(new BasicStroke(1));
        for (Vertex<Equipo> v : grafo.vertices()) {
            Point p = coordenadas.get(v);
            Equipo equipo = v.getElement();

            if (equipo.isStatus()) {
                g2d.setColor(new Color(144, 238, 144));
            } else {
                g2d.setColor(new Color(255, 160, 122));
            }

            g2d.fillOval(p.x, p.y, RADIO_NODO, RADIO_NODO);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(p.x, p.y, RADIO_NODO, RADIO_NODO);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String id = equipo.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textX = p.x + (RADIO_NODO - fm.stringWidth(id)) / 2;
            g2d.drawString(id, textX, p.y - 5);
        }
    }
}