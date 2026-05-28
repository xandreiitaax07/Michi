package BackendBoys.dawBB.servicios;

import BackendBoys.dawBB.dtos.PruebaDTO;
import BackendBoys.dawBB.dtos.PruebaNuevaDTO;
import BackendBoys.dawBB.entidades.Prueba;
import BackendBoys.dawBB.excepciones.PruebasNoEncontrado;
import BackendBoys.dawBB.repositorios.PruebaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PruebaServicio {
    private final PruebaRepositorio pruebaRepositorio;
    private final SlotServicio slotServicio;
    private final MateriaServicio materiaServicio;

    public PruebaServicio(PruebaRepositorio pruebaRepositorio, SlotServicio slotServicio,
                          MateriaServicio materiaServicio) {

        this.pruebaRepositorio = pruebaRepositorio;
        this.slotServicio = slotServicio;
        this.materiaServicio = materiaServicio;
    }

    public List<Prueba> obtenerTodasLasPruebas(Long idConvocatoria, Long idSlot) {
        if (idConvocatoria != null && idSlot != null) {
            return pruebaRepositorio.findBySlotConvocatoriaIdConvocatoriaAndSlotId(idConvocatoria, idSlot);
        } else if (idConvocatoria != null) {
            return pruebaRepositorio.findBySlotConvocatoriaIdConvocatoria(idConvocatoria);
        } else if (idSlot != null) {
            return pruebaRepositorio.findBySlotId(idSlot);
        } else {
            return pruebaRepositorio.findBySlotConvocatoriaActualTrue();
        }
    }

    public Prueba obtenerPruebaPorId(Long id) {
        return pruebaRepositorio.findById(id).orElseThrow(() -> new PruebasNoEncontrado(id));
    }

    @Transactional
    public Prueba crearPrueba(Prueba prueba) {
        prueba.setMateria(materiaServicio.obtenerMateriaPorId(prueba.getMateria().getId()));
        prueba.setSlot(slotServicio.obtenerSlotPorId(prueba.getSlot().getId()));
        prueba.setId(null);
        prueba.setEliminada(false);
        return pruebaRepositorio.save(prueba);
    }

    @Transactional
    public Prueba actualizarPrueba(Long id, Prueba prueba) {
        Prueba existente = obtenerPruebaPorId(id);
        existente.setMateria(materiaServicio.obtenerMateriaPorId(prueba.getMateria().getId()));
        existente.setSlot(slotServicio.obtenerSlotPorId(prueba.getSlot().getId()));
        existente.setEliminada(prueba.getEliminada());
        return pruebaRepositorio.save(existente);
    }

    @Transactional
    public void eliminarPrueba(Long id) {
        Prueba prueba = obtenerPruebaPorId(id);
        prueba.setEliminada(true);
        pruebaRepositorio.save(prueba);
    }
}
