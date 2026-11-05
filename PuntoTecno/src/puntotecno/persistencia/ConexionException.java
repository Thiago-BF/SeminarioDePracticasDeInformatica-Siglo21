package puntotecno.persistencia;

/**
 * Subclase de PersistenciaException para los problemas al ESTABLECER la conexión
 * (servidor apagado, usuario o contraseña incorrectos, base inexistente, driver
 * faltante o archivo de configuración ausente). Permite que la interfaz distinga
 * "no hay conexión" de "falló una consulta".
 */
public class ConexionException extends PersistenciaException {

    public ConexionException(String mensajeParaElUsuario, Throwable causa) {
        super(mensajeParaElUsuario, causa);
    }

    public ConexionException(String mensajeParaElUsuario) {
        super(mensajeParaElUsuario);
    }
}
