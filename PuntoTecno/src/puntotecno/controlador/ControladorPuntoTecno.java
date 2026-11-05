package puntotecno.controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import puntotecno.Cliente;
import puntotecno.GarantiaException;
import puntotecno.LineaBlanca;
import puntotecno.PequenoElectrodomestico;
import puntotecno.Producto;
import puntotecno.PuntoTecno;
import puntotecno.TecnologiaHogar;
import puntotecno.Vendedor;
import puntotecno.Venta;
import puntotecno.persistencia.PersistenciaException;
import puntotecno.persistencia.RegistroVenta;
import puntotecno.persistencia.RepositorioPuntoTecno;

/**
 * CONTROLADOR (patrón MVC). Se ubica entre la Vista (MenuPrincipal y las Ventana*) y el
 * Modelo (PuntoTecno en memoria + RepositorioPuntoTecno contra MySQL).
 *
 * Qué hace:
 *  - Recibe las acciones que piden las pantallas (alta de cliente, registrar venta, etc.).
 *  - Para las ESCRITURAS aplica siempre el mismo orden: 1) valida creando el objeto,
 *    2) lo guarda en MySQL, 3) recién entonces lo agrega a las listas en memoria.
 *    Así, si MySQL falla, las listas no quedan con datos que no existen en la base.
 *  - Para las CONSULTAS usa las listas ya cargadas desde MySQL al iniciar.
 *
 * Depende de la INTERFAZ RepositorioPuntoTecno, no de JDBC.
 */
public class ControladorPuntoTecno {

    private final PuntoTecno sistema;
    private final RepositorioPuntoTecno repositorio;
    private int garantiasActualizadas;

    public ControladorPuntoTecno(PuntoTecno sistema, RepositorioPuntoTecno repositorio) {
        this.sistema = sistema;
        this.repositorio = repositorio;
    }

    // ===================================================================
    //  INICIO: lectura de MySQL
    // ===================================================================

    /**
     * Prepara el sistema al arrancar: verifica las categorías, actualiza los estados de las
     * garantías en MySQL y carga todos los datos en las listas.
     * @return advertencias encontradas al verificar (vacía si todo es coherente).
     */
    public List<String> iniciar() throws PersistenciaException {
        List<String> advertencias = verificarCategorias();

        garantiasActualizadas = repositorio.sincronizarEstadosGarantias();

        List<Cliente> clientes = repositorio.listarClientes();
        List<Vendedor> vendedores = repositorio.listarVendedores();
        List<Producto> productos = repositorio.listarProductos();
        sistema.cargarDatos(clientes, vendedores, productos, new ArrayList<Venta>());

        // Las ventas se arman con las MISMAS instancias de cliente, vendedor y producto de las listas
        for (RegistroVenta r : repositorio.listarVentas()) {
            Cliente cliente = sistema.buscarClientePorId(r.getIdCliente());
            Vendedor vendedor = sistema.buscarVendedorPorLegajo(r.getLegajo());
            Producto producto = sistema.buscarProductoPorId(r.getIdProducto());
            if (cliente == null || vendedor == null || producto == null) {
                throw new PersistenciaException("La venta " + r.getNumero() + " hace referencia a un cliente, "
                        + "vendedor o producto que no existe.");
            }
            try {
                sistema.agregarVenta(new Venta(r.getNumero(), r.getFecha(), cliente, vendedor, producto));
            } catch (GarantiaException | IllegalArgumentException e) {
                throw new PersistenciaException("No se pudo reconstruir la venta " + r.getNumero()
                        + ": " + e.getMessage(), e);
            }
        }
        return advertencias;
    }

