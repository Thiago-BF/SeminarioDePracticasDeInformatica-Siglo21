package puntotecno;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Pantalla "Modificar cliente" (RFS10). */
public class VentanaModificarCliente extends JDialog {

    private PuntoTecno sistema;
    private JComboBox<Cliente> comboCliente;
    private JTextField txtNombre;
    private JTextField txtContacto;

    public VentanaModificarCliente(Window owner, PuntoTecno sistema) {
        super(owner, "PuntoTecno — Modificar cliente", ModalityType.APPLICATION_MODAL);
        this.sistema = sistema;
        construirPantalla();
        cargarCombo(0);
        pack();
        setLocationRelativeTo(owner);
    }

    // ---------- Armado de la pantalla ----------
    private void construirPantalla() {
        comboCliente = new JComboBox<>();
        txtNombre = new JTextField(22);
        txtContacto = new JTextField(22);

        // Al elegir otro cliente, se cargan sus datos en los campos
        comboCliente.addActionListener(e -> mostrarDatosDelSeleccionado());

        PanelFormulario formulario = new PanelFormulario();
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        formulario.agregar("Cliente:", comboCliente);
        formulario.agregar("Nombre:", txtNombre);
        formulario.agregar("Contacto (teléfono):", txtContacto);

        JButton btnGuardar = new JButton("Guardar cambios");
        JButton btnCerrar = new JButton("Cerrar");
        btnGuardar.addActionListener(e -> guardar());
        btnCerrar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnCerrar);

        setLayout(new BorderLayout());
        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    /** Llena el combo con los clientes actuales y deja seleccionado el índice indicado. */
    private void cargarCombo(int indiceASeleccionar) {
        comboCliente.setModel(new DefaultComboBoxModel<>(sistema.getClientes().toArray(new Cliente[0])));
        if (comboCliente.getItemCount() > 0) {
            comboCliente.setSelectedIndex(indiceASeleccionar);
        }
        mostrarDatosDelSeleccionado();
    }

    private void mostrarDatosDelSeleccionado() {
        Cliente c = (Cliente) comboCliente.getSelectedItem();
        if (c != null) {
            txtNombre.setText(c.getNombre());
            txtContacto.setText(c.getContacto());
        }
    }

    // ---------- Acción del botón Guardar cambios ----------
    private void guardar() {
        Cliente elegido = (Cliente) comboCliente.getSelectedItem();
        if (elegido == null) {
            JOptionPane.showMessageDialog(this, "No hay clientes para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int indice = comboCliente.getSelectedIndex();
            sistema.modificarCliente(elegido.getIdCliente(), txtNombre.getText(), txtContacto.getText());
            cargarCombo(indice);   // refresca el combo para que muestre el nombre actualizado
            JOptionPane.showMessageDialog(this, "Datos del cliente actualizados.",
                    "Modificar cliente", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }
}
