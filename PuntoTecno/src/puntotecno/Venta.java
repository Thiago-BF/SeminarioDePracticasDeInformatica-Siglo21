package puntotecno;

import java.time.LocalDate;

/**
 * Entidad Venta. Relaciona Cliente, Vendedor y Producto, y GENERA su Garantia
 * (caso de uso incluido "Calcular garantía"): fechaFin = fecha + días de la categoría.
 */
public class Venta {

    private int numero;
    private LocalDate fecha;
    private Cliente cliente;
    private Vendedor vendedor;
    private Producto producto;
    private Garantia garantia;

    // Constructor: valida los datos y crea la garantía automáticamente
    public Venta(int numero, LocalDate fecha, Cliente cliente, Vendedor vendedor, Producto producto)
            throws GarantiaException {
        if (fecha == null || cliente == null || vendedor == null || producto == null) {
            throw new GarantiaException("La venta necesita fecha, cliente, vendedor y producto.");
        }
        this.numero = numero;
        this.fecha = fecha;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.producto = producto;

        // POLIMORFISMO: no sabemos qué categoría es el producto, él responde su propio período
        LocalDate fin = fecha.plusDays(producto.getPeriodoGarantiaDias());
        this.garantia = new Garantia(fecha, fin);
    }

    public int getNumero()        { return numero; }
    public LocalDate getFecha()   { return fecha; }
    public Cliente getCliente()   { return cliente; }
    public Vendedor getVendedor() { return vendedor; }
    public Producto getProducto() { return producto; }
    public Garantia getGarantia() { return garantia; }

    @Override
    public String toString() {
        return "Venta #" + numero + " - " + cliente.getNombre() + " - " + producto.getNombre();
    }
}
