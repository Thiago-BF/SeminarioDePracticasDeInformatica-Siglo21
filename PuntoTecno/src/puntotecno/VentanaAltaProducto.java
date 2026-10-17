package puntotecno;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Pantalla "Dar de alta producto" (RFS04). La categoría elegida decide qué subclase se crea. */
public class VentanaAltaProducto extends JDialog {

    private PuntoTecno sistema;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> comboCategoria;

    public VentanaAltaProducto(Window owner, PuntoTecno sistema) {
        super(owner, "PuntoTecno — Alta de producto", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        construirPantalla();
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- Armado de la pantalla ----------
    private void construirPantalla() {
        txtNombre = new JTextField(22);
        txtPrecio = new JTextField(22);
        comboCategoria = new JComboBox<>(new String[] {
            "Línea blanca (365 días)",
            "Pequeños electrodomésticos (180 días)",
            "Tecnología del hogar (90 días)"
        });

        PanelFormulario formulario = new PanelFormulario();
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        formulario.agregar("Nombre:", txtNombre);
        formulario.agregar("Precio ($):", txtPrecio);
        formulario.agregar("Categoría:", comboCategoria);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        setLayout(new BorderLayout());
        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnGuardar);
    }

    // ---------- Acción del botón Guardar ----------
    private void guardar() {
        try {
            // Texto -> número. Si no es numérico lanza NumberFormatException
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            int id = sistema.siguienteIdProducto();
            String nombre = txtNombre.getText();

            // POLIMORFISMO: se crea la subclase según la categoría, pero se guarda como Producto
            Producto nuevo;
            switch (comboCategoria.getSelectedIndex()) {
                case 0:  nuevo = new LineaBlanca(id, nombre, precio); break;
                case 1:  nuevo = new PequenoElectrodomestico(id, nombre, precio); break;
                default: nuevo = new TecnologiaHogar(id, nombre, precio); break;
            }
            sistema.agregarProducto(nuevo);

            JOptionPane.showMessageDialog(this,
                    "Producto registrado correctamente.\n" + nuevo + "\nGarantía: " + nuevo.getPeriodoGarantiaDias() + " días",
                    "Alta de producto", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número (ej: 150000 o 150000.50).",
                    "Precio inválido", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // Nombre vacío o precio negativo (validados dentro de Producto)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }
}
