package puntotecno;

import puntotecno.controlador.ControladorPuntoTecno;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana principal con el MENÚ DE SELECCIÓN (equivalente a GestionCentralEolica del ejemplo).
 * Desde acá se navega a todas las funciones del sistema. Cada botón abre una pantalla (JDialog).
 */
public class MenuPrincipal extends JFrame {

    private ControladorPuntoTecno controlador;

    // Constructor: recibe el controlador que van a compartir todas las pantallas
    public MenuPrincipal(ControladorPuntoTecno controlador) {
        super("PuntoTecno — Sistema de Gestión de Ventas y Garantías");
        this.controlador = controlador;
        construirPantalla();
        setSize(430, 460);
        setLocationRelativeTo(null);   // centrada en la pantalla

        // Al cerrar con la "X" se pide confirmación, igual que la opción Salir
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });
    }

    // ---------- Armado del menú ----------
    private void construirPantalla() {
        JLabel titulo = new JLabel("MENÚ PRINCIPAL — " + controlador.getNombre(), SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JButton btnVenta = crearBoton("1. Registrar venta");
        JButton btnProducto = crearBoton("2. Dar de alta producto");
        JButton btnCliente = crearBoton("3. Dar de alta cliente");
        JButton btnModificar = crearBoton("4. Modificar cliente");
        JButton btnGarantia = crearBoton("5. Consultar estado de garantía");
        JButton btnListar = crearBoton("6. Listar ventas");
        JButton btnSalir = crearBoton("7. Salir");

        btnVenta.addActionListener(e -> new VentanaRegistrarVenta(this, controlador).setVisible(true));
        btnProducto.addActionListener(e -> new VentanaAltaProducto(this, controlador).setVisible(true));
        btnCliente.addActionListener(e -> new VentanaAltaCliente(this, controlador).setVisible(true));
        btnModificar.addActionListener(e -> new VentanaModificarCliente(this, controlador).setVisible(true));
        btnGarantia.addActionListener(e -> new VentanaConsultaGarantia(this, controlador).setVisible(true));
        btnListar.addActionListener(e -> new VentanaListarVentas(this, controlador).setVisible(true));
        btnSalir.addActionListener(e -> salir());

        JPanel botones = new JPanel(new GridLayout(0, 1, 0, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
        botones.add(btnVenta);
        botones.add(btnProducto);
        botones.add(btnCliente);
        botones.add(btnModificar);
        botones.add(btnGarantia);
        botones.add(btnListar);
        botones.add(btnSalir);

        setLayout(new BorderLayout());
        add(titulo, BorderLayout.NORTH);
        add(botones, BorderLayout.CENTER);
    }

    // Auxiliar: crea un botón con el mismo estilo para todo el menú
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(300, 38));
        boton.setFont(boton.getFont().deriveFont(14f));
        return boton;
    }

    // ---------- Opción Salir ----------
    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea salir del sistema?",
                "Salir", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
