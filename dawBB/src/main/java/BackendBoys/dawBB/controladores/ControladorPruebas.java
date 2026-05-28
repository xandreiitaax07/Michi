package BackendBoys.dawBB.controladores;

import BackendBoys.dawBB.dtos.PruebaNuevaDTO;
import BackendBoys.dawBB.utils.DtoAndEntityMapper;
import BackendBoys.dawBB.dtos.PruebaDTO;
import BackendBoys.dawBB.entidades.Prueba;
import BackendBoys.dawBB.excepciones.PruebasNoEncontrado;
import BackendBoys.dawBB.servicios.PruebaServicio;
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
@RequestMapping("/pruebas")
@Tag(name = "Pruebas", description = "Gestión de pruebas")
public class ControladorPruebas {

    private final PruebaServicio servicio;

    public ControladorPruebas(PruebaServicio pruebaServicio) {
        this.servicio = pruebaServicio;
    }

    @GetMapping("/{idPrueba}")
    @Operation(
            operationId = "consultarPrueba",
            description = "Devuelve información de una prueba concreta.",
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
                            description = "Prueba no encontrada"
                    )
            }

    )
    public ResponseEntity<PruebaDTO> obtenerPrueba(@PathVariable Long idPrueba) {
        Prueba prueba = servicio.obtenerPruebaPorId(idPrueba);
        PruebaDTO pruebaDTO = DtoAndEntityMapper.pruebaToDto(prueba);
        return ResponseEntity.ok(pruebaDTO);
    }

    @PutMapping("/{idPrueba}")
    @Operation(
            description = "Actualiza una prueba concreta",
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
                            description = "Prueba no encontrada"
                    )
            }

    )
    public ResponseEntity<PruebaDTO> actualizarPrueba(@PathVariable Long idPrueba, @RequestBody PruebaNuevaDTO pruebaNueva) {
        Prueba actualizada = servicio.actualizarPrueba(idPrueba, DtoAndEntityMapper.pruebaNuevaToPrueba(pruebaNueva));
        PruebaDTO actDto = DtoAndEntityMapper.pruebaToDto(actualizada);
        return ResponseEntity.ok(actDto);
    }

    @DeleteMapping("/{idPrueba}")
    @Operation(
            description = "Elimina una prueba concreta. Solo se puede hacer una eliminación lógica (marcando la prueba como eliminada).",
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
                            description = "Prueba no encontrada"
                    )
            }

    )
    public ResponseEntity<Void> eliminarPrueba(@PathVariable Long idPrueba) {
        servicio.eliminarPrueba(idPrueba);
        return ResponseEntity.ok().build();
    }


    @GetMapping("")
    @Operation(
            operationId = "consultarPruebas",
            description = "Devuelve la lista de pruebas planificadas en un slot y convocatoria.",
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
                            description = "Slot o convocatoria no encontrada"
                    )
            }


    )
    public ResponseEntity<List<PruebaDTO>> obtenerPruebas(@RequestParam(required = false) Long idConvocatoria, @RequestParam(required = false) Long idSlot) {
        List<Prueba> lista = servicio.obtenerTodasLasPruebas(idConvocatoria, idSlot);
        List<PruebaDTO> listaDto = lista.stream().map(DtoAndEntityMapper::pruebaToDto).toList();
        return ResponseEntity.ok(listaDto);
    }

    @PostMapping("")
    @Operation(
            description = "Crea una nueva prueba. Devuelve la prueba creada.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "La prueba se ha creado correctamente",
                            headers = @Header(name = "Location", description = "URI del nuevo recurso", schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Materia o el slot no se encuentran"
                    )
            }


    )
    public ResponseEntity<PruebaDTO> crearPrueba(@RequestBody PruebaNuevaDTO pruebaNueva) {
        Prueba creada = servicio.crearPrueba(DtoAndEntityMapper.pruebaNuevaToPrueba(pruebaNueva));
        PruebaDTO creadaDto = DtoAndEntityMapper.pruebaToDto(creada);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(creadaDto);
    }

    @ExceptionHandler(PruebasNoEncontrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado() {
    }

}
