package puntotecno;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Clase utilitaria: convierte fechas e importes a texto (y viceversa) para la interfaz.
 * Todos sus métodos son estáticos, por eso no se pueden crear objetos de esta clase.
 */
public class Formato {

    // Formato DD/MM/AAAA. STRICT rechaza fechas imposibles (ej: 31/02/2026)
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    // Constructor privado: impide instanciar la clase
    private Formato() { }

    /** Convierte un LocalDate a texto "dd/MM/aaaa". */
    public static String fecha(LocalDate fecha) {
        return fecha.format(FECHA);
    }

    /** Convierte texto "dd/MM/aaaa" a LocalDate. Lanza DateTimeParseException si es inválido. */
    public static LocalDate parseFecha(String texto) throws DateTimeParseException {
        return LocalDate.parse(texto.trim(), FECHA);
    }

    /** Convierte un importe a texto, ej: "$ 850.000,00". */
    public static String dinero(double importe) {
        return String.format(Locale.forLanguageTag("es-AR"), "$ %,.2f", importe);
    }
}
