package BackendBoys.dawBB.servicios;

import BackendBoys.dawBB.dtos.ConvocatoriaNuevaDTO;
import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.repositorios.ConvocatoriaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConvocatoriaServicio {

    private final ConvocatoriaRepositorio convocatoriaRepositorio;

    public ConvocatoriaServicio(ConvocatoriaRepositorio convocatoriaRepositorio) {
        this.convocatoriaRepositorio = convocatoriaRepositorio;
    }

    public List<Convocatoria> obtenerTodasConvocatorias() {
        return convocatoriaRepositorio.findAll();
    }

    public Convocatoria obtenerConvocatoriaActual() {
        return convocatoriaRepositorio.findFirstByOrderByIdConvocatoriaDesc();
    }

    @Transactional
    public Convocatoria addConvocatoria(Convocatoria convocatoria) {
        //Obtenemos la convocatoria que esta a true como actual y la seteamos como no activa
        Optional<Convocatoria> actual = convocatoriaRepositorio.findByActualTrue();
        actual.ifPresent(
                convocatoria1 -> {
                    convocatoria1.setActual(false);
                    convocatoriaRepositorio.save(convocatoria1);
                })
        ;

        convocatoria.setIdConvocatoria(null);
        convocatoria.setActual(true);

        return convocatoriaRepositorio.save(convocatoria);
    }
}
