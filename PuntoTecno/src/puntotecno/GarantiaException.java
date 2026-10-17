package puntotecno;

/**
 * Excepción propia del sistema (equivalente a TurbinaExcepcion del ejemplo ECOviento).
 * Se lanza cuando no se puede crear o calcular correctamente una venta o una garantía
 * (por ejemplo: datos faltantes o fechas incoherentes).
 * Es una excepción "checked": obliga a quien la usa a tratarla con try/catch.
 */
public class GarantiaException extends Exception {

    // Constructor: recibe el mensaje que se mostrará al usuario
    public GarantiaException(String mensaje) {
        super(mensaje);
    }
}
