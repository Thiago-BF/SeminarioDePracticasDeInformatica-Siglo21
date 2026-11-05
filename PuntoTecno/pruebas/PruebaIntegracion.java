import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import puntotecno.Cliente;
import puntotecno.GarantiaException;
import puntotecno.LineaBlanca;
import puntotecno.Producto;
import puntotecno.PuntoTecno;
import puntotecno.TecnologiaHogar;
import puntotecno.Venta;
import puntotecno.controlador.ControladorPuntoTecno;
import puntotecno.persistencia.ConexionBD;
import puntotecno.persistencia.ConexionException;
import puntotecno.persistencia.ModeloMySQL;
import puntotecno.persistencia.PersistenciaException;

/**
 * Prueba de integración por CONSOLA (sin interfaz gráfica) del acceso a MySQL.
 * Verifica cada operación y comprueba el resultado directamente en la base.
 *
 * Antes de ejecutarla hay que importar sql/puntotecno_tp4.sql (deja la base en su estado inicial)
 * y tener config/conexion.properties. Ver README.md, sección "Prueba por consola".
 * ATENCIÓN: modifica datos de la base "puntotecno" (agrega registros de prueba).
 */
public class PruebaIntegracion {

    private static int ok = 0;
    private static int fallos = 0;
    private static ConexionBD conexion;

