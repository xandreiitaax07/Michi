package BackendBoys.dawBB.excepciones;

public class SlotsNoEncontrado extends RuntimeException {
    public SlotsNoEncontrado(Long id) {
        super("No se encontró ningun slot de ID: " + id);
    }
}
