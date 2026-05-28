package BackendBoys.dawBB.excepciones;

public class ConvocatoriaNoEncontrado extends RuntimeException {
    public ConvocatoriaNoEncontrado(Long id) {
        super("No se encontró ninguna convocatoria de ID: " + id);
    }
}
