package BackendBoys.dawBB.controladores;

import BackendBoys.dawBB.dtos.MateriaNuevaDTO;
import BackendBoys.dawBB.utils.DtoAndEntityMapper;
import BackendBoys.dawBB.dtos.MateriaDTO;
import BackendBoys.dawBB.entidades.Materia;
import BackendBoys.dawBB.excepciones.MateriaNoEncontrado;
import BackendBoys.dawBB.servicios.MateriaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/materias")
@Tag(name = "Materias", description = "Gestión de materias")
public class ControladorMaterias {

    private final MateriaServicio servicio;

    public ControladorMaterias(MateriaServicio servicio) {
        this.servicio = servicio;
    }

    //Obtener Materia por ID
    @GetMapping("/{idMateria}")
    @Operation(
            operationId = "consultarMateria",
            description = "Devuelve información de una materia concreta.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Devolución correcta"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Materia no encontrada"
                    )
            }
    )
    public ResponseEntity<MateriaDTO> obtenerMateria(@PathVariable Long idMateria) {

        // Llamada al serivico
        Materia materia = servicio.obtenerMateriaPorId(idMateria);
        //convertir materia a materiaDTO
        MateriaDTO materiaDTO = DtoAndEntityMapper.materiaToDto(materia);
        // Devolver materia con codigo 200 (ok)
        return ResponseEntity.ok(materiaDTO);
    }

    //Actualiza una materia concreta
    @PutMapping("/{idMateria}")
    @Operation(
            description = "Actualiza una materia concreta.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Actualización correcta"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Materia no encontrada"
                    )
            }
    )
    public ResponseEntity<MateriaDTO> actualizarMateria(@PathVariable Long idMateria, @RequestBody MateriaNuevaDTO materiaNueva) {
        //conversion de dto a materia
        Materia materiaEntidad = DtoAndEntityMapper.materiaNuevaToMateria(materiaNueva);
        // Le pasamos el ID y materia al servicio
        Materia materiaGuardada = servicio.actualizarMateria(idMateria, materiaEntidad);
        //convertir materia guardada a DTO
        MateriaDTO respuestaDTO = DtoAndEntityMapper.materiaToDto(materiaGuardada);

        return ResponseEntity.ok(respuestaDTO);
    }

    //eliminar una materia especifica de manera logica
    @DeleteMapping("/{idMateria}")
    @Operation(
            description = "Elimina una materia concreta. Solo se puede hacer una eliminación lógica (marcando la materia como eliminada).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Eliminación correcta"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Materia no encontrada"
                    )
            }
    )
    public ResponseEntity<Void> eliminarMateria(@PathVariable Long idMateria) {
        //Llamada al servicio
        servicio.eliminarMateria(idMateria);

        // Devolver un código 200 (OK) sin cuerpo (build)
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    @Operation(
            operationId = "consultarMaterias",
            description = "Devuelve la lista de materias.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Devolución correcta"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    )
            }
    )
    public ResponseEntity<List<MateriaDTO>> obtenerMaterias() {
        //llamada a servicio
        List<Materia> listaMaterias = servicio.obtenerTodasLasMateriasActivas();

        List<MateriaDTO> listaMateriasDto = listaMaterias.stream().map(DtoAndEntityMapper::materiaToDto).toList();

        //Devolver la lista con un 200 OK
        return ResponseEntity.ok(listaMateriasDto);
    }

    @PostMapping("")
    @Operation(
            description = "Crea una nueva materia. Devuelve la materia creada.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "La materia se ha creado correctamente",
                            headers = @Header(name = "Location", description = "URI del nuevo recurso", schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    )
            }
    )
    public ResponseEntity<MateriaDTO> crearMateria(@RequestBody MateriaNuevaDTO materiaNueva) {
        //Llamada al servicio
        Materia materiaGuardada = servicio.crearMateria(DtoAndEntityMapper.materiaNuevaToMateria(materiaNueva));

        //materia a dto
        MateriaDTO respuestaDTO = DtoAndEntityMapper.materiaToDto(materiaGuardada);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().
                path("/{idMateria}")
                .buildAndExpand(respuestaDTO.getId())
                .toUri();

        //porque no acepta esto? --> por el tipo de retorno del Response Entity
        return ResponseEntity.created(location).body(respuestaDTO);
    }

    @ExceptionHandler(MateriaNoEncontrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado() {
    }
}