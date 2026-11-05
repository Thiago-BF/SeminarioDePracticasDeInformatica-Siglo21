package puntotecno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase gestora del sistema (equivalente a CentralEolica del ejemplo ECOviento).
 * Guarda en memoria, en ArrayList, los clientes, vendedores, productos y ventas,
 * y ofrece las operaciones que usará el menú: altas, modificación, búsqueda y
 * listados ordenados.
 *
 * TP4: es la parte del MODELO que vive en memoria. Los datos NO se escriben acá a mano:
 * el ControladorPuntoTecno los carga desde MySQL al iniciar y, cada vez que se guarda
 * algo en la base, lo refleja en estas listas.
 *
 * Las listas son PRIVADAS (encapsulamiento): desde afuera solo se accede por métodos.
 */
public class PuntoTecno {

    // ===================== ATRIBUTOS =====================
    private String nombre;
    private ArrayList<Cliente> clientes;
    private ArrayList<Vendedor> vendedores;
    private ArrayList<Producto> productos;
    private ArrayList<Venta> ventas;   // siempre ordenada por número de venta (ver agregarVenta)

    // ===================== CONSTRUCTOR =====================
    // Inicializa las listas vacías (igual que CentralEolica). Los datos llegan desde MySQL
    // mediante cargarDatos(), que invoca el controlador.
    public PuntoTecno(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<>();
        this.vendedores = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.ventas = new ArrayList<>();
    }

    // ===================== CARGA DE DATOS (TP4) =====================
    /**
     * Reemplaza el contenido de las listas por los datos leídos de MySQL.
     * Reemplaza a cargarDatosIniciales() del TP3, que escribía los datos de prueba en el código:
     * ahora esos mismos datos están en sql/puntotecno_tp4.sql.
     * Las ventas deben llegar ordenadas por número (requisito de la búsqueda binaria).
     */
    public void cargarDatos(List<Cliente> clientes, List<Vendedor> vendedores,
                            List<Producto> productos, List<Venta> ventas) {
        this.clientes = new ArrayList<>(clientes);
        this.vendedores = new ArrayList<>(vendedores);
        this.productos = new ArrayList<>(productos);
        this.ventas = new ArrayList<>(ventas);
    }

    // ===================== ALTAS Y MODIFICACIÓN =====================

    /** Incorpora a la lista un cliente que ya fue guardado en MySQL (RFS05). */
    public void agregarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        clientes.add(cliente);
    }

    /** Incorpora a la lista un producto que ya fue guardado en MySQL (RFS04). */
    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        productos.add(producto);
    }

    /** Modificación de datos de un cliente existente (RFS10), ya actualizado en MySQL. */
    public void modificarCliente(int idCliente, String nombre, String contacto) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) {
            throw new IllegalArgumentException("No existe un cliente con id " + idCliente + ".");
        }
        c.actualizarDatos(nombre, contacto);
    }

    /**
     * Incorpora a la lista una venta que ya fue guardada en MySQL (RFS02).
     * Se exige número creciente para que la lista siga ordenada y la búsqueda binaria funcione.
     */
    public void agregarVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }
        if (!ventas.isEmpty() && venta.getNumero() <= ventas.get(ventas.size() - 1).getNumero()) {
            throw new IllegalArgumentException("El número de venta debe ser mayor al de la última venta cargada.");
        }
        ventas.add(venta);
    }

    // ===================== BÚSQUEDAS =====================

    /** Búsqueda LINEAL por id: recorre la lista hasta encontrarlo. Devuelve null si no existe. */
    public Cliente buscarClientePorId(int idCliente) {
        for (Cliente c : clientes) {
            if (c.getIdCliente() == idCliente) return c;
        }
        return null;
    }

    public Producto buscarProductoPorId(int idProducto) {
        for (Producto p : productos) {
            if (p.getIdProducto() == idProducto) return p;
        }
        return null;
    }

    public Vendedor buscarVendedorPorLegajo(int legajo) {
        for (Vendedor v : vendedores) {
            if (v.getLegajo() == legajo) return v;
        }
        return null;
    }

    /**
     * Búsqueda BINARIA de una venta por número (RFS06).
     * Funciona porque la lista de ventas está ordenada por número.
     * Cada paso descarta la mitad de la lista. Devuelve null si no existe.
     */
    public Venta buscarVenta(int numero) {
        int inicio = 0;
        int fin = ventas.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            int numeroMedio = ventas.get(medio).getNumero();
            if (numeroMedio == numero) {
                return ventas.get(medio);
            } else if (numeroMedio < numero) {
                inicio = medio + 1;   // buscar en la mitad derecha
            } else {
                fin = medio - 1;      // buscar en la mitad izquierda
            }
        }
        return null;
    }

    // ===================== CONSULTAS Y FILTROS =====================

    /** Historial de ventas de un cliente (RFS07). */
    public List<Venta> ventasDeCliente(Cliente cliente) {
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getCliente().getIdCliente() == cliente.getIdCliente()) resultado.add(v);
        }
        return resultado;
    }

    /** Historial de ventas de un producto (RFS08). */
    public List<Venta> ventasDeProducto(Producto producto) {
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getProducto().getIdProducto() == producto.getIdProducto()) resultado.add(v);
        }
        return resultado;
    }

    /** Ventas dentro de un rango de fechas, ambas incluidas (filtro del RFS09). */
    public List<Venta> ventasEntreFechas(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : ventas) {
            if (!v.getFecha().isBefore(desde) && !v.getFecha().isAfter(hasta)) resultado.add(v);
        }
        return resultado;
    }

    // ===================== ORDENACIÓN =====================

    /**
     * Devuelve una COPIA de la lista de ventas ordenada por fecha (RFS09),
     * usando el algoritmo de INSERCIÓN. Trabaja sobre una copia para no alterar
     * el orden por número que necesita la búsqueda binaria.
     * @param ascendente true = de la más antigua a la más reciente; false = al revés.
     */
    public List<Venta> ventasOrdenadasPorFecha(boolean ascendente) {
        return ordenarPorFecha(new ArrayList<>(ventas), ascendente);
    }

    /** Ordenación por inserción aplicada a cualquier lista de ventas (también a las filtradas). */
    public List<Venta> ordenarPorFecha(List<Venta> lista, boolean ascendente) {
        List<Venta> copia = new ArrayList<>(lista);
        for (int i = 1; i < copia.size(); i++) {
            Venta actual = copia.get(i);
            int j = i - 1;
            // Desplaza hacia la derecha los elementos que deben ir después de "actual"
            while (j >= 0 && debeIrDespues(copia.get(j), actual, ascendente)) {
                copia.set(j + 1, copia.get(j));
                j--;
            }
            copia.set(j + 1, actual);
        }
        return copia;
    }

    // Auxiliar: indica si "a" debe ubicarse después de "b" según el sentido pedido
    private boolean debeIrDespues(Venta a, Venta b, boolean ascendente) {
        if (ascendente) return a.getFecha().isAfter(b.getFecha());
        return a.getFecha().isBefore(b.getFecha());
    }

    // ===================== GETTERS (devuelven COPIAS para proteger las listas) =====================
    public String getNombre()            { return nombre; }
    public List<Cliente> getClientes()   { return new ArrayList<>(clientes); }
    public List<Vendedor> getVendedores(){ return new ArrayList<>(vendedores); }
    public List<Producto> getProductos() { return new ArrayList<>(productos); }
    public List<Venta> getVentas()       { return new ArrayList<>(ventas); }
    public boolean hayVentas()           { return !ventas.isEmpty(); }
}
