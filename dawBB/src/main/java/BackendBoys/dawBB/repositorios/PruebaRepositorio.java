package BackendBoys.dawBB.repositorios;

import BackendBoys.dawBB.entidades.Prueba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PruebaRepositorio extends JpaRepository<Prueba, Long> {

    List<Prueba> findByEliminadaFalse();

    List<Prueba> findByMateriaId(Long idMateria);

    List<Prueba> findBySlotId(Long idSlot);

    List<Prueba> findByMateriaIdAndEliminadaFalse(Long idMateria);

    List<Prueba> findBySlotConvocatoriaIdConvocatoriaAndSlotId(Long idConvocatoria, Long idSlot);

    List<Prueba> findBySlotConvocatoriaIdConvocatoria(Long idConvocatoria);

    List<Prueba> findBySlotConvocatoriaActualTrue();
}
