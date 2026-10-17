package puntotecno;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Componente reutilizable para armar formularios: una columna angosta de etiquetas
 * a la izquierda y una columna de campos a la derecha, alineadas fila por fila.
 * Se usa con agregar("Nombre:", campo).
 */
public class PanelFormulario extends JPanel {

    private JPanel columnaEtiquetas;
    private JPanel columnaCampos;

    public PanelFormulario() {
        super(new BorderLayout(12, 0));
        columnaEtiquetas = new JPanel(new GridLayout(0, 1, 0, 10));
        columnaCampos = new JPanel(new GridLayout(0, 1, 0, 10));
        add(columnaEtiquetas, BorderLayout.WEST);
        add(columnaCampos, BorderLayout.CENTER);
    }

    /** Agrega una fila: etiqueta + campo. */
    public void agregar(String etiqueta, Component campo) {
        columnaEtiquetas.add(new JLabel(etiqueta));
        columnaCampos.add(campo);
    }
}
