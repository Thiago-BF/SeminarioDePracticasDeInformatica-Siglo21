package puntotecno.persistencia;

import java.time.LocalDate;

/**
 * Datos "crudos" de una fila de la tabla ventas, tal como vienen de MySQL: solo los
 * identificadores del cliente, el vendedor y el producto. El controlador los convierte
 * en un objeto Venta usando las listas ya cargadas, de modo que todos los objetos del
 * sistema compartan las mismas instancias de Cliente, Vendedor y Producto.
 */
public final class RegistroVenta {

    private final int numero;
    private final LocalDate fecha;
    private final int idCliente;
    private final int legajo;
    private final int idProducto;

    public RegistroVenta(int numero, LocalDate fecha, int idCliente, int legajo, int idProducto) {
        this.numero = numero;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.legajo = legajo;
        this.idProducto = idProducto;
    }

    public int getNumero()      { return numero; }
    public LocalDate getFecha() { return fecha; }
    public int getIdCliente()   { return idCliente; }
    public int getLegajo()      { return legajo; }
    public int getIdProducto()  { return idProducto; }
}
