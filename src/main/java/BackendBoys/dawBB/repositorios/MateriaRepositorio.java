package BackendBoys.dawBB.repositorios;

import BackendBoys.dawBB.entidades.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepositorio extends JpaRepository<Materia, Long> {
    
    List<Materia> findByEliminadaFalse();

    Materia findById(long id);

    Optional<Materia> findByIdAndEliminadaFalse(Long idMateria);

    boolean existsByNombreAndEliminadaFalse(String nombre);

}
