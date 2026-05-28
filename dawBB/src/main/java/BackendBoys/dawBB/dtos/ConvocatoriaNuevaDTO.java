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
@Schema(name="ConvocatoriaNueva")
public class ConvocatoriaNuevaDTO {
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}