    public static void main(String[] args) throws Exception {
        conexion = ConexionBD.desdeArchivo(ConexionBD.ARCHIVO_POR_DEFECTO);
        int conexionesBase = hilosConectados();

        PuntoTecno sistema = new PuntoTecno("PuntoTecno");
        ControladorPuntoTecno ctrl = new ControladorPuntoTecno(sistema, new ModeloMySQL(conexion));

        System.out.println("=== 1. LECTURA (SELECT) ===");
        List<String> advertencias = ctrl.iniciar();
        verificar("Clientes cargados = 4", sistema.getClientes().size() == 4);
        verificar("Vendedores cargados = 2", sistema.getVendedores().size() == 2);
        verificar("Productos cargados = 6", sistema.getProductos().size() == 6);
        verificar("Ventas cargadas = 5", sistema.getVentas().size() == 5);
        verificar("Producto 1 es LineaBlanca (polimorfismo)", sistema.buscarProductoPorId(1) instanceof LineaBlanca);
        verificar("Producto 4 es TecnologiaHogar", sistema.buscarProductoPorId(4) instanceof TecnologiaHogar);
        verificar("Venta 3 vencida el 08/08/2025 (calculada)", sistema.buscarVenta(3).getGarantia().getFechaFin().equals(LocalDate.of(2025, 8, 8)));
        verificar("Categorías coherentes: sin advertencias", advertencias.isEmpty());

        System.out.println("\n=== 2. ARREGLO DE CATEGORÍAS: detecta una diferencia ===");
        ejecutar("UPDATE categorias SET periodo_dias = 400 WHERE id_categoria = 1");
        List<String> adv2 = new ControladorPuntoTecno(new PuntoTecno("x"), new ModeloMySQL(conexion)).iniciar();
        verificar("Se genera 1 advertencia", adv2.size() == 1);
        System.out.println("    -> " + (adv2.isEmpty() ? "-" : adv2.get(0)));
        ejecutar("UPDATE categorias SET periodo_dias = 365 WHERE id_categoria = 1");

        System.out.println("\n=== 3. UPDATE MASIVO: sincronizar estados ===");
        ejecutar("UPDATE garantias SET estado = 'Vigente' WHERE numero_venta = 5");   // lo dejo desactualizado a propósito
        ControladorPuntoTecno c3 = new ControladorPuntoTecno(new PuntoTecno("x"), new ModeloMySQL(conexion));
        c3.iniciar();
        verificar("Se corrigió 1 garantía", c3.getGarantiasActualizadas() == 1);
        verificar("En MySQL la garantía de la venta 5 quedó 'Vencida'", "Vencida".equals(consultarTexto("SELECT estado FROM garantias WHERE numero_venta = 5")));

        System.out.println("\n=== 4. INSERT de cliente ===");
        Cliente nuevo = ctrl.agregarCliente("Laura Gimenez", "3564-555666");
        verificar("Cliente recibió id 5", nuevo.getIdCliente() == 5);
        verificar("Está en MySQL", "Laura Gimenez".equals(consultarTexto("SELECT nombre FROM clientes WHERE id_cliente = 5")));
        verificar("Está en la lista en memoria", sistema.buscarClientePorId(5) != null);

        System.out.println("\n=== 5. UPDATE de cliente ===");
        ctrl.modificarCliente(1, "Juan Perez", "3564-999888");
        verificar("Contacto actualizado en MySQL", "3564-999888".equals(consultarTexto("SELECT contacto FROM clientes WHERE id_cliente = 1")));
        verificar("Contacto actualizado en memoria", "3564-999888".equals(sistema.buscarClientePorId(1).getContacto()));

        System.out.println("\n=== 6. INSERT de producto ===");
        Producto cafetera = new puntotecno.PequenoElectrodomestico(ctrl.siguienteIdProducto(), "Cafetera Pro", 95000);
        ctrl.agregarProducto(cafetera);
        verificar("Producto 7 guardado con id_categoria = 2", "2".equals(consultarTexto("SELECT id_categoria FROM productos WHERE id_producto = 7")));

        System.out.println("\n=== 7. INSERT de venta + garantía (transacción) ===");
        Venta v = ctrl.registrarVenta(nuevo, sistema.getVendedores().get(0), cafetera, LocalDate.of(2026, 10, 15));
        verificar("Venta número 6", v.getNumero() == 6);
        verificar("Venta 6 en MySQL", "1".equals(consultarTexto("SELECT COUNT(*) FROM ventas WHERE numero_venta = 6")));
        verificar("Garantía 6 en MySQL con fin 2027-04-13 (15/10/2026 + 180 días)",
                "2027-04-13".equals(consultarTexto("SELECT fecha_fin FROM garantias WHERE numero_venta = 6")));

        System.out.println("\n=== 8. CASOS DE ERROR ===");
        try {
            ctrl.agregarCliente("", "3564-000000");
            verificar("Cliente con nombre vacío debe rechazarse", false);
        } catch (IllegalArgumentException e) {
            verificar("Nombre vacío -> IllegalArgumentException: \"" + e.getMessage() + "\"", true);
        }
        verificar("... y no se guardó nada (siguen 5 clientes)", "5".equals(consultarTexto("SELECT COUNT(*) FROM clientes")));

        try {
            new ModeloMySQL(conexion).insertarCliente(new Cliente(1, "Duplicado", "1"));
            verificar("Clave duplicada debe fallar", false);
        } catch (PersistenciaException e) {
            verificar("Clave duplicada -> PersistenciaException con causa SQL", e.getCause() instanceof SQLException);
            System.out.println("    -> mensaje al usuario: " + e.getMessage().replace('\n', ' '));
            System.out.println("    -> detalle técnico:   " + e.getDetalleTecnico());
        }

        // Rollback: se fuerza un fallo en el SEGUNDO insert (garantías) y se comprueba que la venta no quedó guardada
        ejecutar("RENAME TABLE garantias TO garantias_tmp");
        try {
            ctrl.registrarVenta(nuevo, sistema.getVendedores().get(1), sistema.buscarProductoPorId(1), LocalDate.of(2026, 10, 20));
            verificar("Debe fallar si no existe la tabla garantias", false);
        } catch (PersistenciaException | GarantiaException e) {
            verificar("Falla el segundo INSERT -> PersistenciaException", e instanceof PersistenciaException);
        } finally {
            ejecutar("RENAME TABLE garantias_tmp TO garantias");
        }
        verificar("ROLLBACK: la venta 7 NO quedó guardada", "0".equals(consultarTexto("SELECT COUNT(*) FROM ventas WHERE numero_venta = 7")));
        verificar("ROLLBACK: la lista en memoria sigue con 6 ventas", sistema.getVentas().size() == 6);

        // Errores de conexión
        try {
            new ModeloMySQL(new ConexionBD("jdbc:mysql://localhost:3399/puntotecno", "x", "x")).listarClientes();
            verificar("Puerto incorrecto debe fallar", false);
        } catch (ConexionException e) {
            verificar("Servidor inaccesible -> ConexionException", true);
            System.out.println("    -> " + e.getMessage().replace('\n', ' '));
        } catch (PersistenciaException e) {
            verificar("Servidor inaccesible -> ConexionException (fue " + e.getClass().getSimpleName() + ")", false);
        }
        try {
            ConexionBD.desdeArchivo("config/no_existe.properties");
            verificar("Archivo inexistente debe fallar", false);
        } catch (ConexionException e) {
            verificar("Archivo de configuración ausente -> ConexionException", true);
        }

        System.out.println("\n=== 9. CIERRE DE RECURSOS ===");
        for (int i = 0; i < 30; i++) {
            ctrl.iniciar();
        }
        int despues = hilosConectados();
        verificar("Conexiones abiertas antes = después (" + conexionesBase + " vs " + despues + ")", conexionesBase == despues);

        System.out.println("\nRESULTADO: " + ok + " correctas, " + fallos + " con fallo.");
        System.exit(fallos == 0 ? 0 : 1);
    }

    // ---------- Auxiliares ----------
    private static void verificar(String descripcion, boolean condicion) {
        System.out.println((condicion ? "  [OK]    " : "  [FALLO] ") + descripcion);
        if (condicion) ok++; else fallos++;
    }

    private static void ejecutar(String sql) throws Exception {
        try (Connection con = conexion.obtener(); Statement st = con.createStatement()) {
            st.execute(sql);
        }
    }

    private static String consultarTexto(String sql) throws Exception {
        try (Connection con = conexion.obtener(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    // Cantidad de conexiones abiertas en el servidor (la de esta consulta cuenta como 1)
    private static int hilosConectados() throws Exception {
        try (Connection con = conexion.obtener(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SHOW STATUS LIKE 'Threads_connected'")) {
            rs.next();
            return rs.getInt(2);
        }
    }
}
