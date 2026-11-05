package puntotecno.persistencia;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuración y apertura de conexiones JDBC a MySQL.
 *
 * Los datos de conexión (url, usuario y contraseña) NO están escritos en el código:
 * se leen del archivo config/conexion.properties, que cada persona crea en su equipo
 * a partir de config/conexion.properties y que no se sube a GitHub (.gitignore).
 *
 * Cada llamada a obtener() abre una conexión nueva. Quien la recibe debe cerrarla,
 * por eso en ModeloMySQL siempre se usa try-with-resources.
 */
public class ConexionBD {

    public static final String ARCHIVO_POR_DEFECTO = "config/conexion.properties";

    private final String url;
    private final String usuario;
    private final String clave;

    public ConexionBD(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
    }

    /** Crea la configuración leyendo un archivo .properties. */
    public static ConexionBD desdeArchivo(String ruta) throws ConexionException {
        Properties propiedades = new Properties();
        try (FileInputStream entrada = new FileInputStream(ruta)) {
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new ConexionException("No se pudo leer el archivo de configuración \"" + ruta + "\".\n"
                    + "Copie config/conexion.properties como config/conexion.properties "
                    + "y complete su usuario y contraseña de MySQL.", e);
        }

        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.usuario");
        String clave = propiedades.getProperty("db.clave", "");   // la clave puede estar vacía (XAMPP)
        if (url == null || url.trim().isEmpty() || usuario == null || usuario.trim().isEmpty()) {
            throw new ConexionException("El archivo \"" + ruta + "\" debe tener las propiedades db.url y db.usuario.");
        }
        return new ConexionBD(url.trim(), usuario.trim(), clave);
    }

    /** Abre una conexión nueva. El que la pide es responsable de cerrarla. */
    public Connection obtener() throws ConexionException {
        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            throw new ConexionException(mensajeAmigable(e), e);
        }
    }

    // Traduce los errores técnicos más comunes a un mensaje que el usuario pueda entender.
    // El orden importa: primero los casos específicos y al final el genérico de "no hay conexión".
    private String mensajeAmigable(SQLException e) {
        String sqlState = e.getSQLState() == null ? "" : e.getSQLState();
        String texto = e.getMessage() == null ? "" : e.getMessage();

        if (texto.contains("No suitable driver")) {
            return "No se encontró el driver JDBC de MySQL.\nColoque el archivo .jar en la carpeta lib.";
        }
        if ("28000".equals(sqlState)) {
            return "MySQL rechazó el usuario o la contraseña.\nRevise config/conexion.properties.";
        }
        if ("42000".equals(sqlState) || "3D000".equals(sqlState)) {
            return "La base de datos indicada no existe.\nImporte sql/puntotecno_tp4.sql en MySQL.";
        }
        if (sqlState.startsWith("08") || texto.toLowerCase().contains("connect")) {
            return "No se pudo conectar con el servidor MySQL.\n"
                    + "Verifique que MySQL esté iniciado (por ejemplo, desde XAMPP) y que la url sea correcta.";
        }
        return "No se pudo abrir la conexión con la base de datos.";
    }
}
