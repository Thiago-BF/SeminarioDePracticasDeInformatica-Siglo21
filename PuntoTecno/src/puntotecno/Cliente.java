package puntotecno;

/**
 * Entidad Cliente (ENCAPSULAMIENTO): atributos privados,
 * solo se accede mediante getters y el método actualizarDatos().
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String contacto;

    // Constructor con parámetros: el cliente nace siempre con sus datos completos
    public Cliente(int idCliente, String nombre, String contacto) {
        validar(nombre, contacto);
        this.idCliente = idCliente;
        this.nombre = nombre.trim();
        this.contacto = contacto.trim();
    }

    /** Modifica los datos del cliente (RFS10). */
    public void actualizarDatos(String nombre, String contacto) {
        validar(nombre, contacto);
        this.nombre = nombre.trim();
        this.contacto = contacto.trim();
    }

    // Validación interna (privada: es un detalle que no le interesa al resto del sistema)
    private void validar(String nombre, String contacto) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (contacto == null || contacto.trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto del cliente no puede estar vacío.");
        }
    }

    public int getIdCliente()   { return idCliente; }
    public String getNombre()   { return nombre; }
    public String getContacto() { return contacto; }

    @Override
    public String toString() {
        return nombre + " (" + contacto + ")";
    }
}