    /**
     * Compara los períodos de garantía de la tabla categorias (MySQL) con los que tiene cada
     * subclase de Producto en el código.
     *
     * ARREGLO (tamaño fijo): el sistema tiene exactamente 3 categorías, por eso los períodos
     * y los nombres se guardan en arreglos cuya posición i corresponde a id_categoria = i + 1.
     * ARRAYLIST (tamaño variable): las advertencias pueden ser 0, 1, 2 o 3, por eso se
     * acumulan en una lista.
     */
    private List<String> verificarCategorias() throws PersistenciaException {
        int[] periodosEnBD = repositorio.periodosGarantiaPorCategoria();
        int[] periodosEnCodigo = {
            LineaBlanca.PERIODO_DIAS, PequenoElectrodomestico.PERIODO_DIAS, TecnologiaHogar.PERIODO_DIAS
        };
        String[] nombres = { "Línea blanca", "Pequeños electrodomésticos", "Tecnología del hogar" };

        List<String> advertencias = new ArrayList<>();
        for (int i = 0; i < periodosEnCodigo.length; i++) {
            if (periodosEnBD[i] != periodosEnCodigo[i]) {
                advertencias.add(nombres[i] + ": la base de datos indica " + periodosEnBD[i]
                        + " días y el programa usa " + periodosEnCodigo[i] + " días (se aplicarán " + periodosEnCodigo[i] + ").");
            }
        }
        return advertencias;
    }

    /** Cantidad de garantías cuyo estado se corrigió en MySQL durante el inicio. */
    public int getGarantiasActualizadas() {
        return garantiasActualizadas;
    }

    // ===================================================================
    //  ESCRITURAS: validar -> guardar en MySQL -> reflejar en memoria
    // ===================================================================

    /** Alta de cliente (RFS05). */
    public Cliente agregarCliente(String nombre, String contacto) throws PersistenciaException {
        Cliente nuevo = new Cliente(repositorio.siguienteIdCliente(), nombre, contacto);   // valida los datos
        repositorio.insertarCliente(nuevo);
        sistema.agregarCliente(nuevo);
        return nuevo;
    }

    /** Alta de producto (RFS04). La pantalla crea la subclase; acá se guarda. */
    public void agregarProducto(Producto producto) throws PersistenciaException {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        repositorio.insertarProducto(producto);
        sistema.agregarProducto(producto);
    }

    /** Modificación de un cliente (RFS10): UPDATE en MySQL y luego en la lista. */
    public void modificarCliente(int idCliente, String nombre, String contacto) throws PersistenciaException {
        if (sistema.buscarClientePorId(idCliente) == null) {
            throw new IllegalArgumentException("No existe un cliente con id " + idCliente + ".");
        }
        Cliente validado = new Cliente(idCliente, nombre, contacto);   // valida los datos antes de tocar MySQL
        repositorio.actualizarCliente(validado);
        sistema.modificarCliente(idCliente, nombre, contacto);
    }

    /** Registro de una venta (RFS02). La garantía se calcula sola dentro de Venta (RFS03). */
    public Venta registrarVenta(Cliente cliente, Vendedor vendedor, Producto producto, LocalDate fecha)
            throws GarantiaException, PersistenciaException {
        Venta nueva = new Venta(repositorio.siguienteNumeroVenta(), fecha, cliente, vendedor, producto);
        repositorio.insertarVenta(nueva);   // venta + garantía en una transacción
        sistema.agregarVenta(nueva);
        return nueva;
    }

    /** Siguiente id para un producto nuevo (lo necesita la pantalla para crear la subclase). */
    public int siguienteIdProducto() throws PersistenciaException {
        return repositorio.siguienteIdProducto();
    }

    // ===================================================================
    //  CONSULTAS: se resuelven con los datos cargados desde MySQL
    // ===================================================================

    public String getNombre()               { return sistema.getNombre(); }
    public List<Cliente> getClientes()      { return sistema.getClientes(); }
    public List<Vendedor> getVendedores()   { return sistema.getVendedores(); }
    public List<Producto> getProductos()    { return sistema.getProductos(); }

    public Venta buscarVenta(int numero)                   { return sistema.buscarVenta(numero); }
    public List<Venta> ventasDeCliente(Cliente cliente)    { return sistema.ventasDeCliente(cliente); }
    public List<Venta> ventasDeProducto(Producto producto) { return sistema.ventasDeProducto(producto); }

    public List<Venta> ventasEntreFechas(LocalDate desde, LocalDate hasta) {
        return sistema.ventasEntreFechas(desde, hasta);
    }

    public List<Venta> ordenarPorFecha(List<Venta> lista, boolean ascendente) {
        return sistema.ordenarPorFecha(lista, ascendente);
    }
}
