package puntotecno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase gestora del sistema (equivalente a CentralEolica del ejemplo ECOviento).
 * Guarda en memoria, en ArrayList, los clientes, vendedores, productos y ventas,
 * y ofrece las operaciones que usará el menú: altas, modificación, registro de
 * ventas, búsqueda y listados ordenados.
 *
 * Las listas son PRIVADAS (encapsulamiento): desde afuera solo se accede por métodos.
 */
public class PuntoTecno {

    // ===================== ATRIBUTOS =====================
    private String nombre;
    private ArrayList<Cliente> clientes;
    private ArrayList<Vendedor> vendedores;
    private ArrayList<Producto> productos;
    private ArrayList<Venta> ventas;   // siempre ordenada por número de venta (ver registrarVenta)

    // ===================== CONSTRUCTOR =====================
    // Inicializa las listas vacías (igual que CentralEolica) y carga los datos de prueba
    public PuntoTecno(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<>();
        this.vendedores = new ArrayList<>();
        this.productos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        cargarDatosIniciales();
    }

    // ===================== DATOS PRECARGADOS =====================
    /**
     * Carga los mismos datos que el script puntotecno.sql del TP2.
     * Reemplaza la carga manual: en el TP4 esta información se leerá desde MySQL con JDBC.
     * Las garantías NO se cargan a mano: cada Venta calcula la suya.
     */
    private void cargarDatosIniciales() {
        // Clientes
        clientes.add(new Cliente(1, "Juan Perez", "3564-111222"));
        clientes.add(new Cliente(2, "Maria Gomez", "3564-222333"));
        clientes.add(new Cliente(3, "Carlos Lopez", "3564-333444"));
        clientes.add(new Cliente(4, "Ana Rodriguez", "3564-444555"));

        // Vendedores
        vendedores.add(new Vendedor(101, "Ana Martinez"));
        vendedores.add(new Vendedor(104, "Roberto Fernandez"));

        // Productos (la subclase determina la categoría y la garantía)
        productos.add(new LineaBlanca(1, "Heladera XYZ", 850000.00));
        productos.add(new LineaBlanca(2, "Lavarropas Ultra", 620000.00));
        productos.add(new PequenoElectrodomestico(3, "Microondas Quick", 180000.00));
        productos.add(new TecnologiaHogar(4, "Auriculares BT", 45000.00));
        productos.add(new TecnologiaHogar(5, "Notebook Pro 15", 1200000.00));
        productos.add(new TecnologiaHogar(6, "Smart TV 55 4K", 950000.00));

        // Ventas (número, fecha, cliente, vendedor, producto)
        try {
            registrarVenta(clientes.get(0), vendedores.get(0), productos.get(0), LocalDate.of(2026, 9, 22));
            registrarVenta(clientes.get(1), vendedores.get(1), productos.get(4), LocalDate.of(2026, 8, 15));
            registrarVenta(clientes.get(2), vendedores.get(0), productos.get(5), LocalDate.of(2025, 5, 10));
            registrarVenta(clientes.get(3), vendedores.get(1), productos.get(2), LocalDate.of(2026, 2, 1));
            registrarVenta(clientes.get(0), vendedores.get(0), productos.get(3), LocalDate.of(2024, 11, 12));
        } catch (GarantiaException e) {
            // No debería ocurrir con datos válidos; se informa por si se modifica la carga
            System.err.println("Error al cargar ventas iniciales: " + e.getMessage());
        }
    }

    // ===================== IDENTIFICADORES AUTOMÁTICOS =====================
    // El sistema asigna el siguiente número libre: el usuario no tiene que inventar IDs.

    public int siguienteIdCliente() {
        int max = 0;
        for (Cliente c : clientes) {
            if (c.getIdCliente() > max) max = c.getIdCliente();
        }
        return max + 1;
    }

    public int siguienteIdProducto() {
        int max = 0;
        for (Producto p : productos) {
            if (p.getIdProducto() > max) max = p.getIdProducto();
        }
        return max + 1;
    }

    // Las ventas se agregan siempre al final con número creciente, por lo que
    // la lista queda ordenada por número (requisito de la búsqueda binaria).
    private int siguienteNumeroVenta() {
        if (ventas.isEmpty()) return 1;
        return ventas.get(ventas.size() - 1).getNumero() + 1;
    }

    // ===================== ALTAS Y MODIFICACIÓN =====================

    /** Alta de cliente (RFS05). El id se asigna automáticamente. */
    public Cliente agregarCliente(String nombre, String contacto) {
        Cliente nuevo = new Cliente(siguienteIdCliente(), nombre, contacto); // valida internamente
        clientes.add(nuevo);
        return nuevo;
    }

    /** Alta de producto (RFS04). Recibe el objeto ya creado (de la subclase que corresponda). */
    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        productos.add(producto);
    }

    /** Modificación de datos de un cliente existente (RFS10). */
    public void modificarCliente(int idCliente, String nombre, String contacto) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) {
            throw new IllegalArgumentException("No existe un cliente con id " + idCliente + ".");
        }
        c.actualizarDatos(nombre, contacto);
    }

    /** Registra una venta (RFS02). La garantía se calcula sola dentro de Venta (RFS03). */
    public Venta registrarVenta(Cliente cliente, Vendedor vendedor, Producto producto, LocalDate fecha)
            throws GarantiaException {
        Venta nueva = new Venta(siguienteNumeroVenta(), fecha, cliente, vendedor, producto);
        ventas.add(nueva);
        return nueva;
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
