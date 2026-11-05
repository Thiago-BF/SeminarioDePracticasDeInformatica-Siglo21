package puntotecno.persistencia;

import java.util.List;
import puntotecno.Cliente;
import puntotecno.Producto;
import puntotecno.Vendedor;
import puntotecno.Venta;

/**
 * INTERFAZ que define QUÉ operaciones de persistencia necesita el sistema, sin decir CÓMO
 * se realizan. ModeloMySQL es la implementación que lo hace con JDBC.
 *
 * Por qué una interfaz: el controlador depende de este "contrato" y no de JDBC, de modo
 * que si mañana los datos vinieran de otro motor (u otra clase de prueba), el controlador
 * y las pantallas no se modificarían.
 */
public interface RepositorioPuntoTecno {

    // ----- Consultas (SELECT) -----
    List<Cliente> listarClientes() throws PersistenciaException;
    List<Vendedor> listarVendedores() throws PersistenciaException;
    List<Producto> listarProductos() throws PersistenciaException;
    List<RegistroVenta> listarVentas() throws PersistenciaException;

    /** Períodos de garantía de la tabla categorias; la posición i corresponde a id_categoria = i + 1. */
    int[] periodosGarantiaPorCategoria() throws PersistenciaException;

    // ----- Identificadores -----
    int siguienteIdCliente() throws PersistenciaException;
    int siguienteIdProducto() throws PersistenciaException;
    int siguienteNumeroVenta() throws PersistenciaException;

    // ----- Escrituras (INSERT / UPDATE) -----
    void insertarCliente(Cliente cliente) throws PersistenciaException;
    void actualizarCliente(Cliente cliente) throws PersistenciaException;
    void insertarProducto(Producto producto) throws PersistenciaException;

    /** Inserta la venta y su garantía en una única transacción (o se guardan las dos o ninguna). */
    void insertarVenta(Venta venta) throws PersistenciaException;

    /** Recalcula en MySQL la columna estado de todas las garantías. Devuelve cuántas filas cambiaron. */
    int sincronizarEstadosGarantias() throws PersistenciaException;
}
