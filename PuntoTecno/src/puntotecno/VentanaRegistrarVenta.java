package puntotecno;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Pantalla "Registrar venta" (RFS02 y RFS03).
 * Muestra una vista previa de la garantía que se calculará (caso de uso "Calcular garantía").
 * Los botones "Nuevo cliente" y "Nuevo producto" cubren el flujo alternativo del caso de uso.
 */
public class VentanaRegistrarVenta extends JDialog {

    private PuntoTecno sistema;
    private JComboBox<Cliente> comboCliente;
    private JComboBox<Vendedor> comboVendedor;
    private JComboBox<Producto> comboProducto;
    private JTextField txtFecha;
    private JLabel lblGarantia;

    public VentanaRegistrarVenta(Window owner, PuntoTecno sistema) {
        super(owner, "PuntoTecno — Registrar venta", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        construirPantalla();
        cargarCombos();
        actualizarGarantia();
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- Armado de la pantalla ----------
    private void construirPantalla() {
        comboCliente = new JComboBox<>();
        comboVendedor = new JComboBox<>();
        comboProducto = new JComboBox<>();
        txtFecha = new JTextField(Formato.fecha(LocalDate.now()), 10);   // por defecto, hoy
        lblGarantia = new JLabel();
        lblGarantia.setFont(lblGarantia.getFont().deriveFont(Font.BOLD));
        lblGarantia.setForeground(new Color(0, 100, 0));

        JButton btnNuevoCliente = new JButton("Nuevo cliente");
        JButton btnNuevoProducto = new JButton("Nuevo producto");
        btnNuevoCliente.addActionListener(e -> abrirNuevoCliente());
        btnNuevoProducto.addActionListener(e -> abrirNuevoProducto());

        // La vista previa de la garantía se recalcula al cambiar el producto o la fecha
        comboProducto.addActionListener(e -> actualizarGarantia());
        txtFecha.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { actualizarGarantia(); }
            @Override public void removeUpdate(DocumentEvent e)  { actualizarGarantia(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizarGarantia(); }
        });

        PanelFormulario formulario = new PanelFormulario();
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        formulario.agregar("Cliente:", conBoton(comboCliente, btnNuevoCliente));
        formulario.agregar("Vendedor:", comboVendedor);
        formulario.agregar("Producto:", conBoton(comboProducto, btnNuevoProducto));
        formulario.agregar("Fecha de venta (DD/MM/AAAA):", txtFecha);

        JPanel panelGarantia = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelGarantia.setBorder(BorderFactory.createTitledBorder("Garantía calculada automáticamente"));
        panelGarantia.add(lblGarantia);

        JButton btnRegistrar = new JButton("Registrar venta");
        JButton btnCancelar = new JButton("Cancelar");
        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnRegistrar);
        botones.add(btnCancelar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(formulario, BorderLayout.CENTER);
        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        inferior.add(panelGarantia, BorderLayout.CENTER);
        centro.add(inferior, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(centro, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnRegistrar);
    }

    // Auxiliar: arma una fila "combo + botón"
    private JPanel conBoton(JComboBox<?> combo, JButton boton) {
        JPanel fila = new JPanel(new BorderLayout(5, 0));
        fila.add(combo, BorderLayout.CENTER);
        fila.add(boton, BorderLayout.EAST);
        return fila;
    }

    /** Carga los combos con los datos actuales del sistema. */
    private void cargarCombos() {
        comboCliente.setModel(new DefaultComboBoxModel<>(sistema.getClientes().toArray(new Cliente[0])));
        comboVendedor.setModel(new DefaultComboBoxModel<>(sistema.getVendedores().toArray(new Vendedor[0])));
        comboProducto.setModel(new DefaultComboBoxModel<>(sistema.getProductos().toArray(new Producto[0])));
    }

    // ---------- Flujo alternativo: dar de alta cliente/producto sin salir de la pantalla ----------
    private void abrirNuevoCliente() {
        int antes = comboCliente.getItemCount();
        new VentanaAltaCliente(this, sistema).setVisible(true);   // modal: espera a que se cierre
        cargarCombos();
        if (comboCliente.getItemCount() > antes) {
            comboCliente.setSelectedIndex(comboCliente.getItemCount() - 1);   // selecciona el nuevo
        }
    }

    private void abrirNuevoProducto() {
        int antes = comboProducto.getItemCount();
        new VentanaAltaProducto(this, sistema).setVisible(true);
        cargarCombos();
        if (comboProducto.getItemCount() > antes) {
            comboProducto.setSelectedIndex(comboProducto.getItemCount() - 1);
        }
    }

    /** Vista previa: muestra los días de garantía y la fecha de vencimiento. */
    private void actualizarGarantia() {
        Producto p = (Producto) comboProducto.getSelectedItem();
        if (p == null) {
            lblGarantia.setText("Seleccione un producto.");
            return;
        }
        try {
            LocalDate fecha = Formato.parseFecha(txtFecha.getText());
            LocalDate vence = fecha.plusDays(p.getPeriodoGarantiaDias());
            lblGarantia.setText("Garantía: " + p.getPeriodoGarantiaDias() + " días — Vence el " + Formato.fecha(vence));
        } catch (DateTimeParseException ex) {
            lblGarantia.setText("Fecha inválida (use DD/MM/AAAA)");
        }
    }

    // ---------- Acción del botón Registrar venta ----------
    private void registrar() {
        try {
            LocalDate fecha = Formato.parseFecha(txtFecha.getText());   // puede lanzar DateTimeParseException
            Venta venta = sistema.registrarVenta(
                    (Cliente) comboCliente.getSelectedItem(),
                    (Vendedor) comboVendedor.getSelectedItem(),
                    (Producto) comboProducto.getSelectedItem(),
                    fecha);                                              // puede lanzar GarantiaException

            JOptionPane.showMessageDialog(this,
                    "Venta registrada correctamente.\n\nN° de venta: " + venta.getNumero()
                    + "\nCliente: " + venta.getCliente().getNombre()
                    + "\nProducto: " + venta.getProducto().getNombre()
                    + "\nGarantía hasta: " + Formato.fecha(venta.getGarantia().getFechaFin()),
                    "Venta registrada", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "La fecha es inválida. Use el formato DD/MM/AAAA (ej: 22/09/2026).",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
        } catch (GarantiaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar la venta", JOptionPane.ERROR_MESSAGE);
        }
    }
}
