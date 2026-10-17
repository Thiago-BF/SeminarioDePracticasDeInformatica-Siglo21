package puntotecno;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Pantalla "Dar de alta cliente" (RFS05). */
public class VentanaAltaCliente extends JDialog {

    private PuntoTecno sistema;
    private JTextField txtNombre;
    private JTextField txtContacto;

    public VentanaAltaCliente(Window owner, PuntoTecno sistema) {
        super(owner, "PuntoTecno — Alta de cliente", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        construirPantalla();
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- Armado de la pantalla ----------
    private void construirPantalla() {
        txtNombre = new JTextField(22);
        txtContacto = new JTextField(22);

        PanelFormulario formulario = new PanelFormulario();
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        formulario.agregar("Nombre:", txtNombre);
        formulario.agregar("Contacto (teléfono):", txtContacto);

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
            Cliente nuevo = sistema.agregarCliente(txtNombre.getText(), txtContacto.getText());
            JOptionPane.showMessageDialog(this,
                    "Cliente registrado correctamente.\nN° de cliente: " + nuevo.getIdCliente(),
                    "Alta de cliente", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            // Datos inválidos (nombre o contacto vacío): se informa sin cerrar la pantalla
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }
}
