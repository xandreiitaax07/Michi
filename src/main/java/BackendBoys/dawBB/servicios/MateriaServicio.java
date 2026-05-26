package BackendBoys.dawBB.servicios;


import BackendBoys.dawBB.entidades.Materia;
import BackendBoys.dawBB.excepciones.MateriaNoEncontrado;
import BackendBoys.dawBB.repositorios.MateriaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaServicio {

    private final MateriaRepositorio materiaRepositorio;

    public MateriaServicio(MateriaRepositorio materiaRepositorio) {
        this.materiaRepositorio = materiaRepositorio;
    }

    public Materia obtenerMateriaPorId(Long id) {
        //Buscamos la materia. Si el ID no existe en la BD, lanza excepción 404.
        Materia materia = materiaRepositorio.findById(id).orElseThrow(() -> new MateriaNoEncontrado(id));

        //Si existe, pero está "eliminada", lanzamos 404 también.
        if (materia.getEliminada()) {
            throw new MateriaNoEncontrado(id);
        }

        return materia;
    }

    public List<Materia> obtenerTodasLasMateriasActivas() {
        //usar linea de repositorio para encontrar lo que buscamos
        //esta linea solo busca materias sin eliminar
        return materiaRepositorio.findByEliminadaFalse();
    }

    public List<Materia> obtenerTodasLasMaterias() {
        //busca todas las materias, incluso las eliminadas
        return materiaRepositorio.findAll();
    }

    @Transactional
    public Materia actualizarMateria(Long id, Materia materia) {
        //Busca la materia existente, se encarga del error 404
        Materia materiaExistente = this.obtenerMateriaPorId(id);

        //Actualizacion de materia
        if (materia.getNombre() != null) {
            materiaExistente.setNombre(materia.getNombre());
        }

        //guardar cambios
        return materiaRepositorio.save(materiaExistente);
    }

    @Transactional
    public void eliminarMateria(Long id) {
        //comprobar si existe, error 404 si no
        Materia materia = this.obtenerMateriaPorId(id);

        //"eliminar" la materia
        materia.setEliminada(true);

        //Guarda el cambio en la base de datos
        materiaRepositorio.save(materia);
    }

    @Transactional
    public Materia crearMateria(Materia materia) {

        //Entidad en blanco, la id se genera automaticamente en principio
        Materia nuevaMateria = new Materia();
        //Asignacion de nombre
        nuevaMateria.setNombre(materia.getNombre());

        //Asignacion de estado
        nuevaMateria.setEliminada(false);

        //Guardar en base de datos
        return materiaRepositorio.save(nuevaMateria);
    }
}
