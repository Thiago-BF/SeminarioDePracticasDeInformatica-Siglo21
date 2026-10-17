package puntotecno;

/**
 * Subclase de Producto (HERENCIA): heladeras, lavarropas, etc.
 * Garantía de 365 días (según la tabla categorias del TP2).
 */
public class LineaBlanca extends Producto {

    // Constante: el período de esta categoría no cambia
    private static final int PERIODO_DIAS = 365;

    // Constructor: delega en el de la clase padre con super(...)
    public LineaBlanca(int idProducto, String nombre, double precio) {
        super(idProducto, nombre, precio);
    }

    @Override
    public int getPeriodoGarantiaDias() { return PERIODO_DIAS; }

    @Override
    public String getCategoria() { return "Línea blanca"; }
}
