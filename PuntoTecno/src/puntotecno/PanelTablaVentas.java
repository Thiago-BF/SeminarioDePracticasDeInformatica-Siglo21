package puntotecno;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Componente reutilizable: tabla de ventas con su estado de garantía.
 * Lo usan las pantallas "Consultar estado de garantía" y "Listar ventas",
 * así la tabla se programa una sola vez. El estado se pinta en verde (Vigente) o rojo (Vencida).
 */
public class PanelTablaVentas extends JPanel {

    private static final String[] COLUMNAS = {
        "N° Venta", "Cliente", "Producto", "Vendedor", "Fecha de venta", "Vencimiento", "Estado garantía"
    };

    private DefaultTableModel modelo;
    private JTable tabla;
    private List<Venta> ventasMostradas;   // mismas ventas que las filas, en el mismo orden

    public PanelTablaVentas() {
        super(new BorderLayout());
        ventasMostradas = new ArrayList<>();

        // Modelo de la tabla con las celdas NO editables
        modelo = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(6).setCellRenderer(new RenderEstado());

        // Anchos de columna (para que no se corten los nombres de los productos)
        int[] anchos = {70, 140, 340, 140, 110, 110, 110};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        setPreferredSize(new Dimension(1000, 260));
    }

    /** Reemplaza el contenido de la tabla por la lista de ventas recibida. */
    public void mostrar(List<Venta> ventas) {
        modelo.setRowCount(0);
        ventasMostradas = new ArrayList<>(ventas);
        for (Venta v : ventas) {
            modelo.addRow(new Object[] {
                v.getNumero(),
                v.getCliente().getNombre(),
                v.getProducto().toString(),
                v.getVendedor().getNombre(),
                Formato.fecha(v.getFecha()),
                Formato.fecha(v.getGarantia().getFechaFin()),
                v.getGarantia().getEstado()      // se calcula con la fecha de hoy
            });
        }
    }

    /** Devuelve la venta de la fila seleccionada, o null si no hay ninguna. */
    public Venta getVentaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return ventasMostradas.get(fila);
    }

    public int cantidad() {
        return ventasMostradas.size();
    }

    /** Pinta la columna "Estado" según su valor. */
    private static class RenderEstado extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            JLabel celda = (JLabel) super.getTableCellRendererComponent(t, valor, seleccionada, foco, fila, columna);
            celda.setHorizontalAlignment(CENTER);
            celda.setFont(celda.getFont().deriveFont(Font.BOLD));
            if (!seleccionada) {
                celda.setForeground("Vigente".equals(valor) ? new Color(0, 128, 0) : new Color(190, 0, 0));
            }
            return celda;
        }
    }
}
