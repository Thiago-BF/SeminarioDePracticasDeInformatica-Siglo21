package puntotecno;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Pantalla "Listar ventas" (RFS09). Permite filtrar por rango de fechas
 * y ordenar por fecha (algoritmo de inserción implementado en PuntoTecno).
 */
public class VentanaListarVentas extends JDialog {

    private PuntoTecno sistema;
    private JTextField txtDesde;
    private JTextField txtHasta;
    private JComboBox<String> comboOrden;
    private JLabel lblCantidad;
    private PanelTablaVentas tabla;

    public VentanaListarVentas(Window owner, PuntoTecno sistema) {
        super(owner, "PuntoTecno — Listado de ventas", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        construirPantalla();
        filtrar();   // al abrir, muestra todas las ventas
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- Armado de la pantalla ----------
    private void construirPantalla() {
        txtDesde = new JTextField(10);
        txtHasta = new JTextField(10);
        comboOrden = new JComboBox<>(new String[] { "Más recientes primero", "Más antiguas primero" });

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnFiltrar.addActionListener(e -> filtrar());
        btnLimpiar.addActionListener(e -> {
            txtDesde.setText("");
            txtHasta.setText("");
            filtrar();
        });
        comboOrden.addActionListener(e -> filtrar());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtrar por fecha (DD/MM/AAAA, vacío = sin límite)"));
        filtros.add(new JLabel("Desde:"));
        filtros.add(txtDesde);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(txtHasta);
        filtros.add(btnFiltrar);
        filtros.add(btnLimpiar);
        filtros.add(new JLabel("Orden:"));
        filtros.add(comboOrden);

        tabla = new PanelTablaVentas();

        JButton btnDetalle = new JButton("Ver detalle");
        JButton btnCerrar = new JButton("Cerrar");
        btnDetalle.addActionListener(e -> verDetalle());
        btnCerrar.addActionListener(e -> dispose());
        lblCantidad = new JLabel(" ");

        JPanel inferior = new JPanel(new BorderLayout());
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        izquierda.add(btnDetalle);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        derecha.add(lblCantidad);
        derecha.add(btnCerrar);
        inferior.add(izquierda, BorderLayout.WEST);
        inferior.add(derecha, BorderLayout.EAST);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenido.add(filtros, BorderLayout.NORTH);
        contenido.add(tabla, BorderLayout.CENTER);
        contenido.add(inferior, BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    // ---------- Filtrado y ordenación ----------
    private void filtrar() {
        try {
            // Si un campo queda vacío se usa un límite muy amplio (sin restricción)
            LocalDate desde = txtDesde.getText().trim().isEmpty()
                    ? LocalDate.of(1900, 1, 1) : Formato.parseFecha(txtDesde.getText());
            LocalDate hasta = txtHasta.getText().trim().isEmpty()
                    ? LocalDate.of(2999, 12, 31) : Formato.parseFecha(txtHasta.getText());

            List<Venta> filtradas = sistema.ventasEntreFechas(desde, hasta);   // puede lanzar IllegalArgumentException
            boolean ascendente = comboOrden.getSelectedIndex() == 1;
            tabla.mostrar(sistema.ordenarPorFecha(filtradas, ascendente));
            lblCantidad.setText("Mostrando " + tabla.cantidad() + " registros");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Use el formato DD/MM/AAAA (ej: 01/01/2026).",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Rango inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---------- Botón Ver detalle ----------
    private void verDetalle() {
        Venta v = tabla.getVentaSeleccionada();
        if (v == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta de la tabla.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Garantia g = v.getGarantia();
        String detalle = "Venta N° " + v.getNumero()
                + "\nFecha: " + Formato.fecha(v.getFecha())
                + "\n\nCliente: " + v.getCliente().getNombre() + " (" + v.getCliente().getContacto() + ")"
                + "\nVendedor: " + v.getVendedor()
                + "\n\nProducto: " + v.getProducto().getNombre()
                + "\nCategoría: " + v.getProducto().getCategoria()
                + "\nPrecio: " + Formato.dinero(v.getProducto().getPrecio())
                + "\n\nGarantía: " + v.getProducto().getPeriodoGarantiaDias() + " días"
                + "\nDesde: " + Formato.fecha(g.getFechaInicio()) + "   Hasta: " + Formato.fecha(g.getFechaFin())
                + "\nEstado: " + g.getEstado();
        JOptionPane.showMessageDialog(this, detalle, "Detalle de la venta", JOptionPane.INFORMATION_MESSAGE);
    }
}
