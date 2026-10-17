package puntotecno;

/**
 * Entidad Vendedor: queda asociado a cada venta que registra.
 */
public class Vendedor {

    private int legajo;
    private String nombre;

    public Vendedor(int legajo, String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del vendedor no puede estar vacío.");
        }
        this.legajo = legajo;
        this.nombre = nombre.trim();
    }

    public int getLegajo()    { return legajo; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre + " (Legajo " + legajo + ")";
    }
}
