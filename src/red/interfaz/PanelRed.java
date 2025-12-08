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

public class PanelRed extends JPanel {
    private Graph<Equipo, Conexion> grafo;
    private Map<Vertex<Equipo>, Point> coordenadas;
    private final int RADIO_NODO = 30;

    // Variables para el arrastre (Drag & Drop)
    private Vertex<Equipo> nodoSeleccionado = null;
    private Point offsetMouse = null;

    public PanelRed(Graph<Equipo, Conexion> grafo) {
        this.grafo = grafo;
        this.coordenadas = new HashMap<>();
        this.setBackground(Color.WHITE);

        calcularCoordenadasIniciales();

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                seleccionarNodo(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                nodoSeleccionado = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                moverNodo(e.getPoint());
            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

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

    private void seleccionarNodo(Point click) {
        for (Map.Entry<Vertex<Equipo>, Point> entry : coordenadas.entrySet()) {
            Point p = entry.getValue();
            // Chequeo simple de colisión circular
            if (click.distance(p.x + RADIO_NODO/2.0, p.y + RADIO_NODO/2.0) <= RADIO_NODO/2.0) {
                nodoSeleccionado = entry.getKey();
                offsetMouse = new Point(click.x - p.x, click.y - p.y);
                break;
            }
        }
    }

    private void moverNodo(Point actual) {
        if (nodoSeleccionado != null) {
            Point nuevaPos = new Point(actual.x - offsetMouse.x, actual.y - offsetMouse.y);
            coordenadas.put(nodoSeleccionado, nuevaPos);
            repaint(); // Redibujar el panel
        }
    }

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
                g2d.setColor(new Color(255, 100, 100)); // Rojo suave
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
            }

            g2d.drawLine(c1x, c1y, c2x, c2y);

            String data = e.getElement().getLatencia() + "ms/ " + e.getElement().getBandwidth() + "Mbps";
            int midX = (c1x + c2x)/2;
            int midY = (c1y + c2y)/2;

            g2d.setColor(new Color(255, 255, 255, 200)); // Blanco semi-transparente
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
                g2d.setColor(new Color(144, 238, 144)); // Verde claro
            } else {
                g2d.setColor(new Color(255, 160, 122)); // Rojo salmón
            }

            g2d.fillOval(p.x, p.y, RADIO_NODO, RADIO_NODO);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(p.x, p.y, RADIO_NODO, RADIO_NODO);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String id = equipo.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int textX = p.x + (RADIO_NODO - fm.stringWidth(id)) / 2;
            g2d.drawString(id, textX, p.y - 5); // Un poco arriba del nodo
        }
    }
}