package BackendBoys.dawBB.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@Schema(name = "Slot")
public class SlotDTO {
    private Long id;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private Boolean eliminado;
    private ConvocatoriaDTO convocatoria;
}
