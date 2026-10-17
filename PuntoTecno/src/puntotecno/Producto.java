package puntotecno;

/**
 * Clase ABSTRACTA Producto (ABSTRACCIÓN).
 * Representa un producto del local sin saber de qué categoría es.
 * Cada categoría concreta (subclase) define su propio período de garantía.
 * No se puede hacer "new Producto(...)": solo se instancian las subclases.
 */
public abstract class Producto {

    // ---------- Atributos (protected: visibles para las subclases, ocultos al resto) ----------
    protected int idProducto;
    protected String nombre;
    protected double precio;

    // ---------- Constructor con parámetros (no hay constructor por defecto) ----------
    public Producto(int idProducto, String nombre, double precio) {
        // Validaciones básicas para no crear objetos con datos inválidos
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.idProducto = idProducto;
        this.nombre = nombre.trim();
        this.precio = precio;
    }

    // ---------- Métodos abstractos (cada subclase DEBE implementarlos: POLIMORFISMO) ----------

    /** Devuelve los días de garantía que corresponden a la categoría del producto. */
    public abstract int getPeriodoGarantiaDias();

    /** Devuelve el nombre de la categoría (ej: "Línea blanca"). */
    public abstract String getCategoria();

    // ---------- Getters (solo se expone lo relevante) ----------
    public int getIdProducto() { return idProducto; }
    public String getNombre()  { return nombre; }
    public double getPrecio()  { return precio; }

    /** Texto que se muestra en listas y combos: "Heladera XYZ (Línea blanca)". */
    @Override
    public String toString() {
        return nombre + " (" + getCategoria() + ")";
    }
}
