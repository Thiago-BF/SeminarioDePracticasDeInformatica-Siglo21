package puntotecno;

/**
 * Subclase de Producto (HERENCIA): microondas, licuadoras, etc.
 * Garantía de 180 días.
 */
public class PequenoElectrodomestico extends Producto {

    private static final int PERIODO_DIAS = 180;

    public PequenoElectrodomestico(int idProducto, String nombre, double precio) {
        super(idProducto, nombre, precio);
    }

    @Override
    public int getPeriodoGarantiaDias() { return PERIODO_DIAS; }

    @Override
    public String getCategoria() { return "Pequeños electrodomésticos"; }
}
