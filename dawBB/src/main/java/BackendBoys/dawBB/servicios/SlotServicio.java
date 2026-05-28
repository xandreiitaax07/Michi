package BackendBoys.dawBB.servicios;

import BackendBoys.dawBB.entidades.Slot;
import BackendBoys.dawBB.excepciones.SlotsNoEncontrado;
import BackendBoys.dawBB.repositorios.SlotRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlotServicio {

    private final SlotRepositorio slotRepositorio;

    public SlotServicio(SlotRepositorio slotRepositorio) {
        this.slotRepositorio = slotRepositorio;
    }

    public Slot obtenerSlotPorId(Long id) {
        return slotRepositorio.findById(id).orElseThrow(() -> new SlotsNoEncontrado(id));
    }

    @Transactional
    public Slot modificarSlot(Long id, Slot slot) {
        Slot existente = obtenerSlotPorId(id);
        existente.setEliminado(slot.getEliminado());
        existente.setConvocatoria(slot.getConvocatoria());
        existente.setFin(slot.getFin());
        existente.setInicio(slot.getInicio());
        slotRepositorio.save(existente);
        return existente;
    }

    @Transactional
    public void eliminarSlot(Long id) {
        Slot slot = obtenerSlotPorId(id);
        slot.setEliminado(true);
        slotRepositorio.save(slot);
    }

    public List<Slot> obtenerTodosSlots(Long idConvocatoria) {
        if (idConvocatoria != null) {
            return slotRepositorio.findByConvocatoriaIdConvocatoria(idConvocatoria);
        }
        return slotRepositorio.findByConvocatoriaActualTrue();
    }

    @Transactional
    public Slot addSlot(Slot slot) {
        slot.setId(null);
        return slotRepositorio.save(slot);
    }
}
