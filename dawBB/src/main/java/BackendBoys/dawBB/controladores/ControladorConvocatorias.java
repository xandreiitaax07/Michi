package BackendBoys.dawBB.controladores;

import BackendBoys.dawBB.dtos.ConvocatoriaDTO;
import BackendBoys.dawBB.dtos.ConvocatoriaNuevaDTO;
import BackendBoys.dawBB.utils.DtoAndEntityMapper;
import BackendBoys.dawBB.entidades.Convocatoria;
import BackendBoys.dawBB.excepciones.ConvocatoriaNoEncontrado;
import BackendBoys.dawBB.servicios.ConvocatoriaServicio;
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
@RequestMapping("/convocatorias")
@Tag(name = "Convocatorias", description = "Gestión de convocatorias")
public class ControladorConvocatorias {

    private final ConvocatoriaServicio servicio;

    public ControladorConvocatorias(ConvocatoriaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("")
    @Operation(
            operationId = "consultarConvocatorias",
            description = "Devuelve la lista de convocatorias.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Devolución correcta"
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Acceso no autorizado"
                ),
            }
    )
    public ResponseEntity<List<ConvocatoriaDTO>> obtenerConvocatorias() {
        List<Convocatoria> lista = servicio.obtenerTodasConvocatorias();
        //Ver en que caso se devuelve esto:

        List<ConvocatoriaDTO> listaDTO = lista.stream().map(DtoAndEntityMapper::convocatoriaToDto).toList();
        return ResponseEntity.ok(listaDTO);
    }

    @PostMapping("")
    @Operation(
            description = "Crea una nueva convocatoria y se convierte en actual.",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "La convocatoria se ha creado correctamente",
                        headers = @Header(name = "Location", description = "URI del nuevo recurso", schema = @Schema(type = "string"))
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Acceso no autorizado"
                ),
            }
    )
    public ResponseEntity<ConvocatoriaDTO> crearConvocatoria(@RequestBody ConvocatoriaNuevaDTO convocatoriaNueva) {
        Convocatoria added = servicio.addConvocatoria(DtoAndEntityMapper.convocatoriaNuevaToConvocatoria(convocatoriaNueva));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getIdConvocatoria())
                .toUri();
        return ResponseEntity.created(location).body(DtoAndEntityMapper.convocatoriaToDto(added));
    }

    @GetMapping("/actual")
    @Operation(
            operationId = "consultarConvocatoriaActual",
            description = "Devuelve información de la convocatoria actual.",
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
                            description = "Convocatoria no encontrada"
                    )
            }
    )
    public ResponseEntity<ConvocatoriaDTO> obtenerConvocatoriaActual() {
        Convocatoria actual = servicio.obtenerConvocatoriaActual();
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DtoAndEntityMapper.convocatoriaToDto(actual));
    }

    @ExceptionHandler(ConvocatoriaNoEncontrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado() {
    }

}
