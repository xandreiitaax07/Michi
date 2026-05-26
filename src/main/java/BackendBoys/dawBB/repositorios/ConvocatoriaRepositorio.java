package BackendBoys.dawBB.repositorios;

import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.entidades.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConvocatoriaRepositorio extends JpaRepository<Convocatoria, Long> {
    Convocatoria findFirstByOrderByIdConvocatoriaDesc();

    Optional<Convocatoria> findByActualTrue();
}
