package BackendBoys.dawBB.controladores;

import BackendBoys.dawBB.dtos.SlotNuevoDTO;
import BackendBoys.dawBB.excepciones.SlotsNoEncontrado;
import BackendBoys.dawBB.utils.DtoAndEntityMapper;
import BackendBoys.dawBB.dtos.SlotDTO;
import BackendBoys.dawBB.entidades.Slot;
import BackendBoys.dawBB.servicios.SlotServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/slots")
@Tag(name = "Slots", description = "Gestión de slots")
public class ControladorSlots {

    private final SlotServicio servicio;

    public ControladorSlots(SlotServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/{idSlot}")
    @Operation(
            operationId = "consultarSlot",
            description = "Devuelve información de un slot concreto.",
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
                            description = "Slot no encontrado"
                    )
            })
    public ResponseEntity<SlotDTO> obtenerSlot(@PathVariable Long idSlot) {
        Slot slot = servicio.obtenerSlotPorId(idSlot);
        SlotDTO dto = DtoAndEntityMapper.slotToDto(slot);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{idSlot}")
    @Operation(
            description = "Actualiza un slot concreto.",
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
                            description = "Slot no encontrado"
                    )
            })
    public ResponseEntity<SlotDTO> actualizarSlot(@PathVariable Long idSlot, @RequestBody SlotNuevoDTO slotNuevoDTO) {
        Slot slotActualizado = servicio.modificarSlot(idSlot, DtoAndEntityMapper.slotNuevoToSlot(slotNuevoDTO));
        SlotDTO dto = DtoAndEntityMapper.slotToDto(slotActualizado);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{idSlot}")
    @Operation(
            description = "Elimina un slot concreto. Solo se puede hacer una eliminación lógica " +
                    "(marcando el slot como eliminado) y solo de la convocatoria actual.",
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
                            description = "Slot no encontrado"
                    )
            })
    public ResponseEntity<Void> eliminarSlot(@PathVariable Long idSlot) {
        servicio.eliminarSlot(idSlot);
        return ResponseEntity.ok().build();
    }


    @GetMapping("")
    @Operation(
            operationId = "consultarSlots",
            description = "Devuelve la lista de slots planificados en una convocatoria.",
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
                            description = "Convocatoria no encontrado"
                    )
            })
    public ResponseEntity<List<SlotDTO>> obtenerSlots(@Valid @RequestParam(required = false)
                                                      @Parameter(description = "Si no se indica se asume que se "
                                                              + "pregunta por la convocatoria actual.") Long idConvocatoria) {
        List<Slot> listaSlots = servicio.obtenerTodosSlots(idConvocatoria);
        List<SlotDTO> listaSlotDto = listaSlots.stream().map(DtoAndEntityMapper::slotToDto).toList();
        return ResponseEntity.ok(listaSlotDto);
    }


    @PostMapping("")
    @Operation(
            description = "Crea un nuevo slot en la convocatoria actua. Devuelve el slot creado.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "El slot se ha creado correctamente",
                            headers = @Header(name = "Location", description = "URI del nuevo recurso", schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso no autorizado"
                    )
            })
    public ResponseEntity<SlotDTO> crearSlot(@RequestBody SlotNuevoDTO slotNuevo) {
        Slot nuevoSlot = servicio.addSlot(DtoAndEntityMapper.slotNuevoToSlot(slotNuevo));
        SlotDTO dto = DtoAndEntityMapper.slotToDto(nuevoSlot);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idSlot}").
                buildAndExpand(nuevoSlot.getId())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }


    @ExceptionHandler(SlotsNoEncontrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado() {
    }
}
