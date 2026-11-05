package puntotecno;

import java.awt.Component;
import javax.swing.JOptionPane;
import puntotecno.persistencia.ConexionException;
import puntotecno.persistencia.PersistenciaException;

/**
 * Clase utilitaria de la Vista: muestra al usuario los errores de la base de datos de forma
 * uniforme. El mensaje es comprensible; el detalle técnico se imprime en la consola para
 * poder diagnosticar el problema sin mostrárselo al usuario.
 */
public class Mensajes {

    private Mensajes() { }

    public static void errorBaseDeDatos(Component padre, PersistenciaException ex) {
        System.err.println("[PuntoTecno] Error de base de datos: " + ex.getMessage().replace('\n', ' '));
        System.err.println("[PuntoTecno]   Detalle técnico: " + ex.getDetalleTecnico());

        boolean sinConexion = ex instanceof ConexionException;
        JOptionPane.showMessageDialog(padre, ex.getMessage(),
                sinConexion ? "Sin conexión con MySQL" : "Error en la base de datos",
                JOptionPane.ERROR_MESSAGE);
    }
}
