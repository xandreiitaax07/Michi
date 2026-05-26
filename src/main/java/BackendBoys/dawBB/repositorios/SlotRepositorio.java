package BackendBoys.dawBB.repositorios;

import BackendBoys.dawBB.entidades.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SlotRepositorio extends JpaRepository<Slot, Long> {

    // EN TEORIA SE QUEDA VACIO PORQUE LAS BUSQUEDAS LAS HACE SPRING BOOT AUTOMATICAMENTE
    List<Slot> findByConvocatoriaIdConvocatoria(Long idConvocatoria);
    List<Slot> findByConvocatoriaActualTrue();

    List<Slot> findByEliminadoFalse();

    List<Slot> findByConvocatoriaIdConvocatoriaAndEliminadoFalse(Long idConvocatoria);

    List<Slot> findByInicioAfter(LocalDateTime fecha);
}
