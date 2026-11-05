package puntotecno.persistencia;

/**
 * Excepción propia para los errores al interactuar con la base de datos MySQL.
 * Es "checked": quien la usa está obligado a tratarla con try/catch.
 *
 * Separa dos cosas:
 *  - getMessage(): texto comprensible que se le muestra al usuario.
 *  - getCause(): la excepción técnica original (SQLException), que se conserva
 *    para diagnosticar el problema (se imprime en la consola con el código de error).
 */
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensajeParaElUsuario, Throwable causa) {
        super(mensajeParaElUsuario, causa);
    }

    public PersistenciaException(String mensajeParaElUsuario) {
        super(mensajeParaElUsuario);
    }

    /** Devuelve el detalle técnico (tipo, mensaje, SQLState y código de error) para la consola. */
    public String getDetalleTecnico() {
        Throwable causa = getCause();
        if (causa == null) {
            return "(sin detalle técnico)";
        }
        String detalle = causa.getClass().getSimpleName() + ": " + causa.getMessage();
        if (causa instanceof java.sql.SQLException) {
            java.sql.SQLException sql = (java.sql.SQLException) causa;
            detalle += " [SQLState=" + sql.getSQLState() + ", código=" + sql.getErrorCode() + "]";
        }
        return detalle;
    }
}
