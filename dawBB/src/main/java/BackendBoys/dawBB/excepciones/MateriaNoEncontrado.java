package BackendBoys.dawBB.excepciones;

public class MateriaNoEncontrado extends RuntimeException {
    public MateriaNoEncontrado(Long id) {
        super("No se encontró ninguna materia de ID: " + id);
    }
}
