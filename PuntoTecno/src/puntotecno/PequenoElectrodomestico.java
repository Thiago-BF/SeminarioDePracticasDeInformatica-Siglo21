package puntotecno;

/**
 * Subclase de Producto (HERENCIA): microondas, licuadoras, etc.
 * Garantía de 180 días.
 */
public class PequenoElectrodomestico extends Producto {

    public static final int PERIODO_DIAS = 180;

    public PequenoElectrodomestico(int idProducto, String nombre, double precio) {
        super(idProducto, nombre, precio);
    }

    @Override
    public int getPeriodoGarantiaDias() { return PERIODO_DIAS; }

    @Override
    public int getIdCategoria() { return 2; }   // id en la tabla categorias

    @Override
    public String getCategoria() { return "Pequeños electrodomésticos"; }
}
