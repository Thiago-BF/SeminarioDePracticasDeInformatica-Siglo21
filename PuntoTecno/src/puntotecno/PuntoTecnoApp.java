package puntotecno;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase de arranque del sistema (contiene el método main).
 * Crea el sistema con sus datos precargados y abre el menú principal.
 */
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
            PuntoTecno sistema = new PuntoTecno("PuntoTecno");
            new MenuPrincipal(sistema).setVisible(true);
        });
    }
}
