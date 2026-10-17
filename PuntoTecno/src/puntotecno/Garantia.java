package puntotecno;

import java.time.LocalDate;

/**
 * Entidad Garantia. Conoce su fecha de inicio y de fin, y sabe calcular
 * si está vigente o vencida. El estado NO se guarda: se calcula comparando
 * contra la fecha de consulta (igual que la consulta 4.3 del TP2 con CURDATE()).
 */
public class Garantia {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Constructor: valida que las fechas sean coherentes
    public Garantia(LocalDate fechaInicio, LocalDate fechaFin) throws GarantiaException {
        if (fechaInicio == null || fechaFin == null) {
            throw new GarantiaException("La garantía necesita fecha de inicio y de fin.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new GarantiaException("La fecha de fin de la garantía no puede ser anterior a la de inicio.");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    /**
     * Determina el estado de la garantía en una fecha dada (RFS06).
     * Regla: el día exacto del vencimiento todavía es "Vigente"
     * (coincide con los casos de prueba CP02, CP05 y CP08 del TP2).
     */
    public String calcularVigencia(LocalDate fechaConsulta) {
        if (fechaConsulta.isAfter(fechaFin)) {
            return "Vencida";
        }
        return "Vigente";
    }

    /** Estado a día de hoy. */
    public String getEstado() {
        return calcularVigencia(LocalDate.now());
    }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin()    { return fechaFin; }
}
