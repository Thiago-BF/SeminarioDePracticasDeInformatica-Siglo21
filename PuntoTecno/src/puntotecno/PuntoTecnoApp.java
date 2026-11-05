package puntotecno;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import puntotecno.controlador.ControladorPuntoTecno;
import puntotecno.persistencia.ConexionBD;
import puntotecno.persistencia.ModeloMySQL;
import puntotecno.persistencia.PersistenciaException;

public class PuntoTecnoApp {

    public static void main(String[] args) {
        // Usa el aspecto visual del sistema operativo (si falla, queda el aspecto por defecto)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el aspecto del sistema: " + e.getMessage());
        }

        // Las ventanas Swing se crean en el hilo de eventos
        SwingUtilities.invokeLater(() -> {
            try {
                ConexionBD conexion = ConexionBD.desdeArchivo(ConexionBD.ARCHIVO_POR_DEFECTO);
                PuntoTecno sistema = new PuntoTecno("PuntoTecno");
                ControladorPuntoTecno controlador = new ControladorPuntoTecno(sistema, new ModeloMySQL(conexion));

                List<String> advertencias = controlador.iniciar();   // lee MySQL y carga las listas
                System.out.println("[PuntoTecno] Datos cargados desde MySQL. Garantías con estado corregido: "
                        + controlador.getGarantiasActualizadas());

                if (!advertencias.isEmpty()) {
                    JOptionPane.showMessageDialog(null, String.join("\n", advertencias),
                            "Períodos de garantía distintos entre la base y el programa", JOptionPane.WARNING_MESSAGE);
                }
                new MenuPrincipal(controlador).setVisible(true);

            } catch (PersistenciaException e) {
                Mensajes.errorBaseDeDatos(null, e);
                System.exit(1);
            }
        });
    }
}
