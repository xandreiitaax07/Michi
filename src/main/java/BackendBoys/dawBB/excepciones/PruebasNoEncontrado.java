package BackendBoys.dawBB.excepciones;

public class PruebasNoEncontrado extends RuntimeException {
    public PruebasNoEncontrado(Long id) {
        super("No se encontró ninguna prueba de ID: " + id);
    }
}
