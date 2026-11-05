package puntotecno.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import puntotecno.Cliente;
import puntotecno.LineaBlanca;
import puntotecno.PequenoElectrodomestico;
import puntotecno.Producto;
import puntotecno.TecnologiaHogar;
import puntotecno.Vendedor;
import puntotecno.Venta;

/**
 * MODELO de acceso a datos (patrón MVC): único lugar del sistema que conoce JDBC y SQL.
 * Implementa RepositorioPuntoTecno contra MySQL.
 *
 * Reglas que cumplen todos los métodos:
 *  - Todas las consultas con valores variables usan PreparedStatement (parámetros "?").
 *  - Connection, PreparedStatement y ResultSet se abren en un try-with-resources, por lo
 *    que se cierran solos aunque ocurra un error.
 *  - Los SQLException no se ocultan: se convierten en PersistenciaException (mensaje claro
 *    para el usuario + la excepción original como causa).
 */
public class ModeloMySQL implements RepositorioPuntoTecno {

    private final ConexionBD conexion;

    public ModeloMySQL(ConexionBD conexion) {
        this.conexion = conexion;
    }

    // ===================================================================
    //  CONSULTAS (SELECT) -> los resultados se devuelven en ArrayList
    // ===================================================================

    @Override
    public List<Cliente> listarClientes() throws PersistenciaException {
        String sql = "SELECT id_cliente, nombre, contacto FROM clientes ORDER BY id_cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("contacto")));
            }
        } catch (SQLException e) {
            throw error("No se pudieron leer los clientes.", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("La tabla clientes tiene un registro con datos inválidos: " + e.getMessage(), e);
        }
        return clientes;
    }

    @Override
    public List<Vendedor> listarVendedores() throws PersistenciaException {
        String sql = "SELECT legajo, nombre FROM vendedores ORDER BY legajo";
        List<Vendedor> vendedores = new ArrayList<>();
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vendedores.add(new Vendedor(rs.getInt("legajo"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw error("No se pudieron leer los vendedores.", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("La tabla vendedores tiene un registro con datos inválidos: " + e.getMessage(), e);
        }
        return vendedores;
    }

    /** Lee los productos y, según su id_categoria, crea la subclase de Producto que corresponde. */
    @Override
    public List<Producto> listarProductos() throws PersistenciaException {
        String sql = "SELECT id_producto, nombre, precio, id_categoria FROM productos ORDER BY id_producto";
        List<Producto> productos = new ArrayList<>();
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(crearProducto(rs.getInt("id_categoria"), rs.getInt("id_producto"),
                        rs.getString("nombre"), rs.getDouble("precio")));
            }
        } catch (SQLException e) {
            throw error("No se pudieron leer los productos.", e);
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("La tabla productos tiene un registro inválido: " + e.getMessage(), e);
        }
        return productos;
    }

    @Override
    public List<RegistroVenta> listarVentas() throws PersistenciaException {
        String sql = "SELECT numero_venta, fecha, id_cliente, legajo, id_producto FROM ventas ORDER BY numero_venta";
        List<RegistroVenta> ventas = new ArrayList<>();
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ventas.add(new RegistroVenta(rs.getInt("numero_venta"), rs.getDate("fecha").toLocalDate(),
                        rs.getInt("id_cliente"), rs.getInt("legajo"), rs.getInt("id_producto")));
            }
        } catch (SQLException e) {
            throw error("No se pudieron leer las ventas.", e);
        }
        return ventas;
    }

    /**
     * Lee la tabla categorias en un ARREGLO de tamaño fijo: el sistema tiene exactamente
     * 3 categorías (una subclase de Producto por cada una). La posición i guarda el período
     * de garantía de la categoría con id = i + 1.
     */
    @Override
    public int[] periodosGarantiaPorCategoria() throws PersistenciaException {
        final int CANTIDAD_CATEGORIAS = 3;
        String sql = "SELECT id_categoria, periodo_dias FROM categorias";
        int[] periodos = new int[CANTIDAD_CATEGORIAS];
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_categoria");
                if (id < 1 || id > CANTIDAD_CATEGORIAS) {
                    throw new PersistenciaException("La tabla categorias tiene una categoría con id " + id
                            + ", pero el sistema solo conoce las categorías 1 a " + CANTIDAD_CATEGORIAS + ".");
                }
                periodos[id - 1] = rs.getInt("periodo_dias");
            }
        } catch (SQLException e) {
            throw error("No se pudieron leer las categorías.", e);
        }
        return periodos;
    }

    // ===================================================================
    //  IDENTIFICADORES: siguiente número libre según lo guardado en MySQL
    // ===================================================================

    @Override
    public int siguienteIdCliente() throws PersistenciaException {
        return siguiente("SELECT COALESCE(MAX(id_cliente), 0) + 1 FROM clientes");
    }

    @Override
    public int siguienteIdProducto() throws PersistenciaException {
        return siguiente("SELECT COALESCE(MAX(id_producto), 0) + 1 FROM productos");
    }

    @Override
    public int siguienteNumeroVenta() throws PersistenciaException {
        return siguiente("SELECT COALESCE(MAX(numero_venta), 0) + 1 FROM ventas");
    }

    private int siguiente(String sql) throws PersistenciaException {
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw error("No se pudo obtener el siguiente identificador.", e);
        }
    }

    // ===================================================================
    //  ESCRITURAS (INSERT / UPDATE)
    // ===================================================================

    @Override
    public void insertarCliente(Cliente cliente) throws PersistenciaException {
        String sql = "INSERT INTO clientes (id_cliente, nombre, contacto) VALUES (?, ?, ?)";
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cliente.getIdCliente());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getContacto());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw error("No se pudo guardar el cliente.", e);
        }
    }

    @Override
    public void actualizarCliente(Cliente cliente) throws PersistenciaException {
        String sql = "UPDATE clientes SET nombre = ?, contacto = ? WHERE id_cliente = ?";
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getContacto());
            ps.setInt(3, cliente.getIdCliente());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                // El UPDATE no falló, pero no encontró el cliente: se informa en lugar de ignorarlo
                throw new PersistenciaException("No existe un cliente con id " + cliente.getIdCliente() + " en la base de datos.");
            }
        } catch (SQLException e) {
            throw error("No se pudieron actualizar los datos del cliente.", e);
        }
    }

    @Override
    public void insertarProducto(Producto producto) throws PersistenciaException {
        String sql = "INSERT INTO productos (id_producto, nombre, precio, id_categoria) VALUES (?, ?, ?, ?)";
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getIdProducto());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getIdCategoria());   // POLIMORFISMO: cada subclase conoce su id de categoría
            ps.executeUpdate();
        } catch (SQLException e) {
            throw error("No se pudo guardar el producto.", e);
        }
    }

    /**
     * Guarda la venta y su garantía en una TRANSACCIÓN: se desactiva el auto-commit,
     * se hacen los dos INSERT y recién entonces se confirma (commit). Si algo falla se
     * deshace todo (rollback), para no dejar una venta sin garantía.
     */
    @Override
    public void insertarVenta(Venta venta) throws PersistenciaException {
        String sqlVenta = "INSERT INTO ventas (numero_venta, fecha, id_cliente, legajo, id_producto) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = conexion.obtener()) {
            try {
                con.setAutoCommit(false);

                try (PreparedStatement psVenta = con.prepareStatement(sqlVenta)) {
                    psVenta.setInt(1, venta.getNumero());
                    psVenta.setDate(2, Date.valueOf(venta.getFecha()));
                    psVenta.setInt(3, venta.getCliente().getIdCliente());
                    psVenta.setInt(4, venta.getVendedor().getLegajo());
                    psVenta.setInt(5, venta.getProducto().getIdProducto());
                    psVenta.executeUpdate();
                }

                int idGarantia;
                try (PreparedStatement psId = con.prepareStatement("SELECT COALESCE(MAX(id_garantia), 0) + 1 FROM garantias");
                     ResultSet rs = psId.executeQuery()) {
                    rs.next();
                    idGarantia = rs.getInt(1);
                }

                try (PreparedStatement psGarantia = con.prepareStatement(
                        "INSERT INTO garantias (id_garantia, numero_venta, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?)")) {
                    psGarantia.setInt(1, idGarantia);
                    psGarantia.setInt(2, venta.getNumero());
                    psGarantia.setDate(3, Date.valueOf(venta.getGarantia().getFechaInicio()));
                    psGarantia.setDate(4, Date.valueOf(venta.getGarantia().getFechaFin()));
                    psGarantia.setString(5, venta.getGarantia().getEstado());
                    psGarantia.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);   // el error del rollback no tapa el error original
                }
                throw error("No se pudo registrar la venta. No se guardó ningún dato.", e);
            }
        } catch (SQLException e) {
            // Solo puede venir del cierre de la conexión
            throw error("Error al cerrar la conexión con la base de datos.", e);
        }
    }

    /**
     * UPDATE masivo: deja la columna estado de cada garantía coherente con la fecha de hoy
     * (corrige estados desactualizados, como el de la venta 4 del script del TP2).
     */
    @Override
    public int sincronizarEstadosGarantias() throws PersistenciaException {
        String sql = "UPDATE garantias SET estado = CASE WHEN fecha_fin >= ? THEN 'Vigente' ELSE 'Vencida' END "
                + "WHERE estado IS NULL OR estado <> CASE WHEN fecha_fin >= ? THEN 'Vigente' ELSE 'Vencida' END";
        Date hoy = Date.valueOf(LocalDate.now());
        try (Connection con = conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, hoy);
            ps.setDate(2, hoy);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw error("No se pudo actualizar el estado de las garantías.", e);
        }
    }

    // ===================================================================
    //  AUXILIARES
    // ===================================================================

    /** Crea la subclase de Producto que corresponde al id_categoria guardado en MySQL. */
    private Producto crearProducto(int idCategoria, int id, String nombre, double precio) throws PersistenciaException {
        switch (idCategoria) {
            case 1:  return new LineaBlanca(id, nombre, precio);
            case 2:  return new PequenoElectrodomestico(id, nombre, precio);
            case 3:  return new TecnologiaHogar(id, nombre, precio);
            default: throw new PersistenciaException("El producto " + id + " tiene una categoría desconocida (" + idCategoria + ").");
        }
    }

    /** Convierte un SQLException en PersistenciaException, con un mensaje claro según el tipo de error. */
    private PersistenciaException error(String accion, SQLException e) {
        String sqlState = e.getSQLState() == null ? "" : e.getSQLState();
        if (sqlState.startsWith("23")) {
            // Violación de integridad: clave repetida o referencia a un registro que no existe
            return new PersistenciaException(accion + "\nYa existe un registro con ese identificador "
                    + "o se hace referencia a un dato inexistente.", e);
        }
        if (sqlState.startsWith("08")) {
            return new PersistenciaException(accion + "\nSe perdió la conexión con el servidor MySQL.", e);
        }
        return new PersistenciaException(accion, e);
    }
}
