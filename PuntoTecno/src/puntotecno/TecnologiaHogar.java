package puntotecno;

/**
 * Subclase de Producto (HERENCIA): notebooks, televisores, auriculares, etc.
 * Garantía de 90 días.
 */
public class TecnologiaHogar extends Producto {

    public static final int PERIODO_DIAS = 90;

    public TecnologiaHogar(int idProducto, String nombre, double precio) {
        super(idProducto, nombre, precio);
    }

    @Override
    public int getPeriodoGarantiaDias() { return PERIODO_DIAS; }

    @Override
    public int getIdCategoria() { return 3; }   // id en la tabla categorias

    @Override
    public String getCategoria() { return "Tecnología del hogar"; }
}
