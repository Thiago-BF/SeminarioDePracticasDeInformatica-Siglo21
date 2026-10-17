package puntotecno;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Pantalla "Consultar estado de garantía" (RFS06, RFS07 y RFS08).
 * Se puede buscar por número de venta (búsqueda binaria) o por cliente y/o producto.
 */
public class VentanaConsultaGarantia extends JDialog {

    private static final String CUALQUIERA = "(cualquiera)";

    private PuntoTecno sistema;
    private JTextField txtNumero;
    private JComboBox<Object> comboCliente;    // Object: contiene el texto "(cualquiera)" o un Cliente
    private JComboBox<Object> comboProducto;
    private JLabel lblMensaje;
    private PanelTablaVentas tabla;

    public VentanaConsultaGarantia(Window owner, PuntoTecno sistema) {
        super(owner, "PuntoTecno — Consultar estado de garantía", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        construirPantalla();
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- Armado de la pantalla ----------
    private void construirPantalla() {
        txtNumero = new JTextField(10);

        DefaultComboBoxModel<Object> modeloClientes = new DefaultComboBoxModel<>();
        modeloClientes.addElement(CUALQUIERA);
        for (Cliente c : sistema.getClientes()) {
            modeloClientes.addElement(c);
        }
        comboCliente = new JComboBox<>(modeloClientes);

        DefaultComboBoxModel<Object> modeloProductos = new DefaultComboBoxModel<>();
        modeloProductos.addElement(CUALQUIERA);
        for (Producto p : sistema.getProductos()) {
            modeloProductos.addElement(p);
        }
        comboProducto = new JComboBox<>(modeloProductos);

        PanelFormulario criterios = new PanelFormulario();
        criterios.setBorder(BorderFactory.createTitledBorder("Criterios de búsqueda"));
        criterios.agregar("N° de venta:", txtNumero);
        criterios.agregar("— o buscar por —", new JLabel(""));
        criterios.agregar("Cliente:", comboCliente);
        criterios.agregar("Producto:", comboProducto);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrar = new JButton("Cerrar");
        btnBuscar.addActionListener(e -> buscar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnCerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnBuscar);
        botones.add(btnLimpiar);
        botones.add(btnCerrar);

        lblMensaje = new JLabel(" ");
        tabla = new PanelTablaVentas();
        JPanel resultado = new JPanel(new BorderLayout(0, 5));
        resultado.setBorder(BorderFactory.createTitledBorder("Resultado de la consulta"));
        resultado.add(lblMensaje, BorderLayout.NORTH);
        resultado.add(tabla, BorderLayout.CENTER);

        JPanel superior = new JPanel(new BorderLayout());
        superior.add(criterios, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenido.add(superior, BorderLayout.NORTH);
        contenido.add(resultado, BorderLayout.CENTER);

        setContentPane(contenido);
        getRootPane().setDefaultButton(btnBuscar);
    }

    // ---------- Acción del botón Buscar ----------
    private void buscar() {
        String textoNumero = txtNumero.getText().trim();
        List<Venta> encontradas = new ArrayList<>();

        if (!textoNumero.isEmpty()) {
            // Búsqueda por número de venta (binaria)
            try {
                int numero = Integer.parseInt(textoNumero);   // puede lanzar NumberFormatException
                Venta v = sistema.buscarVenta(numero);
                if (v != null) {
                    encontradas.add(v);
                }
            } catch (NumberFormatException ex) {
                lblMensaje.setText("El número de venta debe ser un número entero.");
                tabla.mostrar(new ArrayList<>());
                return;
            }
        } else {
            // Búsqueda por cliente y/o producto
            Object cli = comboCliente.getSelectedItem();
            Object prod = comboProducto.getSelectedItem();
            boolean hayCliente = cli instanceof Cliente;
            boolean hayProducto = prod instanceof Producto;

            if (!hayCliente && !hayProducto) {
                lblMensaje.setText("Ingrese un número de venta o elija un cliente y/o un producto.");
                tabla.mostrar(new ArrayList<>());
                return;
            }
            if (hayCliente) {
                encontradas = sistema.ventasDeCliente((Cliente) cli);
                if (hayProducto) {
                    // Ambos criterios: se queda solo con las ventas de ese producto
                    List<Venta> filtradas = new ArrayList<>();
                    for (Venta v : encontradas) {
                        if (v.getProducto().getIdProducto() == ((Producto) prod).getIdProducto()) {
                            filtradas.add(v);
                        }
                    }
                    encontradas = filtradas;
                }
            } else {
                encontradas = sistema.ventasDeProducto((Producto) prod);
            }
        }

        tabla.mostrar(encontradas);
        if (encontradas.isEmpty()) {
            lblMensaje.setText("No se encontraron ventas con los datos ingresados.");   // flujo alternativo del CU
        } else {
            lblMensaje.setText("Ventas encontradas: " + encontradas.size());
        }
    }

    private void limpiar() {
        txtNumero.setText("");
        comboCliente.setSelectedIndex(0);
        comboProducto.setSelectedIndex(0);
        lblMensaje.setText(" ");
        tabla.mostrar(new ArrayList<>());
    }
}
