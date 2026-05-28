package BackendBoys.dawBB.utils;

import BackendBoys.dawBB.dtos.*;
import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.entidades.Materia;
import BackendBoys.dawBB.entidades.Prueba;
import BackendBoys.dawBB.entidades.Slot;
import BackendBoys.dawBB.repositorios.MateriaRepositorio;

public class DtoAndEntityMapper {

    public static Prueba pruebaNuevaToPrueba(PruebaNuevaDTO pruebaNueva) {
        if (pruebaNueva == null) {
            return null;
        }
        Prueba prueba = new Prueba();
        Slot slot = new Slot();
        Materia materia = new Materia();
        prueba.setId(null);
        prueba.setEliminada(false);
        materia.setId(pruebaNueva.getMateria().getId());
        slot.setId(pruebaNueva.getSlot().getId());
        prueba.setMateria(materia);
        prueba.setSlot(slot);
        return prueba;
    }

    public static Convocatoria convocatoriaNuevaToConvocatoria(ConvocatoriaNuevaDTO convocatoriaNueva) {
        if (convocatoriaNueva == null) {
            return null;
        }

        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setIdConvocatoria(null);
        convocatoria.setNombre(convocatoriaNueva.getNombre());
        convocatoria.setFechaInicio(convocatoriaNueva.getFechaInicio());
        convocatoria.setFechaFin(convocatoriaNueva.getFechaFin());
        convocatoria.setActual(false);
        convocatoria.setMaterias(null);
        convocatoria.setSlots(null);
        return convocatoria;
    }

    public static Materia materiaNuevaToMateria(MateriaNuevaDTO materiaNueva) {
        if (materiaNueva == null) {
            return null;
        }
        Materia materia = new Materia();
        materia.setId(null);
        materia.setNombre(materiaNueva.getNombre());
        materia.setEliminada(false);
        materia.setPruebas(null);
        return materia;
    }

    public static Slot slotNuevoToSlot(SlotNuevoDTO slotNuevo) {
        if (slotNuevo == null) {
            return null;
        }
        Slot slot = new Slot();
        slot.setConvocatoria(convocatoriaDTOtoEntity(slotNuevo.getConvocatoria()));
        slot.setInicio(slotNuevo.getInicio());
        slot.setFin(slotNuevo.getFin());
        slot.setEliminado(slotNuevo.getEliminado());
        slot.setId(null);
        slot.setEliminado(false);
        return slot;
    }

    public static ConvocatoriaDTO convocatoriaToDto(Convocatoria convocatoria) {
        if (convocatoria == null) {
            return null;
        }

        return new ConvocatoriaDTO(
                convocatoria.getIdConvocatoria(),
                convocatoria.getNombre(),
                convocatoria.getFechaInicio(),
                convocatoria.getFechaFin()
        );
    }

    public static Convocatoria convocatoriaDTOtoEntity(ConvocatoriaDTO convocatoriaDTO) {
        if (convocatoriaDTO == null) {
            return null;
        }

        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setIdConvocatoria(convocatoriaDTO.getIdConvocatoria());
        convocatoria.setNombre(convocatoriaDTO.getNombre());
        convocatoria.setFechaInicio(convocatoriaDTO.getFechaInicio());
        convocatoria.setFechaFin(convocatoriaDTO.getFechaFin());
        return convocatoria;
    }

    public static MateriaDTO materiaToDto(Materia materia) {
        if (materia == null) {
            return null;
        }

        return new MateriaDTO(
                materia.getId(),
                materia.getNombre(),
                materia.getEliminada()
        );
    }

    public static Materia materiaDTOtoEntity(MateriaDTO materiaDTO) {
        if (materiaDTO == null) {
            return null;
        }
        Materia materia = new Materia();
        materia.setId(materiaDTO.getId());
        materia.setNombre(materiaDTO.getNombre());
        materia.setEliminada(materiaDTO.getEliminada());
        return materia;
    }

    public static PruebaDTO pruebaToDto(Prueba prueba) {
        if (prueba == null) {
            return null;
        }
        return new PruebaDTO(
                prueba.getId(),
                slotToDto(prueba.getSlot()),
                materiaToDto(prueba.getMateria()),
                prueba.getEliminada()
        );
    }

    public static Prueba pruebaDTOtoEntity(PruebaDTO pruebaDTO) {
        if (pruebaDTO == null) {
            return null;
        }
        Prueba prueba = new Prueba();
        prueba.setId(pruebaDTO.getId());
        prueba.setSlot(slotDTOtoEntity(pruebaDTO.getSlot()));
        prueba.setMateria(materiaDTOtoEntity(pruebaDTO.getMateria()));
        prueba.setEliminada(pruebaDTO.getEliminada());
        return prueba;
    }

    public static SlotDTO slotToDto(Slot slot) {
        if (slot == null) {
            return null;
        }

        return new SlotDTO(
                slot.getId(),
                slot.getInicio(),
                slot.getFin(),
                slot.getEliminado(),
                convocatoriaToDto(slot.getConvocatoria())
        );
    }

    public static Slot slotDTOtoEntity(SlotDTO slotDTO) {
        if (slotDTO == null) {
            return null;
        }
        Slot slot = new Slot();
        slot.setId(slotDTO.getId());
        slot.setInicio(slotDTO.getInicio());
        slot.setFin(slotDTO.getFin());
        slot.setEliminado(slotDTO.getEliminado());
        slot.setConvocatoria(convocatoriaDTOtoEntity(slotDTO.getConvocatoria()));
        return slot;
    }


}
